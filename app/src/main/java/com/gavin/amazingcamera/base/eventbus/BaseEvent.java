package com.gavin.amazingcamera.base.eventbus;

/**
 * Author: Gavin
 * E-mail: gavin.zhang@healthbok.com
 * Date:  2015/12/4 0004
 */
public abstract class BaseEvent {

    public int code;
    public String msg;

    public BaseEvent(int code) {
        this.code = code;
    }

    public BaseEvent(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
