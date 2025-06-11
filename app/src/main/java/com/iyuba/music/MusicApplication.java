package com.iyuba.music;

import static com.iyuba.music.dubbing.utils.Util.getAppProcessName;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;


import com.buaa.ct.videocachelibrary.HttpProxyCacheServer;
import com.bumptech.glide.Glide;
import com.iyuba.ad.adblocker.AdBlocker;
import com.iyuba.dlex.bizs.DLManager;
import com.iyuba.headlinelibrary.IHeadline;
import com.iyuba.imooclib.IMooc;
import com.iyuba.module.commonvar.CommonVars;
import com.iyuba.module.dl.BasicDL;
import com.iyuba.module.favor.BasicFavor;
import com.iyuba.module.movies.IMovies;
import com.iyuba.module.privacy.IPrivacy;
import com.iyuba.module.privacy.PrivacyInfoHelper;
import com.iyuba.music.data.Constant;
import com.iyuba.music.data.local.DubDBManager;
import com.iyuba.music.download.DownloadTask;
import com.iyuba.music.entity.article.StudyRecordUtil;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.model.NetWorkManager;
import com.iyuba.music.receiver.ChangePropertyBroadcast;
import com.iyuba.music.service.PlayerService;
import com.iyuba.music.sqlite.ImportDatabase;
import com.iyuba.music.util.AdTimeCheck;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.util.ThreadPoolUtil;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.share.ShareExecutor;
import com.iyuba.share.mob.MobShareExecutor;
import com.iyuba.widget.unipicker.IUniversityPicker;
import com.mob.MobSDK;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.commonsdk.UMConfigure;
import com.yd.saas.ydsdk.manager.YdConfig;
import com.youdao.sdk.common.OAIDHelper;
import com.youdao.sdk.common.YouDaoAd;
import com.youdao.sdk.common.YoudaoSDK;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.fly.verify.FlyVerify;
import personal.iyuba.personalhomelibrary.PersonalHome;
import timber.log.Timber;

/**
 * Created by 10202 on 2015/11/16.
 */
public class MusicApplication extends Application {
    private List<Activity> activityList = new ArrayList<>();
    private int sleepSecond;
    private Handler baseHandler = new Handler();
    private PlayerService playerService;
    private HttpProxyCacheServer proxy;
    private ChangePropertyBroadcast changeProperty;
    private CountDownTimer timer;
    private static MusicApplication musicApplication;
    private static String channel = "Umeng";
    private int mFinalCount;


    private long startTime, endTime;
    private HandlerThread mHandlerThread;
    private static Handler mSubHandler;

    @Override
    public void onCreate() {
        super.onCreate();//必须调用父类方法
        RuntimeManager.initRuntimeManager(this);
        musicApplication = this;


        Timber.plant(new Timber.DebugTree());
        webViewSetPath(musicApplication);
        if (BuildConfig.DEBUG) {
            UMConfigure.setLogEnabled(true);
            UMConfigure.setEncryptEnabled(false);
        } else {
            UMConfigure.setLogEnabled(false);
            UMConfigure.setEncryptEnabled(true);
        }
        channel = AnalyticsConfig.getChannel(musicApplication);
        if (!TextUtils.isEmpty(ConfigManager.getInstance().loadString(ConfigManager.USER_ID))) {
            ConfigManager.getInstance().setAutoLogin(true);
        }
        if (TextUtils.isEmpty(channel)) {
            channel = getChannel(getApplicationContext());
        }
        if (TextUtils.isEmpty(channel)) {
            channel = "Umeng";
        }
        Log.e("MusicApplication", "onCreate AnalyticsConfig.getChannel = " + channel);
        UMConfigure.preInit(musicApplication, "5375a4a756240b3f7d005886", channel);

        NetWorkManager.getInstance().initDev();
        if (!ConfigManager.getInstance().isFirstPrivacy()) {
            initLib();
        }

      /*  registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                mFinalCount++;
                //如果mFinalCount ==1，说明是从后台到前台
                Log.e("onActivityStarted", mFinalCount + "");
                if (mFinalCount == 1) {
                    //说明从后台回到了前台
                    endTime = new Date().getTime();
                    Log.e("onActivityStarted", new Date().toString() + "");

                    long timeload = endTime - startTime;
                    Log.e("onActivityStarted", timeload + "--" + startTime);
                    if (startTime != 0 && timeload / 1000 >= 180) {
                        Log.e("onActivityStarted", "===============" + activity.toString());

                        Intent intent = new Intent(activity, WelcomeActivity.class);
                        intent.putExtra("onActivityStarted", true);
                        activity.startActivity(intent);
                    }

                }
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                mFinalCount--;
                //如果mFinalCount ==0，说明是前台到后台
                Log.e("onActivityStopped", mFinalCount + "");
                if (mFinalCount == 0) {
                    //说明从前台回到了后台

                    startTime = new Date().getTime();
                    Log.e("onActivityStopped", new Date().toString() + "");
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });*/
    }

