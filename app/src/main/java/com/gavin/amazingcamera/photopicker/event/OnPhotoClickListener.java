package com.gavin.amazingcamera.photopicker.event;

import android.view.View;

/**
 * Author: Gavin
 * E-mail: gavin.zhang@healthbok.com
 * Date:  2015/12/3 0003
 */
public interface OnPhotoClickListener {

    void onClick(View v, int position, boolean showCamera);
}
