package com.iyuba.music.dubbing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.google.android.material.tabs.TabLayout;
import com.iyuba.dlex.bizs.DLManager;
import com.iyuba.headlinelibrary.data.model.VideoMiniData;
import com.iyuba.music.R;
import com.iyuba.music.data.Constant;
import com.iyuba.music.data.local.DubDBManager;
import com.iyuba.music.data.remote.IntegralService;
import com.iyuba.music.dubbing.utils.BaseVideoControl;
import com.iyuba.music.dubbing.utils.NetStateUtil;
import com.iyuba.music.dubbing.utils.NormalVideoControl;
import com.iyuba.music.dubbing.utils.Share;
import com.iyuba.music.dubbing.utils.StorageUtil;
import com.iyuba.music.dubbing.utils.TimeUtil;
import com.iyuba.music.dubbing.utils.Util;
import com.iyuba.music.dubbing.views.DownloadDialog;
import com.iyuba.music.dubbing.views.MyOnTouchListener;
import com.iyuba.music.dubbing.views.VideoDownMenuDialog;
import com.iyuba.music.entity.MusicVoa;
import com.iyuba.music.event.DownloadEvent;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.util.ChangePropery;
import com.iyuba.music.util.ScreenUtils;
import com.iyuba.music.util.ToastUtil;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.widget.popmenu.ActionItem;
import com.iyuba.widget.popmenu.PopMenu;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import personal.iyuba.personalhomelibrary.utils.StatusBarUtil;
import rx.Subscription;
import timber.log.Timber;

/**
 * 主页面 - 配音列表 - 配音详情
 */
@RuntimePermissions
public class LessonPlayActivity extends AppCompatActivity implements LessonPlayMvpView {

    public static final String VOA = "voa_data";
    public static final String VOA_LIST = "voa_data_list";

    VideoView mVideoView;
    TextView tvDifficultyLabel;
    RatingBar mDifficultyRb;
    ImageView ivCollect;
    ImageView icMore;//点点点 更多
    LinearLayout refreshOrig;
    TextView dubbing;
    TabLayout mTabLayout;
    ViewPager mViewPager;
    TextView mLoadingTv;
    View mLoadingView;


    private DownloadDialog mDownloadDialog;

    private NormalVideoControl mVideoControl;
    private Subscription subscribe;
    private String mPathMp4;

    private boolean mIsPause;
    private long mCurPosition;
    private MusicVoa mTalkLesson;
    private ArrayList<MusicVoa> mTalkLessonList;

    private List<Fragment> mFragmentList = new ArrayList<>();

    private String mUid;
    private int mUidInt;
    private Context mContext;
    private CommonPagerAdapter mFragmentAdapter;

    private IntroductionFragment introductionFragment;
    private RankFragment myTalkListFragment;
    private RecommendFragment recommendFragment;

    private String mDir;
    private PopMenu menu;

    private LessonPlayPresenter mPresenter;

    private DubbingPresenter mDownloadPresenter;
    private VideoDownMenuDialog mMenuDialog;
    private VideoMiniData vmd;

    public static Intent buildIntent(Context context, MusicVoa voa, ArrayList<MusicVoa> itemList) {
        Intent intent = new Intent();
        intent.setClass(context, LessonPlayActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(VOA, voa);
        intent.putExtra(VOA_LIST, itemList);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ChangePropery.setAppConfig(this);
        setContentView(R.layout.activity_lesson_play);

        mLoadingView = findViewById(R.id.loading_view);
        mVideoView = findViewById(R.id.video_view);
        tvDifficultyLabel = findViewById(R.id.tv_difficulty_label);
        mDifficultyRb = findViewById(R.id.difficulty_rb);
        ivCollect = findViewById(R.id.collect);
        refreshOrig = findViewById(R.id.refresh_orig);
        icMore = findViewById(R.id.ic_more);
        dubbing = findViewById(R.id.dubbing);
//        share = findViewById(R.id.share);
        mTabLayout = findViewById(R.id.detail_tabs);
        mViewPager = findViewById(R.id.viewpager);
        mLoadingTv = findViewById(R.id.loading_tv);

        dubbing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onDubbingClick();

            }
        });

        StatusBarUtil.setColor(this, ResourcesCompat.getColor(getResources(), android.R.color.black, getTheme()));

        mContext = this;
        mTalkLesson = (MusicVoa) getIntent().getSerializableExtra(VOA);
        mTalkLessonList = (ArrayList<MusicVoa>) getIntent().getSerializableExtra(VOA_LIST);

