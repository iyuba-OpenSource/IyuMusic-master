package com.iyuba.music.util;

import android.content.Context;

import com.iyuba.music.R;
import com.iyuba.music.manager.RuntimeManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateFormat {


    private static SimpleDateFormat datetime = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    private static ThreadLocal<SimpleDateFormat> timeFormatThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        }
    };
    private static ThreadLocal<SimpleDateFormat> yearFormatThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        }
    };

    public static Date parseTime(String date) throws ParseException {
        return timeFormatThreadLocal.get().parse(date);
    }

    public static Date parseYear(String date) throws ParseException {
        return yearFormatThreadLocal.get().parse(date);
    }

    public static String formatTime(Date date) {
        return timeFormatThreadLocal.get().format(date);
    }

    public static String formatYear(Date date) {
        return yearFormatThreadLocal.get().format(date);
    }

    public static String showTime(Context context, Date ctime) {
        long nowtimelong = System.currentTimeMillis();
        long ctimelong = ctime.getTime();
        long result = Math.abs(nowtimelong - ctimelong);
        String r;
        if (result < 60000) {// 一分钟内
            long seconds = result / 1000;
            if (seconds < 10) {
                r = context.getString(R.string.message_now);
            } else {
                r = seconds + context.getString(R.string.message_second_ago);
            }
        } else if (result < 3600000) {// 一小时内
            long seconds = result / 60000;
            r = seconds + context.getString(R.string.message_minutes_ago);
        } else if (result < 86400000) {// 一天内
            long seconds = result / 3600000;
            r = seconds + context.getString(R.string.message_hour_ago);
        } else if (result < 172800000) {// 昨天
            SimpleDateFormat hour = new SimpleDateFormat(" HH:mm", Locale.CHINA);
            r = context.getString(R.string.message_lastday) + hour.format(ctime);
        } else {// 日期格式
            r = formatYear(ctime);
        }
        return r;
    }

    public static String contentShowTime(Date time, Date compareTime) {
        long compareTimeLong = compareTime.getTime();
        long timeLong = time.getTime();
        long result = Math.abs(compareTimeLong - timeLong);
        String r;
        if (result < 300000) {// 五分钟内
            r = "";
        } else if (System.currentTimeMillis() - timeLong < 86400000) {
            if (daysMinus(time, compareTime) == 0) {// 一天内
                SimpleDateFormat hour = new SimpleDateFormat("HH:mm", Locale.CHINA);
                r = hour.format(time);
            } else if (Math.abs(daysMinus(time, compareTime)) == 1) {// 差一天
                SimpleDateFormat hour = new SimpleDateFormat("HH:mm", Locale.CHINA);
                r = RuntimeManager.getString(R.string.message_lastday) + hour.format(time);
            } else {
                SimpleDateFormat hour = new SimpleDateFormat("MM月dd日 HH:mm", Locale.CHINA);
                r = hour.format(time);
            }
        } else {// 本年度
            Calendar today = Calendar.getInstance();
            Calendar target = Calendar.getInstance();
            target.setTime(time);
            if (target.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
                SimpleDateFormat hour = new SimpleDateFormat("MM月dd日 HH:mm", Locale.CHINA);
                r = hour.format(time);
            } else {
                SimpleDateFormat hour = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);
                r = hour.format(time);
            }
        }
        return r;
    }

    private static int daysMinus(Date fDate, Date oDate) {
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTime(fDate);
        int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
        aCalendar.setTime(oDate);
        int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
        return day2 - day1;
    }

    public static long getTimeDate() {
        Date date = new Date();
        long unixTimestamp = date.getTime() / 1000 + 3600 * 8; //东八区;
        return unixTimestamp / 86400;
    }



    /**
     * 将String转化成date
     *
     * @throws ParseException
     * */
    public static Date pStringToDate(String str, String sfgs)
            throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat(sfgs);
        return sf.parse(str);
    }


    /**
     * 将String转化成date 格式为yyyy-MM-dd hh:mm:ss
     *
     * @throws ParseException
     * */
    public static Date pStringToDate(String str) throws ParseException {
        return datetime.parse(str);
    }


    /**
     * 通过字符串获得long型时间
     *
     * @param String
     * @return long
     */
    public static long getDateFromStr(String dateStr) {
        long temp = 0L;
        Date date = null;
        try {
            date = datetime.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
            return temp;
        }
        temp = date.getTime();
        return temp;
    }



}
