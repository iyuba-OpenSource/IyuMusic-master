package com.iyuba.music.dubbing;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.listener.OnSeekCompletionListener;
import com.devbrackets.android.exomedia.listener.VideoControlsButtonListener;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.iyuba.dlex.bizs.DLManager;
import com.iyuba.music.R;
import com.iyuba.music.data.DataManager;
import com.iyuba.music.data.local.DubDBManager;
import com.iyuba.music.data.local.EvaluateScore;
import com.iyuba.music.data.model.SendEvaluateResponse;
import com.iyuba.music.data.model.VoaText;
import com.iyuba.music.data.model.WavListItem;
import com.iyuba.music.data.remote.WordResponse;
import com.iyuba.music.dubbing.utils.BaseVideoControl;
import com.iyuba.music.dubbing.utils.DubbingVideoControl;
import com.iyuba.music.dubbing.utils.MediaRecordHelper;
import com.iyuba.music.dubbing.utils.PreviewInfoBean;
import com.iyuba.music.dubbing.utils.StorageUtil;
import com.iyuba.music.dubbing.utils.TimeUtil;
import com.iyuba.music.dubbing.utils.VoaMediaUtil;
import com.iyuba.music.dubbing.views.MyOnTouchListener;
import com.iyuba.music.entity.MusicVoa;
import com.iyuba.music.event.DownloadEvent;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.util.ChangePropery;
import com.iyuba.music.util.ScreenUtils;
import com.iyuba.music.util.ToastUtil;
import com.iyuba.play.ExtendedPlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import personal.iyuba.personalhomelibrary.utils.StatusBarUtil;
import timber.log.Timber;

/**
 * 主页面 -配音 - 配音主页面 - 配音
 */
public class DubbingActivity extends AppCompatActivity implements DubbingMvpView {
    private static final String VOA = "voa_data";
    private static final String TIMESTAMP = "timestamp";
    private String playUrl = "http://dict.youdao.com/dictvoice?audio=";


    TextView word;
    TextView pron;
    TextView def;
    CardView wordRoot;

    public VideoView mVideoView;
    public RecyclerView mRecyclerView;
    public View mLoadingView;
    public TextView mLoadingTv;
    public TextView mPreview;

    public FrameLayout mFlLoadError;

    private MediaPlayer mAccAudioPlayer;//背景音播放器
    private ExtendedPlayer mRecordPlayer;
    private MediaPlayer wordPlayer;

    private DubbingVideoControl mVideoControl;
    public DubbingPresenter mPresenter;
    public DubbingAdapter mAdapter;
    private List<VoaText> mVoaTextList;
    private PreviewInfoBean previewInfoBean;

    private MusicVoa mTalkLesson;
    private Map<Integer, WavListItem> map = new HashMap<>();

    public long mDuration;
    private long mTimeStamp;
    public static boolean isSend = false;
    private Context mContext;

    private NewScoreCallback mNewScoreCallback;
    private boolean mIsFirstIn = true;
    private MediaRecordHelper mediaRecordHelper = new MediaRecordHelper();
    //    private AudioEncoder mAudioEncoder;//录音
    private String mUid;
    private int delayTime;
    private boolean isSeekToItem;
    private String wordString;
    private AccountManager accountManager;

    ImageView ivAudio;

    Button dialog_btn_addword, close;


