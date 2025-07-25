package com.iyuba.music.manager;

import android.app.Activity;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

import com.iyuba.music.util.ThreadPoolUtil;

/**
 * Created by 10202 on 2015/11/18.
 */
public class ConfigManager {


    public static final String GROUP_NAME = "听歌学英语官方群";
    public static final int GROUP_ID = 10109;
    public static final String USER_NAME = "userName";
    public static final String USER_PASS = "userPwd";
    public static final String USER_ID = "userId";
    private static final String CONFIG_NAME = "IyuMusic";
    private final static String EGGSHELL_TAG = "eggshell";
    private final static String PUSH_TAG = "push";
    private final static String LANGUAGE_TAG = "language";
    private final static String NIGHT_TAG = "night";
    private final static String FIRST_START = "first_start";
    private final static String FIRST_PRIVACY = "first_privacy";
    private final static String AUTO_LOGIN_TAG = "autoLogin";
    private final static String AUTO_PLAY_TAG = "autoplay";
    private final static String AUTO_STOP_TAG = "autostop";
    private final static String MEDIA_BUTTON = "media_button";
    private final static String AD_TAG = "lastADUrl";
    private final static String UPGRADE_TAG = "upgrade";
    private final static String SAYING_MODE_TAG = "sayingMode";
    private final static String WORD_DEF_SHOW_TAG = "wordDefShow";
    private final static String WORD_ORDER_TAG = "wordOrder";
    private final static String WORD_AUTO_PLAY_TAG = "wordAutoPlay";
    private final static String WORD_AUTO_ADD_TAG = "wordAutoAdd";
    private final static String STUDY_MODE = "studyMode";
    private final static String LYRIC_MODE = "lyricMode";
    private final static String STUDY_TRANSLATE = "studyTranslate";
    private final static String STUDY_PLAY_MODE = "studyPlayMode";
    private final static String ORIGINAL_SIZE = "originalSize";
    private final static String PHOTO_TIMESTAMP = "photoTimestamp";
    private final static String DOWNLOAD = "download";
    private final static String DOWNLOADMEANWHILE = "downloadMeanwhile";
    private final static String AUTOROUND = "autoRound";

    private final static String INITWEIGHT = "initWeight";
    private final static String TARGETWEIGHT = "targetWeight";
    private final static String SHOWTARGET = "showTarget";
    public static final String DOMAIN_NAME = "domain_name";
    public static final String DOMAIN_SHORT1 = "domain_short1";
    public static final String DOMAIN_SHORT2 = "domain_short2";
    private static SharedPreferences preferences;
    private int language, sayingMode, wordOrder, studyMode, lyricMode,studyPlayMode, originalSize, download, studyTranslate;
    private boolean eggshell, push, night, mediaButton, autoLogin, autoPlay, autoStop, autoRound,
            autoDownload, upgrade, wordDefShow, wordAutoPlay, wordAutoAdd, showWeightTarget;
    private float initWeight, targetWeight;
    private String lastADUrl, photoTimestamp;

