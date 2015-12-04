package com.gavin.amazingcamera.http;

import android.os.Bundle;

import com.gavin.amazingcamera.eventbus.OcrEvent;
import com.gavin.amazingcamera.http.IDCardPostRequest.IDCardCommands;
import com.gavin.amazingcamera.util.ImageProcess;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Set;

import de.greenrobot.event.EventBus;

/**
 * Author: Gavin
 * E-mail: gavin.zhang@healthbok.com
 * Date:  2015/12/4 0004
 */
public class IDCardRecog {

    private final int MaxImageSize = 1920*1080;
    private final int MaxIDCardSide = 1280;
    private int quality = 80;

    private String apixKey;

    public IDCardRecog(String apixKey, int quality) {
        this.apixKey = apixKey;
        this.quality = quality;
    }

    public void recogFront(String imgPath) {
        final String path= imgPath;
        final String picType = "jpg";
        final IDCardCommands cmd = IDCardCommands.IDCardPhotoFront;

        Thread mThread = new Thread(new Runnable(){
            @Override
            public void run() {
                ByteArrayOutputStream img_out = ImageProcess.getImageOutJpeg(path, MaxImageSize, MaxIDCardSide, quality);
                IDCardPostRequest pRequest = new IDCardPostRequest(cmd, apixKey);

                pRequest.initConetent(img_out, picType);
                pRequest.sendRequest();

                Bundle bundle = new Bundle();
                int res = pRequest.analyzeIDCardFrontResult(bundle);

                if (res == 1)
                    sendMessage(1, bundle);
                else
                    sendMessage(0, bundle);
            }
        });

        mThread.setPriority(Thread.MAX_PRIORITY);
        mThread.start();
    }

    public void recogBack(String imgPath) {
        final String path= imgPath;
        final String picType = "jpg";
        final IDCardCommands cmd = IDCardCommands.IDCardPhotoBack;

        Thread mThread = new Thread(new Runnable(){
            @Override
            public void run() {
                ByteArrayOutputStream img_out = ImageProcess.getImageOutJpeg(path, MaxImageSize, MaxIDCardSide, quality);
                IDCardPostRequest pRequest = new IDCardPostRequest(cmd, apixKey);

                pRequest.initConetent(img_out, picType);
                pRequest.sendRequest();

                Bundle bundle = new Bundle();
                int res = pRequest.analyzeIDCardBackResult(bundle);

                if (res == 1)
                    sendMessage(2, bundle);
                else
                    sendMessage(0, bundle);
            }
        });

        mThread.setPriority(Thread.MAX_PRIORITY);
        mThread.start();
    }

    private void sendMessage(int result, Bundle data) {

        JSONObject json = new JSONObject();
        Set<String> keys = data.keySet();
        for (String key : keys) {
            try {
                // json.put(key, bundle.get(key)); see edit below
                json.put(key, JSONObject.wrap(data.get(key)));
            } catch(JSONException e) {
                //Handle exception here
            }
        }

        OcrEvent event = new OcrEvent(result, json.toString());

        EventBus.getDefault().post(event);
    }
}
