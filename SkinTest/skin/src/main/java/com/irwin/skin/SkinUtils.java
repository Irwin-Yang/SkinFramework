package com.irwin.skin;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import static com.irwin.skin.ComposedResources.LAYOUT_TAG_ID;

/**
 * Created by Irwin on 2016/5/4.
 */
public class SkinUtils {

    private SkinUtils() {
    }

    public static boolean hasLolliPop() {
        //TODO Check if we use the Build.VERSIONCODE.XXXX, May be not defined on low version of sdk.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static Drawable getDrawable(Context context, Resources resources, int resId) {
        if (SkinUtils.hasLolliPop()) {
            return resources.getDrawable(resId, context.getTheme());
        }
        return resources.getDrawable(resId);
    }

    public static int getColor(Context context, Resources resources, int resId) {
        if (SkinUtils.hasLolliPop()) {
            return resources.getColor(resId, context.getTheme());
        }
        return resources.getColor(resId);
    }

    /**
     * Tell if two layout view is from the same skin.
     *
     * @param currentView View from <code>ResourceManager.getView() </code> used currently.
     * @param newView     View from <code>ResourceManager.getView() </code> after change skin.
     * @return true if same layout, false otherwise.
     */
    public static boolean isSameLayout(View currentView, View newView) {
        if (currentView == newView) {
            return true;
        }
        if (currentView == null || newView == null) {
            return false;
        }
        if (currentView.getClass() != newView.getClass()) {
            return false;
        }
        Object currentTag = currentView.getTag(LAYOUT_TAG_ID);
        Object newTag = newView.getTag(LAYOUT_TAG_ID);
        if (currentTag == newTag) {
            return true;
        }
        if (currentTag == null || newTag == null) {
            return false;
        }
        return currentTag.equals(newTag);
    }

    public static void showIds(View target) {
        if (target.getId() != View.NO_ID) {
            Log.i("Ids", "View class: " + target.getClass().getSimpleName() + "  id: " + target.getId());
        }
        if (target instanceof ViewGroup) {
            int childs = ((ViewGroup) target).getChildCount();
            for (int i = 0; i < childs; i++) {
                showIds(((ViewGroup) target).getChildAt(i));
            }
        }

    }

    public static void copyFile(File src, File dest, IProgressListener listener) throws IOException {
        FileInputStream ins = null;
        FileOutputStream outs = null;
        try {
            ins = new FileInputStream(src);
            outs = new FileOutputStream(dest);
            byte[] buffer = new byte[20 * 1024];
            int length = 0;
            long startTime = System.currentTimeMillis();
            long now = 0;
            long total = src.length();
            long currentBytes = 0;
            while ((length = ins.read(buffer)) != -1) {
                outs.write(buffer, 0, length);
                currentBytes += length;
                if (listener != null) {
                    now = System.currentTimeMillis();
                    if (now - startTime >= 2000) {
                        startTime = now;
                        listener.onProgress(currentBytes, total);
                    }
                }
            }
            outs.close();
            outs = null;
            ins.close();
            ins = null;
        } finally {
            if (outs != null) {
                outs.close();
            }
            if (ins != null) {
                ins.close();
            }
        }
    }

    /**
     * Unzip file.It will load file content on demand so won't take too much memory.
     *
     * @param archive
     * @param decompressDir
     * @param listener
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ZipException
     */
    public static void unZipFile(String archive, String decompressDir, IProgressListener listener) throws IOException, FileNotFoundException, ZipException {
        BufferedOutputStream bos = null;
        ZipInputStream zins = null;
        try {
            File target = new File(archive);
            long totalSize = target.length();
            zins = new ZipInputStream(new FileInputStream(target));
            int progress = 0;
            byte[] buffer = new byte[10 * 1024];
            ZipEntry entry = null;
            long startTime = System.currentTimeMillis();
            long now = 0;
            while ((entry = zins.getNextEntry()) != null) {
                String entryName = entry.getName();
                String path = decompressDir + "/" + entryName;
                if (entry.isDirectory()) {
                    File decompressDirFile = new File(path);
                    if (!decompressDirFile.exists()) {
                        decompressDirFile.mkdirs();
                    }
                } else {
                    String fileDir = path.substring(0, path.lastIndexOf("/"));
                    File fileDirFile = new File(fileDir);
                    if (!fileDirFile.exists()) {
                        fileDirFile.mkdirs();
                    }
                    bos = new BufferedOutputStream(new FileOutputStream(decompressDir + "/" + entryName));
                    int length = 0;
                    while ((length = zins.read(buffer)) != -1) {
                        if (length > 0) {
                            bos.write(buffer, 0, length);
                            progress += length;
                        }
                    }
                    bos.close();
                    bos = null;
                }
                now = System.currentTimeMillis();
                if (now - startTime >= 2000) {
                    if (listener != null) {
                        listener.onProgress(progress, totalSize);
                    }
                    startTime = now;
                }
            }
        } finally {
            if (bos != null) {
                bos.close();
            }
            if (zins != null) {
                zins.close();
            }
        }
    }

    /**
     * Created by Irwin on 2015/12/4.
     * Common progress listener for background job.
     */
    public interface IProgressListener {

        /**
         * Called on progress changed.
         *
         * @param currentProgress
         * @param totalProgress
         */
        public void onProgress(long currentProgress, long totalProgress);

    }

}
