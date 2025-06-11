package com.iyuba.music.data.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/1/18/018.
 */

public  class BackData {
    @SerializedName("againstCount")
    @Nullable
    public  int againstCount;
    @SerializedName("agreeCount")
    @Nullable
    public Integer agreeCount;
    @SerializedName("backId")
    @Nullable
    public Integer backId;
    @SerializedName("CreateDate")
    @Nullable
    public String createDate;
    @SerializedName("id")
    @Nullable
    public Integer id;
    @SerializedName("ImgSrc")
    @Nullable
    public String imgSrc;
    @SerializedName("ShuoShuo")
    @Nullable
    public String shuoshuo;
    @SerializedName("ShuoShuoType")
    @Nullable
    public Integer shuoshuoType;
    @SerializedName("Userid")
    @Nullable
    public Integer userId;
    @SerializedName("userName")
    @Nullable
    public String username;

}
