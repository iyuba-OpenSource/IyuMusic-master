package com.iyuba.music.request.merequest;

import androidx.collection.ArrayMap;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.R;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyJsonRequest;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONObject;

/**
 * Created by 10202 on 2015/9/30.
 */
public class SignBeanRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {

        Log.e("SignBeanRequest",url);
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {

            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    Log.e("获取数据","huoqush");
                    response.response(jsonObject);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("获取数据","huoqush1");
                    response.onServerError(VolleyErrorHelper.getMessage(error));
                }
            });
            MyVolley.getInstance().addToRequestQueue(request);
        } else {
            response.onNetError(RuntimeManager.getString(R.string.no_internet));
        }
    }

    public static String generateUrl(String time, String userID, String appid) {
        String originalUrl = "http:api." + ConstantManager.IYUBA_CN + "credits/updateScore.jsp?srid=81&mobile=1&flag=";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("srid", 81);
        para.put("mobile", 1);
        para.put("flag", time);
        para.put("uid", userID);
        para.put("appid", appid);
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
