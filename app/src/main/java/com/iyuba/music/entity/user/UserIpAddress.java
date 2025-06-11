package com.iyuba.music.entity.user;

/**
 * Created by carl shen on 2020/10/29.
 */
public class UserIpAddress {
    public int result;
    public int adcode;
    public int infocode;
    public int status;
    public String province;
    public String city;
    public String rectangle;
    public String info;

    @Override
    public String toString() {
        return "UserIpAddress{" +
                "result=" + result +
                ", adcode='" + adcode + '\'' +
                ", infocode='" + infocode + '\'' +
                ", status='" + status + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
