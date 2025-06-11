package com.iyuba.music.request.merequest;

import androidx.collection.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.iyuba.music.R;
import com.iyuba.music.data.Constant;
import com.iyuba.music.entity.user.UserIpAddress;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyJsonRequest;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by carl shen on 2020/10/29.
 */
public class GetIpAddressRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        String resultCode = jsonObject.getString("result");
                        if ("200".equals(resultCode)) {
                            response.response(new Gson().fromJson(jsonObject.toString(), UserIpAddress.class));
                            return;
                        }
                    } catch (JSONException e) {
                        if (e != null) {
                            e.printStackTrace();
                        }
                    }
                    response.onServerError(RuntimeManager.getString(R.string.data_error));
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

    public static String generateUrl(String uid) {
        String originalUrl = "http://apps.iyuba.cn/minutes/doCheckIP.jsp";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("uid", uid);
        para.put("appid", Constant.APP_ID);
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
