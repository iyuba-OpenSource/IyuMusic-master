package com.iyuba.music.dubbing;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.listener.VideoControlsButtonListener;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.iyuba.music.R;
import com.iyuba.music.data.Constant;
import com.iyuba.music.data.DataManager;
import com.iyuba.music.data.local.DubDBManager;
import com.iyuba.music.data.model.Ranking;
import com.iyuba.music.data.model.TalkLesson;
import com.iyuba.music.dubbing.comment.CommentFragment;
import com.iyuba.music.dubbing.utils.BaseVideoControl;
import com.iyuba.music.dubbing.utils.NormalVideoControl;
import com.iyuba.music.dubbing.utils.ThumbAction;
import com.iyuba.music.dubbing.views.CircularImageView;
import com.iyuba.music.dubbing.views.MyOnTouchListener;
import com.iyuba.music.entity.MusicVoa;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.util.ScreenUtils;
import com.iyuba.music.util.ToastUtil;
import com.iyuba.music.widget.CustomToast;

import personal.iyuba.personalhomelibrary.utils.StatusBarUtil;

public class WatchDubbingActivity extends AppCompatActivity implements WatchDubbingMvpView {

    private static final String RANKING = "ranking";
    private static final String VOA = "voa";
    private static final String UID = "uid";

    public static Intent buildIntent(Context context, Ranking ranking, MusicVoa voa, Integer uid) {
        Intent intent = new Intent();
        intent.setClass(context, WatchDubbingActivity.class);
        intent.putExtra(RANKING, ranking);
        intent.putExtra(VOA, voa);
        intent.putExtra(UID, uid);
        return intent;
    }

    VideoView mVideoView;
    CircularImageView mPhotoIv;
    ImageView mThumbIv;
    TextView mThumbNumTv;
    TextView mUserNameTv;
    TextView mDateTv;
    TextView mPlayTimes;

//    @BindView(R.id.download_layout)
//    View mDownloadView;
//    @BindView(R.id.iv_download)
//    ImageView mDownloadViewIv;
//    @BindView(R.id.tv_download)
//    TextView mDownloadViewTv;

    View mLoadingView;
    TextView mLoadingTv;

    private boolean isInterrupted = false;
    private Ranking mRanking;
    private MusicVoa mVoa;


    //private DownloadDialog mDownloadDialog;

    //public WatchDownloadPresenter mDownloadPresenter;
    WatchDubbingPresenter mPresenter;
    //UploadStudyRecordUtil uploadStudyRecordUtil;
    private NormalVideoControl mVideoControl;

    int uid;
    private String myUid;
    private Context mContext;