//        mPathMp4 = "http://staticvip." + ConstantManager.IYUBA_CN + "video/voa/" + mTalkLesson.voaId + ".mp4";
        mPathMp4 = ConstantManager.videoPrefix + mTalkLesson.video;
        Log.e("LessonPlayActivity", "mPathMp4: " + mPathMp4);

        vmd = new VideoMiniData();
        vmd.video = mPathMp4;
        vmd.srtChVideo = mPathMp4;
        vmd.srtEngVideo = mPathMp4;
        mDir = StorageUtil
                .getMediaDir(mContext, mTalkLesson.voaId)
                .getAbsolutePath();
        //mPathMp4 = "http://staticvip.iyuba.cn/video/voa/321/321001.mp4";
        mUid = AccountManager.getInstance().getUserId();
        mUidInt = Integer.parseInt(AccountManager.getInstance().getUserId());
        initVideo();
        initView();
        initFragment();

        menu = buildMenu();
        mPresenter = new LessonPlayPresenter();
        mPresenter.attachView(this);
        DLManager dlManager = DLManager.getInstance();//单例引用对象了

        mDownloadPresenter = new DubbingPresenter(dlManager);
        mDownloadPresenter.init(mContext, mTalkLesson);

        EventBus.getDefault().register(this);
        mDownloadDialog = new DownloadDialog(this);
        mDownloadDialog.setmOnDownloadListener(new DownloadDialog.OnDownloadListener() {
            @Override
            public void onContinue() {
                mDownloadDialog.dismiss();
            }

            @Override
            public void onCancel() {
                mDownloadPresenter.cancelDownload();
                mLoadingView.setVisibility(View.GONE);
                mDownloadDialog.dismiss();
//                finish();
            }
        });

        icMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.show(view);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mVideoView.pause();


        mTalkLesson = (MusicVoa) intent.getSerializableExtra(VOA);
        mTalkLessonList = (ArrayList<MusicVoa>) intent.getSerializableExtra(VOA_LIST);
