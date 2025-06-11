package com.iyuba.music.dubbing;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.listener.OnSeekCompletionListener;
import com.devbrackets.android.exomedia.listener.VideoControlsButtonListener;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.iyuba.music.R;
import com.iyuba.music.activity.MainActivity;
import com.iyuba.music.data.model.VoaText;
import com.iyuba.music.data.model.WavListItem;
import com.iyuba.music.data.remote.IntegralService;
import com.iyuba.music.dubbing.utils.BaseVideoControl;
import com.iyuba.music.dubbing.utils.NormalVideoControl;
import com.iyuba.music.dubbing.utils.PreviewInfoBean;
import com.iyuba.music.dubbing.utils.Share;
import com.iyuba.music.dubbing.utils.StorageUtil;
import com.iyuba.music.dubbing.views.LoadingAdDialog;
import com.iyuba.music.dubbing.views.MyOnTouchListener;
import com.iyuba.music.entity.MusicVoa;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.util.ScreenUtils;
import com.iyuba.music.util.ToastUtil;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import personal.iyuba.personalhomelibrary.utils.StatusBarUtil;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class PreviewActivity extends AppCompatActivity implements PreviewMvpView {

    private static final String IS_FROM_UNRELEASED = "isFromReleased";
    private static final String TIMESTAMP = "timestamp";
    private static final String VOA = "voa";
    private static final String MAP = "map";
    private static final String PREVIEW_INFO = "previewInfo";
    //private static final String RECORD = "RECORD";
    private static final String VOA_TEXT = "VOA_TEXT";


    public static Intent buildIntent(List<VoaText> voaTextList, Context context, MusicVoa voa, Map<Integer, WavListItem> map,
                                     PreviewInfoBean previewInfoBean, /*Record draftRecord,*/ long timeStamp, boolean isFromReleased) {
        Intent intent = new Intent();
        intent.putExtra(VOA, voa);
        intent.putExtra(VOA_TEXT, (Serializable) voaTextList);
        intent.putExtra(MAP, (Serializable) map);
        //intent.putExtra(RECORD, draftRecord);
        intent.putExtra(PREVIEW_INFO, previewInfoBean);
        intent.putExtra(TIMESTAMP, timeStamp);
        intent.putExtra(IS_FROM_UNRELEASED, isFromReleased);
        intent.setClass(context, PreviewActivity.class);
        return intent;
    }

    VideoView mVideoView;
    TextView mPublishShareTv;
    TextView mShareFriendsTv;
    //@BindView(R.id.tv_save_draft)
    //TextView mSaveDraftTv;
    Button mBackBtn;//返回并修改


    ProgressBar mProgressAccuracy;//准确度
    TextView mTextAccuracy;

    ProgressBar mProgressCompleteness;//完整度
    TextView mTextCompleteness;
    ProgressBar mProgressFluence;//流畅度
    TextView mTextFluence;

    PreViewPresenter mPresenter;

    private MusicVoa mVoa;
    private long mTimestamp;
    private HashMap<Integer, WavListItem> map = new HashMap<>();
    private List<VoaText> mVoaTexts;
    private boolean isFromReleased;
    private PreviewInfoBean previewInfoBean;

    private LoadingAdDialog mLoadingDialog;
    private NormalVideoControl mVideoControl;


    private MediaPlayer mAacMediaPlayer;
    private MediaPlayer mMp3MediaPlayer;
    private IjkMediaPlayer dubbingPlayer;

    private Disposable dis;
    private int dubbingPosition = -1;

    private Context mContext;

    private String mUid;
    private String mUserName;

    TextView tv_back_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        mVideoView = findViewById(R.id.video_view);
        mProgressAccuracy = findViewById(R.id.progress_accuracy);
        mTextAccuracy = findViewById(R.id.tv_accuracy);
        mProgressCompleteness = findViewById(R.id.progress_completeness);
        mTextCompleteness = findViewById(R.id.tv_completeness);
        mProgressFluence = findViewById(R.id.progress_fluence);
        mTextFluence = findViewById(R.id.tv_fluence);
        mPublishShareTv = findViewById(R.id.tv_publish_share);
        mShareFriendsTv = findViewById(R.id.tv_share_friends);
        tv_back_home = findViewById(R.id.tv_back_home);
        mBackBtn = findViewById(R.id.back_btn);

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OnClickBackBtn();
            }
        });
        mShareFriendsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickShareFriends();
            }
        });
        mPublishShareTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickReleaseAndShare();
            }
        });
        tv_back_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickBackHome();
            }
        });

        StatusBarUtil.setColor(this, ResourcesCompat.getColor(getResources(), android.R.color.black, getTheme()));
        mContext = this;

        mPresenter = new PreViewPresenter();
        mPresenter.attachView(this);

        mTimestamp = getIntent().getLongExtra(TIMESTAMP, 0);
        mVoa = (MusicVoa) getIntent().getSerializableExtra(VOA);
        mVoaTexts = getIntent().getParcelableArrayListExtra(VOA_TEXT);
        map = (HashMap<Integer, WavListItem>) getIntent().getSerializableExtra(MAP);
        isFromReleased = getIntent().getBooleanExtra(IS_FROM_UNRELEASED, false);
        //draftRecord = getIntent().getParcelableExtra(RECORD);
        previewInfoBean = (PreviewInfoBean) getIntent().getSerializableExtra(PREVIEW_INFO);


        mLoadingDialog = new LoadingAdDialog(this);
        mLoadingDialog.setTitleText(mPresenter.formatTitle("正在发布配音，请耐心等待\n成功后积分 +5"));
        mLoadingDialog.setMessageText(
                mPresenter.formatMessage(
                        previewInfoBean.getWordCount(),
                        previewInfoBean.getAverageScore(),
                        previewInfoBean.getRecordTime()
                )
        );
        mLoadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mPresenter.cancelUpload();
            }
        });
        mLoadingDialog.setRetryOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoadingDialog.retry();
                //onClickReleaseAndShare();
            }
        });

        mUid = AccountManager.getInstance().getUserId();
        mUserName = AccountManager.getInstance().getUserInfo().getUsername();

        setProgressBar();
        try {
            startDubbingOb();
            mAacMediaPlayer = new MediaPlayer();
            dubbingPlayer = new IjkMediaPlayer();
            dubbingPlayer.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(IMediaPlayer iMediaPlayer) {
                    if (dubbingPosition == mVoaTexts.size() - 1) return;
                    startDubbingOb();
                }
            });
            mAacMediaPlayer.setDataSource(mPresenter.getMp3RecordPath(mVoa.voaId, mTimestamp));
            mAacMediaPlayer.prepareAsync();
            mAacMediaPlayer.setVolume(1f, 1f);

            mAacMediaPlayer.setOnPreparedListener(mAacPreparedListener);
            mMp3MediaPlayer = new MediaPlayer();
            mMp3MediaPlayer.setDataSource(mPresenter.getMp3Path(mVoa.voaId));

            mMp3MediaPlayer.prepareAsync();
            mMp3MediaPlayer.setVolume(1f, 1f);

            mMp3MediaPlayer.setOnPreparedListener(mMp3PreparedListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isFromReleased) {
            //mSaveDraftTv.setVisibility(View.GONE);
            mBackBtn.setVisibility(View.GONE);
        } else {
            //mSaveDraftTv.setVisibility(View.VISIBLE);
            mBackBtn.setVisibility(View.VISIBLE);
        }

        initMedia();

        //String textImage = "\uD83D\uDCD5\uD83D\uDCDA\uD83C\uDF32\uD83D\uDC16";
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mBackBtn.setVisibility(View.GONE);
        } else {
            mBackBtn.setVisibility(View.VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setVideoViewParams();
    }

    public void onClickBackHome() {
        //mPresenter.deleteRecord(mVoa.voaId, mTimestamp);
        startMainActivity();
    }

    public void onClickReleaseAndShare() {
        pause();
        try {
            File file1 = getAacRecordFile(mVoa.voaId, mTimestamp);
            File file = new File(file1.getAbsolutePath().replace("aac", "mp3"));
            mPresenter.releaseDubbing(map, mVoa.voaId, mVoa.sound,
                    previewInfoBean.getAverageScore(), mVoa.category, mUid, mUserName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClickShareFriends() {
        pause();
        showShareView("shareString");
    }

    private File getAacRecordFile(int voaId, long timeStamp) {
        return StorageUtil.getAacMergeFile(mContext, voaId, timeStamp);
    }

    public void OnClickBackBtn() {
        finish();
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


    MediaPlayer.OnPreparedListener mAacPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //isAccPrepared = true;
            start();
        }
    };

    MediaPlayer.OnPreparedListener mMp3PreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //isMp3Prepared = true;
            start();
        }
    };


    private void setProgressBar() {
        mProgressAccuracy.setProgress(previewInfoBean.getAverageScore());
        mTextAccuracy.setText(previewInfoBean.getAverageScore() + "");
        mProgressCompleteness.setProgress(previewInfoBean.getCompleteness());
        mTextCompleteness.setText(previewInfoBean.getCompleteness() + "");
        mProgressFluence.setProgress(previewInfoBean.getFluence());
        mTextFluence.setText(previewInfoBean.getFluence() + "");
    }

    public void initMedia() {
        MyOnTouchListener listener = new MyOnTouchListener(this);
        listener.setSingleTapListener(mSingleTapListener);

        mVideoControl = new NormalVideoControl(this);
//        mVideoControl.setPlayPauseImages(R.drawable.play, R.drawable.pause);
        mVideoControl.setMode(BaseVideoControl.Mode.SHOW_MANUAL);
        mVideoControl.setButtonListener(mBtnListener);
        mVideoControl.setBackCallback(new BaseVideoControl.BackCallback() {
            @Override
            public void onBack() {
                finish();
            }
        });
        mVideoView.setControls(mVideoControl);
        mVideoControl.setOnTouchListener(listener);
        mVideoView.setVideoURI(mPresenter.getVideoUri(mVoa.voaId));

        mVideoView.setVolume(0);
        mVideoView.setOnPreparedListener(mVideoPreparedListener);
        mVideoView.setOnCompletionListener(mVideoCompletionListener);
        mVideoView.setOnSeekCompletionListener(new OnSeekCompletionListener() {
            @Override
            public void onSeekComplete() {
                long curPosition = mVideoView.getCurrentPosition();
                if (mMp3MediaPlayer != null) {
                    mMp3MediaPlayer.seekTo((int) curPosition);
                    mAacMediaPlayer.seekTo((int) curPosition);
                }
            }
        });
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

    VideoControlsButtonListener mBtnListener = new VideoControlsButtonListener() {
        @Override
        public boolean onPlayPauseClicked() {
            if (mVideoView == null || mAacMediaPlayer == null || mMp3MediaPlayer == null) {
                return false;
            }

            if (mVideoView.isPlaying() || mAacMediaPlayer.isPlaying() || mMp3MediaPlayer.isPlaying()) {
                pause();
            } else {
                start();
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
    };

    public void start() {
        if (mVideoView != null && mAacMediaPlayer != null && mMp3MediaPlayer != null) {
            mVideoView.start();
            mAacMediaPlayer.start();
            mMp3MediaPlayer.start();
        }
    }

    public void pause() {
        if (mVideoView != null) {
            mVideoView.pause();
        }
        if (mAacMediaPlayer != null) {
            mAacMediaPlayer.pause();
        }
        if (mMp3MediaPlayer != null) {
            mMp3MediaPlayer.pause();
        }
    }

    OnPreparedListener mVideoPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared() {
            //isVideoPrepared = true;
            start();
        }
    };

    OnCompletionListener mVideoCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion() {
            mVideoView.restart();

            mAacMediaPlayer.seekTo(0);
            mAacMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mAacMediaPlayer.pause();
                }
            });

            mMp3MediaPlayer.seekTo(0);
            mMp3MediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMp3MediaPlayer.pause();
                }
            });
        }
    };

    private void startDubbingOb() {
        dis = Observable.interval(50, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (dubbingPosition != findVoaPosition(mVideoView.getCurrentPosition())) {
                            dubbingPosition = findVoaPosition(mVideoView.getCurrentPosition());
                        } else {
                            return;
                        }
                        File file = StorageUtil.getParaRecordAacFile(mContext, mVoa.voaId,
                                dubbingPosition + 1, mTimestamp);
                        if (file.exists()) {
                            dubbingPlayer.reset();
                            dubbingPlayer.setDataSource(file.getAbsolutePath());
                            dubbingPlayer.prepareAsync();
                            dubbingPlayer.start();
                            dis.dispose();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private int findVoaPosition(long currentPosition) {
        int res = -1;
        for (int i = 0; i < mVoaTexts.size(); i++) {
            if (currentPosition > mVoaTexts.get(i).timing * 1000) {
                res = i;
            }
        }
        return res;
    }


    @Override
    public void startLoginActivity() {

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
    public void showShareView(String url) {
        IntegralService integralService = IntegralService.Creator.newIntegralService();
        //shareString = filePath ;
        Share.prepareDubbingMessage(this, mVoa, mPresenter.getShuoshuoId(), mUserName, integralService,
                Integer.parseInt(AccountManager.getInstance().getUserId()));
    }

    @Override
    public void showShareHideReleaseButton() {
        mPublishShareTv.setVisibility(View.GONE);
        mShareFriendsTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoadingDialog() {
        mLoadingDialog.show("1".equals(AccountManager.getInstance().getUserInfo().getVipStatus()));
    }

    @Override
    public void dismissLoadingDialog() {
        mLoadingDialog.dismiss();
    }

    @Override
    public void showPublishSuccess(int resID) {
        mLoadingDialog.showSuccess(mPresenter.formatTitle(getString(resID)));
        dismissPublishDialog();
    }

    void dismissPublishDialog() {
        mLoadingDialog.dismiss();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mLoadingDialog.dismiss();
//            }
//        }, 5 * 1000);
    }

    @Override
    public void showPublishFailure(int resID) {
        mLoadingDialog.showFailure(getString(resID));
        mLoadingDialog.showRetryButton();
    }

    @Override
    public void startMainActivity() {
        Intent intent = new Intent(mContext, MainActivity.class);
        startActivity(intent);
    }
}
