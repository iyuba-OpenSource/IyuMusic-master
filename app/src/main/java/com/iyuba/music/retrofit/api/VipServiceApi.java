package com.iyuba.music.retrofit.api;

import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.retrofit.result.NotifyAliPayResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by carl shen on 2021/1/15
 */
public interface VipServiceApi {
    String BASEURL = "http://vip."+ ConstantManager.IYUBA_CN;


    @POST("notifyAliNew.jsp?")
    Call<NotifyAliPayResponse> NotifyAliPay(@QueryMap Map<String, String> map);

}
