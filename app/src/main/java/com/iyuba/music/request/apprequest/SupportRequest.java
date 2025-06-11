package com.iyuba.music.request.apprequest;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iyuba.music.R;
import com.iyuba.music.entity.SupportQQ;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyJsonRequest;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SupportRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    Log.e("支持qq", jsonObject.toString());
//                    BaseListEntity baseListEntity = new BaseListEntity();
                    try {
                        if (jsonObject.getInt("result") == 200) {
                            Type listType = new TypeToken<ArrayList<SupportQQ>>() {
                            }.getType();
                            ArrayList<SupportQQ> list = new Gson().fromJson(jsonObject.getString("data"), listType);
                            response.response(list);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        response.onServerError(VolleyErrorHelper.getMessage(e));
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    response.onServerError(VolleyErrorHelper.getMessage(error));
                }
            });
            MyVolley.getInstance().addToRequestQueue(request);
        } else {
            response.onNetError(RuntimeManager.getString(R.string.no_internet));
        }
    }

    public static String generateUrl(String type) {
        String originalUrl = "http://iuserspeech.iyuba.cn:9001/japanapi/getJpQQ.jsp";
        return ParameterUrl.setRequestParameter(originalUrl, "appid", type);
    }
}
