package com.iyuba.music.retrofit.api;


import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.retrofit.result.DownLoadJFResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by iyuba on 2017/11/30.
 */

public interface NewDeductPointsForDownloadApi {
    String SRID = "40";
    String MOBILE = "1";
    String NEWJIFEN_URL = "http://api." + ConstantManager.IYUBA_CN + "credits/" ;
    //        "updateScore.jsp?srid=40&mobile=1&flag=MjAxNzExMzAxMDU0NTA=&uid=4586981&appid=148&idindex=200560";
    @GET("updateScore.jsp")
    Call<DownLoadJFResult> DuctPointsForDownload(@Query("srid") String srid,
                                                 @Query("mobile") String mobile,
                                                 @Query("flag") String flag,
                                                 @Query("uid") String uid,
                                                 @Query("appid") String appid,
                                                 @Query("idindex") String idindex);

}
