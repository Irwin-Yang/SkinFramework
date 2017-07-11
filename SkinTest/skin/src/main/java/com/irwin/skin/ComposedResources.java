package com.irwin.skin;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AnimRes;
import android.support.annotation.AnyRes;
import android.support.annotation.ArrayRes;
import android.support.annotation.BoolRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.FractionRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.annotation.RawRes;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.annotation.XmlRes;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import com.irwin.skin.resources.BaseSkinResources;
import com.irwin.skin.resources.BaseResources;

import java.io.InputStream;

/**
 * Created by ARES on 2017/7/5.
 * This is a resources class consists of App default skin and external skin resources if exists. We will find resource in external skin resources first,then the default.
 * Assume all resources ids are original  so that we should find corresponding resources ids in skin .
 */

public class ComposedResources extends BaseResources {
    static int LAYOUT_TAG_ID = -1;
    private Context mContext;
    private BaseSkinResources mSkinResources;

    public ComposedResources(Context context) {
        this(context, null);
    }

    public ComposedResources(Context context, BaseSkinResources skinResources) {
        super(context.getResources());
        mContext = context;
        mSkinResources = skinResources;
    }


    public ComposedResources setSkinResources(BaseSkinResources resources) {
        mSkinResources = resources;
        return this;
    }

    public BaseSkinResources getSkinResources() {
        return mSkinResources;
    }

