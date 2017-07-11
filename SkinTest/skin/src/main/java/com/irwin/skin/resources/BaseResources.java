package com.irwin.skin.resources;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.LayoutRes;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by ARES on 2017/7/6.
 */

public abstract class BaseResources extends Resources {


    public BaseResources(Resources baseResources) {
        this(baseResources.getAssets(), baseResources.getDisplayMetrics(), baseResources.getConfiguration());
    }

    public BaseResources(AssetManager assets, DisplayMetrics metrics, Configuration config) {
        super(assets, metrics, config);
    }

    /**
     * Get layout view by resource id.
     *
     * @param resId
     * @return
     */
    public abstract View getView(Context context, @LayoutRes int resId);

    /**
     * Get package name.
     *
     * @return
     */
    public abstract String getPackageName();


}
