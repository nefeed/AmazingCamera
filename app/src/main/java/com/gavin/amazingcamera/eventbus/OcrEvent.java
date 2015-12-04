package com.gavin.amazingcamera.eventbus;

import com.gavin.amazingcamera.base.eventbus.BaseEvent;

/**
 * Author: Gavin
 * E-mail: gavin.zhang@healthbok.com
 * Date:  2015/12/4 0004
 */
public class OcrEvent extends BaseEvent {

    public OcrEvent(int code) {
        super(code);
    }

    public OcrEvent(int code, String msg) {
        super(code, msg);
    }
}
