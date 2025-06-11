package com.iyuba.music.data.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by carl shen on 2021/4/19
 * English Music, new study experience.
 */
public class LoginResponse {
    @SerializedName("result")
    public int result;
    @SerializedName("message")
    @Nullable
    public String message;

}
