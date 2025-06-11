package com.iyuba.music.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.iyuba.ad.adblocker.AdBlocker;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.data.Constant;
import com.iyuba.music.entity.ad.AdEntity;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.local_music.LocalMusicActivity;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.apprequest.AdPicRequest;
import com.iyuba.music.request.newsrequest.StudyWXRequest;
import com.iyuba.music.sqlite.ImportDatabase;
import com.iyuba.music.util.AdTimeCheck;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.PrivicyDialog;
import com.iyuba.music.widget.RoundProgressBar;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.youdao.sdk.common.NativeIndividualDownloadOptions;
import com.youdao.sdk.nativeads.NativeErrorCode;
import com.youdao.sdk.nativeads.NativeResponse;
import com.youdao.sdk.nativeads.RequestParameters;
import com.youdao.sdk.nativeads.YouDaoNative;

/**
 * Created by 10202 on 2015/11/16.
 */
public class WelcomeActivity extends AppCompatActivity {
    public static final String NORMAL_START = "normalStart";
    public static final int SET_DEFAULT_EXIT = 10001;
    private View escapeAd;
    private ImageView header;
    private RoundProgressBar welcomeAdProgressbar;              // 等待进度条
    private AdEntity adEntity;                                  // 开屏广告对象
    private boolean normalStart = true;                         // 是否正常进入程序
    private boolean showAd = false;                             // 是否进入广告
    private YouDaoNative youdaoNative;
    private Context context;
    private String adUrl;
    private Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());

    public static final String TAG = "WelcomActivity";

    private RelativeLayout re_root;

    public boolean canJump = false;


    private boolean onActivityStarted; //判断app是否从后台回到前台

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.welcome);
        context = this;
        normalStart = getIntent().getBooleanExtra(NORMAL_START, true);

        onActivityStarted = getIntent().getBooleanExtra("onActivityStarted", false);

        Log.e(TAG, "onCreate onActivityStarted = " + onActivityStarted);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initWidget();
        if (!Constant.APP_TENCENT_PRIVACY && !ConfigManager.getInstance().isFirstPrivacy()) {
            if (RuntimeManager.getApplication().getPlayerService() != null && RuntimeManager.getApplication().getPlayerService().isPlaying()) {
                if (!onActivityStarted) {
//                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                    startMainActivity();
                    finish();
                } else {
                    initAdData();
                }

            } else {
                initAdData();
            }
        } else {
//            if (Constant.APP_TENCENT_PRIVACY && ConfigManager.getInstance().isFirstPrivacy()) {
            if (ConfigManager.getInstance().isFirstPrivacy()) {
                PrivicyDialog privicyDialog = new PrivicyDialog(context);
                privicyDialog.showFirstOpenAlertDialog(handler);
//                initialDatabase();
            } else {
//                MusicApplication.initUMMob();
                initAdData();
            }
        }
        ((MusicApplication) getApplication()).pushActivity(this);

        //保存app启动次数，一定次数后弹出好评送书弹框
        int newInt = ConfigManager.getInstance().loadInt("firstSendBookFlag") + 1;
        ConfigManager.getInstance().putInt("firstSendBookFlag", newInt);
        Log.e("启动次数", ConfigManager.getInstance().loadInt("firstSendBookFlag") + "ss");
    }

    private void initAdData() {
        if (AdTimeCheck.setAd()) {
            getBannerPic();
        } else {
            header.setBackgroundResource(R.drawable.default_header);
        }
        setListener();
        initialDatabase();
        RuntimeManager.getInstance().setShowSignInToast(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        handler.removeCallbacksAndMessages(null);
        canJump = false;
        Log.e(TAG, "onPause canJump = " + canJump);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (welcomeAdProgressbar != null && welcomeAdProgressbar.getProgress() > 200) {
            Log.e(TAG, "onResume welcomeAdProgressbar.getProgress() = " + welcomeAdProgressbar.getProgress());
            handler.sendEmptyMessage(1);
        }
        if (!Constant.APP_TENCENT_PRIVACY || !ConfigManager.getInstance().isFirstPrivacy()) {
            Log.e(TAG, "onResume checkFirstStart after privacy check. ");
            checkFirstStart();
            next();
        }
        Log.e(TAG, "onResume canJump = " + canJump);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (youdaoNative != null) {
            youdaoNative.destroy();
        }
        handler.removeCallbacksAndMessages(null);
    }

    private void checkFirstStart() {
        if (ConfigManager.getInstance().isFirstStart()) {
            Intent intent = new Intent(WelcomeActivity.this, HelpUseActivity.class);
            startActivity(intent);
        }
    }

    private void startMainActivity() {
        Log.e(TAG, "startMainActivity onActivityStarted = " + onActivityStarted);
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void initWidget() {
        welcomeAdProgressbar = (RoundProgressBar) findViewById(R.id.welcome_ad_progressbar);
        escapeAd = findViewById(R.id.welcome_escape_ad);
        header = (ImageView) findViewById(R.id.welcome_header);
        re_root = (RelativeLayout) findViewById(R.id.re_root);
    }

    private void setListener() {
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (adEntity != null) {
                    Log.e(TAG, "setListener adEntity.getType() = " + adEntity.getType());
                    if (adEntity.getType().equals("web")) {
                        showAd = true;
                        int nextActivity = normalStart ? 0 : 1;
                        WelcomeAdWebView.launch(context, TextUtils.isEmpty(adEntity.getLoadUrl()) ?
                                "http://app." + ConstantManager.IYUBA_CN + "android/" : adEntity.getLoadUrl(), nextActivity, onActivityStarted);
                        ((MusicApplication) getApplication()).popActivity(WelcomeActivity.this);
                        finish();

                    } else if (adEntity.getType().equals("ads5")) {

                        setWXsmall(adEntity.getLoadUrl());
                        StudyWXRequest.exeRequest(StudyWXRequest.generateUrl(adEntity.getId()), new IProtocolResponse() {
                            @Override
                            public void onNetError(String msg) {

                            }

                            @Override
                            public void onServerError(String msg) {

                            }

                            @Override
                            public void response(Object object) {

                                Log.e("点击小程序广告", object.toString());
                            }
                        });
                        finish();
                    }

                }
            }
        });
        escapeAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeMessages(1);
                handler.removeMessages(3);
                startMainActivity();
            }
        });
    }

    private void initWelcomeAdProgress() {
        if (ConfigManager.getInstance().isFirstStart()) {
            return;
        }
        welcomeAdProgressbar.setCricleProgressColor(GetAppColor.getInstance().getAppColor());
        welcomeAdProgressbar.setProgress(150);                  // 为progress设置一个初始值
        welcomeAdProgressbar.setMax(4000);                      // 总计等待4s
        handler.sendEmptyMessageDelayed(3, 500);                // 半秒刷新进度
    }

    private void getBannerPic() {
        adUrl = ConfigManager.getInstance().getADUrl();
        if (TextUtils.isEmpty(adUrl)) {
            handler.sendEmptyMessage(2);
        }
        AdPicRequest.exeRequest(AdPicRequest.generateUrl(), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                header.setImageResource(R.drawable.default_header);
            }

            @Override
            public void onServerError(String msg) {
                header.setImageResource(R.drawable.default_header);
            }

            @Override
            public void response(Object object) {
                adUrl = new Gson().toJson(object);
                handler.sendEmptyMessageDelayed(2, 2000);
                ConfigManager.getInstance().setADUrl(adUrl);
                Log.e(TAG, "getBannerPic response adUrl = " + adUrl);
            }
        });
    }

    private void loadYouDaoSplash() {
        youdaoNative = new YouDaoNative(context, "a710131df1638d888ff85698f0203b46",
                new YouDaoNative.YouDaoNativeNetworkListener() {
                    @Override
                    public void onNativeLoad(final NativeResponse nativeResponse) {
                        if (nativeResponse == null) {
                            Log.e(TAG, "onNativeLoad nativeResponse is null.");
                            return;
                        }
                        Log.e(TAG, "onNativeLoad nativeResponse = " + nativeResponse.getDestUrl());
                        header.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                nativeResponse.handleClick(header);
                            }
                        });

                        if (context != null && !WelcomeActivity.this.isDestroyed()) {

                            Glide.with(getApplicationContext()).asDrawable().load(nativeResponse.getMainImageUrl())
                                    .into(new CustomTarget<Drawable>() {
                                        @Override
                                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                                            if (nativeResponse != null && !WelcomeActivity.this.isDestroyed()) {
                                                nativeResponse.recordImpression(header);
                                            }
                                        }

                                        @Override
                                        public void onLoadCleared(@Nullable Drawable placeholder) {

                                        }
                                    });
                        }

                    }

                    @Override
                    public void onNativeFail(NativeErrorCode nativeErrorCode) {
                        Log.e(TAG, "onNativeFail nativeErrorCode = " + nativeErrorCode);
                    }
                });
