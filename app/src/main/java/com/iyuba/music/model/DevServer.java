package com.iyuba.music.model;


import com.iyuba.music.model.bean.AdEntryBean;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DevServer {

    /**
     * 获取广告
     *
     * @param appId
     * @param flag  2 广告顺序  5自家广告内容
     * @param uid
     * @return
     */
    @GET("/getAdEntryAll.jsp")
    Observable<List<AdEntryBean>> getAdEntryAll(@Query("appId") String appId, @Query("flag") int flag, @Query("uid") String uid);
}
