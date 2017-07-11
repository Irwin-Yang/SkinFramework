package com.irwin.skin;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.irwin.skin.resources.BaseResources;


/**
 * Created by Irwin on 2016/5/9.
 */
public abstract class SkinnableFragment extends Fragment implements ISkinnable {
    private static final String TAG = "SkinnableFragment";


    private ViewGroup mContentContainer;
    private View mContentView;
    private boolean mForceUpdateSkin = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerSkinCallback();
    }

    protected void setForceUpdateSkin(boolean force) {
        mForceUpdateSkin = force;
    }

    protected boolean isForceUpdateSkin() {
        return mForceUpdateSkin;
    }


    public View findViewById(@IdRes int id) {
        if (mContentView != null) {
            return mContentView.findViewById(id);
        }
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate a view group as the content container so we can change the content view dynamically.
        mContentContainer = (ViewGroup) inflater.inflate(R.layout.frag_skin_container, container, false);
        setContentView(getLayoutRes());
        onViewCreated(mContentView);
        return mContentContainer;
    }

    protected void onViewCreated(View view) {
        bindViewData();
    }

    public void setContentView(@LayoutRes int resId) {
        View v = getSkinResources().getView(mContentContainer.getContext(), resId);
        setContentView(v);
    }

    public void setContentView(View v) {
        if (mContentContainer != null) {
            mContentContainer.removeAllViews();
            mContentContainer.addView(v);
            mContentView = v;
        }
    }

    protected void registerSkinCallback() {
        SkinManager.getInstance().register(this);
    }

    protected void unregisterSkinCallback() {
        SkinManager.getInstance().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterSkinCallback();
    }

    @Override
    public void onSkinChanged(BaseResources resources) {
        final View v = resources.getView(mContentContainer.getContext(), getLayoutRes());
        if (!isForceUpdateSkin() && SkinUtils.isSameLayout(mContentView, v)) {
            //No need to change layout.
            return;
        }
        Bundle state = saveState();
        setContentView(v);
        bindViewData();
        restoreState(state);
    }


    /**
     * Save state before layout change.If the returned value is not null,it will be deliver in {@link #restoreState(Bundle)}.
     *
     * @return State value or null.
     */
    protected Bundle saveState() {
        return null;
    }

    /**
     * Restore view state after layout has been changed.Only called when {@link #saveState()} returns  non-null value.
     * This is called after {@link #bindViewData()}.
     *
     * @param savedState State value returned by {@link #saveState()}
     */
    protected void restoreState(Bundle savedState) {

    }


    public BaseResources getSkinResources() {
        return SkinManager.getInstance().getResources();
    }


}
