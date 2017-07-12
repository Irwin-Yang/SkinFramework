package com.dyy.skintest;

import android.app.Application;
import android.content.res.Resources;

import com.irwin.skin.SkinManager;

/**
 * Created by Irwin on 2016/5/5.
 */
public class SkinApplication extends Application {

    private Resources mResources;

    @Override
    public void onCreate() {
        super.onCreate();
        SkinManager.getInstance().initialize(this).resumeSkin(null);
    }

}
