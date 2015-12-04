package com.gavin.amazingcamera.base.view;

import android.util.Log;
import android.view.View;

/**
 * Author: Gavin
 * E-mail: gavin.zhang@healthbok.com
 * Date:  2015/12/4 0004
 */
public abstract class OnNoMultiClickListener implements View.OnClickListener {

    private long prevMillis;
    private long duration = 500;
    private String tag = "multi_click";

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        long thisMillis = System.currentTimeMillis();
        if (thisMillis - prevMillis >= duration) {
            Log.d(tag, hashCode() + "$valid click interval is :" + (thisMillis - prevMillis));
            prevMillis = thisMillis;
            onNoMultiClick(v);
        } else {
            Log.d(tag, hashCode() + "$ignore multi click event.");
        }

    }

    public abstract void onNoMultiClick(View view);
}
