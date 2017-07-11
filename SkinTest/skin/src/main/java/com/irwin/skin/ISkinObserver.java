package com.irwin.skin;

import com.irwin.skin.resources.BaseResources;

/**
 * Created by Irwin on 2016/5/4.
 * An interface used to observe skin change dynamically.
 */
public interface ISkinObserver {

    void onSkinChanged(BaseResources resources);
}
