package com.iyuba.music.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 10202 on 2015/10/9.
 * 广告时间控制
 */
public class AdTimeCheck {

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private static String showAdTime = "2022-07-06 19:00:00";


    public static boolean setAd() {
        long time = System.currentTimeMillis();
        Date date = null;
        try {
            date = sdf.parse(showAdTime);
            if (time > date.getTime()) {
                return true;
            } else {
                return false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return true;
        }
    }
}