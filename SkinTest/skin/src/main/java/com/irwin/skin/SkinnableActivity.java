package com.irwin.skin;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.irwin.skin.resources.BaseResources;


/**
 * Created by Irwin on 2016/5/9.
 * Super Activity who's skin can be changed.
 */
public abstract class SkinnableActivity extends AppCompatActivity implements ISkinnable {

    private View mContentView;
    private int mLayoutId = -1;
    private boolean mUseSkinResource = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerSkinCallback();
    }

    public void setUseSkinResources(boolean use) {
        mUseSkinResource = use;
    }

    protected void registerSkinCallback() {
        SkinManager.getInstance().register(this);
    }

    protected void unregisterSkinCallback() {
        SkinManager.getInstance().unregister(this);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        mLayoutId = layoutResID;
        View contentView = ((BaseResources) getResources()).getView(this, layoutResID);
        setContentView(contentView);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        mContentView = view;
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        mContentView = view;
    }


    @Override
    public int getLayoutRes() {
        if (mLayoutId <= 0) {
            throw new IllegalStateException("Layout id should be provided ether by setContentView(int layoutId) or override getLayoutRes() method");
        }
        return mLayoutId;
    }

    @Override
    public void onSkinChanged(BaseResources resources) {
        View v = resources.getView(this, getLayoutRes());
        if (SkinUtils.isSameLayout(mContentView, v)) {
            //No need to change layout.
            return;
        }
        Bundle state = saveState();
        setContentView(v);
        bindViewData();
        restoreState(state);
    }

    @Override
    public Resources getResources() {
        if (mUseSkinResource) {
            return getSkinResources();
        }
        return super.getResources();
    }

    public BaseResources getSkinResources() {
        return SkinManager.getInstance().getResources();
    }

    /**
     * Save state before layout change.If the returned value is not null,it will be deliver in {@link #restoreState(Bundle)}.
     * </br>You can do something such as remove/detach fragments from FragmentManager.
     *
     * @return State value or null.
     */
    protected Bundle saveState() {
        return null;
    }

    /**
     * Restore view state after layout has been changed with the value {@link #saveState()} returned.
     * This is called after {@link #bindViewData()}.
     *
     * @param savedState State value returned by {@link #saveState()}
     */
    protected void restoreState(Bundle savedState) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unregister(this);
    }
}
