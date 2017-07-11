package com.irwin.skin;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Irwin on 2016/5/5.
 */
public class SkinStorage extends AbstractSPProvider {
    public static final String STORE_NAME = "SkinStore";

    public static final String KEY_SKIN_REAL_PATH = "SkinRealPath";

    public static final String KEY_SKIN_PATH = "SkinPath";

    public static final String KEY_SKIN_PKG = "SkinPkg";

    public static final SkinStorage INSTANCE = new SkinStorage();

    private SharedPreferences mDB;

    public static SkinStorage getInstance() {
        return INSTANCE;
    }

    private SkinStorage() {
    }

    public void init(Context context) {
        mDB = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public SharedPreferences getDB() {
        return mDB;
    }

}
