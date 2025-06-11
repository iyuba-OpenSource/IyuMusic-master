package com.iyuba.music.data.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class TalkClass {

    /**
     * DescCn : 5A
     * Category : 321
     * SeriesName : 新概念英语青少版5A
     * CreateTime : 2020-05-26 03:05:09.0
     * UpdateTime : 2020-05-26 04:05:54.0
     * HotFlg : 0
     * Id : 288
     * pic : http://apps.iyuba.cn/iyuba/images/voaseries/288.jpg
     * KeyWords : 少儿英语
     */

    @SerializedName("DescCn")
    public String DescCn;
    @SerializedName("Category")
    public String Category;
    @SerializedName("SeriesName")
    public String SeriesName;
    @SerializedName("CreateTime")
    public String CreateTime;
    @SerializedName("UpdateTime")
    public String UpdateTime;
    @SerializedName("HotFlg")
    public String HotFlg;
    @SerializedName("Id")
    public String Id;
    @SerializedName("pic")
    public String pic;
    @SerializedName("KeyWords")
    public String KeyWords;

    public int getId(){
        if (TextUtils.isEmpty(Id)){
            return 0;
        }
        return Integer.parseInt(Id);
    }
}