    @NonNull
    @Override
    public CharSequence getText(@StringRes int id) throws NotFoundException {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            try {
                return mSkinResources.getText(realId);
            } catch (Exception e) {
            }
        }
        return super.getText(id);
    }

    @NonNull
    @Override
    public CharSequence getQuantityText(@PluralsRes int id, int quantity) throws NotFoundException {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            return mSkinResources.getQuantityText(realId, quantity);
        }
        return super.getQuantityText(id, quantity);
    }

    @NonNull
    @Override
    public String getQuantityString(@PluralsRes int id, int quantity, Object... formatArgs) throws NotFoundException {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            return mSkinResources.getQuantityString(realId, quantity, formatArgs);
        }
        return super.getQuantityString(id, quantity, formatArgs);
    }

    @NonNull
    @Override
    public String getQuantityString(@PluralsRes int id, int quantity) throws NotFoundException {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            return mSkinResources.getQuantityString(realId, quantity);
        }
        return super.getQuantityString(id, quantity);
    }

    @Override
    public CharSequence getText(@StringRes int id, CharSequence def) {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            return mSkinResources.getText(realId, def);
        }
        return super.getText(id, def);
    }

    @NonNull
    @Override
    public CharSequence[] getTextArray(@ArrayRes int id) throws NotFoundException {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            return mSkinResources.getTextArray(id);
        }
        return super.getTextArray(id);
    }

    @NonNull
    @Override
    public String[] getStringArray(@ArrayRes int id) throws NotFoundException {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            return mSkinResources.getStringArray(realId);
        }
        return super.getStringArray(id);
    }

    @NonNull
    @Override
    public int[] getIntArray(@ArrayRes int id) throws NotFoundException {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            return mSkinResources.getIntArray(realId);
        }
        return super.getIntArray(id);
    }

    @NonNull
    @Override
    public TypedArray obtainTypedArray(@ArrayRes int id) throws NotFoundException {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            return mSkinResources.obtainTypedArray(realId);
        }
        return super.obtainTypedArray(id);
    }

    @Override
    public float getDimension(@DimenRes int id) throws NotFoundException {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            return mSkinResources.getDimension(realId);
        }
        return super.getDimension(id);
    }

    @Override
    public int getDimensionPixelOffset(@DimenRes int id) throws NotFoundException {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            return mSkinResources.getDimensionPixelOffset(realId);
        }
        return super.getDimensionPixelOffset(id);
    }

    @Override
    public int getDimensionPixelSize(@DimenRes int id) throws NotFoundException {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            return mSkinResources.getDimensionPixelSize(realId);
        }
        return super.getDimensionPixelSize(id);
    }

    @Override
    public float getFraction(@FractionRes int id, int base, int pbase) {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            return mSkinResources.getFraction(id, base, pbase);
        }
        return super.getFraction(id, base, pbase);
    }

    @Override
    public Drawable getDrawable(@DrawableRes int id) throws NotFoundException {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            return mSkinResources.getDrawable(realId);
        }
        return super.getDrawable(id);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Drawable getDrawable(@DrawableRes int id, @Nullable Theme theme) throws NotFoundException {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            return mSkinResources.getDrawable(realId, theme);
        }
        return super.getDrawable(id, theme);
    }

    @Override
    public Drawable getDrawableForDensity(@DrawableRes int id, int density) throws NotFoundException {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            return mSkinResources.getDrawableForDensity(realId, density);
        }
        return super.getDrawableForDensity(id, density);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Drawable getDrawableForDensity(@DrawableRes int id, int density, @Nullable Theme theme) {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            return mSkinResources.getDrawableForDensity(realId, density, theme);
        }
        return super.getDrawableForDensity(id, density, theme);
    }

    @Override
    public Movie getMovie(@RawRes int id) throws NotFoundException {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            return mSkinResources.getMovie(realId);
        }
        return super.getMovie(id);
    }

    @Override
    public int getColor(@ColorRes int id) throws NotFoundException {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            return mSkinResources.getColor(realId);
        }
        return super.getColor(id);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int getColor(@ColorRes int id, @Nullable Theme theme) throws NotFoundException {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            return mSkinResources.getColor(realId, theme);
        }
        return super.getColor(id, theme);
    }

    @Nullable
    @Override
    public ColorStateList getColorStateList(@ColorRes int id) throws NotFoundException {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            return mSkinResources.getColorStateList(realId);
        }
        return super.getColorStateList(id);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public ColorStateList getColorStateList(@ColorRes int id, @Nullable Theme theme) throws NotFoundException {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            return mSkinResources.getColorStateList(realId, theme);
        }
        return super.getColorStateList(id, theme);
    }

    @Override
    public boolean getBoolean(@BoolRes int id) throws NotFoundException {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            return mSkinResources.getBoolean(realId);
        }
        return super.getBoolean(id);
    }

    @Override
    public int getInteger(@IntegerRes int id) throws NotFoundException {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            return mSkinResources.getInteger(realId);
        }
        return super.getInteger(id);
    }

    @Override
    public XmlResourceParser getLayout(@LayoutRes int id) throws NotFoundException {
        int realId = getCorrespondResIdStrictly(id);
        if (realId > 0) {
            return mSkinResources.getLayout(realId);
        }
        return super.getLayout(id);
    }

    @Override
    public XmlResourceParser getAnimation(@AnimRes int id) throws NotFoundException {
        int realId = getCorrespondResIdStrictly(id);
        if (realId > 0) {
            return mSkinResources.getAnimation(realId);
        }
        return super.getAnimation(id);
    }

    @Override
    public XmlResourceParser getXml(@XmlRes int id) throws NotFoundException {
        int realId = getCorrespondResIdStrictly(id);
        if (realId > 0) {
            return mSkinResources.getXml(realId);
        }
        return super.getXml(id);
    }

    @Override
    public InputStream openRawResource(@RawRes int id) throws NotFoundException {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            return mSkinResources.openRawResource(realId);
        }
        return super.openRawResource(id);
    }

    @Override
    public InputStream openRawResource(@RawRes int id, TypedValue value) throws NotFoundException {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            return mSkinResources.openRawResource(realId, value);
        }
        return super.openRawResource(id, value);
    }

    @Override
    public AssetFileDescriptor openRawResourceFd(@RawRes int id) throws NotFoundException {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            return mSkinResources.openRawResourceFd(realId);
        }
        return super.openRawResourceFd(id);
    }

    @Override
    public void getValue(@AnyRes int id, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            mSkinResources.getValue(realId, outValue, resolveRefs);
            return;
        }
        super.getValue(id, outValue, resolveRefs);
    }

    @Override
    public void getValueForDensity(@AnyRes int id, int density, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
        int realId = getCorrespondResId(id);
        if (realId > 0) {
            mSkinResources.getValueForDensity(realId, density, outValue, resolveRefs);
            return;
        }
        super.getValueForDensity(id, density, outValue, resolveRefs);
    }

    @Override
    public void getValue(String name, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
        if (mSkinResources != null) {
            try {
                mSkinResources.getValue(name, outValue, resolveRefs);
                return;
            } catch (Exception e) {
            }
        }
        super.getValue(name, outValue, resolveRefs);
    }

    @Override
    public void updateConfiguration(Configuration config, DisplayMetrics metrics) {
        if (mSkinResources != null) {
            mSkinResources.updateConfiguration(config, metrics);
        }
        super.updateConfiguration(config, metrics);
    }

    /**
     * Get correspond resources id with  app package. See also {@link #getCorrespondResId(int)}
     *
     * @param resId
     * @return 0 if not exist
     */
    public int getCorrespondResIdStrictly(int resId) {
        if (mSkinResources == null) {
            return 0;
        }
        String resName = getResourceName(resId);
        return mSkinResources.getIdentifier(resName, null, null);
    }

    /**
     * Get correspond resources id with skin package. See also {@link #getCorrespondResId(int)}
     *
     * @param resId
     * @return
     */
    public int getCorrespondResId(int resId) {
        if (mSkinResources == null) {
            return 0;
        }
        return mSkinResources.getCorrespondResId(resId);
    }


    @Override
    public View getView(Context context, @LayoutRes int resId) {
        //Take a resource id as the tag key.
        if (LAYOUT_TAG_ID < 1) {
            LAYOUT_TAG_ID = resId;
        }
        View view;
        if (mSkinResources != null) {
            int realId = getCorrespondResId(resId);
            if (realId > 0) {
                view = mSkinResources.getView(context, realId);
                if (view != null) {
                    view.setTag(LAYOUT_TAG_ID, mSkinResources.getPackageName());
                    SkinUtils.showIds(view);
                    return view;
                }
            }
        }
        view = LayoutInflater.from(context).inflate(resId, null);
        view.setTag(LAYOUT_TAG_ID, getPackageName());
        SkinUtils.showIds(view);
        return view;
    }

    @Override
    public String getPackageName() {
        return mContext.getPackageName();
    }
}
