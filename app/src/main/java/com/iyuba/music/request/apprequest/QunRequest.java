package com.iyuba.music.request.apprequest;

import androidx.collection.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyJsonRequest;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 10202 on 2015/9/30.
 */
public class QunRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    BaseApiEntity baseApiEntity = new BaseApiEntity();
                    try {
                        ConfigManager.getInstance().setQQGroup(jsonObject.optString("QQ"));
                        ConfigManager.getInstance().setQQKey(jsonObject.optString("key"));
                        baseApiEntity.setData(jsonObject.getString("QQ"));
                        baseApiEntity.setValue(jsonObject.getString("key"));
                        baseApiEntity.setState(BaseApiEntity.SUCCESS);
                        response.response(baseApiEntity);
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

    public static String generateFullUrl(String type, String userId, String appId) {
        String originalUrl = "http://m." + ConstantManager.IYUBA_CN + "m_login/getQQGroup.jsp";
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("type", type);
        map.put("userId", userId);
        map.put("appId", appId);
        return ParameterUrl.setRequestParameter(originalUrl, map);
    }
    public static String generateUrl(String type) {
        String originalUrl = "http://m." + ConstantManager.IYUBA_CN + "m_login/getQQGroup.jsp";
        return ParameterUrl.setRequestParameter(originalUrl, "type", type);
    }
}
