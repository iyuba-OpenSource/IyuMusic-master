package com.iyuba.music.activity.study;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.addam.library.api.AddamBanner;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.iyuba.module.toolbox.DensityUtil;
import com.iyuba.music.BuildConfig;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.MainActivity;
import com.iyuba.music.activity.WelcomeAdWebView;
import com.iyuba.music.data.Constant;
import com.iyuba.music.download.DownloadUtil;
import com.iyuba.music.entity.ad.AdEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.event.ChangePlayMode;
import com.iyuba.music.event.StudyEvent;
import com.iyuba.music.fragmentAdapter.StudyFragmentAdapter;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IPlayerListener;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.receiver.ChangeUIBroadCast;
import com.iyuba.music.request.account.AuditDateRequest;
import com.iyuba.music.request.newsrequest.CommentCountRequest;
import com.iyuba.music.request.newsrequest.StudyAdRequest;
import com.iyuba.music.request.newsrequest.StudyWXRequest;
import com.iyuba.music.service.PlayerService;
import com.iyuba.music.util.AdTimeCheck;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.util.InterNetUtil;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.Mathematics;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.MusicListPop;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.dialog.StudyMore;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.iyuba.music.widget.imageview.MorphButton;
import com.iyuba.music.widget.imageview.PageIndicator;
import com.iyuba.music.widget.player.StandardPlayer;
import com.iyuba.music.widget.roundview.RoundTextView;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.yd.saas.base.interfaces.AdViewBannerListener;
import com.yd.saas.config.exception.YdError;
import com.yd.saas.ydsdk.YdBanner;
import com.youdao.sdk.nativeads.NativeErrorCode;
import com.youdao.sdk.nativeads.NativeResponse;
import com.youdao.sdk.nativeads.RequestParameters;
import com.youdao.sdk.nativeads.YouDaoNative;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 首页 - 解说 - 播放主页面
 */
public class StudyActivity extends BaseActivity implements View.OnClickListener, AdViewBannerListener {
    public static final int START = 0x01;
    public static final int END = 0x02;
    public static final int NONE = 0x03;
    private static final int RECORD_AUDIO_TASK_CODE = 2;
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private StudyFragmentAdapter studyFragmentAdapter;
    private StudyMore studyMoreDialog;
    private StandardPlayer player;
    private TextView currTime, duration;
    private ViewPager viewPager;
    private PageIndicator pageIndicator;
    private float lastChange = 0;
    private SeekBar seekBar;
    public MorphButton playSound;
    private ImageView former, latter, playMode, interval, studyMode, studyMore, studyTranslate;
    private RoundTextView comment;
    private int aPosition, bPosition;// 区间播放
    @IntervalState
    private int intervalState;
    private IyubaDialog waittingDialog;
    public LinearLayout studyroot;

    private PlayerService playerService;

    IPlayerListener iPlayerListener = new IPlayerListener() {
        @Override
        public void onPrepare() {
            if (waittingDialog.isShowing()) {
                waittingDialog.dismiss();
            }
            int i = player.getDuration();
            seekBar.setMax(i);
            duration.setText(Mathematics.formatTime(i / 1000));
            StudyManager.getInstance().setAllTime(i / 1000);
            handler.sendEmptyMessage(0);
            playSound.setState(MorphButton.PLAY_STATE);
        }

        @Override
        public void onBufferChange(int buffer) {
            seekBar.setSecondaryProgress(buffer * seekBar.getMax() / 100);
        }

        @Override
        public void onFinish() {

            if (musicListPop != null) {
                musicListPop.handler.sendEmptyMessage(3);
            }

            checkNetWorkState();
            refresh(false);
        }

        @Override
        public void onError() {

        }


    };
    private RelativeLayout adRoot;
    private View adView;
    private ImageView photoImage;
    private Timer timer;
    private TimerTask timerTask;
    private YouDaoNative youdaoNative;
    private AddamBanner addamBanner;
    private StudyChangeUIBroadCast studyChangeUIBroadCast;

    private MusicListPop musicListPop;
    private int studyPlayMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.layout_study);
        context = this;

        playerService = ((MusicApplication) getApplication()).getPlayerService();
        player = playerService.getPlayer();
        ((MusicApplication) getApplication()).getPlayerService().startPlay(
                StudyManager.getInstance().getCurArticle(), false);
