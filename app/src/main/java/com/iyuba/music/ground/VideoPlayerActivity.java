package com.iyuba.music.ground;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.balysv.materialripple.MaterialRippleLayout;
import com.buaa.ct.imageselector.utils.ScreenUtils;
import com.iyuba.headlinelibrary.data.model.VideoMiniData;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.dubbing.views.VideoDownMenuDialog;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.entity.article.StudyRecord;
import com.iyuba.music.entity.original.Original;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.request.account.ScoreOperRequest;
import com.iyuba.music.request.newsrequest.LrcRequest;
import com.iyuba.music.request.newsrequest.ReadCountAddRequest;
import com.iyuba.music.request.newsrequest.StudyRecordRequest;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.Mathematics;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.WordCard;
import com.iyuba.music.widget.imageview.MorphButton;
import com.iyuba.music.widget.original.HighLightTextCallBack;
import com.iyuba.music.widget.original.OriginalSynView;
import com.iyuba.music.widget.original.SeekToCallBack;
import com.iyuba.music.widget.original.TextSelectCallBack;
import com.iyuba.music.widget.player.StandardPlayer;
import com.iyuba.music.widget.player.VideoView;
import com.kwad.sdk.collector.g;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

/**
 * 主页面 - MTV  - MTV详情页
 */
@RuntimePermissions
public class VideoPlayerActivity extends BaseActivity implements View.OnClickListener {
    private static final int WRITE_EXTERNAL_TASK_CODE = 1;
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private boolean isSystemPlaying;
    private int currPos;
    private Article article;
    private ArrayList<Article> articles;
    private VideoView videoView;
    private WordCard wordCard;
    private TextView currTime, duration;
    private SeekBar seekBar;
    private OriginalSynView originalView;
    private TextView subtitle;
    private ArrayList<Original> originalList;
    private ImageView largePause;
    private MorphButton playSound;
    private ImageView former, latter, playMode, studyTranslate, changescreen, subtitlteimg;
    private RelativeLayout video_layout;
    private View toorbar, menu, seekbar_layout, video_content_layout, ll_play_state_info, menuTop;
    private boolean isfullscreen = false;
    private boolean isplay = true;
    private boolean isshowcontrol = true;
    private boolean isshowchinese = false;

    private RelativeLayout root;

    private int lrcNum = 0, finishFlag, allTime; //歌词总数,是否播放完成，歌曲总时间
    private String startTime, endTime;
    private VideoDownMenuDialog mMenuDialog;
    private VideoMiniData vmd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_videoplayer);


        context = this;
        Log.e("onCreate", "执行了");
