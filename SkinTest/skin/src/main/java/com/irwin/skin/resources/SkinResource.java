package com.irwin.skin.resources;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Irwin on 2016/5/4.
 */
public class SkinResource extends BaseSkinResources {

    private static final String TAG = "SkinResource";


    private static final int BASE_ID = 1000;
    private int ID_TRACKER = BASE_ID;

    private String mPackageName;

    private synchronized int generateId() {
        return ID_TRACKER++;
    }

    private synchronized void resetID() {
        ID_TRACKER = BASE_ID;
    }

    public static SkinResource create(Context context, String skinPath, String packageName) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        AssetManager assetManager = AssetManager.class.newInstance();
        Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
        //Return 0 on failure.
        Object ret = addAssetPath.invoke(assetManager, skinPath);
        if (Integer.parseInt(ret.toString()) == 0) {
            throw new IllegalStateException("Add asset fail");
        }
        Resources localRes = context.getResources();
        return new SkinResource(context, assetManager, localRes.getDisplayMetrics(), localRes.getConfiguration(), packageName);
    }

    SkinResource(Context context, AssetManager assets, DisplayMetrics metrics, Configuration config, String packageName) {
        super(assets, metrics, config);
        setAppResource(context.getResources());
        mPackageName = packageName;
    }


    @Override
    public View getView(Context context, @LayoutRes int resId) {
        try {
            Context skinContext = new SkinThemeContext(context);
            View v = LayoutInflater.from(skinContext).inflate(resId, null);
            handleLayout(skinContext, v);
            return v;
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public String getPackageName() {
        return mPackageName;
    }

    protected void handleLayout(Context context, View v) {
        buildInflateRules(context, v);
    }


    /**
     * Map ids to which app can recognize locally.
     *
     * @param v View resource from skin package.
     */
    public void buildInflateRules(Context context, View v) {
        resetID();
        //Id map: Key as skin id and Value as local id.
        SparseIntArray array = new SparseIntArray();
        buildIdRules(context, v, array);
        int size = array.size();
        for (int i = 0; i < size; i++) {
            //Map id defined in skin package into real id in app.
            v.findViewById(array.keyAt(i)).setId(array.valueAt(i));
        }
    }

    /**
     * Extract id from view , build id rules and inflate rules if needed.
     *
     * @param v
     * @param array
     */
    protected void buildIdRules(Context context, View v, SparseIntArray array) {
        if (v.getId() != View.NO_ID) {
            //Get mapped id by id name.
            String idName = getResourceEntryName(v.getId());
            int mappedId = getAppResources().getIdentifier(idName, "id", context.getPackageName());
            //Add custom id to avoid id conflict when mapped id not exist.
            //Key as skin id and value as mapped id.
            array.put(v.getId(), mappedId > 0 ? mappedId : generateId());
        }
        if (v instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) v;
            int childCount = vp.getChildCount();
            for (int i = 0; i < childCount; i++) {
                buildIdRules(context, vp.getChildAt(i), array);
            }
        }
        buildInflateRules(v, array);
    }

    /**
     * Build inflate rules.
     *
     * @param v
     * @param array ID map of which Key as skin id and value as mapped id.
     */
    protected void buildInflateRules(View v, SparseIntArray array) {
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        if (lp == null) {
            return;
        }
        if (lp instanceof RelativeLayout.LayoutParams) {
            int[] rules = ((RelativeLayout.LayoutParams) lp).getRules();
            if (rules == null) {
                return;
            }
            int size = rules.length;
            int mapRule = -1;
            for (int i = 0; i < size; i++) {
                //Key as skin id and value as mapped id.
                if (rules[i] > 0 && (mapRule = array.get(rules[i])) > 0) {
//                    Log.i(TAG, "Rules[" + i + "]: Mapped from: " + rules[i] + "  to  " +mapRule);
                    rules[i] = mapRule;
                }
            }
        }
    }

    /**
     * @param resId
     * @return 0 if not exist
     */
    public int getCorrespondResId(int resId) {
        Resources appResources = getAppResources();
        String resName = appResources.getResourceName(resId);
        if (!TextUtils.isEmpty(resName)) {
            String skinName = resName.replace("com.dyy.skintest", getPackageName());
            int id = getIdentifier(skinName, null, null);
            String value = null;
            if(skinName.contains("BTN_Restore"))
            {
                if (id > 0) {
                    value = getText(id).toString();
                }
            }
            Log.i("Resources", "skin name: " + skinName + "  id: " + id + " value: " + value);
            return id;
        }
        return 0;

//        String resType = appResources.getResourceTypeName(resId);
//        String resName = appResources.getResourceEntryName(resId);
//        return getIdentifier(resName, resType, getPackageName());
    }


    private class SkinThemeContext extends ContextThemeWrapper {
        private WeakReference<Context> mContextRef;

        public SkinThemeContext(Context base) {
            super();
            if (base instanceof ContextThemeWrapper) {
                attachBaseContext(((ContextThemeWrapper) base).getBaseContext());
                mContextRef = new WeakReference<Context>(base);
            } else {
                attachBaseContext(base);
            }
            int themeRes = getThemeRes();
            if (themeRes <= 0) {
                themeRes = android.R.style.Theme_Light;
            }
            setTheme(themeRes);
        }


        public void onClick(View v) {
            Context context = mContextRef == null ? null : mContextRef.get();
            if (context == null) {
                return;
            }
            if (context instanceof View.OnClickListener) {
                ((View.OnClickListener) context).onClick(v);
            } else {
                Class cls = context.getClass();
                try {
                    Method m = cls.getDeclaredMethod("onClick", View.class);
                    if (m != null) {
                        m.invoke(context, v);
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public AssetManager getAssets() {
            return getAssets();
        }

        @Override
        public Resources getResources() {
            return SkinResource.this;
        }

        private int getThemeRes() {
            try {
                Method m = Context.class.getMethod("getThemeResId");
                return (int) m.invoke(getBaseContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return -1;
        }

    }


}

