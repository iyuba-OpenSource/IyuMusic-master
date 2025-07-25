package com.iyuba.music.request.account;

import androidx.collection.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.R;
import com.iyuba.music.data.Constant;
import com.iyuba.music.entity.BaseApiEntity;
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

import java.util.Map;

/**
 * Created by 10202 on 2015/11/25.
 */
public class RegistRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.EXCEPT_2G)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        BaseApiEntity baseApiEntity = new BaseApiEntity();
                        baseApiEntity.setData(jsonObject.getInt("result"));
                        baseApiEntity.setMessage(jsonObject.getString("message"));
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
        } else if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            response.onNetError(RuntimeManager.getString(R.string.net_speed_slow));
        } else {
            response.onNetError(RuntimeManager.getString(R.string.no_internet));
        }
    }

    public static String generateUrl(String[] paras) {
        String originalUrl = "http://api." + Constant.IYUBA_COM + "v2/api.iyuba";
        Map<String, Object> para = new ArrayMap<>();
        para.put("protocol", 10002);
        para.put("platform", "android");
        para.put("username", paras[0]);
        para.put("password", MD5.getMD5ofStr(paras[1]));
        para.put("email", paras[2]);
        para.put("appid", Constant.APPID);
        para.put("format", "json");
        para.put("sign", MD5.getMD5ofStr("10002" + paras[0]
                + MD5.getMD5ofStr(paras[1]) + paras[2] + "iyubaV2"));
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