    public static Intent buildIntent(Context context, MusicVoa voa, long timeStamp) {

        Intent intent = new Intent();
        intent.setClass(context, DubbingActivity.class);
        intent.putExtra(VOA, voa);
        intent.putExtra(TIMESTAMP, timeStamp);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ChangePropery.setAppConfig(this);
        setContentView(R.layout.activity_dubbing);

        mLoadingView = findViewById(R.id.loading_view);
        mVideoView = findViewById(R.id.video_view_dub);
        mPreview = findViewById(R.id.preview_dubbing);
        mRecyclerView = findViewById(R.id.recycler_view);
        mFlLoadError = findViewById(R.id.fl_load_error);
        mLoadingTv = findViewById(R.id.loading_tv);
        wordRoot = findViewById(R.id.jiexi_root);
        word = findViewById(R.id.word);
        pron = findViewById(R.id.pron);
        ivAudio = findViewById(R.id.iv_audio);
        def = findViewById(R.id.def);
        dialog_btn_addword = findViewById(R.id.dialog_btn_addword);
        close = findViewById(R.id.close);

        ivAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onAudioClicked();
            }
        });
        dialog_btn_addword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onAddClicked();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onViewClicked();
            }
        });


        StatusBarUtil.setColor(this, ResourcesCompat.getColor(getResources(), android.R.color.black, getTheme()));

        mTalkLesson = (MusicVoa) getIntent().getSerializableExtra(VOA);
        mTimeStamp = getIntent().getLongExtra(TIMESTAMP, 0);

        DataManager mDataManager = DataManager.getInstance();
        accountManager = AccountManager.getInstance();//单例引用对象了
        DLManager dlManager = DLManager.getInstance();//单例引用对象了
        mPresenter = new DubbingPresenter(dlManager);
        mPresenter.attachView(this);
        mPresenter.init(mTalkLesson);
        mContext = this;
        previewInfoBean = new PreviewInfoBean();
        mUid = accountManager.getUserId();

        initMedia();
        //initAnimation();
        initRecyclerView();
        initListener();
        //mPresenter.getVoaTexts(mTalkLesson.voaId());
        mPresenter.syncVoaTexts(mTalkLesson.voaId);
        //showEmptyTexts();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onBackPressed() {
        if (mLoadingView.getVisibility() == View.VISIBLE) {
            mLoadingView.setVisibility(View.GONE);
            mPresenter.cancelDownload();
            showToast("下载已经取消。");
            return;
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
        handler.removeCallbacksAndMessages(null);
        if (mAccAudioPlayer.isPlaying()) {
            mAccAudioPlayer.pause();
        }
        mAccAudioPlayer.release();
        EventBus.getDefault().unregister(this);
    }

    private void initRecyclerView() {
        mAdapter = new DubbingAdapter(mPresenter);
        mAdapter.setPlayVideoCallback(mPlayVideoCallback);
        mAdapter.setPlayRecordCallback(mPlayRecordCallback);
        mAdapter.setRecordingCallback(mRecordingCallback);
        mAdapter.setScoreCallback(new DubbingAdapter.ScoreCallback() {
            @Override
            public void onResult(int pos, int score, int fluec, String url) {
                previewInfoBean.setSentenceScore(pos, score);
                previewInfoBean.setSentenceFluent(pos, fluec);
                previewInfoBean.setSentenceUrl(pos, url);
                //存入数据库 流畅度
                DubDBManager.getInstance().setFluent("" + mTalkLesson.voaId, mUid, String.valueOf(pos), fluec, url);
            }
        });
        mAdapter.setTimeStamp(mTimeStamp);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        //如果Item够简单，高度是确定的，打开FixSize将提高性能。
        //mRecyclerView.setHasFixedSize(true);
        //设置Item默认动画，加也行，不加也行。
        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initListener() {
        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StorageUtil.hasRecordFile(getApplicationContext(), mTalkLesson.voaId, mTimeStamp)) {
                    pause();
                    startPreviewActivity();
                } else {
                    showToast("还没有配音哦，点击话筒试试吧");
                }

            }
        });
        mFlLoadError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.syncVoaTexts(mTalkLesson.voaId);
            }
        });
    }

    public void onViewClicked() {
        wordRoot.setVisibility(View.GONE);
    }

    public void onAddClicked() {
        if (!accountManager.checkUserLogin()) {
            ToastUtil.showToast(this, "请先登录");
            return;
        }
        List<String> words = Arrays.asList(wordString);
        mPresenter.insertWords(Integer.parseInt(accountManager.getUserId()), words);
    }

    public void onAudioClicked() {
        try {
            wordPlayer.reset();
            wordPlayer.setDataSource(playUrl + wordString);
            wordPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    DubbingAdapter.PlayVideoCallback mPlayVideoCallback = new DubbingAdapter.PlayVideoCallback() {
        @Override
        public void start(final VoaText voaText) {
            handler.removeCallbacksAndMessages(null);
            startPlayVideo(voaText);
        }

        @Override
        public boolean isPlaying() {
            return isPlayVideo();
        }

        @Override
        public int getCurPosition() {
            return (int) mVideoView.getCurrentPosition();
        }

        @Override
        public void stop() {
            pause();
        }
    };

    DubbingAdapter.PlayRecordCallback mPlayRecordCallback = new DubbingAdapter.PlayRecordCallback() {
        @Override
        public void start(final VoaText voaText) {
            startPlayRecord(voaText);
        }

        @Override
        public void stop() {
            pause();
            handler.removeCallbacksAndMessages(null);
            mAdapter.mOperateHolder.iPlay.setVisibility(View.VISIBLE);
            mAdapter.mOperateHolder.iPause.setVisibility(View.INVISIBLE);
        }

        @Override
        public int getLength() {
            return (int) mRecordPlayer.getDuration();
        }
    };

//    OnPreparedListener mOnPreparedListener = new OnPreparedListener() {
//        @Override
//        public void onPrepared() {
//            if (mIsFirstIn) {
//                mIsFirstIn = false;
//                mAdapter.repeatPlayVoaText(mRecyclerView, mVoaTextList.get(0));
//            }
//        }
//    };

    /**
     * 保存草稿
     */
    private void saveDraft() {
        // Record record = getDraftRecord();
        // mPresenter.saveRecord(record);
    }

    private void initMedia() {
//        mAudioEncoder = new AudioEncoder();
        mAccAudioPlayer = new MediaPlayer();
        wordPlayer = new MediaPlayer();
        wordPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mRecordPlayer = new ExtendedPlayer(getApplicationContext());
        MyOnTouchListener listener = new MyOnTouchListener(this);
        listener.setSingleTapListener(mSingleTapListener);
        mVideoControl = new DubbingVideoControl(mContext);
        mVideoControl.setMode(BaseVideoControl.Mode.SHOW_MANUAL);
        mVideoControl.setFullScreenBtnVisible(false);
        mVideoControl.setButtonListener(new VideoControlsButtonListener() {
            @Override
            public boolean onPlayPauseClicked() {
                if (mVideoView.isPlaying()) {
                    pause();
                } else {
                    mVideoView.start();
                    mAdapter.repeatPlayVoaText(mRecyclerView, mAdapter.getOperateVoaText());
                }
                return true;
            }

            @Override
            public boolean onPreviousClicked() {
                return false;
            }

            @Override
            public boolean onNextClicked() {
                return false;
            }

            @Override
            public boolean onRewindClicked() {
                return false;
            }

            @Override
            public boolean onFastForwardClicked() {
                return false;
            }
        });
        mVideoControl.setBackCallback(new BaseVideoControl.BackCallback() {
            @Override
            public void onBack() {
                try {
                    if (mPresenter.checkFileExist()) {
                        finish();
                        if (!isSend) {
                            int finishNum = mPresenter.getFinishNum(mTalkLesson.voaId, mTimeStamp);
                            if (finishNum > 0) saveDraft();
                        } else {
                            finish();
                        }
                    } else {
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mVideoView.setControls(mVideoControl);

        mVideoView.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion() {
                try {
                    mVideoView.setVideoURI(Uri.fromFile(StorageUtil.getVideoFile(DubbingActivity.this, mTalkLesson.voaId)));
                    mAccAudioPlayer.reset();
//                    String path = StorageUtil.getAudioFile(DubbingActivity.this,
//                            mTalkLesson.voaId).getAbsolutePath();
                    String soundUrl = VoaMediaUtil.getAudioUrl(mTalkLesson.sound);
                    Log.e("DubbingActivity", "mAccAudioPlayer soundUrl = " + soundUrl);
                    mAccAudioPlayer.setDataSource(soundUrl.replace(".mp3", "_s.mp3"));
                    mAccAudioPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mVideoView.setOnSeekCompletionListener(new OnSeekCompletionListener() {
            @Override
            public void onSeekComplete() {

            }
        });
        mVideoControl.setOnTouchListener(listener);
        checkVideoAndMedia();


        mVideoView.setOnSeekCompletionListener(new OnSeekCompletionListener() {
            @Override
            public void onSeekComplete() {
                if (isSeekToItem) {
                    isSeekToItem = false;
                    Timber.e("评测" + "原视频播放暂停");
                    handler.sendEmptyMessageDelayed(1, delayTime);
                }
                startRecording();
            }
        });
    }

    private void startRecording() {
        if (mAdapter.isRecording && !mediaRecordHelper.isRecording) {
            try {
                Timber.d("record" + "startRecording: " + System.currentTimeMillis());
                mediaRecordHelper.recorder_Media();
            } catch (Exception e) {
                e.printStackTrace();
                //Timber.e("评测 配音开启失败");
            }
        }
//        if (mAdapter.isRecording && !mAudioEncoder.isRecording()) {
//            try {
//                Timber.d("record" + "startRecording: " + System.currentTimeMillis());
//                mAudioEncoder.prepare();
//                //Timber.e("评测 seekTo完成 录音开始");
//                mAudioEncoder.start();
//            } catch (Exception e) {
//                e.printStackTrace();
//                //Timber.e("评测 配音开启失败");
//            }
//        }
    }

    MyOnTouchListener.SingleTapListener mSingleTapListener = new MyOnTouchListener.SingleTapListener() {
        @Override
        public void onSingleTap() {
            if (mVideoControl != null) {
                if (mVideoControl.getControlVisibility() == View.GONE) {
                    mVideoControl.show();
                    if (mVideoView.isPlaying()) {
                        mVideoControl.hideDelayed(VideoControls.DEFAULT_CONTROL_HIDE_DELAY);
                    }
                } else {
                    mVideoControl.hideDelayed(0);
                }
            }
        }
    };

    public void checkVideoAndMedia() {
        if (mPresenter.checkFileExist()) {
            mLoadingView.setVisibility(View.GONE);
            setVideoAndAudio();
            Timber.e("下载完成");
        } else {
            Timber.e("下载未完成");
            //下载音频视频
            mLoadingView.setVisibility(View.VISIBLE);
            mLoadingTv.setText("正在下载");
            mPresenter.download();
        }
    }

    public void setVideoAndAudio() {
        try {
            File file = StorageUtil.getVideoFile(this, mTalkLesson.voaId);
            mVideoView.setVideoURI(Uri.fromFile(file));
            String backGroundAudio = StorageUtil.getAudioFile(this,
                    mTalkLesson.voaId).getAbsolutePath();
            Timber.e("背景音地址" + backGroundAudio);
//            mAccAudioPlayer.setDataSource(backGroundAudio);
            Log.e("DubbingActivity", "mAccAudioPlayer sound = " + backGroundAudio);
            String soundUrl = VoaMediaUtil.getAudioUrl(mTalkLesson.sound);
            Log.e("DubbingActivity", "mAccAudioPlayer soundUrl = " + soundUrl);
            mAccAudioPlayer.setDataSource(soundUrl.replace(".mp3", "_s.mp3"));
            mAccAudioPlayer.prepare();
            mVideoView.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared() {
                    mDuration = mVideoView.getDuration();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void startPlayVideo(VoaText voaText) {
        pause();
        handler.removeCallbacksAndMessages(null);
        mVideoView.setVolume(1);
        if (voaText != null) {
            mVideoView.seekTo(TimeUtil.secToMilliSec(voaText.timing));
            isSeekToItem = true;
            delayTime = (int) ((voaText.endTiming - voaText.timing) * 1000);
            Timber.e("持续时间" + delayTime);
        }
        mVideoView.start();
    }

    public boolean isPlayVideo() {
        return mVideoView.isPlaying();
    }


    public void startPlayRecord(VoaText voaText) {
        try {
//            mRecordPlayer.reset();
//            mRecordPlayer.setDataSource(
//                    StorageUtil.getParaRecordAacFile(getApplicationContext(),
//                            voaText.getVoaId(), voaText.paraId, mTimeStamp).getAbsolutePath());
//            mRecordPlayer.prepareAsync();
            Log.e("DubbingActivity", "startPlayRecord voaText.pathLocal = " + voaText.pathLocal);
            mRecordPlayer.initialize(voaText.pathLocal);
            mRecordPlayer.setOnCompletionListener(mp -> {
                mVideoView.pause();
                mPlayRecordCallback.stop();
            });
            mRecordPlayer.prepareAndPlay();
            mVideoView.setVolume(0);
//            mRecordPlayer.setVolume(1.0f, 1.0f);
            seekTo(TimeUtil.secToMilliSec(voaText.timing));
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (!mVideoView.isPlaying()) {
            mVideoView.start();
        }
        if (!mAccAudioPlayer.isPlaying()) {
            mAccAudioPlayer.start();
        }
        if (!mRecordPlayer.isPlaying()) {
            mRecordPlayer.start();
        }
    }

    public void seekTo(int millSec) {
        mVideoView.seekTo(millSec);
        mAccAudioPlayer.seekTo(millSec);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    pause();
                    break;
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadFinish(DownloadEvent downloadEvent) {
        switch (downloadEvent.status) {
            case DownloadEvent.Status.FINISH:
                DubDBManager.getInstance().setDownload("" + mTalkLesson.voaId, mUid, mTalkLesson.title,
                        mTalkLesson.descCn, mTalkLesson.pic, "" + mTalkLesson.category);
                mLoadingView.setVisibility(View.GONE);
                setVideoAndAudio();
                break;
            case DownloadEvent.Status.DOWNLOADING:
                mLoadingTv.setText(downloadEvent.msg);
                break;
            default:
                break;
        }
    }

    @Override
    public void showVoaTexts(List<VoaText> voaTextList) {
        //获取 课程举止列表
        Timber.e("获取数据成功" + voaTextList.size());
        mFlLoadError.setVisibility(View.GONE);
        if (TextUtils.isEmpty(mUid) || mUid.equals("0")) {
            showToast("未登录！");
        }
        List<EvaluateScore> list = DubDBManager.getInstance().getEvaluate("" + mTalkLesson.voaId, mUid);
        if (list != null && list.size() > 0) {
            for (EvaluateScore score : list) {
                for (VoaText voaText : voaTextList) {
                    if (score.paraId.equals(String.valueOf(voaText.paraId))) {
                        voaText.words = DubDBManager.getInstance().getEvWord("" + mTalkLesson.voaId, mUid, score.paraId);
                        Timber.e("查找" + mTalkLesson.voaId + "=" + mUid + "=" + score.paraId + "=" + voaText.words.size());
                        voaText.isEvaluate = true;
                        voaText.isDataBase = true;
                        voaText.setIscore(true);
                        voaText.setIsshowbq(true);
                        voaText.setScore(Integer.parseInt(score.score));
                        voaText.progress = score.progress;
                        voaText.progress2 = score.progress2;
                        voaText.pathLocal = score.path;
                    }
                }
                if (score.fluent != 0) {
                    //获取 流畅度
                    previewInfoBean.setSentenceScore(score.getParaId(), score.getScore());
                    previewInfoBean.setSentenceFluent(score.getParaId(), score.fluent);
                    previewInfoBean.setSentenceUrl(score.getParaId(), score.url);
                }
                if (score.endTime != 0) {
                    WavListItem item = new WavListItem();
                    item.setUrl(score.url);
                    item.setBeginTime(score.beginTime);
                    item.setEndTime(score.endTime);
                    item.setDuration(score.duration);
                    int paraId = Integer.parseInt(score.paraId);
                    item.setIndex(paraId);
                    buildMap(paraId, item);
                }
            }
        }

        for (VoaText voaText : voaTextList) {
            voaText.setVoaId(mTalkLesson.voaId);
        }
        mVoaTextList = voaTextList;
        //mVideoView.setOnPreparedListener(mOnPreparedListener);
        mAdapter.setList(voaTextList);
        mAdapter.mEvaluateNum = list.size();
        mAdapter.notifyDataSetChanged();
        //studyRecordUpdateUtil.getStudyRecord().setWordCount(voaTextList);
        previewInfoBean.setVoaTexts(voaTextList);
        //mPresenter.checkDraftExist(mTimeStamp);
    }

    @Override
    public void showEmptyTexts() {
        mAdapter.setList(Collections.<VoaText>emptyList());
        mAdapter.notifyDataSetChanged();
        mFlLoadError.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissDubbingDialog() {

    }

    @Override
    public void showMergeDialog() {

    }

    @Override
    public void dismissMergeDialog() {

    }

    //合成之后，开始预览
    @Override
    public void startPreviewActivity() {
        stopStudyRecord("1");
        previewInfoBean.initIndexList();
        //Record record = getDraftRecord();
        if (map == null || map.size() == 0) {
            showToast("textList 为空");
            return;
        }
        Intent intent = PreviewActivity.buildIntent(mVoaTextList, this, mTalkLesson, map,
                previewInfoBean, mTimeStamp, false);
        startActivity(intent);

//        String json = new Gson().toJson(map);
//        Timber.d("json数据" + json);

    }

    @Override
    public void showToast(int resId) {
        ToastUtil.showToast(mContext, getResources().getString(resId));
    }

    @Override
    public void showToast(String resId) {
        ToastUtil.showToast(mContext, resId);
    }

    @Override
    public void pause() {
        if (mVideoView.isPlaying()) {
            pauseVideoView();
        }
        try {
            if (mAccAudioPlayer.isPlaying()) {
                mAccAudioPlayer.pause();
            }
            if (mRecordPlayer.isPlaying()) {
                mRecordPlayer.pause();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            ToastUtil.showToast(mContext, "播放器停止异常！");
        }
    }

    @Override
    public void showWord(WordResponse bean) {
        showWordView(bean);
    }

    private void showWordView(WordResponse bean) {
        pauseVideoView();
        wordString = bean.getKey();
        wordRoot.startAnimation(initAnimation());
        wordRoot.setVisibility(View.VISIBLE);
        wordRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        word.setText(bean.getKey());
        def.setText(bean.getDef());
        pron.setText(String.format("[%s]", bean.getPron()));
    }


    public TranslateAnimation initAnimation() {
        TranslateAnimation animation = new TranslateAnimation(-300, 0, 0, 0);
        animation.setDuration(200);
        return animation;
    }

    //评测接口返回
    @Override
    public void getEvaluateResponse(SendEvaluateResponse response, int paraId, File file, int progress, int secondaryProgress) {
        //Timber.d("评测请求成功" + response.getTotal_score());

        WavListItem item = new WavListItem();
        item.setUrl(response.getURL());
        item.setBeginTime(mVoaTextList.get(paraId - 1).timing);
        if (paraId < mVoaTextList.size()) {
            item.setEndTime(mVoaTextList.get(paraId).timing);
        } else {
            item.setEndTime(mVoaTextList.get(paraId - 1).endTiming);
        }
        float duration = getAudioFileVoiceTime(file.getAbsolutePath()) / 1000.0f;
        String temp = String.format("%.1f", duration);
        item.setDuration(Float.parseFloat(temp));//获取录音文件长度
        item.setIndex(paraId);
        buildMap(paraId, item);
        Log.e("DubbingActivity", "getEvaluateResponse mediaRecordHelper.getFilePath() = " + file.getAbsolutePath());
        int score = (int) (Math.sqrt(Float.parseFloat(response.getTotal_score()) * 2000));
        DubDBManager dbManager = DubDBManager.getInstance();
        dbManager.setEvaluate("" + mTalkLesson.voaId, mUid, file.getAbsolutePath(), String.valueOf(paraId), String.valueOf(score), progress, secondaryProgress);
        dbManager.setEvaluateTime("" + mTalkLesson.voaId, mUid, String.valueOf(paraId), item.getBeginTime(), item.getEndTime(), item.getDuration());
        for (SendEvaluateResponse.WordsBean bean : response.getWords()) {
            dbManager.setEvWord("" + mTalkLesson.voaId, mUid, String.valueOf(paraId), bean);
        }
        mNewScoreCallback.onResult(paraId, score, response);//开始计算

        int position = Math.max(paraId - 1, 0);
        if (!mVoaTextList.get(position).isEvaluate) {
            mAdapter.mEvaluateNum++;
            mVoaTextList.get(position).isEvaluate = true;
        }
        mVoaTextList.get(position).isDataBase = false;
        mVoaTextList.get(position).pathLocal = file.getAbsolutePath();
    }

    @Override
    public void evaluateError(String message) {
        showToast(message);
        mAdapter.notifyDataSetChanged();
    }


    private void pauseVideoView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mVideoView.pause();
            }
        });
        stopStudyRecord("0");
    }

    public void stopStudyRecord(String flag) {
        //studyRecordUpdateUtil.stopStudyRecord(getApplicationContext(), mPresenter.checkLogin(), flag, mPresenter.getUploadStudyRecordService());
    }

    private void setVideoViewParams() {
        ViewGroup.LayoutParams lp = mVideoView.getLayoutParams();
        int[] screenSize = ScreenUtils.getScreenSize(this);
        lp.width = screenSize[0];
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lp.height = screenSize[1]; // 16 : 9
        } else {
            lp.height = (int) (lp.width * 0.5625);
        }
        mVideoView.setLayoutParams(lp);
    }

    interface NewScoreCallback {
        void onResult(int pos, int score, SendEvaluateResponse beans);

        void onError(String errorMessage);
    }

    void setNewScoreCallback(NewScoreCallback mNewScoreCallback) {
        this.mNewScoreCallback = mNewScoreCallback;
    }

    /**
     * 评测监听
     */
    DubbingAdapter.RecordingCallback mRecordingCallback = new DubbingAdapter.RecordingCallback() {
        @Override
        public void init(String path) {
            //Timber.e("评测文件路径" + path);
            File file = new File(path);
            try {
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            initRecord(path);
        }

        @Override
        public void start(final VoaText voaText) {
            startRecording(voaText);
        }

        @Override
        public boolean isRecording() {
            return isOnRecording();
        }

        @Override
        public void stop() {
            stopRecording();
        }

        @Override
        public void convert(int paraId, List<VoaText> list) {
//            String saveFile = mAudioEncoder.getmSavePath();
//            File flacFile = new File(saveFile);
        }

        @Override
        public void upload(final int paraId, List<VoaText> list, int progress, int secondaryProgress) {
//            String saveFile = mAudioEncoder.getmSavePath();
            String saveFile = mediaRecordHelper.getFilePath();
            final File flakFile = new File(saveFile);
            boolean isExists = flakFile.exists();
            Timber.e("评测网络请求开始，录音结束" + saveFile + "存在" + isExists);
            if (flakFile.length() != 0 && (paraId - 1) >= 0 && (paraId - 1) < list.size()) {
                mPresenter.uploadSentence(list.get(paraId - 1).sentence, paraId, list.get(paraId - 1).getVoaId(),
                        paraId, "music", mUid, flakFile, saveFile, progress, secondaryProgress);//type 原来是talkshow
            } else {
                mediaRecordHelper.stop_record();
//                mAudioEncoder.stop();
                showToast("空的文件！，不能进行评测");
            }
        }
    };

    private void buildMap(int index, WavListItem item) {
        map.put(index, item);
    }

    public long getAudioFileVoiceTime(String filePath) {
        long mediaPlayerDuration = 0L;
        if (filePath == null || filePath.isEmpty()) {
            return 0;
        }
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayerDuration = mediaPlayer.getDuration();
        } catch (IOException ioException) {
//            LogUtil.i("tag", ioException.getMessage());
        }
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        return mediaPlayerDuration;
    }

    public void initRecord(String path) {
        //Timber.e("评测 录音初始化");
//        mAudioEncoder.setSavePath(path);
        mediaRecordHelper.setFilePath(path);
    }

    //开始评测
    public void startRecording(VoaText voaText) {
        //Timber.e("评测 录音准备");
        try {
            mVideoView.setVolume(0);
            int seekToVideo = TimeUtil.secToMilliSec(voaText.timing);
            //Timber.e("评测seekTo Time" + seekToVideo);
            mVideoView.seekTo(seekToVideo);//seekTo之后进行评测！！
            //mLoadingView.setVisibility(View.VISIBLE);
            mAccAudioPlayer.seekTo(TimeUtil.secToMilliSec(voaText.timing));
            mVideoView.start();
            startRecording();

            if (!mAccAudioPlayer.isPlaying()) {
                mAccAudioPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("配音出现异常");
        }
    }


    public boolean isOnRecording() {
        return mediaRecordHelper.isRecording; //mAudioEncoder.isRecording();
    }

    public void stopRecording() {
        if (mediaRecordHelper.isRecording) {
            Timber.d("record" + "stopRecording: 2" + System.currentTimeMillis());
            mediaRecordHelper.stop_record();
            mVideoView.setOnSeekCompletionListener(null);
            mAccAudioPlayer.pause();
            pauseVideoView();
        }
//        if (mAudioEncoder.isRecording()) {
//            Timber.d("record" + "stopRecording: 2" + System.currentTimeMillis());
//            mAudioEncoder.stop();
//            //mVideoView.setOnSeekCompletionListener(null);
//            mAccAudioPlayer.pause();
//            pauseVideoView();
//        }
    }

}
