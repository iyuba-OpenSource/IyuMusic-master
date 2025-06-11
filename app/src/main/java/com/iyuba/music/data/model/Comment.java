package com.iyuba.music.data.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public  class Comment {
    @SerializedName("id")
    @Nullable
    public Integer id;
    @SerializedName("ImgSrc")
    @Nullable
    public String imgSrc;
    @SerializedName("score")
    @Nullable
    public  float score;
    @SerializedName("Userid")
    @Nullable
    public  int userId;
    @SerializedName("UserName")
    @Nullable
    public String userName;
    @SerializedName("vip")
    @Nullable
    public  int vip;
    @SerializedName("againstCount")
    @Nullable
    public  int againstCount;
    @SerializedName("agreeCount")
    @Nullable
    public  int agreeCount;
    @SerializedName("ShuoShuoType")
    @Nullable
    public  int shuoShuoType;
    @SerializedName("ShuoShuo")
    @Nullable
    public String shuoShuo;
    @SerializedName("CreateDate")
    @Nullable
    public String createDate;
    @SerializedName("backId")
    @Nullable
    public Integer backId;
    @SerializedName("backList")
    @Nullable
    public List<BackData> backList;
}
