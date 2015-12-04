package com.gavin.amazingcamera.base.eventbus;

import com.gavin.amazingcamera.base.view.IActionBarObservable;

/**
 * Author: Gavin
 * E-mail: gavin.zhang@healthbok.com
 * Date:  2015/12/4 0004
 */
public class ActionBarUpdateEvent {
    public IActionBarObservable observable;

    public ActionBarUpdateEvent(IActionBarObservable observable) {
        this.observable = observable;
    }
}