//        Location location = new Location("appPos");
//        location.setLatitude(AccountManager.getInstance().getLatitude());
//        location.setLongitude(AccountManager.getInstance().getLongitude());
//        location.setAccuracy(100);

        RequestParameters requestParameters = new RequestParameters.RequestParametersBuilder()
                .build();
        if (AdBlocker.getInstance().shouldBlockAd()) {
            Log.e(TAG, "showYoudaoAd " + AdBlocker.getInstance().getBlockStartDate());
        } else {
            if (youdaoNative != null) {
                NativeIndividualDownloadOptions nativeDownloadOptions = new NativeIndividualDownloadOptions();
                nativeDownloadOptions.setConfirmDialogEnabled(true);
                youdaoNative.setmNativeIndividualDownloadOptions(nativeDownloadOptions);
                youdaoNative.makeRequest(requestParameters);
            }
        }
    }

    private void initialDatabase() {
        int lastVersion = ConfigManager.getInstance().loadInt("version");
        Log.e(TAG, "initialDatabase lastVersion = " + lastVersion);
        int currentVersion = 0;
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersion = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (lastVersion == 0) {
            ImportDatabase db = ImportDatabase.getInstance();
            db.setVersion(0, 1);                                          // 有需要数据库更改使用
            db.openDatabase();
            appUpgrade(currentVersion);
        } else if (currentVersion > lastVersion) {
            if (lastVersion < 72 && ConfigManager.getInstance().getOriginalSize() == 14) {
                ConfigManager.getInstance().setOriginalSize(16);          // 修改默认文字大小
            }
            if (lastVersion < 83) {                                       // 广告获取方式改变
                ConfigManager.getInstance().setADUrl("");
                ConfigManager.getInstance().setDownloadMode(1);
            }
            appUpgrade(currentVersion);
        } else {
            initWelcomeAdProgress();
        }
        handler.sendEmptyMessageDelayed(1, 6000);  //6s后自动进入
    }

    private void appUpgrade(int currentVersion) {
        ConfigManager.getInstance().putInt("version", currentVersion);
        ConfigManager.getInstance().setUpgrade(true);
        escapeAd.setVisibility(View.GONE);
        welcomeAdProgressbar.setVisibility(View.GONE);
    }

    private boolean isNetworkAvailable() {
        Context context = RuntimeManager.getApplication();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
            if (activeInfo != null) {
                return activeInfo.isConnected();
            } else {
                return false;
            }
        }
    }

    private class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<WelcomeActivity> {
        @Override
        public void handleMessageByRef(final WelcomeActivity activity, Message msg) {
            Log.e(TAG, "handleMessageByRef msg.what = " + msg.what);
            if (activity == null) {
                Log.e(TAG, "handleMessageByRef activity is null.");
                return;
            }
            Log.e(TAG, "handleMessageByRef activity.showAd = " + activity.showAd);
            Log.e(TAG, "handleMessageByRef activity.normalStart = " + activity.normalStart);
            switch (msg.what) {
                case 1:
                    if (ConfigManager.getInstance().isFirstStart())
                        break;
                    if (!activity.showAd) {
                        if (activity.normalStart) {
                            if (!activity.onActivityStarted) {
//                                    activity.startActivity(new Intent(activity, MainActivity.class));
                                activity.startMainActivity();
                            } else {
                                activity.startActivity(new Intent(activity, LocalMusicActivity.class));
                                StudyManager.getInstance().setApp("101");
                            }
                        } else {
                            activity.startActivity(new Intent(activity, LocalMusicActivity.class));
                            StudyManager.getInstance().setApp("101");
                        }
//                        ((MusicApplication) activity.getApplication()).popActivity(activity);
//                        activity.finish();
                    } else {
//                        activity.startActivity(new Intent(activity, MainActivity.class));
//                        activity.startMainActivity();
//                        ((MusicApplication) activity.getApplication()).popActivity(activity);
//                        activity.finish();
                    }
                    break;
                case 2:
                    String adUrl = activity.adUrl;
                    if (TextUtils.isEmpty(adUrl)) {
                        activity.header.setImageResource(R.drawable.default_header);
                    } else if (!activity.isDestroyed() && adUrl.contains("@@@")) {
                        String[] adUrls = adUrl.split("@@@");
                        ImageUtil.loadImage(adUrls[0], activity.header);
                    } else if (!activity.isDestroyed()) {
                        activity.adEntity = new Gson().fromJson(adUrl, AdEntity.class);
                        Log.e("广告类型", activity.adEntity.getType() + "==");

                        switch (activity.adEntity.getType()) {
                            default:
                            case "youdao":
                                if (activity.isNetworkAvailable()) {
                                    activity.loadYouDaoSplash();
                                } else {
                                    activity.header.setImageResource(R.drawable.default_header);
                                }
                                break;
                            case "web":
                                if (activity.isNetworkAvailable()) {


                                    Glide.with(activity).load(activity.adEntity.getPicUrl()).into(activity.header);
//                                    ImageUtil.loadImage(activity.adEntity.getPicUrl(), activity.header, R.drawable.default_header);
                                } else {
                                    activity.header.setImageResource(R.drawable.default_header);
                                }
                                break;
                            case "ads2":
                                if (activity.isNetworkAvailable()) {
                                    activity.initWangMaiAD();
                                } else {
                                    activity.header.setImageResource(R.drawable.default_header);
                                }
                                break;
                            case "ads5":
                                if (activity.isNetworkAvailable()) {
                                    ImageUtil.loadImage(activity.adEntity.getPicUrl(), activity.header, R.drawable.default_header);
                                } else {
                                    activity.header.setImageResource(R.drawable.default_header);
                                }
                                break;
                        }
                    }
                    break;
                case 3:
                    int progress = activity.welcomeAdProgressbar.getProgress();
                    if (progress < 4000) {
                        progress = progress < 500 ? 500 : progress + 500;
                        activity.welcomeAdProgressbar.setProgress(progress);
                        activity.handler.sendEmptyMessageDelayed(3, 500);
                    } else {
                        activity.welcomeAdProgressbar.setVisibility(View.INVISIBLE);
                    }
                    break;
                case 101:
                    initAdData();
                    checkFirstStart();
                    next();
                    MusicApplication.getApp().initLib();
                    break;
                case SET_DEFAULT_EXIT:
                    System.exit(0);
                    WelcomeActivity.this.finish();
                    handler.removeCallbacksAndMessages(null);
                    break;
            }
        }
    }


    //旺脉广告
    private void initWangMaiAD() {

        String WM_appkey = "", WM_token = "", WM_id = "";

        WM_appkey = "238db0eec0c7c76d";// api-key
        WM_token = "uww2ii3qhg"; //应用标识
        WM_id = "111450"; //代码位id


        String WM_sign = MD5.getMD5ofStr(WM_appkey + WM_token);
        Log.e("wangmai--", WM_sign + "WM_appkey" + WM_appkey);

        /*new WMSplashad(WelcomeActivity.this, re_root, null, WM_token, WM_sign, WM_id, new WmAdListener() {
            @Override
            public void onWmAdPresent() {
                Log.e(TAG, "onWmAdPresent: ");
//                CustomToast.getInstance().showToast("请求了旺脉广告", 2000);
            }

            @Override
            public void onWmAdDismissed() {
                Log.e(TAG, "onWmAdDismissed: ");
                next();
            }
            @Override
            public void onWmAdClick() {
                Log.e(TAG, "onWmAdClick: ");
            }
            @Override
            public void onWmAdfailed(String s) {
                Log.e(TAG, "onWmAdfailed: " + s);
                //旺脉广告请求失败处理--请求有道广告
                if (isNetworkAvailable()) {
                    loadYouDaoSplash();
                } else {
                    header.setImageResource(R.drawable.default_header);
                }
            }
            @Override
            public void onWmAdTick(long l) {
                Log.e(TAG, "onWmAdTick: ");
            }
        });*/

    }

    /**
     * 设置一个变量来控制当前开屏页面是否可以跳转，当开屏广告为普链类广告时，点击会打开一个广告落地页，此时开发者还不能打开自己的App主页。当从广告落地页返回以后，
     * 才可以跳转到开发者自己的App主页；当开屏广告是App类广告时只会下载App。
     */
    private void next() {
        if (canJump) {
            if (!this.onActivityStarted) {
//                this.startActivity(new Intent(this, MainActivity.class));
                startMainActivity();
            } else {
                this.startActivity(new Intent(this, LocalMusicActivity.class));
                StudyManager.getInstance().setApp("101");
            }

            this.finish();
        } else {
            canJump = true;
        }
    }

    //跳转微信小程序
    private void setWXsmall(String sId) {
        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        //req.userName = "gh_601036872849"; // 填小程序原始id
        // req.path = path;  //拉起小程序页面的可带参路径，不填默认拉起小程序首页
        if (!sId.contains("##")) {

            req.userName = sId;

        } else if (sId.endsWith("##")) {
            sId = sId.substring(0, sId.length() - 2);
            req.userName = sId;

        } else {

            String[] strings = sId.split("##");
            req.userName = strings[0];
            req.path = strings[1];

        }
        String appId = ""; // 填应用AppId
        appId = "wxa512f1de837454c6";


        com.tencent.mm.opensdk.openapi.IWXAPI api = com.tencent.mm.opensdk.openapi.WXAPIFactory.createWXAPI(WelcomeActivity.this, appId);


        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
        api.sendReq(req);

    }
}
