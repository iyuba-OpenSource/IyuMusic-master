package com.iyuba.music.data.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.iyuba.module.toolbox.SingleParser;

import java.util.List;

import io.reactivex.Single;

public class ChildWordResponse implements SingleParser<List<VoaWord2>> {
    @SerializedName("result")
    public String result;
    @SerializedName("data")
    public List<VoaWord2> list;

    @Override
    public Single<List<VoaWord2>> parse() {
        if (!TextUtils.isEmpty(result)&&result.equals("200")) {
            return Single.just(list);
        }else {
            return Single.error(new Throwable("网络请求失败"));
        }
    }
}