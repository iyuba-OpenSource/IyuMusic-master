package com.iyuba.music.request.apprequest;

import androidx.collection.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.R;
import com.iyuba.music.data.Constant;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyStringRequest;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

/**
 * Created by 10202 on 2015/11/21.
 */
public class FeedbackRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyStringRequest request = new MyStringRequest(url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String message) {
                            String[] content = message.split(",");
                            response.response(content[0]);
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

    public static String generateUrl(String uid, String content, String contact) {
        String feedbackUrl = "http://apis." + ConstantManager.IYUBA_CN + "v2/api.iyuba?";
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("uid", uid);
        map.put("content", content);
        map.put("email", contact);
        map.put("protocol", "91001");
        map.put("app", Constant.EVAL_TYPE);
        map.put("platform", "android");
        map.put("format", "json");
        return ParameterUrl.setRequestParameter(feedbackUrl, map);
    }
}

