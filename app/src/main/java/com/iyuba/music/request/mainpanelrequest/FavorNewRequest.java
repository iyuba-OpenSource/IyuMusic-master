package com.iyuba.music.request.mainpanelrequest;

/**
 * Created by 10202 on 2016/3/10.
 */

import androidx.collection.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyJsonRequest;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 10202 on 2015/10/8.
 */
public class FavorNewRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    BaseListEntity baseListEntity = new BaseListEntity();
                    String songids = "";
                    try {

                        JSONArray array = new JSONArray(jsonObject.getString("data"));
                        if (array.length() > 0) {

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject json = array.optJSONObject(i);
                                if ("song".equals(json.optString("Type"))) {


                                    if (songids.equals("")) {
                                        songids = json.optString("Id");
                                    } else {
                                        songids = songids + "," + json.optString("Id");
                                    }
                                }

                            }
                        }

                        baseListEntity.setData(songids);
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

    public static String generateUrl(String userid) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String date = sdf.format(new Date());
        String originalUrl = "http://cms." + ConstantManager.IYUBA_CN + "dataapi/jsp/getCollect.jsp";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("userId", userid);
        para.put("appid", ConstantManager.appId);
        para.put("topic", "all");
        para.put("sentenceFlg", 0);
        para.put("format", "json");
        para.put("sign", MD5.getMD5ofStr(userid + "all" + ConstantManager.appId + date));

        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
