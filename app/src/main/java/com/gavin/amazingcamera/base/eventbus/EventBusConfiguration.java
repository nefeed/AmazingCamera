package com.gavin.amazingcamera.base.eventbus;

/**
 * Author: Gavin
 * E-mail: gavin.zhang@healthbok.com
 * Date:  2015/12/4 0004
 */
public enum EventBusConfiguration {
    KEEP_ALIVE_NONE,
    /**
     * onStart register()
     * onStop unregister()
     */
    KEEP_ALIVE_ONSTART_PAIR,
    /**
     * onCreate register()
     * onDestory unregister()
     */
    KEEP_ALIVE_ONCREATE_PAIR
}
