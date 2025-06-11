package com.iyuba.assessment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by carl shen on 2021/1/28
 * New English Music, new study experience.
 */
public class EvaluateRequset {

    public static void post(String actionUrl, Map<String, String> params, String filePath, final Handler handler) throws Exception {

        //POST参数构造MultipartBody.Builder，表单提交
        final OkHttpClient okHttpClient = new OkHttpClient().newBuilder().
                connectTimeout(7, TimeUnit.SECONDS).
                readTimeout(7, TimeUnit.SECONDS).
                writeTimeout(7, TimeUnit.SECONDS)
                .build();
        //一：文本类的
        MultipartBody.Builder urlBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (params != null) {
            for (String key : params.keySet()) {
                if (params.get(key) != null) {
                    urlBuilder.addFormDataPart(key, params.get(key));
                }
            }
        }
        //二种：文件请求体
        MediaType type = MediaType.parse("application/octet-stream");//"text/xml;charset=utf-8"
        File file1 = new File(filePath);
        RequestBody fileBody = RequestBody.create(type, file1);
        urlBuilder.addPart(Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"" + filePath + "\""), fileBody);

        // 构造Request->call->执行
        final Request request = new Request.Builder().headers(new Headers.Builder().build())//extraHeaders 是用户添加头
                .url(actionUrl).post(urlBuilder.build())//参数放在body体里
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e != null) {
                    Log.e("sendRank", "onFailure " + e.getMessage());
                } else {
                    Log.e("sendRank", "onFailure!!");
                }
                handler.sendEmptyMessage(7);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response == null) {
                    Log.e("sendRank", "response null. " );
                    handler.sendEmptyMessage(7);
                    return;
                }
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        String result = jsonObject.optString("result");
                        Log.e("sendRank", jsonObject.toString());
                        if ("0".equals(result)) {
                            handler.sendEmptyMessage(7);
                        } else {
                            JSONObject data = jsonObject.getJSONObject("data");
                            if (data != null) {
                                Message message = new Message();
                                message.what = 6;
                                message.obj = data.toString();
                                handler.sendMessage(message);
                            } else {
                                handler.sendEmptyMessage(7);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(7);
                    }
                } else {
                    Log.e("sendRank", "onResponse = " + response.toString());
                    handler.sendEmptyMessage(7);
                }

            }
        });
    }

}
