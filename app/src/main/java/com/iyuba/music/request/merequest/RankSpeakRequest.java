package com.iyuba.music.request.merequest;

import androidx.collection.ArrayMap;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.module.toolbox.GsonUtils;
import com.iyuba.music.R;
import com.iyuba.music.entity.RankBean;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by 10202 on 2015/9/30.
 */
public class RankSpeakRequest {




    public String myImgSrc = "";
    public String myId = "";
    public String myRanking = "";
    public String myName = "";
    public String result = "";
    public String message = "";

    public String myScores = "";//总分
    public String myCount= "";//总文章数

    public List<RankBean> rankBeans = new ArrayList<RankBean>();

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private static String date = sdf.format(new Date());


    public void exeRequest(final String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {


                    MyJsonRequest request = new MyJsonRequest(
                            url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonRoot) {
                            Log.e("GetRank-口语排行榜", "===================="+jsonRoot);
                            try {
                                message = jsonRoot.getString("message");

                                if (message.equals("Success")) {

                                    if (jsonRoot.has("myimgSrc"))
                                        myImgSrc = jsonRoot.getString("myimgSrc");//---
                                    if (jsonRoot.has("myid")) //-----
                                        myId = jsonRoot.getString("myid");
                                    if (jsonRoot.has("myranking"))
                                        myRanking = jsonRoot.getString("myranking"); //---
                                    if (jsonRoot.has("result"))
                                        result = jsonRoot.getString("result");//---
                                    if (jsonRoot.has("myname"))
                                        myName = jsonRoot.getString("myname");

                                    if (jsonRoot.has("myscores"))
                                        myScores = jsonRoot.getString("myscores"); //总分
                                    if (jsonRoot.has("mycount"))
                                        myCount = jsonRoot.getString("mycount"); //总文章数



                                    rankBeans = GsonUtils.toObjectList(jsonRoot.getString("data"), RankBean.class);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                            Log.e("获取数据","huoqush");
                            response.response(RankSpeakRequest.this);

                        }
                    },  new Response.ErrorListener() {
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

    public static String generateUrl(String uid, String type, String start, String total, String topic, String topicid, String shuoshuotype) {
        String sign = MD5.getMD5ofStr(uid + topic + topicid  + start + total + date);
        String originalUrl = "http://daxue." + ConstantManager.IYUBA_CN + "ecollege/getStudyRanking.jsp";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("uid", uid);
        para.put("type", type);
        para.put("start", start);
        para.put("total", total);
        para.put("sign", sign);
        para.put("topic", topic);
        para.put("shuoshuotype", shuoshuotype);
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
