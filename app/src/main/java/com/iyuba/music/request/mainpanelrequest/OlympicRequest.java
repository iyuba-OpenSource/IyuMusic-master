package com.iyuba.music.request.mainpanelrequest;

import androidx.collection.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyJsonRequest;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 10202 on 2017/10/30.
 */
public class OlympicRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    BaseListEntity baseListEntity = new BaseListEntity();
                    try {
                        baseListEntity.setTotalCount(jsonObject.getInt("length"));
                        if (jsonObject.getInt("result") != 200) {
                            baseListEntity.setIsLastPage(true);
                        } else {
                            baseListEntity.setIsLastPage(false);
                            ArrayList<Article> list = new ArrayList<>();
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            JSONObject jsonObjectData;
                            Article article;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                article = new Article();
                                jsonObjectData = jsonArray.getJSONObject(i);
                                article.setId(jsonObjectData.getInt("titleid"));
                                article.setTitle_cn(jsonObjectData.getString("title"));
                                article.setTitle(jsonObjectData.getString("title"));
                                article.setContent(jsonObjectData.getString("introduce"));
                                article.setMusicUrl(jsonObjectData.getString("audio"));
                                article.setPicUrl(jsonObjectData.getString("pic"));
                                article.setTime(jsonObjectData.getString("createtime"));
                                article.setReadCount(jsonObjectData.getString("viewcount"));
//                                article.setCategory(jsonObjectData.getString("Category"));
                                article.setApp(ConstantManager.appId);
                                list.add(article);
                            }
                            baseListEntity.setData(list);
                        }
                        response.response(baseListEntity);
                    } catch (JSONException e) {
                        e.printStackTrace();
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

    public static String generateUrl(int page, int icid) {
        String originalUrl = "http://api." + ConstantManager.QOMOLAMA_CN + "afterclass/getItemTitle.jsp";
        ArrayMap<String, Object> map = new ArrayMap<>();
//        map.put("type", "Android");
        map.put("icid", icid);
        map.put("pages", page);
        map.put("pageNum", 20);
        return ParameterUrl.setRequestParameter(originalUrl, map);
    }
}
