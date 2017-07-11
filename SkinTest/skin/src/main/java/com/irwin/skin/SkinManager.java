package com.irwin.skin;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.text.TextUtils;


import com.irwin.skin.resources.BaseResources;
import com.irwin.skin.resources.SkinResource;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Irwin on 2016/5/4.
 */
public class SkinManager {
    public static final String SKIN_SUFFIX = ".skin";
    private static final String TAG = "SkinManager";
    private static final SkinManager INSTANCE = new SkinManager();

    private Context mContext;

    private ComposedResources mComposedResources;

    private ArrayList<WeakReference<ISkinObserver>> mObservers = new ArrayList<>();

    private SkinLoaderTask mSkinTask;

    public static SkinManager getInstance() {
        return INSTANCE;
    }

    private SkinManager() {

    }

    public SkinManager init(Context context) {
        //Avoid initialize multiple times.
        if (mContext != null) {
            return this;
        }
        mContext = context.getApplicationContext();
        mComposedResources = new ComposedResources(context);
        SkinStorage.getInstance().init(context);
        return this;
    }


    public void register(ISkinObserver observer) {
        if (observer != null) {
            synchronized (mObservers) {
                mObservers.add(new WeakReference<ISkinObserver>(observer));
            }
        }
    }

    public void unregister(ISkinObserver observer) {
        synchronized (mObservers) {
            Iterator<WeakReference<ISkinObserver>> iterator = mObservers.iterator();
            ISkinObserver item;
            while (iterator.hasNext()) {
                item = iterator.next().get();
                if (item == null) {
                    iterator.remove();
                    continue;
                }
                if (observer == item) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    public void changeSkin(String skinPath, String pkgName, ISkinCallback cb) {
        if (isSameSkin(skinPath, pkgName)) {
            if (cb != null) {
                cb.onSuccess();
            }
            return;
        }
        synchronized (INSTANCE) {
            if (mSkinTask != null) {
                mSkinTask.cancel(true);
            }
        }
        File file = new File(skinPath);
        if (!file.exists()) {
            if (cb != null) {
                cb.onFail(new Exception("Skin archive does not exsit: " + skinPath));
            }
            return;
        }
        doChangeSkin(skinPath, pkgName, true, cb);
    }

    /**
     * @param skinPath
     * @param pkgName
     * @param check
     * @param cb       If need to check the existence、validation of the skin.
     */
    private void doChangeSkin(String skinPath, String pkgName, boolean check, ISkinCallback cb) {
        new SkinLoaderTask(skinPath, pkgName, cb, check).execute();
    }

    /**
     * Tell if same skin, skinPath and pkgName must not be null at the same time.
     *
     * @param skinPath
     * @param pkgName
     * @return
     */
    public boolean isSameSkin(String skinPath, String pkgName) {
        boolean invalidPath = TextUtils.isEmpty(skinPath);
        boolean invalidPkg = TextUtils.isEmpty(pkgName);
        if (invalidPath && invalidPkg) {
            throw new IllegalArgumentException("Ether Skin path or package name should be valid");
        }
        String[] skinInfo = getCurrentSkinInfo();
        if (skinInfo == null) {
            //No skin.
            return mContext.getPackageName().equals(pkgName);
        }
        String path = skinInfo[0];
        String skinPkg = skinInfo[1];
        boolean samePkg = invalidPkg ? true : pkgName.equals(skinPkg);
        boolean samePath = invalidPath ? true : skinPath.equals(path);
        return (samePkg && samePath);
    }

    /**
     * Get information of skin used currently.
     *
     * @return An array consists of skin path at [0] and skin package at [1], or null if no skin used.
     */
    public String[] getCurrentSkinInfo() {
        SkinStorage storage = SkinStorage.getInstance();
        String skinPath = storage.getString(SkinStorage.KEY_SKIN_PATH, null);
        String skinPkg = storage.getString(SkinStorage.KEY_SKIN_PKG, null);
        if (TextUtils.isEmpty(skinPath) && TextUtils.isEmpty(skinPkg)) {
            return null;
        }
        return new String[]{skinPath, skinPkg};
    }


    private void saveCurrentSkin(String skinPath, String skinPkg) {
        SkinStorage storage = SkinStorage.getInstance();
        storage.putApply(SkinStorage.KEY_SKIN_PATH, TextUtils.isEmpty(skinPath) ? "" : skinPath);
        storage.putApply(SkinStorage.KEY_SKIN_PKG, TextUtils.isEmpty(skinPkg) ? "" : skinPkg);
    }

    /**
     * @param skinPath
     * @param pkgName
     * @param check    If need to check the existence、validation of the skin.
     * @param cb
     * @return An array consists of skin path at [0] and skin package at [1], or null if no skin used.
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws IOException
     */
    String[] loadSkin(String skinPath, String pkgName, boolean check, ISkinCallback cb) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        //If package name is null, we process skin to get the package name.
        if (check || TextUtils.isEmpty(pkgName)) {
            String[] skinInfo = processSkin(skinPath, pkgName);
            skinPath = skinInfo[0];
            pkgName = skinInfo[1];
        }
        SkinResource resource = SkinResource.create(mContext, skinPath, pkgName);
        mComposedResources.setSkinResources(resource);
        return new String[]{
                skinPath, pkgName
        };
    }

    /**
     * Process skin.
     *
     * @param skinPath
     * @param pkgName
     * @return An array consists of skin path at [0] and skin package at [1], or null if no skin used.
     * @throws IOException
     */
    private String[] processSkin(String skinPath, String pkgName) throws IOException {
        //If we need to verify pkg name.
        PackageInfo info = getPackageInfo(skinPath);
        String archivePkg = info.packageName;
        int versionCode = info.versionCode;
        if (pkgName == null) {
            pkgName = archivePkg;
        } else if (!archivePkg.equals(pkgName)) {
            throw new IllegalArgumentException("Package name not match:need " + archivePkg + " but " + pkgName + " provided.");
        }
        String realSkinPath = generateSkinPath(pkgName, versionCode);
        //If resuming skin, skin path may equals to real skin path. skin path is the external path otherwise.
        if (!new File(realSkinPath).exists()) {
            deleteObsoleteSkins();
            SkinUtils.copyFile(new File(skinPath), new File(realSkinPath), null);

        }
        return new String[]{
                realSkinPath, pkgName
        };
    }

    public String getSkinRoot() {
        return mContext.getFilesDir().getAbsolutePath();
    }

    public String generateSkinPath(String pkgName, int versionCode) {
        String skinName = pkgName + "_" + versionCode + SKIN_SUFFIX;
        return getSkinRoot() + File.separator + skinName;
    }

    void deleteObsoleteSkins() {
        //Scan for skins used currently.
        String skinRoot = getSkinRoot();
        File[] obsoleteSkins = new File(skinRoot).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return (pathname.getName().endsWith(SKIN_SUFFIX));
            }
        });
        //Delete skins will not be used.
        if (obsoleteSkins != null && obsoleteSkins.length > 0) {
            for (File f : obsoleteSkins) {
                f.delete();
            }
        }
    }

    /**
     * Get package archive info.This will cost >=500ms.
     *
     * @param skinPath
     * @return
     */
    private PackageInfo getPackageInfo(String skinPath) {
        PackageManager pm = mContext.getPackageManager();
        return pm.getPackageArchiveInfo(skinPath, PackageManager.GET_CONFIGURATIONS);
    }

    /**
     * Restore skin to app default skin.
     *
     * @param cb
     */
    public void restoreSkin(ISkinCallback cb) {
        if (isSameSkin(null, mContext.getPackageName())) {
            return;
        }
        if (cb != null) {
            cb.onStart();
        }
        mComposedResources.setSkinResources(null);
        saveCurrentSkin(null, mContext.getPackageName());
        notifyLayoutChanged();
        if (cb != null) {
            cb.onSuccess();
        }
    }

    /**
     * Resume skin.Call it on application started.
     *
     * @param cb
     */
    public void resumeSkin(ISkinCallback cb) {
        SkinStorage storage = SkinStorage.getInstance();
        String skinPath = storage.getString(SkinStorage.KEY_SKIN_REAL_PATH, null);
        String skinPkg = storage.getString(SkinStorage.KEY_SKIN_PKG, null);
        //If real skin path exists,use it to resume skin, use skin path otherwise.
        if (TextUtils.isEmpty(skinPath)) {
            skinPath = storage.getString(SkinStorage.KEY_SKIN_PATH, null);
        }
        if (TextUtils.isEmpty(skinPath)) {
            if (cb != null) {
                cb.onSuccess();
            }
            return;
        }
        doChangeSkin(skinPath, skinPkg, false, cb);
    }


    protected void notifyLayoutChanged() {
        synchronized (mObservers) {
            Iterator<WeakReference<ISkinObserver>> iterator = mObservers.iterator();
            ISkinObserver item;
            while (iterator.hasNext()) {
                item = iterator.next().get();
                if (item == null) {
                    iterator.remove();
                    continue;
                }
                item.onSkinChanged(mComposedResources);
            }
        }
    }

    public BaseResources getResources() {
        return mComposedResources;
    }


    public interface ISkinCallback {
        /**
         * Called on starting changing skin.
         */
        public void onStart();

        /**
         * Called on changing skin success.
         */
        public void onSuccess();

        /**
         * Called on changing skin fail.
         *
         * @param error
         */
        public void onFail(Throwable error);
    }

    private class SkinLoaderTask extends AsyncTask<Void, Void, Void> {
        private final boolean mCheck;
        private String mSkinPath;
        private String mPkgName;
        private ISkinCallback mCB;
        private Exception mError = null;

        /**
         * @param skinPath
         * @param pkgName
         * @param cb
         * @param check    If need to check the existence、validation of the skin.
         */
        public SkinLoaderTask(String skinPath, String pkgName, ISkinCallback cb, boolean check) {
            mSkinTask = this;
            mSkinPath = skinPath;
            mPkgName = pkgName;
            mCB = cb;
            //If package name is null,do checking work to get pakcage name.
            mCheck = TextUtils.isEmpty(pkgName) ? true : check;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mCB != null) {
                mCB.onStart();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String[] skinInfo = loadSkin(mSkinPath, mPkgName, mCheck, mCB);
                mPkgName = skinInfo[1];
            } catch (Exception e) {
                e.printStackTrace();
                mError = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mError != null) {
                if (mCB != null) {
                    mCB.onFail(mError);
                }
                return;
            }
            if (mCheck) {
                saveCurrentSkin(mSkinPath, mPkgName);
            }
            notifyLayoutChanged();
            if (mCB != null) {
                mCB.onSuccess();
            }
            synchronized (INSTANCE) {
                if (mSkinTask == this) {
                    mSkinTask = null;
                }
            }
        }
    }
}
