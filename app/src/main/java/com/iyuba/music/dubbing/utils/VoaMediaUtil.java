package com.iyuba.music.dubbing.utils;


/**
 * Created by Administrator on 2016/12/1 0001.
 */

public class VoaMediaUtil {

    private static final String QUESTION_MARK = "?";
    private static final String TIMESTAMP = "timestamp";
    private static final String EQUAL = "=";
    private static String VIP_VIDEO_PREFIX = "http://staticvip.iyuba.cn/video/voa/";
    private static String VIP_SOUND_PREFIX = "http://staticvip.iyuba.cn/sounds/voa/";
    private static String VIDEO_PREFIX = "http://staticvip.iyuba.cn/video/voa/";
    private static String SOUND_PREFIX = "http://staticvip.iyuba.cn/sounds/voa/";
    public static String getAudioUrl(String sound) {
        return SOUND_PREFIX + sound;//SOUND_PREFIX + sound + addTimestamp();
    }

    public static String getAudioErrorUrl(String sound) {
        return SOUND_PREFIX + sound + addTimestamp();
    }


    public static String getVideoUrl(int cat , int voaId) {
        return VIDEO_PREFIX+cat+"/"+ voaId + ".mp4";
    }


    public static String getAudioVipUrl(String sound) {
        return VIP_SOUND_PREFIX + sound + addTimestamp();
    }

    public static String getVideoVipUrl(int voaId) {
        return VIP_VIDEO_PREFIX+ voaId + ".mp4";
    }

    private static String addTimestamp() {
        return QUESTION_MARK + TIMESTAMP + EQUAL + TimeUtil.getTimeStamp();

    }
}