//        mPathMp4 = "http://staticvip.iyuba.cn/video/voa/321/" + mTalkLesson.voaId + ".mp4";
        mPathMp4 = ConstantManager.videoPrefix + mTalkLesson.video;
        mDir = StorageUtil
                .getMediaDir(mContext, mTalkLesson.voaId)
                .getAbsolutePath();
        if (checkFileExist()) {
            Timber.e("文件存在，本地播放");
            mVideoView.setVideoURI(Uri.fromFile(StorageUtil.getVideoFile(this, mTalkLesson.voaId)));
        } else {
            Timber.e("文件不存在，播放网络音频");
            mVideoView.setVideoPath(mPathMp4);
        }

        if (introductionFragment != null) {
            introductionFragment.setText(mTalkLesson.descCn);
        }
        if (recommendFragment != null) {
            recommendFragment.upData(mTalkLessonList, mTalkLesson.voaId);
        }
    }

    @Override
    protected void onPause() {
        pauseVideoPlayer("0");
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        mDownloadDialog.dismiss();
        mPresenter.detachView();
        //mDownloadPresenter.detachView();
        EventBus.getDefault().unregister(this);
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        }
        mVideoView.release();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        Timber.d("onConfigurationChanged");
        setVideoViewParams();
    }

    @Override
    public void onBackPressed() {
        if (isDownloading()) {
            mDownloadDialog.show();
        } else {
            if (mVideoControl.isFullScreen()) {
                mVideoControl.exitFullScreen();
            } else {
                finish();
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initVideo() {
        MyOnTouchListener listener = new MyOnTouchListener(this);
        listener.setSingleTapListener(mSingleTapListener);
        mVideoControl = new NormalVideoControl(this);
        mVideoControl.setMode(BaseVideoControl.Mode.SHOW_AUTO);
        mVideoControl.setBackCallback(new BaseVideoControl.BackCallback() {
            @Override
            public void onBack() {
                if (!isDownloading()) {
                    finish();
                } else {
                    mDownloadDialog.show();
                }
                finish();
            }
        });
        mVideoControl.setToTvCallBack(new BaseVideoControl.ToTvCallback() {
            @Override
            public void onToTv() {
                List<String> voaUrls = new ArrayList<>();
                List<String> voaTitles = new ArrayList<>();
                for (MusicVoa lesson : mTalkLessonList) {
                    voaUrls.add(lesson.url);
                    voaTitles.add(lesson.titleCn);
                }
                chooseDevice(mPathMp4, mTalkLesson.titleCn, voaUrls, voaTitles);
            }
        });
        mVideoView.setControls(mVideoControl);//VideoControlsCore
        mVideoControl.setOnTouchListener(listener);
        mVideoView.setOnPreparedListener(videoPreparedListener);
        mVideoView.setOnCompletionListener(videoCompletionListener);
        //mVideoView.setVideoURI(mPresenter.getVideoUri());
        if (checkFileExist()) {
            Timber.e("文件存在，本地播放");
            mVideoView.setVideoURI(Uri.fromFile(StorageUtil.getVideoFile(this, mTalkLesson.voaId)));
        } else {
            Timber.e("文件不存在，播放网络音频");
            mVideoView.setVideoPath(mPathMp4);
        }
        setVideoViewParams();
    }

    private void chooseDevice(String url, String title, List<String> voaUrls, List<String> voaTitles) {
//        Intent intent = new Intent(this, DevControlActivity.class);
//        intent.putExtra("url", url);
//        intent.putExtra("urls", (Serializable) voaUrls);
//        intent.putExtra("titles", (Serializable) voaTitles);
//        intent.putExtra("title", title);
//        startActivity(intent);
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

    OnPreparedListener videoPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared() {
            if (mCurPosition != 0) {
                pauseVideoPlayer("0");
            } else {
                if (!mIsPause) {
                    mVideoView.start();
                }
            }
        }
    };

    OnCompletionListener videoCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion() {
            mVideoView.restart();
            mVideoView.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared() {
                    pauseVideoPlayer("1");
                }
            });
        }
    };

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

    private void pauseVideoPlayer(String flag) {
        mVideoView.pause();
        //studyRecordUpdateUtil.stopStudyRecord(getApplicationContext(), mPresenter.checkLogin(), flag, mDataManager.getUploadStudyRecordService());
    }

    private void initView() {
        Drawable drawable = mDifficultyRb.getProgressDrawable();
        int drawableSize = (int) getResources().getDimension(R.dimen.difficulty_image_size);
        drawable.setBounds(0, 0, drawableSize, drawableSize);
        mDifficultyRb.setMax(5);
        mDifficultyRb.setProgress(mTalkLesson.hotFlag);
        final DubDBManager dubDBManager = DubDBManager.getInstance();
        boolean isCollect = false;
        if (!TextUtils.isEmpty(mUid)) {
            isCollect = dubDBManager.getCollect("" + mTalkLesson.voaId, mUid);
        }

        final Drawable collect = getResources().getDrawable(R.drawable.select);
        final Drawable collectNot = getResources().getDrawable(R.drawable.not_selected);
        ivCollect.setImageDrawable(isCollect ? collect : collectNot);
        mMenuDialog = new VideoDownMenuDialog(this, new VideoDownMenuDialog.ActionDelegate() {
            public VideoMiniData getVideoMiniData() {
                return vmd;
            }

            public boolean isJP() {
                return false;
            }
        }, mTalkLesson.voaId + "");
        ivCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountManager.getInstance().checkUserLogin()) {
                    boolean ok = dubDBManager.setCollect("" + mTalkLesson.voaId, mUid, mTalkLesson.title, mTalkLesson.descCn,
                            mTalkLesson.pic, "" + mTalkLesson.category);
                    if (ok) {
                        boolean isCollect = dubDBManager.getCollect("" + mTalkLesson.voaId, mUid);
                        if (isCollect) {
                            ivCollect.setImageDrawable(collect);
                        } else {
                            ivCollect.setImageDrawable(collectNot);
                        }
                    }
                } else {
                    showMessage("请先登录");
                }
            }
        });

    }

    private void initFragment() {
        String[] titles = {"简介", "排行", "相关"};
        introductionFragment = IntroductionFragment.newInstance(mTalkLesson.descCn);
        myTalkListFragment = RankFragment.newInstance(mTalkLesson);
        recommendFragment = RecommendFragment.newInstance(mTalkLessonList, mTalkLesson.voaId);

        mFragmentList.add(introductionFragment);
        mFragmentList.add(myTalkListFragment);
        mFragmentList.add(recommendFragment);

        mFragmentAdapter = new CommonPagerAdapter(
                this.getSupportFragmentManager(), mFragmentList, titles);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mFragmentAdapter);
        mViewPager.setCurrentItem(0);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private boolean isDownloading() {
        return mLoadingView.getVisibility() == View.VISIBLE && !mDownloadPresenter.checkFileExist();
    }

    public void stopPlaying() {
        if (mVideoView.isPlaying()) {
            mCurPosition = mVideoView.getCurrentPosition();
            pauseVideoPlayer("0");
        }
    }

    public boolean checkFileExist() {
        return StorageUtil.checkFileExist(mDir, mTalkLesson.voaId);
    }

    private PopMenu buildMenu() {
        String title = checkFileExist() ? "已下载" : "下载";
        boolean isDownload = (!TextUtils.isEmpty(mTalkLesson.title) && !TextUtils.isEmpty(mTalkLesson.descCn));
        List<ActionItem> items = new ArrayList<>(4);
        items.add(new ActionItem(this, "分享", R.drawable.ic_talk_share));
        items.add(new ActionItem(this, "导出PDF", R.drawable.ic_talk_pdf));
        if (isDownload)
            items.add(new ActionItem(this, title, R.drawable.ic_talk_download));
        menu = new PopMenu(this, items);
        menu.setItemClickListener(new PopMenu.PopMenuOnItemClickListener() {
            @Override
            public void setItemOnclick(ActionItem item, int position) {
                AccountManager accountManager = AccountManager.getInstance();
                switch (position) {
                    case 0:
                        if (accountManager.checkUserLogin()) {
                            onShareClick();
                        } else {
                            ToastUtil.showToast(mContext, "请登录后操作");
                        }
                        break;
                    case 1:
                        if (accountManager.checkUserLogin()) {
                            if (Integer.parseInt(accountManager.getUserInfo().getVipStatus()) > 0) {
                                showPDFDialog(true);//里面也扣积分了啊
                            } else {
                                showCreditDialog();
                            }
                        } else {
                            ToastUtil.showToast(mContext, "请登录后操作");
                        }
                        break;
                    case 2:
                        if (accountManager.checkUserLogin()) {
                            mMenuDialog.show();
//                            downloadLesson();
                        } else {
                            ToastUtil.showToast(mContext, "请登录后操作");
                        }
                        break;
                }
            }
        });
        return menu;
    }


    public void onDubbingClick() {
        if (!AccountManager.getInstance().checkUserLogin()) {
            CustomToast.getInstance().showToast(R.string.article_dub_request);
            return;
        }
        LessonPlayActivityPermissionsDispatcher.requestAudioSuccessWithPermissionCheck(LessonPlayActivity.this);
    }

    public void onShareClick() {
        pauseVideoPlayer("0");
        IntegralService integralService = IntegralService.Creator.newIntegralService();
        Share.prepareVideoMessage(this, mTalkLesson, integralService,
                Integer.parseInt(AccountManager.getInstance().getUserId()));
    }

    private void downloadLesson() {
        if (checkFileExist()) {
            showMessage("文件已存在");
        } else {
            if (!NetStateUtil.isConnected(mContext)) {
                showToast("网络异常");
                return;
            }
            //下载音频视频
            mLoadingView.setVisibility(View.VISIBLE);
            mLoadingTv.setVisibility(View.VISIBLE);
            mLoadingTv.setText("正在下载~");
            mDownloadPresenter.download();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadFinish(DownloadEvent downloadEvent) {
        switch (downloadEvent.status) {
            case DownloadEvent.Status.FINISH:
                mLoadingView.setVisibility(View.GONE);
                setVideoAndAudio();
                //此处下载完视频 音频后 本地数据库记录一下 ，应该不会出现 数据拿不到的情况，发布的配音不进入这个页面
                if (!TextUtils.isEmpty(mTalkLesson.title) && !TextUtils.isEmpty(mTalkLesson.descCn)) {
                    DubDBManager.getInstance().setDownload("" + mTalkLesson.voaId, mUid, mTalkLesson.title,
                            mTalkLesson.descCn, mTalkLesson.pic, "" + mTalkLesson.category);
                } else {
                    Timber.e("下载数据不全，无法保存！");
                }
                menu = buildMenu();
                //mDownloadPresenter.addFreeDownloadNumber();
                break;
            case DownloadEvent.Status.DOWNLOADING:
                mLoadingTv.setText(downloadEvent.msg);
                break;
            default:
                break;
        }
    }

    public void setVideoAndAudio() {
        try {
            if (mVideoView.isPlaying()) {
                mVideoView.pause();
            }
            int pos = (int) mVideoView.getCurrentPosition();
            mVideoView.setVideoURI(Uri.fromFile(StorageUtil.getVideoFile(this, mTalkLesson.voaId)));
            mVideoView.seekTo(pos - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LessonPlayActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    public void requestAudioSuccess() {
        stopPlaying();
        mIsPause = true;
        if (AccountManager.getInstance().checkUserLogin()) {
            long timestamp = TimeUtil.getTimeStamp();
            Intent intent = DubbingActivity.buildIntent(this, mTalkLesson, timestamp);
            startActivity(intent);
        } else {
            showMessage("您还没有登录，请先登录！");
        }
    }

    @OnPermissionDenied(Manifest.permission.RECORD_AUDIO)
    public void requestAudioFailed() {
        Toast.makeText(this, "请授予必要的权限", Toast.LENGTH_LONG).show();
    }

    private void showMessage(String msg) {
        ToastUtil.showToast(mContext, msg);
    }

    private void showCreditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("温馨提示");
        builder.setMessage("非VIP用户每篇PDF需扣除20积分");
        builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showPDFDialog(false);
                dialog.dismiss();
            }
        });
        builder.show();

    }

    public void showPDFDialog(final boolean isVip) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] choices = {"英文", "中英双语"};
        builder.setTitle("请选择导出文件的语言").setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which) {
                    case 0:
                        if (isVip) {
                            mPresenter.getPdf(mTalkLesson.voaId, 1);
                        } else {
                            mPresenter.deductIntegral(LessonPlayPresenter.PDF_ENG, mUidInt, mTalkLesson.voaId);
                        }
                        break;
                    case 1:
                        if (isVip) {
                            mPresenter.getPdf(mTalkLesson.voaId, 0);
                        } else {
                            mPresenter.deductIntegral(LessonPlayPresenter.PDF_BOTH, mUidInt, mTalkLesson.voaId);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        builder.show();
    }

    @Override
    public void showToast(int resId) {
        ToastUtil.showToast(mContext, getString(resId));
    }

    @Override
    public void showToast(String message) {
        ToastUtil.showToast(mContext, message);
    }

    @Override
    public void onDeductIntegralSuccess(int type) {
        if (type == LessonPlayPresenter.TYPE_DOWNLOAD) {  //永不会走这步
            downloadLesson();
            /*if (mMenuDialog != null) {
                mMenuDialog.show();
            }*/
        } else if (type == LessonPlayPresenter.PDF_ENG) {
            mPresenter.getPdf(mTalkLesson.voaId, 1);
        } else {
            mPresenter.getPdf(mTalkLesson.voaId, 0);
        }
    }

    @Override
    public void showPdfFinishDialog(String url) {
        final String downloadPath = "http://apps.iyuba.cn/iyuba" + url;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final AlertDialog dialog = builder.setTitle("PDF已生成 请妥善保存。")
                .setMessage("下载链接：" + downloadPath + "\n[已复制到剪贴板]\n")
                .setNegativeButton("下载", null)
                .setPositiveButton("关闭", null)
                .setNeutralButton("发送", null)
                .create();

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        try {
            View v = dialog.getWindow().getDecorView().findViewById(android.R.id.message);
            if (v != null) {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.copy2ClipBoard(mContext, downloadPath);
                        ToastUtil.showToast(mContext, "PDF下载链接已复制到剪贴板");
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Button positive = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    ToastUtil.showToast(mContext, "文件将会下载到" + "iyuba/" + Constant.AppName + "/ 目录下");
                    download(mTalkLesson.titleCn, downloadPath, mContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Button neutral = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        neutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mTalkLesson.title + " PDF";
                Share.shareMessage(mContext, Constant.APP_ICON, "", downloadPath, title, null);
            }
        });
        Util.copy2ClipBoard(mContext, downloadPath);
        ToastUtil.showToast(mContext, "PDF下载链接已复制到剪贴板");
    }

    public void download(String title, String url, Context mContext) {
        //创建下载任务,downloadUrl就是下载链接
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //指定下载路径和下载文件名
        request.setTitle(title);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            request.setDestinationInExternalPublicDir("/iyuba/" + Constant.AppName, title + ".pdf");
        } else {
            request.setDestinationInExternalFilesDir(mContext, "/iyuba/" + Constant.AppName, title + ".pdf");
        }
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //获取下载管理器
        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        //将下载任务加入下载队列，否则不会进行下载
        downloadManager.enqueue(request);
    }
}
