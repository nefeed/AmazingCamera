package com.gavin.amazingcamera.http;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Author: Gavin
 * E-mail: gavin.zhang@healthbok.com
 * Date:  2015/12/4 0004
 */
public class IDCardPostRequest {
    private String httpUrl, httpArg, regResult;
    private IDCardCommands cmd;
    private String apixKey;
    private JSONObject root;

    public static enum IDCardCommands {
        IDCardPhotoFront, IDCardPhotoBack,
    }

    public IDCardPostRequest(IDCardCommands cmd, String apixKey)
    {
        this.regResult = null;
        this.cmd = cmd;
        this.apixKey = apixKey;
    }

    private String getCmdString()
    {
        if (cmd == IDCardCommands.IDCardPhotoFront) {
            return "idcard_front";
        }

        else if (cmd == IDCardCommands.IDCardPhotoBack) {
            return "idcard_back";
        }
        return "";
    }

    public void initConetent(ByteArrayOutputStream jpgImg, String type) {

        httpUrl = "http://a.apix.cn/apixlab/idcardrecog/idcardimage";
        String str64 = Base64.encodeToString(jpgImg.toByteArray(), Base64.DEFAULT);

        try {
            JSONObject obj = new JSONObject();
            obj.put("cmd", getCmdString());
            obj.put("pictype", type);
            obj.put("pic", str64);
            httpArg = obj.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendRequest() {
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // 填入apix-key到HTTP header
            connection.setRequestProperty("apix-key", apixKey);
            connection.setDoOutput(true);
            connection.getOutputStream().write(httpArg.getBytes("UTF-8"));
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            regResult = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int analyzeIDCardFrontResult(Bundle bundle)
    {
        if(regResult == null)
            return -1;

        int state=-1;

        try
        {
            root = new JSONObject(regResult);
            state = root.getInt("state");
            Log.i("idcard", "state:" + state);
        } catch (JSONException e1) {
            e1.printStackTrace();
            return -1;
        }
        if(state==1)
        {
            try {
                JSONObject data = root.getJSONObject("data");
                bundle.putString("name", data.getString("name"));
                bundle.putString("sex", data.getString("sex"));
                bundle.putString("nation", data.getString("nation"));
                bundle.putString("address", data.getString("address"));

                String number = data.getString("number");
                bundle.putString("number", number);
                bundle.putString("birth", number.substring(6, 14));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (state == 0) {
            try {
                String message = root.getString("message");
                bundle.putString("message", message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return state;
    }

    public int analyzeIDCardBackResult(Bundle bundle)
    {
        if(regResult == null)
            return -1;

        int state=0;

        try
        {
            root = new JSONObject(regResult);
            state = root.getInt("state");
        } catch (JSONException e1) {
            e1.printStackTrace();
            return -1;
        }
        if(state==1)
        {
            try {
                JSONObject data = root.getJSONObject("data");
                String office = data.getString("office");
                String date1 = data.getString("date1");
                String date2 = data.getString("date2");

                bundle.putString("office", office);
                bundle.putString("date", date1+"-"+date2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (state == 0) {
            try {
                String message = root.getString("message");
                bundle.putString("message", message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return state;
    }
}
