package com.irwin.skin;

/**
 * Created by Irwin on 2016/5/9.
 * Interface defines convenient rules for  UI elements(Activity,Fragment.etc) which can change skin to implement.
 * Generally,When use a fragment, You should detach it from screen from FragmentManager.
 * <br/></><Note>Only used when you want to update ui when skin changed dynamically</Note>
 */
public interface ISkinnable extends ISkinObserver {

    /**
     * Init view logic and bind data to view after view created.Called after layout has changed.
     */
    public void bindViewData();

    /**
     * Get layout resources id of this ui element.
     *
     * @return
     */
    public int getLayoutRes();
}
