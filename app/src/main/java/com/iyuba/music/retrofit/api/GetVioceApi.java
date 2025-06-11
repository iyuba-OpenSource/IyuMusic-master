package com.iyuba.music.retrofit.api;


import com.iyuba.music.data.Constant;
import com.iyuba.music.data.model.VoicesResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by carl shen on 2021/4/12
 * New English News, new study experience.
 */

public interface GetVioceApi {

    String GETVIOCEURL = "http://app." + Constant.IYUBA_CN;
    String FORMAT = "json";
    @GET("getShareInfo.jsp?")
    Call<VoicesResult> getShareInfo(@Query("uid") String uid, @Query("appId") String appId,
                                    @Query("pageNum") int pageNum, @Query("pageCount") int pageCount);
    @GET("getShareInfoShow.jsp?")
    Call<VoicesResult> getCalendar(@Query("uid") String uid, @Query("appId") String appId, @Query("time") String time);

}
