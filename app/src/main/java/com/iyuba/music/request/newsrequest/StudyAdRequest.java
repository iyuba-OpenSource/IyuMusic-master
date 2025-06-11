package com.iyuba.music.request.newsrequest;

import androidx.collection.ArrayMap;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.iyuba.music.R;
import com.iyuba.music.data.Constant;
import com.iyuba.music.entity.ad.AdEntity;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyStringRequest;
import com.iyuba.music.volley.MyVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by 10202 on 2015/10/8.
 */
public class StudyAdRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {

        Log.e("StudyAdRequest", url);
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyStringRequest request = new MyStringRequest(StringRequest.Method.GET,
                    url, new Response.Listener<String>() {
                @Override
                public void onResponse(String data) {

                    try {
                        data = data.trim();
                        JSONObject jsonObject = new JSONArray(data).getJSONObject(0);
                        String result = jsonObject.getString("result");

                        if (result.equals("1")) {

                            String type = jsonObject.getJSONObject("data").getString("type");
                            String id = jsonObject.getJSONObject("data").getString("id");
                            String startuppic = jsonObject.getJSONObject("data").getString("startuppic");
                            String startuppic_Url = jsonObject.getJSONObject("data").getString("startuppic_Url");
                            String adId = jsonObject.getJSONObject("data").getString("adId");

                            AdEntity adEntity = new AdEntity();
                            adEntity.setType(type);
                            adEntity.setId(id);
                            adEntity.setTitle(adId);
                            adEntity.setLoadUrl(startuppic_Url);
                            adEntity.setPicUrl(startuppic);
                            response.response(adEntity);
                        }
                    } catch (JSONException e) {
                        AdEntity adEntity = new AdEntity();
                        adEntity.setType("youdao");
                        response.response(adEntity);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    AdEntity adEntity = new AdEntity();
                    adEntity.setType("youdao");
                    response.response(adEntity);
                }
            });
            MyVolley.getInstance().addToRequestQueue(request);
        } else {
            response.onNetError(RuntimeManager.getString(R.string.no_internet));
        }
    }

    public static String generateUrl() {

        String originalUrl = "http://dev." + ConstantManager.IYUBA_CN + "getAdEntryAll.jsp";
        Map<String, Object> para = new ArrayMap<>();
        para.put("uid", AccountManager.getInstance().getUserId());
        para.put("appId", Constant.APPID);
        para.put("flag", 4);
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
