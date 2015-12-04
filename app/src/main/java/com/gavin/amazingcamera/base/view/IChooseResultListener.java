package com.gavin.amazingcamera.base.view;

import android.content.Intent;

/**
 * Author: Gavin
 * E-mail: gavin.zhang@healthbok.com
 * Date:  2015/12/4 0004
 */
public interface IChooseResultListener {

    Intent onResultSet();

    int onResultCodeSet();
}
