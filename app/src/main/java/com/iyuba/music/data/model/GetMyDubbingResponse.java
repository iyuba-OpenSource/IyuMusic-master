package com.iyuba.music.data.model;

import com.google.gson.annotations.SerializedName;
import com.iyuba.module.toolbox.SingleParser;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by Administrator on 2017/1/17/017.
 */

public  class GetMyDubbingResponse implements SingleParser<GetMyDubbingResponse> {
    @SerializedName("result")
    public  boolean result;
    @SerializedName("data")
    public List<Ranking> data;

    @Override
    public Single<GetMyDubbingResponse> parse() {
        if (result) {
            return Single.just(this);
        }else {
            return Single.error(new Throwable("请求失败了~"));
        }
    }
}
