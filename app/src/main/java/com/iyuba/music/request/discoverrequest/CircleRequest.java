package com.iyuba.music.request.discoverrequest;

import androidx.collection.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iyuba.music.R;
import com.iyuba.music.data.Constant;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.doings.Circle;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyJsonRequest;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by 10202 on 2015/9/30.
 */
public class CircleRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        String resultCode = jsonObject.getString("result");
                        BaseListEntity baseListEntity = new BaseListEntity();
                        if ("391".equals(resultCode)) {
                            baseListEntity.setTotalCount(jsonObject.getInt("cnt"));
                            baseListEntity.setIsLastPage(false);
                            Type listType = new TypeToken<ArrayList<Circle>>() {
                            }.getType();
                            ArrayList<Circle> list = new Gson().fromJson(jsonObject.getString("data"), listType);
                            baseListEntity.setData(list);
                        } else {
                            baseListEntity.setIsLastPage(true);
                        }
                        response.response(baseListEntity);
                    } catch (JSONException e) {
                        response.onServerError(RuntimeManager.getString(R.string.data_error));
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

    public static String generateUrl(String uid, int page) {
        String originalUrl = "http://api." + Constant.IYUBA_COM + "v2/api.iyuba";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 31001);
        para.put("uid", uid);
        para.put("find", 2);
        para.put("appid", ConstantManager.appId);
        para.put("feeds", "blog,doing,album");
        para.put("pageNumber", page);
        para.put("pageCounts", 20);
        para.put("sign", MD5.getMD5ofStr("31001" + uid + "iyubaV2"));
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