    private ConfigManager() {
        preferences = RuntimeManager.getContext().getSharedPreferences(CONFIG_NAME, Activity.MODE_PRIVATE);
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                push = loadBoolean(PUSH_TAG, true);
                night = loadBoolean(NIGHT_TAG);
                language = loadInt(LANGUAGE_TAG);
                autoLogin = loadBoolean(AUTO_LOGIN_TAG, false);
                autoPlay = loadBoolean(AUTO_PLAY_TAG);
                autoStop = loadBoolean(AUTO_STOP_TAG, true);
                mediaButton = loadBoolean(MEDIA_BUTTON, true);
                lastADUrl = loadString(AD_TAG);

                sayingMode = loadInt(SAYING_MODE_TAG);
                wordDefShow = loadBoolean(WORD_DEF_SHOW_TAG, true);
                wordOrder = loadInt(WORD_ORDER_TAG);
                wordAutoPlay = loadBoolean(WORD_AUTO_PLAY_TAG, true);
                wordAutoAdd = loadBoolean(WORD_AUTO_ADD_TAG);

                originalSize = loadInt(ORIGINAL_SIZE, 16);
                studyPlayMode = loadInt(STUDY_PLAY_MODE, 1);
                studyMode = loadInt(STUDY_MODE, 1);
                lyricMode = loadInt(LYRIC_MODE,0);
                studyTranslate = loadInt(STUDY_TRANSLATE, 1);

                eggshell = loadBoolean(EGGSHELL_TAG);
                upgrade = loadBoolean(UPGRADE_TAG);
                autoRound = loadBoolean(AUTOROUND, true);
                photoTimestamp = loadString(PHOTO_TIMESTAMP, "");
                autoDownload = loadBoolean(DOWNLOADMEANWHILE);
                download = loadInt(DOWNLOAD, 0);

                initWeight = loadFloat(INITWEIGHT, 100);
                targetWeight = loadFloat(TARGETWEIGHT, 80);
                showWeightTarget = loadBoolean(SHOWTARGET);
            }
        });
    }

    public static ConfigManager getInstance() {
        return SingleInstanceHelper.instance;
    }

    /**
     * 原configmanager迁移，合并settingconfigmanager和configmanager
     */

    public void putBoolean(String name, boolean value) {
        preferences.edit().putBoolean(name, value).apply();
    }

    public void putInt(String name, int value) {
        preferences.edit().putInt(name, value).apply();
    }

    public void putFloat(String name, float value) {
        preferences.edit().putFloat(name, value).apply();
    }

    public void putString(String name, String value) {
        preferences.edit().putString(name, value).apply();
    }

    public boolean loadBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public boolean loadBoolean(String key, boolean defaultBool) {
        return preferences.getBoolean(key, defaultBool);
    }

    public int loadInt(String key) {
        return preferences.getInt(key, 0);
    }

    public int loadInt(String key, int defaultInt) {
        return preferences.getInt(key, defaultInt);
    }

    public float loadFloat(String key) {
        return preferences.getFloat(key, 0);
    }

    public float loadFloat(String key, float defaultInt) {
        return preferences.getFloat(key, defaultInt);
    }

    public String loadString(String key) {
        return preferences.getString(key, "");
    }

    public String loadString(String key, @NonNull String defaultString) {
        return preferences.getString(key, defaultString);
    }

    public void removeKey(String key) {
        preferences.edit().remove(key).apply();
    }

    public void clearKey() {
        preferences.edit().clear().apply();
    }


    public boolean isPush() {
        return push;
    }

    public void setPush(boolean push) {
        this.push = push;
        putBoolean(PUSH_TAG, push);
    }

    public boolean isNight() {
        return night;
    }

    public void setNight(boolean night) {
        this.night = night;
        putBoolean(NIGHT_TAG, night);
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
        putInt(LANGUAGE_TAG, language);
    }

    public boolean isFirstStart() {
        return loadBoolean(FIRST_START, true);
    }

    public void setFirstStart(boolean firstStart) {
        putBoolean(FIRST_START, firstStart);
    }

    public boolean isFirstPrivacy() {
        return loadBoolean(FIRST_PRIVACY, true);
    }

    public void setFirstPrivacy(boolean firstStart) {
        putBoolean(FIRST_PRIVACY, firstStart);
    }

    public boolean isAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        this.autoLogin = autoLogin;
        putBoolean(AUTO_LOGIN_TAG, autoLogin);
    }

    public void setQQGroup(String qqGroup) {
        putString("qqGroup", qqGroup);
    }

    public String getQQGroup() {
        return loadString("qqGroup", "");
    }

    public String getQQKey() {
        return loadString("qqKey", "");
    }

    public void setQQKey(String qqKey) {
        putString("qqKey", qqKey);
    }

    public boolean isAutoPlay() {
        return autoPlay;
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
        putBoolean(AUTO_PLAY_TAG, autoPlay);
    }

    public boolean isAutoStop() {
        return autoStop;
    }

    public void setAutoStop(boolean autoStop) {
        this.autoStop = autoStop;
        putBoolean(AUTO_STOP_TAG, autoStop);
    }

    public boolean isMediaButton() {
        return mediaButton;
    }

    public void setMediaButton(boolean mediaButton) {
        this.mediaButton = mediaButton;
        putBoolean(MEDIA_BUTTON, mediaButton);
    }

    public String getADUrl() {
        return lastADUrl;
    }

    public void setADUrl(String url) {
        this.lastADUrl = url;
        putString(AD_TAG, url);
    }

    public int getSayingMode() {
        return sayingMode;
    }

    public void setSayingMode(int mode) {
        this.sayingMode = mode;
        putInt(SAYING_MODE_TAG, mode);
    }

    public boolean isWordDefShow() {
        return wordDefShow;
    }

    public void setWordDefShow(boolean show) {
        this.wordDefShow = show;
        putBoolean(WORD_DEF_SHOW_TAG, show);
    }

    public int getWordOrder() {
        return wordOrder;
    }

    public void setWordOrder(int order) {
        this.wordOrder = order;
        putInt(WORD_ORDER_TAG, order);
    }

    public boolean isWordAutoPlay() {
        return wordAutoPlay;
    }

    public void setWordAutoPlay(boolean play) {
        this.wordAutoPlay = play;
        putBoolean(WORD_AUTO_PLAY_TAG, play);
    }

    public boolean isWordAutoAdd() {
        return wordAutoAdd;
    }

    public void setWordAutoAdd(boolean add) {
        this.wordAutoAdd = add;
        putBoolean(WORD_AUTO_ADD_TAG, add);
    }

    public int getStudyMode() {
        return studyMode;
    }

    public void setStudyMode(int mode) {
        this.studyMode = mode;
        putInt(STUDY_MODE, mode);
    }

    public int getLyricMode() {
        return lyricMode;
    }

    public void setLyricMode(int mode) {
        this.lyricMode = mode;
        putInt(LYRIC_MODE, mode);
    }

    public int getStudyTranslate() {
        return studyTranslate;
    }

    public void setStudyTranslate(int mode) {
        this.studyTranslate = mode;
        putInt(STUDY_TRANSLATE, mode);
    }

    public int getStudyPlayMode() {
        return studyPlayMode;
    }

    public void setStudyPlayMode(int mode) {
        this.studyPlayMode = mode;
        putInt(STUDY_PLAY_MODE, mode);
    }

    public boolean isEggShell() {
        return eggshell;
    }

    public void setEggShell(boolean eggShell) {
        this.eggshell = eggShell;
        putBoolean(EGGSHELL_TAG, eggShell);
    }

    public boolean isUpgrade() {
        return upgrade;
    }

    public void setUpgrade(boolean upgrade) {
        this.upgrade = upgrade;
        putBoolean(UPGRADE_TAG, upgrade);
    }

    public boolean isAutoRound() {
        return autoRound;
    }

    public void setAutoRound(boolean round) {
        this.autoRound = round;
        putBoolean(AUTOROUND, round);
    }

    public boolean isDownloadMeanwhile() {
        return autoDownload;
    }

    public void setDownloadMeanwhile(boolean meanwhile) {
        this.autoDownload = meanwhile;
        putBoolean(DOWNLOADMEANWHILE, meanwhile);
    }

    public int getDownloadMode() {
        return download;
    }

    public void setDownloadMode(int mode) {
        this.download = mode;
        putInt(DOWNLOAD, mode);
    }

    public int getOriginalSize() {
        return originalSize;
    }

    public void setOriginalSize(int size) {
        this.originalSize = size;
        putInt(ORIGINAL_SIZE, size);
    }

    public float getInitWeight() {
        return initWeight;
    }

    public void setInitWeight(float weight) {
        this.initWeight = weight;
        putFloat(INITWEIGHT, weight);
    }

    public float getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(float weight) {
        this.targetWeight = weight;
        putFloat(TARGETWEIGHT, weight);
    }

    public boolean isShowTarget() {
        return showWeightTarget;
    }

    public void setShowTarget(boolean show) {
        this.showWeightTarget = show;
        putBoolean(SHOWTARGET, show);
    }

    public String getUserPhotoTimeStamp() {
        return photoTimestamp;
    }

    public void setUserPhotoTimeStamp() {
        this.photoTimestamp = "time=" + System.currentTimeMillis();
        putString(PHOTO_TIMESTAMP, photoTimestamp);
    }

    public void setDomain(String domain) {
        putString(DOMAIN_NAME, domain);
    }

    public String getDomainShort() {
        return loadString(DOMAIN_SHORT1, "iyuba.cn");
    }

    public void setDomainShort(String domain1) {
        putString(DOMAIN_SHORT1, domain1);
    }

    public String getDomainLong() {
        return loadString(DOMAIN_SHORT2, "iyuba.com.cn");
    }

    public void setDomainLong(String domain2) {
        putString(DOMAIN_SHORT2, domain2);
    }

    private static class SingleInstanceHelper {
        private static ConfigManager instance = new ConfigManager();
    }
}
