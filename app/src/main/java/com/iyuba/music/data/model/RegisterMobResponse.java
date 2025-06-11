package com.iyuba.music.data.model;

import com.google.gson.annotations.SerializedName;
import com.iyuba.music.entity.user.UserInfo;

/**
 * Created by carl shen on 2021/3/18
 * New English News, new study experience.
 */
public class RegisterMobResponse {
    @SerializedName("isLogin")
    public int isLogin;
    @SerializedName("userinfo")
    public UserInfo userinfo;
    @SerializedName("res")
    public MobBean res;

    public class MobBean {
        @SerializedName("valid")
        public String valid;
        @SerializedName("phone")
        public String phone;
        @SerializedName("isValid")
        public int isValid;
    }
}
