package com.iyuba.music.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by carl shen on 2021/3/18
 * New English News, new study experience.
 */
public class ChangeNameResponse {
    @SerializedName("result")
    public String result;
    @SerializedName("message")
    public String message;
    @SerializedName("uid")
    public String uid;
    @SerializedName("username")
    public String username;

}
