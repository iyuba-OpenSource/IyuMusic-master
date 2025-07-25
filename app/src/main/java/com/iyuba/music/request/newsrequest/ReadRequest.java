package com.iyuba.music.request.newsrequest;

import androidx.collection.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.comment.Comment;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.util.InterNetUtil;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyJsonRequest;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by 10202 on 2016/3/21.
 */
public class ReadRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
        if (InterNetUtil.isNetworkConnected(MusicApplication.getApp())) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        String resultCode = jsonObject.getString("ResultCode");
                        if ("511".equals(resultCode)) {
                            BaseListEntity baseListEntity = new BaseListEntity();
                            Type listType = new TypeToken<ArrayList<Comment>>() {
                            }.getType();
                            baseListEntity.setTotalCount(jsonObject.getInt("Counts"));
                            baseListEntity.setCurPage(jsonObject.getInt("PageNumber"));
                            baseListEntity.setTotalPage(jsonObject.getInt("TotalPage"));
                            if (baseListEntity.getTotalPage() == baseListEntity.getCurPage()) {
                                baseListEntity.setIsLastPage(true);
                            } else {
                                baseListEntity.setIsLastPage(false);
                            }
                            ArrayList<Comment> list = new Gson().fromJson(jsonObject.getString("data"), listType);
                            baseListEntity.setData(list);
                            response.response(baseListEntity);
                        } else {
                            BaseListEntity baseListEntity = new BaseListEntity();
                            baseListEntity.setTotalCount(0);
                            response.response(baseListEntity);
                        }
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

    public static String generateUrl(int id, int page, String sort) {
        String originalUrl = "http://daxue." + ConstantManager.IYUBA_CN + "appApi/UnicomApi";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 60001);
        para.put("platform", "android");
        para.put("format", "json");
        para.put("voaid", id);
        para.put("pageNumber", page);
        para.put("pageCounts", 20);
        para.put("appName", "music");
        para.put("sort", sort);
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
