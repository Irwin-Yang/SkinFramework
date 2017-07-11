package com.dyy.skintest;

import android.content.Context;
import android.os.Environment;
import android.util.SparseArray;

import com.irwin.skin.SkinManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ARES on 2017/7/11.
 */

public class SkinHelper {

    private static final String SKIN_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "skin" + File.separator;
    public static final String MOCK_SKIN = "Mock Skin";
    private static final String TAG = "Skin";
    private static final String SKIN_SUFFIX = ".skin";

    public String[] initialize(Context context) {
        if (!hasSkinArchive()) {
            SkinManager.getInstance().restoreSkin(null);
            initSkin(context.getApplicationContext());
        }
        return getSkinArchive();
    }

    boolean hasSkinArchive() {
        String[] archives = getSkinArchive();
        return (archives != null && archives.length > 0);
    }


    String[] getSkinArchive() {
        return new File(SKIN_ROOT).list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".skin");
            }
        });
    }

    /**
     * Copy skin from assets.
     */
    void initSkin(Context context) {
        SparseArray<String> array = new SparseArray<>();
        array.put(R.raw.skin_vivid, "生动皮肤");
        array.put(R.raw.skin_plain, "普通皮肤");
        File root = new File(SKIN_ROOT);
        if (!root.exists()) {
            root.mkdirs();
        }
        for (int i = 0; i < array.size(); i++) {
            copySkinArchive(context, array.keyAt(i), SKIN_ROOT + File.separator + array.valueAt(i) + SKIN_SUFFIX);
        }
    }

    void copySkinArchive(Context context, int rawRes, String path) {
        InputStream ins = null;
        FileOutputStream outs = null;
        File target = new File(path);
        byte[] buffer = new byte[2048];
        try {
            target.createNewFile();
            outs = new FileOutputStream(target);
            ins = context.getResources().openRawResource(rawRes);
            int len = 0;
            while ((len = ins.read(buffer)) != -1) {
                if (len > 0) {
                    outs.write(buffer, 0, len);
                }
            }
            ins.close();
            ins = null;
            outs.close();
            outs = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outs != null) {
                try {
                    outs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
