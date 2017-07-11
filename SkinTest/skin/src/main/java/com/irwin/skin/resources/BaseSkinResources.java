package com.irwin.skin.resources;


import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by Irwin on 2016/5/5.
 */
public abstract class BaseSkinResources extends BaseResources {

    private Resources mAppResources;

    public BaseSkinResources(Resources baseResources) {
        super(baseResources);
    }

    public BaseSkinResources(AssetManager assets, DisplayMetrics metrics, Configuration config) {
        super(assets, metrics, config);
    }

    public void setAppResource(Resources resource) {
        mAppResources = resource;
    }

    public Resources getAppResources() {
        return mAppResources;
    }

    public abstract int getCorrespondResId(int resId);
}
