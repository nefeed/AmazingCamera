package com.gavin.amazingcamera.photopicker.event;


import com.gavin.amazingcamera.photopicker.entity.Photo;

/**
 * Author: Gavin
 * E-mail: gavin.zhang@healthbok.com
 * Date:  2015/12/3 0003
 */
public interface OnItemCheckListener {

    /***
     *
     * @param position 所选图片的位置
     * @param path     所选的图片
     * @param isCheck   当前状态
     * @param selectedItemCount  已选数量
     * @return enable check
     */
    boolean OnItemCheck(int position, Photo path, boolean isCheck, int selectedItemCount);

}
