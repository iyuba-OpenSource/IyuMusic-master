package com.iyuba.music.request.merequest;

import androidx.collection.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.data.Constant;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyJsonRequest;
import com.iyuba.music.volley.MyVolley;

import org.json.JSONObject;

/**
 * Created by 10202 on 2015/9/30.
 */
public class SetMessageReadRequest {
    public static void exeRequest(String url) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    //621 ok
//                        String result = jsonObject.getString("result");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            MyVolley.getInstance().addToRequestQueue(request);
        }
    }

    public static String generateUrl(String uid, String plid) {
        String originalUrl = "http://api." + Constant.IYUBA_COM + "v2/api.iyuba";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 60003);
        para.put("uid", uid);
        para.put("plid", plid);
        para.put("sign", MD5.getMD5ofStr(60003 + uid + plid + "iyubaV2"));
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
