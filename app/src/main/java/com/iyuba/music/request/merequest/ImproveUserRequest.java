package com.iyuba.music.request.merequest;

import androidx.collection.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.R;
import com.iyuba.music.data.Constant;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyJsonRequest;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by carl shen on 2020/10/29.
 */
public class ImproveUserRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        String resultCode = jsonObject.getString("result");
                        if ("200".equals(resultCode)) {
                            response.response(jsonObject);
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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

    public static String generateUrl(String uid, String gender, String age, String province, String city, String title) {
        String originalUrl = "http://api." + Constant.IYUBA_COM + "v2/api.iyuba";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("format", "json");
        para.put("protocol", 99010);
        para.put("platform", "android");
        para.put("userid", uid);
        if ("男生".equalsIgnoreCase(gender)) {
            para.put("gender", 1);
        } else {
            para.put("gender", 0);
        }
        para.put("age", age);
        para.put("appid", Constant.APP_ID);
        para.put("resideprovince", ParameterUrl.encode(ParameterUrl.encode(province)));
        para.put("residecity", ParameterUrl.encode(ParameterUrl.encode(city)));
        para.put("occupation", ParameterUrl.encode(ParameterUrl.encode(title)));
        para.put("sign", MD5.getMD5ofStr("iyubaV2" + uid));
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
