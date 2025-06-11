package com.iyuba.music.request.newsrequest;

import androidx.collection.ArrayMap;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
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
 * Created by 10202 on 2015/10/8.
 */
public class StudyPDFRequest {
    public static void exeRequest(final String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {


                        Log.e("下载pdf", url + jsonObject.toString());
                        BaseApiEntity apiEntity = new BaseApiEntity();
                        apiEntity.setData(jsonObject);
                        response.response(apiEntity);

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

    /**
     * 参数isenglish=1 下载英文
     * 参数isenglish=2 下载中文
     * 参数isenglish=（除了1，2）下载中英文
     */
    public static String generateUrl(String id, int isenglish) {
        String originalUrl = "http://apps." + ConstantManager.IYUBA_CN + "afterclass/getSongpdfFile_new.jsp";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("type", "song");
        para.put("songid", id);
        para.put("isenglish", isenglish);
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
