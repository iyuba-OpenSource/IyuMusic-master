package com.iyuba.music.data.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.iyuba.module.toolbox.SingleParser;

import io.reactivex.Single;


public class IntegralBean implements SingleParser<IntegralBean> {

    /**
     * result : 200
     * addcredit : -20
     * totalcredit : 1465
     */
    @SerializedName("result")
    public String result;
    @SerializedName("addcredit")
    public String addCredit;
    @SerializedName("totalcredit")
    public String totalCredit;

    @Override
    public Single<IntegralBean> parse() {
        if (!TextUtils.isEmpty(result)&&result.equals("200")){
            return Single.just(this);
        }
        return Single.error(new Throwable(result));
    }
}