    public void initLib() {

        IHeadline.init(getApplicationContext(), Constant.APPID, Constant.AppName); //视频
        mHandlerThread = new HandlerThread("HeadNewsApplication");
        mHandlerThread.start();
        mSubHandler = new Handler(mHandlerThread.getLooper());
        mSubHandler.post(new Runnable() {
            //        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                YdConfig.getInstance().init(getApplicationContext(), Constant.APPID);

                PersonalHome.init(getApplicationContext());
                IUniversityPicker.init(getApplicationContext());
                DubDBManager.init(getApplicationContext());
                DLManager.init(getApplicationContext(), 5);  //下载

                IMovies.init(getApplicationContext(), Constant.APPID, Constant.AppName); //美剧
                IMooc.init(getApplicationContext(), Constant.APPID, Constant.AppName); //微课


                ShareExecutor.getInstance().setRealExecutor(new MobShareExecutor());
                PersonalHome.setEnableShare(false);
                PersonalHome.setAppInfo(Constant.APPID, Constant.AppName);
                if (Constant.APP_NAME_PRIVACY.equalsIgnoreCase("隐私政策")) {
                    IPrivacy.init(getApplicationContext(), Constant.PROTOCOL_VIVO_USAGE + Constant.APP_Name, Constant.PROTOCOL_VIVO_PRIVACY + Constant.APP_Name);
                } else {
                    IPrivacy.init(getApplicationContext(), Constant.PROTOCOL_URL_IYUBA + Constant.APP_Name, Constant.PROTOCOL_URL_IYUBA + Constant.APP_Name);
                }
                PrivacyInfoHelper.init(getApplicationContext());
                PrivacyInfoHelper.getInstance().putApproved(true);
                if (Constant.APP_TENCENT_PRIVACY) {
                    initUMMob();
                }

                PersonalHome.init(getApplicationContext());//个人中心初始化  -- 新版
                BasicDL.init(getApplicationContext());//下载
                BasicFavor.init(getApplicationContext(), Constant.APPID);//收藏
                prepareLazy();

                IHeadline.resetMseUrl();
                IHeadline.setExtraMseUrl(Constant.EVALUATE_URL);
                IHeadline.setExtraMergeAudioUrl(Constant.MERGE_URL);
            }
        });
    }

    private static final String showAdDate = "2022-10-01 16:29:55";

    public static void initUMMob() {


        MobSDK.submitPolicyGrantResult(true);
        FlyVerify.submitPolicyGrantResult(true);

        UMConfigure.init(musicApplication, "5375a4a756240b3f7d005886", channel, UMConfigure.DEVICE_TYPE_PHONE, "");
        UMConfigure.submitPolicyGrantResult(musicApplication, true);
        //分享
        if ("com.iyuba.englishfm".equals(getAppProcessName(musicApplication))) {

            MobSDK.init(musicApplication, ConstantManager.SMS_FM_ID, ConstantManager.SMS_FM_SECRET);
        } else {

            MobSDK.init(musicApplication, ConstantManager.SMSAPPID, ConstantManager.SMSAPPSECRET);
        }
        /**
         * 通用模块
         */
        try {

            System.loadLibrary("msaoaidsec");
            OAIDHelper.getInstance().init(musicApplication);
            YoudaoSDK.init(musicApplication);

            YouDaoAd.getNativeDownloadOptions().setConfirmDialogEnabled(true);
            YouDaoAd.getYouDaoOptions().setAppListEnabled(false);
            YouDaoAd.getYouDaoOptions().setPositionEnabled(false);
            YouDaoAd.getYouDaoOptions().setSdkDownloadApkEnabled(true);
            YouDaoAd.getYouDaoOptions().setDeviceParamsEnabled(false);
            YouDaoAd.getYouDaoOptions().setWifiEnabled(false);

//            Date compileDate = AdTimeCheck.sdf.parse(BuildConfig.COMPILE_DATETIME);
//            AdBlocker.getInstance().setBlockStartDate(compileDate);
        } catch (Exception arg1) {
            arg1.printStackTrace();
        }
    }

    public static MusicApplication getApp() {
        return musicApplication;
    }

    private void pushSdkInit() {
        final String TAG = "mipush";
        if (ConfigManager.getInstance().isPush()) {
//            MiPushClient.registerPush(this, ConstantManager.MIPUSH_APP_ID, ConstantManager.MIPUSH_APP_KEY);
        }
//        LoggerInterface newLogger = new LoggerInterface() {
//
//            @Override
//            public void setTag(String tag) {
//            }
//
//            @Override
//            public void log(String content, Throwable t) {
//                if ((getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
//                    Log.d(TAG, content, t);
//                }
//            }
//
//            @Override
//            public void log(String content) {
//                if ((getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
//                    Log.d(TAG, content);
//                }
//            }
//        };
//        Logger.setLogger(this, newLogger);
    }

    public void webViewSetPath(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getAppProcessName(context);
            if (!"com.iyuba.music".equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }
    }

    public static String getChannel(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return "";
    }

    @Override
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
//        Glide.get(this).trimMemory(level);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        YoudaoSDK.terminate();
        if (mSubHandler != null) {
            mSubHandler.removeCallbacks(null);
            mSubHandler = null;
        }
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
    }

    private void initApplication() {
        ConfigManager.getInstance();
//        SkinManager.getInstance().init(this, "MusicSkin");
        baseHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                prepareLazy();
            }
        }, 800);
    }

    private void prepareLazy() {
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                // 文件目录的初始化
                File file = new File(ConstantManager.envir);
                if (!file.exists()) {
                    file.mkdirs();
                }
                file = new File(ConstantManager.envir + "/.nomedia");
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        // 皮肤等状态切换监听
        changeProperty = new ChangePropertyBroadcast();
        IntentFilter intentFilter = new IntentFilter(ChangePropertyBroadcast.FLAG);
        registerReceiver(changeProperty, intentFilter);

        // 初始化推送
        pushSdkInit();
        // 初始化addam
//        AddamManager.start(this, "iyuba@sina.com", "a01c1754adf58704df15e929dc63b4ce", "addam_market");
//        AddamManager.initialize(new AddamManager.Callback() {
//            @Override
//            public void initialized(boolean success) {
//            }
//        });

    }

    public void pushActivity(Activity activity) {
        activityList.add(activity);
        if (playerService == null) {
            baseHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startService(new Intent(getApplicationContext(), PlayerService.class));
                }
            }, 600);
        }
    }

    public void popActivity(Activity activity) {
        activityList.remove(activity);
    }

    public void clearActivityList() {
        Activity activity;
        for (int i = activityList.size() - 1; i > -1; i--) {
            activity = activityList.get(i);
            if (null != activity) {
                activity.finish();
            }
        }
        activityList.clear();
    }

    private void stopLessonRecord() {
        if (playerService.getCurArticleId() != 0) {
            StudyRecordUtil.recordStop(StudyManager.getInstance().getLesson(), 0);
        }
    }

    public void exit() {

        stopService(new Intent(getApplicationContext(), PlayerService.class));
        stopLessonRecord();
        clearActivityList();
        ImageUtil.destroy(this);
        DownloadTask.shutDown();
        ThreadPoolUtil.getInstance().shutdown();
        ImportDatabase.getInstance().closeDatabase();
        try {
            if (proxy != null) {
                proxy.shutdown();
            }
            if (changeProperty != null) {
                unregisterReceiver(changeProperty);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public int getSleepSecond() {
        return sleepSecond;
    }

    public void setSleepSecond(final int setTime) {
        if (timer != null) {
            timer.cancel();
        }
        if (setTime == 0) {
            sleepSecond = 0;
            return;
        }
        timer = new CountDownTimer(setTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                sleepSecond = (int) millisUntilFinished / 1000;
                if (sleepSecond == 2) {
                    CustomToast.getInstance().showToast(R.string.sleep_time_finish);
                }
            }

            @Override
            public void onFinish() {
                exit();
            }
        };
        timer.start();
    }

    public boolean isAppointForeground(String appoint) {
        if (activityList != null && activityList.size() > 0) {
            Activity activity = activityList.get(activityList.size() - 1);
            if (activity.getLocalClassName().contains(appoint)) {
                return true;
            }
        }
        return false;
    }

    public boolean onlyForeground(String appoint) {
        return activityList.size() == 1 && activityList.get(0).getLocalClassName().contains(appoint);
    }

    public boolean noMain() {
        if (activityList != null && activityList.size() > 0) {
            for (Activity activity : activityList) {
                if (activity.getLocalClassName().contains("MainActivity")) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }

    public PlayerService getPlayerService() {
        return playerService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public HttpProxyCacheServer getProxy() {
        return proxy == null ? (proxy = new HttpProxyCacheServer(this)) : proxy;
    }

    public static void setDefaultWeb() {
        if (!CommonVars.domain.equalsIgnoreCase(ConfigManager.getInstance().getDomainShort())) {
            CommonVars.domain = ConfigManager.getInstance().getDomainShort();
            Log.e("WelcomeActivity", "setDefaultWeb update CommonVars.domain = " + CommonVars.domain);
        }
        if (Constant.IYUBA_CN.equalsIgnoreCase(ConfigManager.getInstance().getDomainShort() + "/")) {
            Log.e("WelcomeActivity", "setDefaultWeb no need update domain as no change. ");
        } else {
            Constant.IYUBA_CN = ConfigManager.getInstance().getDomainShort() + "/";
            Log.e("WelcomeActivity", "setDefaultWeb update web domain = " + Constant.IYUBA_CN);
            // set new domains
            setDomainShort();
        }
        if (!CommonVars.domainLong.equalsIgnoreCase(ConfigManager.getInstance().getDomainLong())) {
            CommonVars.domainLong = ConfigManager.getInstance().getDomainLong();
            Log.e("WelcomeActivity", "setDefaultWeb update CommonVars.domainLong = " + CommonVars.domainLong);
        }
        if (Constant.IYUBA_COM.equalsIgnoreCase(ConfigManager.getInstance().getDomainLong() + "/")) {
            Log.e("WelcomeActivity", "setDefaultWeb no need for domain long as no change. ");
        } else {
            Constant.IYUBA_COM = ConfigManager.getInstance().getDomainLong() + "/";
            Log.e("WelcomeActivity", "setDefaultWeb update web2 domain = " + Constant.IYUBA_COM);
            setDomainLong();
        }
    }

    public static void setDomainShort() {
        // set domain for update

        Constant.VIDEO_VIP_ADD = "http://staticvip." + Constant.IYUBA_CN + "video";
        Constant.AUDIO_VIP_ADD = "http://staticvip." + Constant.IYUBA_CN + "sounds";
        Constant.appUpdateUrl = "http://api." + Constant.IYUBA_CN + "mobile/android/headline/islatest.plain?";
        Constant.titleUrl = "http://cms." + Constant.IYUBA_CN + "cmsApi/getMyNewsList.jsp?";
        Constant.detailUrl = "http://cms." + Constant.IYUBA_CN + "cmsApi/getText.jsp?";
        Constant.searchUrl = "http://cms." + Constant.IYUBA_CN + "search/searchNewsApi.jsp?tag=";
        Constant.vipurl = "http://staticvip." + Constant.IYUBA_CN + "sounds/song/";
        Constant.soundurl = "http://static2." + Constant.IYUBA_CN + "go/musichigh/";
        Constant.PIC_ABLUM__ADD = "http://static1." + Constant.IYUBA_CN + "data/attachment/album/";
    }

    public static void setDomainLong() {
        // set domain long for update
        Constant.userimage = "http://api." + Constant.IYUBA_COM + "v2/api.iyuba?protocol=10005&uid=";
    }

}
