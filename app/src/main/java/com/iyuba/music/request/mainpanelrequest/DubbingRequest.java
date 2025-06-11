package com.iyuba.music.request.mainpanelrequest;

import androidx.collection.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.MusicVoa;
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
 * Created by carl shen on 2020/9/18.
 */
public class DubbingRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    BaseListEntity baseListEntity = new BaseListEntity();
                    try {
                        baseListEntity.setTotalCount(jsonObject.optInt("total"));
                        if (jsonObject.has("result")) {
                            baseListEntity.setIsLastPage(true);
                        } else {
                            baseListEntity.setIsLastPage(false);
                            ArrayList<MusicVoa> list = new ArrayList<>();
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            JSONObject jsonObjectData;
                            MusicVoa article;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                article = new MusicVoa();
                                jsonObjectData = jsonArray.getJSONObject(i);
                                article.introDesc = jsonObjectData.optString("IntroDesc");
                                article.createTime = jsonObjectData.optString("CreatTime");
                                article.category = jsonObjectData.optInt("Category");
                                article.keyword = jsonObjectData.optString("Keyword");
                                article.title = jsonObjectData.optString("Title");
                                article.texts = jsonObjectData.optString("Texts");
                                article.video = jsonObjectData.optString("Video");
                                article.sound = jsonObjectData.optString("Sound");
                                article.pic = jsonObjectData.optString("Pic");
                                article.voaId = jsonObjectData.optInt("VoaId");
                                article.pageTitle = jsonObjectData.optString("Pagetitle");
                                article.url = jsonObjectData.optString("Url");
                                article.descCn = jsonObjectData.optString("DescCn");
                                article.titleCn = jsonObjectData.optString("Title_cn");
                                article.publishTime = jsonObjectData.optString("PublishTime");
                                article.hotFlag = jsonObjectData.optInt("HotFlg");
                                article.readCount = jsonObjectData.optInt("ReadCount");
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

    public static String generateUrl(int page) {
        String originalUrl = "http://apps." + ConstantManager.IYUBA_CN + "iyuba/titleApi.jsp";
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("format", "json");
        map.put("type", "Android");
        map.put("parentID", -1);
        map.put("category", "music");
        map.put("maxid", 0);
        map.put("pages", page);
        map.put("pageNum", 20);
        return ParameterUrl.setRequestParameter(originalUrl, map);
    }
}
