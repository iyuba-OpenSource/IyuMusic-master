package com.iyuba.music;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.util.Log;
import com.iyuba.music.activity.MainActivity;
import com.iyuba.music.activity.mvp.BaseActivity;
import com.iyuba.music.data.Constant;
import com.iyuba.music.databinding.ActivitySplashBinding;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.model.bean.AdEntryBean;
import com.iyuba.music.presenter.SplashPresenter;
import com.iyuba.music.sqlite.ImportDatabase;
import com.iyuba.music.util.popup.PrivacyPopup;
import com.iyuba.music.view.SplashContract;
import com.yd.saas.base.interfaces.AdViewSpreadListener;
import com.yd.saas.config.exception.YdError;
import com.yd.saas.ydsdk.YdSpread;

public class SplashActivity extends BaseActivity<SplashContract.SplashView, SplashContract.SplashPresenter>
        implements SplashContract.SplashView, AdViewSpreadListener {


    private PrivacyPopup privacyPopup;

    private ActivitySplashBinding binding;

    private AdEntryBean.DataDTO dataDTO;

    private boolean isAdCLick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dealWindow();
        initView();


        boolean isFirstEnter = ConfigManager.getInstance().isFirstPrivacy();

        if (isFirstEnter) {

            initPrivacyPopup();
        } else {


            initialDatabase();
            MusicApplication.getApp().initLib();


            if ((System.currentTimeMillis() / 1000) > BuildConfig.AD_TIME) {
                String userId = ConfigManager.getInstance().loadString("userId", "0");
                presenter.getAdEntryAll(Constant.APPID, 1, userId);
            }

            countDownTimer.start();
        }
    }

    private void initView() {

        binding.splashIvAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dataDTO == null) {
                    return;
                }
                if (dataDTO.getType().equals("web")) {

                    MyWebActivity.startActivity(SplashActivity.this, dataDTO.getStartuppicUrl(), "详情");
                }
            }
        });
    }

    @Override
    public View initLayout() {
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public SplashContract.SplashPresenter initPresenter() {
        return new SplashPresenter();
    }

    private void initPrivacyPopup() {

        if (privacyPopup == null) {

            privacyPopup = new PrivacyPopup(this);
            privacyPopup.setCallback(new PrivacyPopup.Callback() {
                @Override
                public void yes() {

                    //初始化
                    ConfigManager.getInstance().setFirstPrivacy(false);
                    privacyPopup.dismiss();
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));

                    initialDatabase();
                    MusicApplication.getApp().initLib();
                    finish();
                }

                @Override
                public void no() {

                    privacyPopup.dismiss();
                    finish();
                }

                @Override
                public void user() {

                    String url = Constant.PROTOCOL_URL_IYUBA + Constant.APP_Name;

                    MyWebActivity.startActivity(SplashActivity.this, url, "用户协议");
                }

                @Override
                public void privacy() {

                    String url = Constant.PROTOCOL_VIVO_PRIVACY + Constant.APP_Name;
                    MyWebActivity.startActivity(SplashActivity.this, url, "隐私政策");
                }
            });
        }
        privacyPopup.showPopupWindow();
    }


    /**
     * 处理状态栏和虚拟返回键
     */
    private void dealWindow() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {

            WindowInsetsController windowInsetsController = getWindow().getInsetsController();
            windowInsetsController.hide(WindowInsets.Type.systemBars());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isAdCLick) {//点击了就直接跳转mainactivity

            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (countDownTimer != null) {

            countDownTimer.cancel();
        }
    }

    /**
     * 计时器
     */
    CountDownTimer countDownTimer = new CountDownTimer(2000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {


            if (dataDTO == null) {

                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            } else {

                if (dataDTO.getType().equals("web")) {

                    webCountDownTimer.start();
                }
            }
        }
    };


    CountDownTimer webCountDownTimer = new CountDownTimer(5000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

            int s = (int) (millisUntilFinished / 1000);
            binding.splashTvJump.setText("跳转(" + s + ")");
        }

        @Override
        public void onFinish() {

            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    };

    @Override
    public void showLoading(String msg) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void toast(String msg) {

        Toast.makeText(MusicApplication.getApp(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getAdEntryAllComplete(AdEntryBean adEntryBean) {

        dataDTO = adEntryBean.getData();
        String type = dataDTO.getType();
        if (type.equals("web")) {

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

            ImageView imageView = new ImageView(SplashActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(layoutParams);
            binding.splashFlAd.addView(imageView);
            Glide.with(SplashActivity.this).load(R.mipmap.ic_launcher).into(imageView);
        } else if (type.equals(Constant.AD_ADS1) || type.equals(Constant.AD_ADS2) || type.equals(Constant.AD_ADS3)
                || type.equals(Constant.AD_ADS4) || type.equals(Constant.AD_ADS5)) {

            YdSpread mSplashAd = new YdSpread.Builder(SplashActivity.this)
                    .setKey("0143")
                    .setContainer(binding.splashFlAd)
                    .setSpreadListener(this)
                    .setCountdownSeconds(4)
                    .setSkipViewVisibility(true)
                    .build();
            mSplashAd.requestSpread();
        } else {

            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onAdDisplay() {
        Log.d("adadad", "onAdDisplay");
    }

    @Override
    public void onAdClose() {

        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onAdClick(String s) {

        isAdCLick = true;
        Log.d("adadad", "onAdClick");
    }

    @Override
    public void onAdFailed(YdError ydError) {

        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
        Log.d("adadad", "onAdFailed:" + ydError.getMsg());
    }


    private void initialDatabase() {
        int lastVersion = ConfigManager.getInstance().loadInt("version");
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
        }
    }


    private void appUpgrade(int currentVersion) {

        ConfigManager.getInstance().putInt("version", currentVersion);
        ConfigManager.getInstance().setUpgrade(true);
    }
}