/*        if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.M) && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_TASK_CODE);
        }*/
        StandardPlayer player = ((MusicApplication) getApplication()).getPlayerService().getPlayer();
        if (player != null && player.isPlaying()) {
            sendBroadcast(new Intent("iyumusic.pause"));
            isSystemPlaying = true;
        } else {
            isSystemPlaying = false;
        }
        articles = (ArrayList<Article>) getIntent().getSerializableExtra("articleList");
        currPos = getIntent().getIntExtra("pos", 0);
        Log.e("歌曲信息", articles.get(currPos) + "ppppppp");
        initWidget();
        setListener();
        changeUIByPara();
        refresh();
    }

    @Override
    public void onBackPressed() {
        if (wordCard.isShown()) {
            wordCard.dismiss();
        } else {
            StandardPlayer player = ((MusicApplication) getApplication()).getPlayerService().getPlayer();
            if (player != null && isSystemPlaying) {
                sendBroadcast(new Intent("iyumusic.pause"));
            }
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("onPause", "执行了");


        if (videoView.isPlaying()) {
            finishFlag = 0;
            commitStudyRecord();
            videoView.pause();
            isplay = true;
        }
        setPauseImage();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy", "执行了");
        if (videoView.isPlaying()) {
            finishFlag = 0;
            commitStudyRecord();
        }

        videoView.stopPlayback();
        handler.removeCallbacksAndMessages(null);
        wordCard.destroy();
    }

    protected void initWidget() {
        super.initWidget();


        MaterialRippleLayout toolbar_mrls_oper = findViewById(R.id.toolbar_mrls_oper);
        toolbar_mrls_oper.setVisibility(View.GONE);

        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        toolbarOper.setVisibility(View.GONE);
        menuTop = findViewById(R.id.img_etc);
//        image_etc = findViewById(R.id.image_etc);
        currTime = (TextView) findViewById(R.id.study_current_time);
        duration = (TextView) findViewById(R.id.study_duration);
        seekBar = (SeekBar) findViewById(R.id.study_progress);
        playSound = (MorphButton) findViewById(R.id.play);
        former = (ImageView) findViewById(R.id.formmer);
        latter = (ImageView) findViewById(R.id.latter);
        playMode = (ImageView) findViewById(R.id.play_mode);
        studyTranslate = (ImageView) findViewById(R.id.translate);
        wordCard = (WordCard) findViewById(R.id.wordcard);
        videoView = (VideoView) findViewById(R.id.videoView_small);
        originalView = (OriginalSynView) findViewById(R.id.original);
        subtitle = (TextView) findViewById(R.id.tv_zimu);
        subtitle.setVisibility(View.GONE);
        largePause = (ImageView) findViewById(R.id.large_pause);
        largePause.setImageDrawable(getResources().getDrawable(R.drawable.pause));
        playSound.setForegroundColorFilter(GetAppColor.getInstance().getAppColor(), PorterDuff.Mode.SRC_IN);
        video_layout = (RelativeLayout) findViewById(R.id.video_layout);
        changescreen = (ImageView) findViewById(R.id.change_screen);
        toorbar = findViewById(R.id.toolbar);
        menu = findViewById(R.id.meun_layout);
        seekbar_layout = findViewById(R.id.seekbar_layout);
        video_content_layout = findViewById(R.id.video_content_layout);
        subtitlteimg = (ImageView) findViewById(R.id.change_zimu);
        ll_play_state_info = findViewById(R.id.ll_play_state_info);
        root = (RelativeLayout) findViewById(R.id.root);
        menuTop.setVisibility(View.VISIBLE);
        vmd = new VideoMiniData();
        mMenuDialog = new VideoDownMenuDialog(this, new VideoDownMenuDialog.ActionDelegate() {
            public VideoMiniData getVideoMiniData() {
                return vmd;
            }

            public boolean isJP() {
                return false;
            }
        }, articles.get(currPos));
    }

    @Override
    protected void setListener() {
        super.setListener();
      /*  toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AccountManager.getInstance().checkUserLogin()) {
                    CustomToast.getInstance().showToast(R.string.article_share_request);
                    return;
                }
                share();
            }
        });*/
        menuTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    VideoPlayerActivityPermissionsDispatcher.downloadActualWithPermissionCheck(VideoPlayerActivity.this);
                } else {

                    new AlertDialog.Builder(VideoPlayerActivity.this)
                            .setTitle("权限说明")
                            .setMessage("存储权限：存储下载的视频")
                            .setPositiveButton("确定", (dialog, which) -> {

                                VideoPlayerActivityPermissionsDispatcher.downloadActualWithPermissionCheck(VideoPlayerActivity.this);
                            })
                            .show();
                }
            }
        });
        subtitlteimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int musicTranslate = ConfigManager.getInstance().getStudyTranslate();
                musicTranslate = (musicTranslate + 1) % 2;
                ConfigManager.getInstance().setStudyTranslate(musicTranslate);
                setStudyTranslateImage(musicTranslate);
                if (musicTranslate == 1) {
                    originalView.setShowChinese(true);
                    isshowchinese = true;
                } else {
                    originalView.setShowChinese(false);
                    isshowchinese = false;
                }
                originalView.setOriginalList(originalList);
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                setScreen();
                int i = videoView.getDuration();
                seekBar.setMax(i);
                duration.setText(Mathematics.formatTime(i / 1000));
                handler.sendEmptyMessage(0);
                videoView.start();
                largePause.setVisibility(View.GONE);
                playSound.setState(MorphButton.PLAY_STATE);
                largePause.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                Log.e("======", "onPrepare");
                allTime = i / 1000;
                startTime = DateFormat.formatTime(Calendar.getInstance().getTime());
            }
        });
        videoView.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                if (percent > 1) {
                    findViewById(R.id.videoView_loading).setVisibility(View.GONE);
                }
                seekBar.setSecondaryProgress(percent * seekBar.getMax() / 100);
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                finishFlag = 1;
                commitStudyRecord();


                //根据模式，判断
                int nextMusicType = ConfigManager.getInstance().getStudyPlayMode();
                if (nextMusicType == 0) {//single_replay

                    //currPos不变
                } else if (nextMusicType == 1) {//列表

                    currPos = (currPos + 1) % articles.size();
                } else if (nextMusicType == 2) {//随机

                    Random random = new Random();
                    currPos = random.nextInt(articles.size());
                }

                refresh();
            }
        });
        largePause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause();
            }
        });
        findViewById(R.id.video_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isshowcontrol = !isshowcontrol;
                if (isshowcontrol) {
                    ll_play_state_info.setVisibility(View.VISIBLE);
                    largePause.setVisibility(View.VISIBLE);
                } else {
                    ll_play_state_info.setVisibility(View.GONE);
                    largePause.setVisibility(View.GONE);
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean user) {
                if (user) {
                    currTime.setTextColor(GetAppColor.getInstance().getAppColor());
                    duration.setTextColor(GetAppColor.getInstance().getAppColor());
                    videoView.seekTo(progress);
                } else {
                    currTime.setTextColor(GetAppColor.getInstance().getAppColorLight());
                    duration.setTextColor(GetAppColor.getInstance().getAppColorLight());
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
        originalView.setHighLightTextCallBack(new HighLightTextCallBack() {
            @Override
            public void getCurrentHighLightText(String content) {
                if (subtitle != null) {
                    String[] zimu = content.split("\n");
                    if (content != null && !"".equals(content)) {
                        if (isshowchinese)
                            subtitle.setText(content);
                        else
                            subtitle.setText(zimu[0]);
                    }
                }
            }
        });
        originalView.setTextSelectCallBack(new TextSelectCallBack() {
            @Override
            public void onSelectText(String text) {
                if ("".equals(text)) {
                    CustomToast.getInstance().showToast(R.string.word_select_null);
                } else {
                    if (!wordCard.isShowing()) {
                        wordCard.show();
                    }
                    wordCard.resetWord(text);
                }
            }
        });

        originalView.setSeekToCallBack(new SeekToCallBack() {
            @Override
            public void onSeekStart() {
                handler.removeMessages(0);
            }

            @Override
            public void onSeekTo(double time) {
                videoView.seekTo((int) (time * 1000));
                handler.sendEmptyMessage(0);
            }
        });
        changescreen.setOnClickListener(this);
        playMode.setOnClickListener(this);
        playSound.setOnClickListener(this);
        former.setOnClickListener(this);
        latter.setOnClickListener(this);
        studyTranslate.setOnClickListener(this);
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
//        toolbarOper.setText(R.string.study_share);
//        toolbarOper.setBackground(getResources().getDrawable(R.drawable.headline_ic_more));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResume", "执行了");
        changeUIResumeByPara();
        if (videoView.isPrepared()) {
            //pause();
            if (isplay)
                videoView.start();
            else
                videoView.pause();
            setPauseImage();
        }
    }


    protected void changeUIResumeByPara() {
        setPauseImage();
        setPlayModeImage(ConfigManager.getInstance().getStudyPlayMode());
        setStudyTranslateImage(ConfigManager.getInstance().getStudyTranslate());
    }

    private void refresh() {
        article = articles.get(currPos);
        findViewById(R.id.videoView_loading).setVisibility(View.VISIBLE);
        LocalInfoOp localInfoOp = new LocalInfoOp();
        localInfoOp.updateSee(article.getId(), article.getApp());
        ReadCountAddRequest.exeRequest(ReadCountAddRequest.generateUrl(article.getId(), "music"), null);
        getOriginal();
        seekBar.setSecondaryProgress(0);
        videoView.reset();
        title.setText(article.getTitle_cn());

        vmd.video = article.getMusicUrl();
        vmd.srtChVideo = article.getMusicUrl();
        vmd.srtEngVideo = article.getMusicUrl();
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.EXCEPT_2G)) {
//            String append = "http://staticvip." + ConstantManager.IYUBA_CN + "video/voa/" + articles.get(currPos).getId() + ".mp4";
            String append = article.getMusicUrl();
            videoView.setVideoPath(append);
        } else if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            CustomToast.getInstance().showToast(R.string.net_speed_slow);
        } else {
            CustomToast.getInstance().showToast(R.string.no_internet);
        }
    }


    @NeedsPermission({"android.permission.WRITE_EXTERNAL_STORAGE"})
    void downloadActual() {
        this.mMenuDialog.show();
    }

    @OnPermissionDenied({"android.permission.WRITE_EXTERNAL_STORAGE"})
    void downloadDenied() {
        CustomToast.getInstance().showToast("未能获取权限无法下载");
    }

    private void setScreen() {
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            int width = RuntimeManager.getWindowWidth() - RuntimeManager.dip2px(20);
            int height = width * videoView.getVideoHeight() / videoView.getVideoWidth();
            videoView.setVideoScale(width, height);
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

    //播放器中间的按钮
    private void pause() {
        if (videoView.isPlaying()) {

            finishFlag = 0;
            commitStudyRecord();

            videoView.pause();
            largePause.setImageDrawable(getResources().getDrawable(R.drawable.play));
            isplay = false;
        } else {
            videoView.start();
            largePause.setImageDrawable(getResources().getDrawable(R.drawable.pause));
            isplay = true;
        }
        Log.e("是否暂停：", isplay + "");
        setPauseImage();
    }

    private void setPauseImage() {

        if (videoView.isPlaying()) {
            Log.e("是否暂停：", "======");
            playSound.setState(MorphButton.PLAY_STATE);
            largePause.setVisibility(View.GONE);
            largePause.setImageDrawable(getResources().getDrawable(R.drawable.pause));
        } else {

            Log.e("是否暂停：", "======1");
            playSound.setState(MorphButton.PAUSE_STATE);
            largePause.setVisibility(View.VISIBLE);
            largePause.setImageDrawable(getResources().getDrawable(R.drawable.play));
        }
    }


    private void setStudyTranslateImage(int state) {
        switch (state) {
            case 1:
                studyTranslate.setImageResource(R.drawable.video_translate);
                subtitlteimg.setImageResource(R.drawable.change_select);
                break;
            case 0:
                studyTranslate.setImageResource(R.drawable.video_untranslate);
                subtitlteimg.setImageResource(R.drawable.change);
                break;
        }
    }

    private int getCurrentPara(double time) {
        int para = 0;
        if (originalList != null && originalList.size() != 0) {
            for (Original original : originalList) {
                if (time < original.getStartTime()) {
                    break;
                } else {
                    para++;
                }
            }
        }
        return para;
    }

    private void getOriginal() {
        getWebLrc(article.getId(), new IOperationFinish() {
            @Override
            public void finish() {
                if (ConfigManager.getInstance().getStudyTranslate() == 1) {
                    originalView.setShowChinese(true);
                } else {
                    originalView.setShowChinese(false);
                }
                originalView.setOriginalList(originalList);
                handler.sendEmptyMessage(0);
            }
        });
    }

    private void getWebLrc(final int id, final IOperationFinish finish) {
        LrcRequest.exeRequest(LrcRequest.generateUrl(id, 0), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg);
            }

            @Override
            public void response(Object object) {
                BaseListEntity listEntity = (BaseListEntity) object;
                originalList = (ArrayList<Original>) listEntity.getData();
                if (originalList == null)
                    return;
                for (Original original : originalList) {
                    original.setArticleID(id);
                    if (TextUtils.isEmpty(original.getSentence_cn())) {
                        original.setSentence_cn(original.getSentence_cn_backup());

                        String lrcItem = original.getSentence();
                        String[] lrc = lrcItem.split(" ");
                        Log.e("歌词长度", original.getSentence() + "歌词" + lrc.length);
                        lrcNum += lrc.length;
                    }
                }
                finish.finish();
            }
        });
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
            pause();
        } else if (id == R.id.latter) {
            currPos = (currPos + 1) % articles.size();
            refresh();
        } else if (id == R.id.formmer) {
            currPos = (currPos - 1) % articles.size();
            if (currPos < 0)
                currPos = articles.size() - 1;
            refresh();
        } else if (id == R.id.translate) {
            int musicTranslate = ConfigManager.getInstance().getStudyTranslate();
            musicTranslate = (musicTranslate + 1) % 2;
            ConfigManager.getInstance().setStudyTranslate(musicTranslate);
            setStudyTranslateImage(musicTranslate);
            if (musicTranslate == 1) {
                originalView.setShowChinese(true);
                isshowchinese = true;
            } else {
                originalView.setShowChinese(false);
                isshowchinese = false;
            }
            originalView.setOriginalList(originalList);
        } else if (id == R.id.change_screen) {
            isplay = true;
            playSound.setState(MorphButton.PLAY_STATE);
            largePause.setImageDrawable(getResources().getDrawable(R.drawable.pause));
            largePause.setVisibility(View.GONE);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                //变成竖屏
                isfullscreen = false;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                //变成横屏了
                isfullscreen = true;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                //变成竖屏
                isfullscreen = false;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
        //继续执行父类其他点击事件
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //变成横屏了
            toorbar.setVisibility(View.GONE);
            subtitle.setVisibility(View.VISIBLE);
//            iv_change_subtitle_type.setVisibility(View.VISIBLE);
//            iv_fullscreen.setImageResource(R.drawable.small_screen);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //setVideoParams(videoView.getmMediaPlayer(), true);
            ViewGroup.LayoutParams rl_paramters = video_layout.getLayoutParams();
            rl_paramters.height = LinearLayout.LayoutParams.MATCH_PARENT;
            rl_paramters.width = LinearLayout.LayoutParams.MATCH_PARENT;
            video_layout.setLayoutParams(rl_paramters);
            originalView.setVisibility(View.GONE);
            video_content_layout.setBackgroundColor(Color.BLACK);
            seekbar_layout.setBackgroundColor(Color.parseColor("#65000000"));
            menu.setVisibility(View.GONE);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //变成竖屏了
//            iv_change_subtitle_type.setVisibility(View.GONE);
            subtitle.setVisibility(View.GONE);
            originalView.setVisibility(View.VISIBLE);
            toorbar.setVisibility(View.VISIBLE);
//            iv_fullscreen.setImageResource(R.drawable.full_screen);
            menu.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams rl_paramters = video_layout.getLayoutParams();
            rl_paramters.height = ScreenUtils.dip2px(context, 210.0f);
            rl_paramters.width = LinearLayout.LayoutParams.MATCH_PARENT;
            video_layout.setLayoutParams(rl_paramters);
            seekbar_layout.setBackgroundColor(Color.WHITE);
            video_content_layout.setBackgroundColor(Color.WHITE);

//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            //setVideoParams(videoView.getmMediaPlayer(), false);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        VideoPlayerActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
   /* @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_TASK_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
            materialDialog.setTitle(R.string.storage_permission);
            materialDialog.setMessage(R.string.storage_permission_content);
            materialDialog.setPositiveButton(R.string.app_sure, new View.OnClickListener() {
                @SuppressLint("NeedOnRequestPermissionsResult")
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(VideoPlayerActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_TASK_CODE);
                    materialDialog.dismiss();
                }
            });
            materialDialog.show();
        }
    }*/

    public void setVideoParams(MediaPlayer mediaPlayer, boolean isLand) {
        //获取surfaceView父布局的参数
        ViewGroup.LayoutParams rl_paramters = video_layout.getLayoutParams();
        //获取SurfaceView的参数
        ViewGroup.LayoutParams sv_paramters = videoView.getLayoutParams();
        //设置宽高比为16/9
        float screen_widthPixels = getResources().getDisplayMetrics().widthPixels;
        float screen_heightPixels = getResources().getDisplayMetrics().widthPixels * 9f / 16f;
        //取消全屏
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (isLand) {
            screen_heightPixels = getResources().getDisplayMetrics().heightPixels;
            //设置全屏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        rl_paramters.width = (int) screen_widthPixels;
        rl_paramters.height = (int) screen_heightPixels;
        //获取MediaPlayer的宽高
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
        float video_por;
        try {
            video_por = videoWidth / videoHeight;
        } catch (Exception e) {
            video_por = 0;
        }
        float screen_por;
        try {
            screen_por = screen_widthPixels / screen_heightPixels;
        } catch (Exception e) {
            screen_por = 0;
        }

        //16:9    16:12
        if (screen_por > video_por) {
            sv_paramters.height = (int) screen_heightPixels;
            sv_paramters.width = (int) (screen_heightPixels * screen_por);
        } else {
            //16:9  19:9
            sv_paramters.width = (int) screen_widthPixels;
            sv_paramters.height = (int) (screen_widthPixels / screen_por);
        }
        video_layout.setLayoutParams(rl_paramters);
        videoView.setLayoutParams(sv_paramters);
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<VideoPlayerActivity> {
        @Override
        public void handleMessageByRef(final VideoPlayerActivity activity, Message msg) {
            switch (msg.what) {
                case 0:
                    activity.currTime.setText(Mathematics.formatTime(activity.videoView.getCurrentPosition() / 1000));
                    activity.seekBar.setProgress(activity.videoView.getCurrentPosition());
                    int current = activity.videoView.getCurrentPosition();
                    if (activity.originalView != null)
                        activity.originalView.synchroParagraph(activity.getCurrentPara(current / 1000.0));
                    activity.handler.sendEmptyMessageDelayed(0, 1000);
                    break;

                case 1:
                    activity.getScore(39);
                    break;
                case 2:
                    activity.getScore(38);
                    break;
            }
        }
    }

    /**
     * 提交视频提交学习记录
     */
    private void commitStudyRecord() {


        endTime = DateFormat.formatTime(Calendar.getInstance().getTime());
        StudyRecord studyRecord = new StudyRecord();
        String userid = AccountManager.getInstance().getUserId();

        studyRecord.setStartTime(startTime);
        studyRecord.setEndTime(endTime);
        studyRecord.setFlag(finishFlag);
        studyRecord.setId(article.getId());
        studyRecord.setLesson(StudyManager.getInstance().getLesson());

        studyRecord.setAllTime(allTime);
        studyRecord.setLrcNum(lrcNum);

        StudyRecordRequest.exeRequest(StudyRecordRequest.getMTVUrl(userid, studyRecord), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
            }

            @Override
            public void onServerError(String msg) {

            }

            @Override
            public void response(Object object) {
                BaseApiEntity apiEntity = (BaseApiEntity) object;
                if (BaseApiEntity.isSuccess(apiEntity)) {
                    if (apiEntity.getMessage().equals("no")) {

                    } else {
                        CustomToast.getInstance().showToast(R.string.study_listen_finish);
                    }
                } else {

                }
            }
        });

    }

    private void share() {


        //ShareSDK.initSDK(this);
        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        weibo.removeAccount(true);
        ShareSDK.removeCookieOnAuthorize(true);

        String text = article.getContent();
        String siteUrl = getShareUrl();
        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字
        // oks.setNotification(R.drawable.ic_launcher,
        // getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(article.getTitle());
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(siteUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(text);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//         oks.setImagePath(getResources().getDrawable(R.drawable.ic_launcher));
        // imageUrl是、We、b图片路径，sina需要开通权限
        oks.setImageUrl(getPicUrl());
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(siteUrl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(text);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(article.getTitle());
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(siteUrl);
        // oks.setDialogMode();
        // oks.setSilent(false);

        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                if (arg2 != null) {
                    Log.e("VideoPlayerActivity", "onError" + arg2.getMessage());
                }
            }

            @Override
            public void onComplete(Platform arg0, int arg1,
                                   HashMap<String, Object> arg2) {
                Log.e("VideoPlayerActivity", "onComplete " + arg1);
                if (AccountManager.getInstance().getUserId() != null) {
                    Message msg = new Message();
                    msg.obj = arg0.getName();


                    if (arg0.getName().equals("QQ")
                            || arg0.getName().equals("Wechat")
                            || arg0.getName().equals("WechatFavorite")) {
                        msg.what = 2;
                    } else if (arg0.getName().equals("QZone")
                            || arg0.getName().equals("WechatMoments")
                            || arg0.getName().equals("SinaWeibo")
                    ) {
                        msg.what = 1;
                    }
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                Log.e("VideoPlayerActivity", "onCancel  " + arg1);
            }
        });
        // 启动分享GUI
        oks.show(context);
    }


    private String getShareUrl() {
        String url;
        switch (StudyManager.getInstance().getApp()) {
            case "201":
            case "218":
                url = "http://m." + ConstantManager.IYUBA_CN + "news.html?type=voa&id=" + article.getId();
                break;
            case "209":
                if ("401".equals(article.getCategory())) {
                    url = "http://m." + ConstantManager.IYUBA_CN + "news.html?type=topvideos&id=" + article.getId();
                } else {
                    url = "http://m." + ConstantManager.IYUBA_CN + "news.html?type=song&id=" + article.getId();
                }
                break;
            case "212":
                url = "http://m." + ConstantManager.IYUBA_CN + "news.html?type=csvoa&id=" + article.getId();
                break;
            case "213":
                url = "http://m." + ConstantManager.IYUBA_CN + "news.html?type=meiyu&id=" + article.getId();
                break;
            case "217":
                url = "http://m." + ConstantManager.IYUBA_CN + "news.html?type=voavideo&id=" + article.getId();
                break;
            case "215":
            case "221":
            case "231":
                url = "http://m." + ConstantManager.IYUBA_CN + "news.html?type=bbc&id=" + article.getId();
                break;
            case "229":
                url = "http://m." + ConstantManager.IYUBA_CN + "news.html?type=ted&id=" + article.getId();
                break;
            default:
                url = "";
                break;
        }
        return url;
    }


    private String getPicUrl() {
        switch (StudyManager.getInstance().getApp()) {
            case "209":
                if (article.getPicUrl().startsWith("http://")) {
                    return article.getPicUrl();
                }
                return "http://staticvip." + ConstantManager.IYUBA_CN + "images/song/" + article.getPicUrl();
            default:
                return article.getPicUrl();
        }
    }

    public void getScore(int type) {
        ScoreOperRequest.exeRequest(ScoreOperRequest.generateUrl(AccountManager.getInstance().getUserId(), article.getId(), type), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {

            }

            @Override
            public void onServerError(String msg) {

            }

            @Override
            public void response(Object object) {
                BaseApiEntity apiEntity = (BaseApiEntity) object;
                switch (apiEntity.getState()) {
                    case BaseApiEntity.SUCCESS:
                        CustomToast.getInstance().showToast(context.getString(R.string.article_share_success, apiEntity.getMessage(), apiEntity.getValue()));
                        break;
                    case BaseApiEntity.FAIL:
                        CustomToast.getInstance().showToast(apiEntity.getMessage());
                        break;
                    case BaseApiEntity.ERROR:
                        break;
                }
            }
        });
    }
}
