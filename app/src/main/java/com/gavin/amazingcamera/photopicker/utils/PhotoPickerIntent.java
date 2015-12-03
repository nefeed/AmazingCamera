package com.gavin.amazingcamera.photopicker.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.gavin.amazingcamera.photopicker.PhotoPickerActivity;


/**
 * Author: Gavin
 * E-mail: gavin.zhang@healthbok.com
 * Date:  2015/12/3 0003
 */
public class PhotoPickerIntent extends Intent {

    private PhotoPickerIntent() {
    }

    private PhotoPickerIntent(Intent o) {
        super(o);
    }

    private PhotoPickerIntent(String action) {
        super(action);
    }

    private PhotoPickerIntent(String action, Uri uri) {
        super(action, uri);
    }

    private PhotoPickerIntent(Context packageContext, Class<?> cls) {
        super(packageContext, cls);
    }

    public PhotoPickerIntent(Context packageContext) {
        super(packageContext, PhotoPickerActivity.class);
    }

    public void setPhotoCount(int photoCount) {
        this.putExtra(PhotoPickerActivity.EXTRA_MAX_COUNT, photoCount);
    }

    public void setShowCamera(boolean showCamera) {
        this.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, showCamera);
    }

    public void setShowGif(boolean showGif) {
        this.putExtra(PhotoPickerActivity.EXTRA_SHOW_GIF, showGif);
    }

}