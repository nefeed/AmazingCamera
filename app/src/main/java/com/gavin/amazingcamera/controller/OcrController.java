package com.gavin.amazingcamera.controller;

import android.util.Log;

import com.gavin.amazingcamera.eventbus.OcrEvent;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.greenrobot.event.EventBus;

/**
 * Author: Gavin
 * E-mail: gavin.zhang@healthbok.com
 * Date:  2015/12/4 0004
 */
public class OcrController {

    public static final String TAG = OcrController.class.getSimpleName();

    public void getIDCardInfo(String bitmap64) {
        Log.d(TAG, "+ getIDCardInfo()");

        OcrThread thread = new OcrThread(bitmap64);
        thread.start();

        Log.d(TAG, "- getIDCardInfo()");
    }

    public class OcrThread extends Thread {

        private String bitmap64;

        public OcrThread(String bitmap64) {
            super();
            this.bitmap64 = bitmap64;
        }

        @Override
        public void run() {
            super.run();
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("text/plain");
            // cmd 正面识别：'idcard_front', 背面识别：'idcard_back'
            // imgurl
            JSONObject json = new JSONObject();
            try {
                json.put("cmd", "idcard_front");
                json.put("pictype", "jpg");
                json.put("pic", bitmap64);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(mediaType, json.toString());
            Request request = new Request.Builder()
                    .url("http://a.apix.cn/apixlab/idcardrecog/idcardimage")
                    .post(body)
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .addHeader("apix-key", "1c62211d344a453a720437bad34ad9ea")
                    .build();

            try {
                Response response = client.newCall(request).execute();
                Log.d(TAG, "得到的返回参数response为：" + response.body());

                // 将事件通过EventBus发送出去
                OcrEvent event = new OcrEvent(response.code()
                        , response.body().toString());
                EventBus.getDefault().post(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
