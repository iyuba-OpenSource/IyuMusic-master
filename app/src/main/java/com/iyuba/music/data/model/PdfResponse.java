package com.iyuba.music.data.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.iyuba.module.toolbox.SingleParser;

import io.reactivex.Single;

public class PdfResponse implements SingleParser<PdfResponse> {
    @SerializedName("exists")
    public String exists;
    @SerializedName("path")
    public String path;

    @Override
    public Single<PdfResponse> parse() {
        if (!TextUtils.isEmpty(exists)&&exists.equals("true")) {
            return Single.just(this);
        }else {
            return Single.error(new Throwable("网络请求失败"));
        }
    }
}