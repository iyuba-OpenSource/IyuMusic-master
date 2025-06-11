package com.iyuba.music.data.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.iyuba.module.toolbox.SingleParser;

import io.reactivex.Single;

/**
 * Created by Administrator on 2016/12/5 0005.
 */

public  class ThumbsResponse implements SingleParser<ThumbsResponse> {
    @SerializedName("ResultCode")
    public String resultCode;//001 ???
    @SerializedName("Message")
    public String message;

    @Override
    public Single<ThumbsResponse> parse() {
        if (!TextUtils.isEmpty(message) && message.equals("OK")) {
            return Single.just(this);
        } else {
            return Single.error(new Throwable(message));
        }
    }
}
