package com.iyuba.music.request.newsrequest;

import androidx.collection.ArrayMap;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
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
 * Created by 10202 on 2015/10/8.
 */
public class StudyWXRequest {
    public static void exeRequest(final String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {

                        Log.e("跳转小程序",url);
                        BaseApiEntity apiEntity = new BaseApiEntity();
                        if (jsonObject.getInt("result") == 1) {


                            apiEntity.setState(BaseApiEntity.SUCCESS);
                            apiEntity.setData(jsonObject.optInt("totalTime", 0));


                        } else {
                            apiEntity.setState(BaseApiEntity.FAIL);
                        }
                        response.response(apiEntity);
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

    public static String generateUrl(String id) {
        String originalUrl = "http://dev." + ConstantManager.IYUBA_CN + "ad.jsp";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("adId", id);
        para.put("uid", AccountManager.getInstance().getUserId());
        para.put("appId", ConstantManager.appId);
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
