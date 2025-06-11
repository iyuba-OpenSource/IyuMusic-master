package com.iyuba.music.request.newsrequest;

import androidx.collection.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.R;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyStringRequest;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

/**
 * Created by 10202 on 2015/10/8.
 */
public class FavorRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyStringRequest request = new MyStringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String message) {
                    if (message.contains("del")) {
                        response.response("del");
                    } else if (message.contains("insert")) {
                        response.response("insert");
                    } else {
                        response.response("");
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

    public static String generateUrl(String userid, int voaid, String type) {


        String originalUrl = "http://apps." + ConstantManager.IYUBA_CN + "afterclass/updateCollect.jsp";//旧
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("userId", userid);
        para.put("voaId", voaid);
        para.put("type", type);
        return ParameterUrl.setRequestParameter(originalUrl, para);


//        String originalUrl = "http://apps." + ConstantManager.IYUBA_CN + "iyuba/updateCollect.jsp"; //新
//        ArrayMap<String, Object> para = new ArrayMap<>();
//        para.put("groupName", "Iyuba");
//        para.put("sentenceFlg", 0);
//        para.put("appId", ConstantManager.appId);
//        para.put("userId", userid);
//        para.put("type", type);
//        para.put("voaId", voaid);
//        para.put("sentenceId", 0);
//        para.put("topic", "song");
//        return ParameterUrl.setRequestParameter(originalUrl, para);

    }
}
