package com.iyuba.music.request.merequest;

import androidx.collection.ArrayMap;

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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 10202 on 2015/9/30.
 */
public class GradeRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        //500 ok
                        response.response(jsonObject.getString("positionByTime") + "@@@" + jsonObject.getString("totalTime"));
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

    public static String generateUrl(String uid) {
        String originalUrl = "http://daxue." + ConstantManager.IYUBA_CN + "ecollege/getPaiming.jsp";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("format", "json");
        para.put("uid", uid);
        para.put("appName", ConstantManager.appEnglishName);
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