    RelativeLayout thumbLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_dubbing);

        mVideoView = findViewById(R.id.video_view);
        mPhotoIv = findViewById(R.id.photo_iv);
        thumbLayout = findViewById(R.id.thumb_layout);
        mThumbIv = findViewById(R.id.thumb_iv);
        mThumbNumTv = findViewById(R.id.thumb_num_tv);
        mUserNameTv = findViewById(R.id.user_name_tv);
        mDateTv = findViewById(R.id.date_tv);
        mPlayTimes = findViewById(R.id.play_times_tv);
        mLoadingTv = findViewById(R.id.loading_tv);
        mLoadingView = findViewById(R.id.loading_view);

        mLoadingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        thumbLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onThumbClick();
            }
        });

        StatusBarUtil.setColor(this, ResourcesCompat.getColor(getResources(), android.R.color.black, getTheme()));

        mRanking = getIntent().getParcelableExtra(RANKING);
        mVoa = (MusicVoa) getIntent().getSerializableExtra(VOA);
        uid = getIntent().getIntExtra(UID, 0);
        mContext = this;

        myUid = AccountManager.getInstance().getUserId();
        int myId = Integer.parseInt(AccountManager.getInstance().getUserId());
        mPresenter = new WatchDubbingPresenter(DataManager.getInstance(), myId);
        mPresenter.attachView(this);
        initVideo();
        initView();
        initFragment();

        if (DubDBManager.getInstance().isAgree(myUid, mRanking.getID())) {
            mThumbIv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_thumb_yellow));
        }
    }


    private void initVideo() {
        MyOnTouchListener listener = new MyOnTouchListener(this);
        listener.setSingleTapListener(mSingleTapListener);

        mVideoControl = new NormalVideoControl(this);
//        mVideoControl.setPlayPauseImages(R.drawable.play, R.drawable.pause);
        mVideoControl.setMode(BaseVideoControl.Mode.SHOW_AUTO);
        mVideoControl.setBackCallback(new BaseVideoControl.BackCallback() {
            @Override
            public void onBack() {
                finish();
            }
        });
        mVideoControl.setButtonListener(new VideoControlsButtonListener() {
            @Override
            public boolean onPlayPauseClicked() {
                if (mVideoView == null) {
                    return false;
                }

                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    stopVideoView("0");
                } else {
                    mVideoView.start();
                    //EventBus.getDefault().post(new StopEvent(StopEvent.SOURCE.VOICE));
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
        mVideoView.setControls(mVideoControl);
        mVideoControl.setOnTouchListener(listener);
        mVideoView.setVideoPath(Constant.getNewDubbingUrl(mRanking.videoUrl));
        mVideoView.setOnPreparedListener(mVideoPreparedListener);
        mVideoView.setOnCompletionListener(mVideoCompletionListener);
    }


    OnPreparedListener mVideoPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared() {
            mVideoView.start();
        }
    };

    OnCompletionListener mVideoCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion() {
            mVideoView.restart();
            mVideoView.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared() {
                    mVideoView.pause();
                    stopVideoView("1");
                }
            });
        }
    };

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

    private void stopVideoView(String flag) {
//        uploadStudyRecordUtil.stopStudyRecord(getApplicationContext(), mPresenter.checkLogin(),
//                flag, mPresenter.getUploadService());
    }

    private void initView() {
        if (mRanking != null) {
            if (mRanking.agreeNum == 0) {
                mRanking.agreeNum = mRanking.agreeCount;
            }
            // mPresenter.checkThumb(mRanking.id);

//            ImageLoader.getInstance().displayImage(mRanking.imgSrc, mPhotoIv);
            Glide.with(this).load(mRanking.imgSrc).into(mPhotoIv);
            mThumbNumTv.setText(String.valueOf(mRanking.agreeNum));
            mUserNameTv.setText(mRanking.userName);
            mDateTv.setText(mRanking.createDate);
        }
    }

    private void initFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container,
                CommentFragment.newInstance(mVoa, mRanking.id, mRanking.userName, mRanking.videoUrl));
        transaction.commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setVideoViewParams();
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

    public void onThumbClick() {
        if (!AccountManager.getInstance().checkUserLogin()) {
            CustomToast.getInstance().showToast(R.string.article_thumb_request);
            return;
        }
        if (DubDBManager.getInstance().isAgree(myUid, mRanking.getID())) {
            //点赞前本地数据库判断
            ToastUtil.showToast(mContext, "您已经点过赞了~");
        } else {
            mPresenter.doThumb(mRanking.id);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
        stopVideoView("0");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mDownloadPresenter.detachView();
        //EventBus.getDefault().unregister(this);
        mPresenter.detachView();
    }

    @Override
    public void onBackPressed() {
        if (mVideoControl.isFullScreen()) {
            mVideoControl.exitFullScreen();
        } else {
            finish();
        }
    }

    @Override
    public void updateThumbIv(int action) { //点赞后操作
        int resId = action == ThumbAction.THUMB ? R.drawable.ic_thumb_yellow : R.drawable.thumb_gray;
        mThumbIv.setImageResource(resId);
    }

    @Override
    public void updateThumbNumTv(String id) {//点赞后操作
        int thumbNum = Integer.parseInt(mThumbNumTv.getText().toString());
        mThumbNumTv.setText(String.valueOf(thumbNum + 1));
        //数据库操作
        DubDBManager.getInstance().setAgree(myUid, id);
    }

    @Override
    public void showToast(int resId) {
        ToastUtil.showToast(mContext, getString(resId));
    }
}