//        if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.M) && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            //申请WRITE_EXTERNAL_STORAGE权限
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    RECORD_AUDIO_TASK_CODE);
//        }
        initBroadCast();
        initWidget();
        setListener();
        changeUIByPara();
        checkNetWorkState();


        Log.e("歌曲信息===", StudyManager.getInstance().getCurArticle().toString() + "--");
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
  /*      int mode = ConfigManager.getInstance().getStudyPlayMode();
        if (studyPlayMode != mode) {
            studyPlayMode = mode;
            setStudyModeImage(studyPlayMode);
        }*/
        changeUIResumeByPara();
        if (player.isPrepared()) {
            handler.sendEmptyMessage(0);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroy() {
        if (youdaoNative != null) {
            youdaoNative.destroy();
        }
        if (addamBanner != null) {
            addamBanner.unLoad();
        }

        if (timer != null) {
            timerTask.cancel();
            timer.purge();
        }
        unregisterReceiver(studyChangeUIBroadCast);
        ((MusicApplication) getApplication()).getPlayerService().setListener(null);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        if (studyMoreDialog != null && studyMoreDialog.isShown()) {
            studyMoreDialog.dismiss();
        } else if (!studyFragmentAdapter.getItem(viewPager.getCurrentItem()).onBackPressed()) {
            if (!mipush && !changeProperty) {
                if (((MusicApplication) getApplication()).noMain()) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    super.onBackPressed();
                }
            } else {
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.play_mode) {
            int nextMusicType = ConfigManager.getInstance().getStudyPlayMode();
            nextMusicType = (nextMusicType + 1) % 3;
            ConfigManager.getInstance().setStudyPlayMode(nextMusicType);
            StudyManager.getInstance().generateArticleList();
            setPlayModeImage(nextMusicType);
        } else if (id == R.id.play) {
            setPauseImage(true);
        } else if (id == R.id.study_mode) {
            int musicType = ConfigManager.getInstance().getStudyMode();
            musicType = (musicType + 1) % 2;
            ConfigManager.getInstance().setStudyMode(musicType);
            setStudyModeImage(musicType);
        } else if (id == R.id.interval) {//                setIntervalImage(1);


            musicListPop = new MusicListPop(this);
            musicListPop.showPop(context, studyroot);
        } else if (id == R.id.latter) {
            ((MusicApplication) getApplication()).getPlayerService().next(false);
            startPlay();
            refresh(true);
        } else if (id == R.id.formmer) {
            ((MusicApplication) getApplication()).getPlayerService().before();
            startPlay();
            refresh(true);
        } else if (id == R.id.study_more) {

            if (studyMoreDialog != null && studyMoreDialog.isShown()) {
                studyMoreDialog.dismiss();
            } else {

                if (studyMoreDialog == null) {
                    studyMoreDialog = new StudyMore(this);
                }
                studyMoreDialog.show();
            }
        } else if (id == R.id.study_translate) {
            int musicTranslate = ConfigManager.getInstance().getStudyTranslate();
            musicTranslate = (musicTranslate + 1) % 2;
            ConfigManager.getInstance().setStudyTranslate(musicTranslate);
            setStudyTranslateImage(musicTranslate, true);
        } else if (id == R.id.study_comment) {
            if (InterNetUtil.isNetworkConnected(MusicApplication.getApp())) {
                startActivity(new Intent(context, CommentActivity.class));
            } else {
                CustomToast.getInstance().showToast(R.string.no_internet);
            }
        }
    }

    private boolean checkNetWorkState() {
        String url = ((MusicApplication) getApplication()).getPlayerService().getUrl(StudyManager.getInstance().getCurArticle());
        if (((MusicApplication) getApplication()).getProxy().isCached(url)) {
            return true;
        } else if (url.startsWith("http")) {
            if (!InterNetUtil.isNetworkConnected(MusicApplication.getApp())) {
                showNoNetDialog();
                return false;
//            } else if (!NetWorkState.getInstance().isConnectByCondition(NetWorkState.EXCEPT_2G_3G)) {
//                CustomSnackBar.make(studyroot, context.getString(R.string.net_speed_slow)).warning(context.getString(R.string.net_set), new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
//                        startActivity(intent);
//                    }
//                });
//                return false;
            } else {
                waittingDialog.show();
                return true;
            }
        } else {
            return true;
        }
    }

    private void showNoNetDialog() {
        final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
        materialDialog.setTitle(R.string.net_study_no_net);
        materialDialog.setMessage(R.string.net_study_no_net_message);
        boolean findFileFlg = false;
        if (ConfigManager.getInstance().getStudyMode() == 1) {
            File packageFile = new File(ConstantManager.originalFolder);
            if (packageFile.exists() && packageFile.list() != null) {
                for (String fileName : packageFile.list()) {
                    if (fileName.startsWith(String.valueOf(StudyManager.getInstance().getCurArticle().getId()))) {
                        materialDialog.setPositiveButton(R.string.net_study_lrc, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                viewPager.setCurrentItem(2);
                                materialDialog.dismiss();
                            }
                        });
                        materialDialog.setNegativeButton(R.string.app_know, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        });
                        findFileFlg = true;
                        break;
                    }
                }
            }
            if (!findFileFlg) {
                materialDialog.setPositiveButton(R.string.app_know, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
            }
        } else {
            File packageFile = new File(ConstantManager.musicFolder);
            if (packageFile.exists() && packageFile.list() != null) {
                for (String fileName : packageFile.list()) {
                    if (fileName.startsWith(String.valueOf(StudyManager.getInstance().getCurArticle().getId()))) {
                        materialDialog.setPositiveButton(R.string.net_study_lrc, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                viewPager.setCurrentItem(2);
                                materialDialog.dismiss();
                            }
                        });
                        materialDialog.setNegativeButton(R.string.app_know, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        });
                        findFileFlg = true;
                        break;
                    }
                }
            }
            if (!findFileFlg) {
                materialDialog.setPositiveButton(R.string.app_know, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
            }
        }
        materialDialog.show();

    }

    private void initBroadCast() {
        studyChangeUIBroadCast = new StudyChangeUIBroadCast(this);
        IntentFilter intentFilter = new IntentFilter("com.iyuba.music.study");
        registerReceiver(studyChangeUIBroadCast, intentFilter);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        studyroot = (LinearLayout) findViewById(R.id.root);
        adRoot = (RelativeLayout) findViewById(R.id.ad_stub);
        studyMore = (ImageView) findViewById(R.id.study_more);
        viewPager = (ViewPager) findViewById(R.id.study_main);
        pageIndicator = (PageIndicator) findViewById(R.id.study_indicator);
        currTime = (TextView) findViewById(R.id.study_current_time);
        duration = (TextView) findViewById(R.id.study_duration);
        seekBar = (SeekBar) findViewById(R.id.study_progress);

        playSound = (MorphButton) findViewById(R.id.play);
        former = (ImageView) findViewById(R.id.formmer);
        latter = (ImageView) findViewById(R.id.latter);
        playMode = (ImageView) findViewById(R.id.play_mode);
        interval = (ImageView) findViewById(R.id.interval);
        comment = (RoundTextView) findViewById(R.id.study_comment);
        studyMode = (ImageView) findViewById(R.id.study_mode);
        studyTranslate = (ImageView) findViewById(R.id.study_translate);
        playSound.setForegroundColorFilter(GetAppColor.getInstance().getAppColor(), PorterDuff.Mode.SRC_IN);
        waittingDialog = WaitingDialog.create(context, null);

        AuditDateRequest.exeRequest(AuditDateRequest.generateUrl(), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
            }

            @Override
            public void onServerError(String msg) {
            }

            @Override
            public void response(Object object) {
                String result = (String) object;
                if ("1".equals(result)) {                    //审核中
                    interval.setVisibility(View.GONE);
                    playMode.setVisibility(View.GONE);
                } else {
                    interval.setVisibility(View.VISIBLE);
                    playMode.setVisibility(View.VISIBLE);
                }
            }
        });
        if (!DownloadUtil.checkVip() && InterNetUtil.isNetworkConnected(MusicApplication.getApp())) {
            if (AdTimeCheck.setAd()) {
                initAdTimer();
            } else {
                adRoot.setVisibility(View.GONE);
            }

        } else {

        }
    }

    private void setAdType(AdEntity adEntity) {

        if (addamBanner != null) {
            addamBanner.unLoad();
            addamBanner = null;
        }
        if (youdaoNative != null) {
            youdaoNative.destroy();
            youdaoNative = null;
        }
        Log.e("广告类型", adEntity.getType());
        switch (adEntity.getType()) {
            case "youdao":

                runOnUiThread(() -> {

                    adView = LayoutInflater.from(context).inflate(R.layout.youdao_ad_layout, null);
                    adRoot.addView(adView);
                    photoImage = (ImageView) adView.findViewById(R.id.photoImage);
                    initYouDaoAd();
                });
                break;
            case "web":

                break;
            case "ads1":
            case "ads2":
            case "ads3":
            case "ads4":
            case "ads5":

                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                int width = displayMetrics.widthPixels;
                int height = DensityUtil.dp2px(StudyActivity.this, 65);

                YdBanner mBanner = new YdBanner.Builder(StudyActivity.this)
                        .setKey("0144")
                        .setWidth(width)
                        .setHeight(height)
                        .setMaxTimeoutSeconds(5)
                        .setBannerListener(this)
                        .build();

                mBanner.requestBanner();
                break;
        }
    }

    private void getAdContent(final IOperationResult iOperationResult) {

        StudyAdRequest.exeRequest(StudyAdRequest.generateUrl(), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {

                runOnUiThread(() -> {

                    initYouDaoAd();
                });
            }

            @Override
            public void onServerError(String msg) {

                runOnUiThread(() -> {

                    initYouDaoAd();
                });
            }

            @Override
            public void response(Object object) {
                iOperationResult.success(object);
            }
        });
    }

    private void initAdTimer() {


        if ((System.currentTimeMillis() / 1000) < BuildConfig.AD_TIME) {

            return;
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {
                getAdContent(new IOperationResult() {
                    @Override
                    public void success(Object object) {
                        handler.obtainMessage(3, object).sendToTarget();
                    }

                    @Override
                    public void fail(Object object) {
                    }
                });
            }
        };
        timer = new Timer(false);
        timer.scheduleAtFixedRate(timerTask, Calendar.getInstance().getTime(), 20000);
    }

    private void refreshNativeAd(final AdEntity adEntity) {
        if (!isDestroyed()) {
            adView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (adEntity.getType().equals("web")) {
                        WelcomeAdWebView.launch(context, TextUtils.isEmpty(adEntity.getLoadUrl()) ?
                                "http://app." + ConstantManager.IYUBA_CN + "android/" : adEntity.getLoadUrl(), -1, false);

                    }

                    if (adEntity.getType().equals("ads5")) {

                        setWXsmall(adEntity.getTitle());


                        StudyWXRequest.exeRequest(StudyWXRequest.generateUrl(adEntity.getId()), new IProtocolResponse() {
                            @Override
                            public void onNetError(String msg) {

                            }

                            @Override
                            public void onServerError(String msg) {

                            }

                            @Override
                            public void response(Object object) {

                            }
                        });


                    }

                }
            });
            Glide.with(context).load(adEntity.getPicUrl()).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(photoImage);
        }
    }

    private void initYouDaoAd() {

        youdaoNative = new YouDaoNative(context, "230d59b7c0a808d01b7041c2d127da95",
                new YouDaoNative.YouDaoNativeNetworkListener() {
                    @Override
                    public void onNativeLoad(final NativeResponse nativeResponse) {

                        Log.e("banner111", nativeResponse.toString());

                        adRoot.removeAllViews();
                        adRoot.setVisibility(View.VISIBLE);
                        adRoot.addView(adView);

                        adView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.e("有道广告", "2");
                                nativeResponse.handleClick(adView);
                            }
                        });
                        ImageView photoImage = adRoot.findViewById(R.id.photoImage);
                        Glide.with(adRoot.getContext())
                                .asDrawable()
                                .load(nativeResponse.getMainImageUrl())
                                .into(new CustomTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                                        photoImage.setImageDrawable(resource);
                                        nativeResponse.recordImpression(photoImage);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                    }
                                });
                    }

                    @Override
                    public void onNativeFail(NativeErrorCode nativeErrorCode) {
                        Log.e("banner111", nativeErrorCode.toString());
                    }
                });
        Location location = new Location("appPos");
        location.setLatitude(AccountManager.getInstance().getLatitude());
        location.setLongitude(AccountManager.getInstance().getLongitude());
        location.setAccuracy(100);

        RequestParameters requestParameters = new RequestParameters.RequestParametersBuilder()
                .location(location).build();
        youdaoNative.makeRequest(requestParameters);
    }

    @Override
    protected void setListener() {
        super.setListener();
        ((MusicApplication) getApplication()).getPlayerService().setListener(iPlayerListener);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (lastChange != 0 && positionOffset != 0) {
                    if (lastChange > positionOffset) {//左滑
                        pageIndicator.setDirection(PageIndicator.LEFT);
                        pageIndicator.setMovePercent(position + 1, positionOffset);
                    } else {
                        pageIndicator.setDirection(PageIndicator.RIGHT);
                        pageIndicator.setMovePercent(position, positionOffset);
                    }
                }
                lastChange = positionOffset;
            }

            @Override
            public void onPageSelected(int position) {
                pageIndicator.setDirection(PageIndicator.NONE);
                pageIndicator.setCurrentItem(viewPager.getCurrentItem());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean user) {
                if (user) {
                    player.seekTo(progress);

                }
                currTime.setText(Mathematics.formatTime(progress / 1000));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        interval.setOnClickListener(this);
        playMode.setOnClickListener(this);
        playSound.setOnClickListener(this);
        former.setOnClickListener(this);
        latter.setOnClickListener(this);
        studyMore.setOnClickListener(this);
        studyMode.setOnClickListener(this);
        studyTranslate.setOnClickListener(this);
        comment.setOnClickListener(this);
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        if (StudyManager.getInstance().getCurArticle() == null)
            return;
        if (((MusicApplication) getApplication()).getPlayerService().getCurArticleId() == StudyManager.getInstance().getCurArticle().getId()) {
            int i = player.getDuration();
            seekBar.setMax(i);
            duration.setText(Mathematics.formatTime(i / 1000));
        } else {
            ((MusicApplication) getApplication()).getPlayerService().setCurArticleId(StudyManager.getInstance().getCurArticle().getId());
        }
    }

    protected void changeUIResumeByPara() {
        setPlayModeImage(ConfigManager.getInstance().getStudyPlayMode());
        switch (StudyManager.getInstance().getMusicType()) {
            case 0:
                studyMode.setImageResource(R.drawable.study_annoucer_mode);
                studyTranslate.setVisibility(View.VISIBLE);
                setStudyTranslateImage(ConfigManager.getInstance().getStudyTranslate(), false);
                break;
            case 1:
                studyMode.setImageResource(R.drawable.study_singer_mode);
                studyTranslate.setVisibility(View.GONE);
                break;
        }
        setPauseImage(false);
        handler.sendEmptyMessage(2);
        setFuncImgShowState();
        if (viewPager.getAdapter() == null || studyFragmentAdapter == null) {
            studyFragmentAdapter = new StudyFragmentAdapter(getSupportFragmentManager());
            viewPager.setAdapter(studyFragmentAdapter);
            viewPager.setCurrentItem(1);
            viewPager.setOffscreenPageLimit(2);

        } else {
            studyFragmentAdapter.refresh();
            studyMoreDialog = new StudyMore(StudyActivity.this);
        }

    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void startPlay() {
        if (checkNetWorkState()) {
            if (musicListPop != null) {
                musicListPop.handler.sendEmptyMessageDelayed(3, 500);
            }

            ((MusicApplication) getApplication()).getPlayerService().startPlay(StudyManager.getInstance().getCurArticle(), false);
            ((MusicApplication) getApplication()).getPlayerService().setCurArticleId(StudyManager.getInstance().getCurArticle().getId());
            ((MusicApplication) getApplication()).getPlayerService().setListener(iPlayerListener);

        }
    }

    private void refresh(boolean defaultPos) {
        handler.sendEmptyMessage(2);
        if (ConfigManager.getInstance().getStudyPlayMode() == 0 || StudyManager.getInstance().getCurArticleList().size() == 1) {
            if (defaultPos) {
                viewPager.setCurrentItem(1);
            } else {
                player.seekTo(0);
            }
            studyFragmentAdapter.refresh();
            studyMoreDialog = new StudyMore(StudyActivity.this);
        } else {
            if (defaultPos) {
                studyFragmentAdapter.refresh();
                studyMoreDialog = new StudyMore(StudyActivity.this);
                viewPager.setCurrentItem(1);
            } else {
                if (checkNetWorkState()) {
                    seekBar.setSecondaryProgress(0);
                    playSound.setState(MorphButton.PLAY_STATE);
                    duration.setText("00:00");
                }
                int currPage = viewPager.getCurrentItem();
                studyFragmentAdapter.refresh();
                studyMoreDialog = new StudyMore(StudyActivity.this);

                viewPager.setCurrentItem(currPage);
            }
        }
        setFuncImgShowState();
    }

    private void setFuncImgShowState() {
        if (StudyManager.getInstance().getCurArticle().getSimple() == 1) {
            studyMode.setVisibility(View.GONE);
            comment.setVisibility(View.VISIBLE);
            studyTranslate.setVisibility(View.VISIBLE);
            getCommentCount();
        } else if (!StudyManager.getInstance().getApp().equals("209")) {
            studyMode.setVisibility(View.GONE);
            comment.setVisibility(View.GONE);
            studyTranslate.setVisibility(View.VISIBLE);
        } else {
            studyMode.setVisibility(View.VISIBLE);
            comment.setVisibility(View.VISIBLE);
            getCommentCount();
        }
    }

    private void setPlayModeImage(int state) {
        switch (state) {
            case 0:
                playMode.setImageResource(R.drawable.single_replay);
                break;
            case 1:
                playMode.setImageResource(R.drawable.list_play);
                break;
            case 2:
                playMode.setImageResource(R.drawable.random_play);
                break;
        }

    }

    private void setPauseImage(boolean click) {


        if (click) {
            if (player != null && player.isPrepared()) {
                sendBroadcast(new Intent("iyumusic.pause"));

            }
        } else {
            if (player == null) {
                playSound.setState(MorphButton.PAUSE_STATE);
            } else if (player.isPlaying()) {
                playSound.setState(MorphButton.PLAY_STATE);
            } else {
                playSound.setState(MorphButton.PAUSE_STATE);
            }
        }

    }


    private void setStudyModeImage(int state) {
        switch (state) {
            case 0:
                studyMode.setImageResource(R.drawable.study_annoucer_mode);
                studyTranslate.setVisibility(View.VISIBLE);
                break;
            case 1:
                studyMode.setImageResource(R.drawable.study_singer_mode);
                studyTranslate.setVisibility(View.GONE);
                break;
        }
        if (checkNetWorkState()) {
            handler.sendEmptyMessage(2);
            seekBar.setSecondaryProgress(0);
            playSound.setState(MorphButton.PLAY_STATE);
            duration.setText("00:00");
            ((MusicApplication) getApplication()).getPlayerService().startPlay(
                    StudyManager.getInstance().getCurArticle(), true);
            ((MusicApplication) getApplication()).getPlayerService().setCurArticleId(StudyManager.getInstance().getCurArticle().getId());
            int currPage = viewPager.getCurrentItem();
            studyFragmentAdapter.refresh();
            studyMoreDialog = new StudyMore(StudyActivity.this);
            viewPager.setCurrentItem(currPage);
        }
    }

    private void setStudyTranslateImage(int state, boolean click) {
        switch (state) {
            case 1:
                studyTranslate.setImageResource(R.drawable.study_translate);
                break;
            case 0:
                studyTranslate.setImageResource(R.drawable.study_no_translate);
                break;
        }
        if (click) {
            studyFragmentAdapter.changeLanguage();
        }
    }

    private void setIntervalImage(int mode) {
        switch (mode) {
            case 0:
                intervalState = NONE;
                interval.setImageResource(R.drawable.interval_none);
                break;
            case 1:
                switch (intervalState) {
                    case NONE:
                        intervalState = START;
                        aPosition = player.getCurrentPosition();
                        CustomToast.getInstance().showToast(R.string.study_a_position);
                        interval.setImageResource(R.drawable.interval_start);
                        break;
                    case START:
                        intervalState = END;
                        bPosition = player.getCurrentPosition();
                        CustomToast.getInstance().showToast(R.string.study_b_position);
                        handler.sendEmptyMessage(1);
                        interval.setImageResource(R.drawable.interval_end);
                        break;
                    case END:
                        CustomToast.getInstance().showToast(R.string.study_ab_cancle);
                        handler.sendEmptyMessage(2);
                        break;
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private void getCommentCount() {
        CommentCountRequest.exeRequest(CommentCountRequest.generateUrl(StudyManager.getInstance().getCurArticle().getId()), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                comment.setText("0");
            }

            @Override
            public void onServerError(String msg) {
                comment.setText("0");
            }

            @Override
            public void response(Object object) {
                if (TextUtils.isEmpty(object.toString())) {
                    comment.setText("0");
                } else {
                    comment.setText(object.toString());
                }
            }
        });
    }

    @Override
    public void onReceived(View view) {

        adRoot.removeAllViews();
        adRoot.addView(view);
        adRoot.setVisibility(View.VISIBLE);
        Log.d("banner111", "onReceived");
    }

    @Override
    public void onAdExposure() {

    }

    @Override
    public void onAdClick(String s) {

    }

    @Override
    public void onClosed() {

        adRoot.setVisibility(View.GONE);
        Log.d("banner111", "onClosed");
    }

    @Override
    public void onAdFailed(YdError ydError) {

        Log.d("banner111", "onAdFailed:" + ydError.getMsg());
    }

    @IntDef({START, END, NONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface IntervalState {
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<StudyActivity> {
        @Override
        public void handleMessageByRef(final StudyActivity activity, Message msg) {
            switch (msg.what) {
                case 0:
                    if (activity.player != null) {
                        int pos = activity.player.getCurrentPosition();
                        activity.currTime.setText(Mathematics.formatTime(pos / 1000));
                        activity.seekBar.setProgress(pos);
                        activity.handler.sendEmptyMessageDelayed(0, 500);
                        if (activity.intervalState == END) {
                            if (Math.abs(pos - activity.bPosition) <= 1000) {
                                activity.handler.sendEmptyMessage(1);
                            }
                        }
                        if (pos != 0 && activity.waittingDialog.isShowing()) {
                            activity.waittingDialog.dismiss();
                        }
                        if (activity.player.isPlaying()) {
                            if (activity.playSound.getState() == MorphButton.PAUSE_STATE) {
                                activity.playSound.setState(MorphButton.PLAY_STATE);

                            }
                        } else if (!activity.player.isPlaying()) {
                            if (activity.playSound.getState() == MorphButton.PLAY_STATE) {
                                activity.playSound.setState(MorphButton.PAUSE_STATE);

                            }
                        }


                        if (activity.player.isPlaying()) {
                            activity.studyFragmentAdapter.imageTrans(true);
                        } else {
                            activity.studyFragmentAdapter.imageTrans(false);
                        }
                    } else {

                        activity.studyFragmentAdapter.imageTrans(false);
                    }
                    break;
                case 1:
                    activity.player.seekTo(activity.aPosition);// A-B播放
                    break;
                case 2:
//                    activity.setIntervalImage(0);
                    break;
                case 3:
                    activity.setAdType((AdEntity) msg.obj);
                    break;
            }
        }
    }

    public static class StudyChangeUIBroadCast extends ChangeUIBroadCast {
        private final WeakReference<StudyActivity> mWeakReference;

        public StudyChangeUIBroadCast(StudyActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void refreshUI(String message) {
            if (mWeakReference.get() != null) {
                switch (message) {
                    case "change":
                        mWeakReference.get().refresh(true);
                        break;
                    case "pause":
                        mWeakReference.get().setPauseImage(false);
                        break;
                }
            }
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
        //旧版的appid ，不能跳转小程序
//        appId = "wx182643cdcfc2b59f";

        // 听歌学英语最新申请的 appid = wxa512f1de837454c6 appsecret = 70cc8c9f7d73bd80a51291e5f8dbe92b
        appId = "wxa512f1de837454c6";
        com.tencent.mm.opensdk.openapi.IWXAPI api = com.tencent.mm.opensdk.openapi.WXAPIFactory.createWXAPI(StudyActivity.this, appId);


        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
        api.sendReq(req);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(StudyEvent event) {

        if (StudyManager.getInstance().getCurArticle().getId() == event.articles.get(event.indexd).getId() &&
                StudyManager.getInstance().getListFragmentPos() == event.type) {


        } else {

            StudyManager.getInstance().setListFragmentPos(event.type);
            StudyManager.getInstance().setStartPlaying(true);
            StudyManager.getInstance().setSourceArticleList((ArrayList<Article>) event.articles);
            StudyManager.getInstance().setLesson("music");
            StudyManager.getInstance().setCurArticle(event.articles.get(event.indexd));

            Log.e("当前播放列表", StudyManager.getInstance().getCurArticleList().size() + "");
            startPlay();
            refresh(true);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(ChangePlayMode event) {

        setPlayModeImage(ConfigManager.getInstance().getStudyPlayMode());
    }


}
