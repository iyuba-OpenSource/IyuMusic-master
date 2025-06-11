package com.iyuba.music.data.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.iyuba.module.toolbox.SingleParser;

import io.reactivex.Single;

/**
 * Created by Administrator on 2017/1/18/018.
 */

public class SendDubbingResponse implements SingleParser<SendDubbingResponse> {
    @SerializedName("AddScore")
    public Integer addScore;
    @SerializedName("FilePath")
    public String filePath;
    @SerializedName("Message")
    public String message;
    @SerializedName("ResultCode")
    public String resultCode;
    @SerializedName("ShuoShuoId")
    public int shuoshuoId;

    @Override
    public Single<SendDubbingResponse> parse() {
        // 背景音地址拼接错误，有402
        if (!TextUtils.isEmpty(resultCode) && resultCode.equals("200")) {
            return Single.just(this);
        } else {
            return Single.error(new Throwable(message));
        }
    }
}
