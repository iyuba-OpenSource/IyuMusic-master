package com.iyuba.music.dubbing.comment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.iyuba.music.R;
import com.iyuba.music.data.model.Comment;
import com.iyuba.music.data.model.TalkLesson;
import com.iyuba.music.data.remote.IntegralService;
import com.iyuba.music.dubbing.DubbingActivity;
import com.iyuba.music.dubbing.views.LoadingDialog;
import com.iyuba.music.dubbing.utils.Recorder;
import com.iyuba.music.dubbing.utils.Share;
import com.iyuba.music.dubbing.utils.StorageUtil;
import com.iyuba.music.dubbing.utils.TimeUtil;
import com.iyuba.music.entity.MusicVoa;
import com.iyuba.music.event.StopEvent;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.util.ToastUtil;
import com.iyuba.music.widget.CustomToast;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;


public class CommentFragment extends Fragment implements CommentMvpView {
    private static final String TAG = CommentFragment.class.getSimpleName();

    private static final String VOA = "voa";
    private static final String RANKING_ID = "ranking_id";
    private static final String RANKING_NAME = "ranking_name";
    private static final String RANKING_URL = "ranking_url";
    public static final int PAGE_NUM = 1;
    public static final int PAGE_SIZE = 20;

    private static final int REQUEST_RECORD_PERMISSION = 1;

    private MusicVoa mVoa;
    private int mRankingId;
    private String mRankingURL;
    private String mRankingName;
    private int pageNum = 1;

    MyRelativeLayout mRootLayout;
    TextView mCommentTitleTv;
    SwipeRefreshLayout mRefreshLayout;
    RecyclerView mRecyclerView;
    CommentInputView mCommentInput;
    View mDubbingView;
    MicVolumeView mVolumeView;
    TextView mShareTv;
    View mEmptyView;
    TextView mEmptyTextTv;
    View mLoadingLayout;
    TextView mTvComment;

    CommentPresenter mPresenter;

    CommentAdapter mAdapter;

    MediaPlayer mMediaPlayer;
    Recorder mRecorder;
    private String mRecorderFilePath;

    boolean mIsShowInputMethod;
    private LoadingDialog mLoadingDialog;

    private Context mContext;

//    CommentAdapter.CommentCallback callback = new CommentAdapter.CommentCallback() {
//        @Override
//        public void onPhotoClick(Comment comment) {
//            //TODO
//        }
//
//        @Override
//        public void onAgreeClick(int id) {
//            mPresenter.doAgree(id);
//        }
//
//        @Override
//        public void onAgainstClick(int id) {
//            mPresenter.doAgainst(id);
//        }
//    };
//
//    OnPreparedListener mOnPreparedListener = new OnPreparedListener() {
//        @Override
//        public void onPrepared() {
//            EventBus.getDefault().post(new AudioEvent(AudioEvent.State.STOP_INTERRUPTED));
//        }
//    };

    RelativeLayout empty_layout;
    TextView share_tv;

    TextView dubbingTv;

    public static CommentFragment newInstance(MusicVoa voa, int rankingId, String rankName, String rankUrl) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putSerializable(VOA, voa);
        args.putInt(RANKING_ID, rankingId);
        args.putString(RANKING_NAME, rankName);
        args.putString(RANKING_URL, rankUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //fragmentComponent().inject(this);
        mPresenter = new CommentPresenter();
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        mRootLayout = view.findViewById(R.id.root_layout);
        mTvComment = view.findViewById(R.id.comment_tv);
        share_tv = view.findViewById(R.id.share_tv);
        mShareTv = share_tv;
        dubbingTv = view.findViewById(R.id.dubbing_tv);
        mCommentInput = view.findViewById(R.id.comment_input_view);
        mCommentTitleTv = view.findViewById(R.id.comment_title_tv);
        mRefreshLayout = view.findViewById(R.id.refresh_layout);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mEmptyView = view.findViewById(R.id.empty_view);
        empty_layout = view.findViewById(R.id.empty_layout);
        mVolumeView = view.findViewById(R.id.mic_volume_view);

        mDubbingView = view.findViewById(R.id.dubbing_view);
        mLoadingLayout = view.findViewById(R.id.loading_layout);
        //mPresenter.attachView(this);

        mCommentTitleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickOthers();
            }
        });
        mCommentTitleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickOthers();
            }
        });
        mTvComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickComment();
            }
        });
        dubbingTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickDubbing();
            }
        });
        share_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickShare();
            }
        });

        init();
        initRecyclerView();
        initCommentInputView();
        if (getArguments() != null) {
            mVoa = (MusicVoa) getArguments().getSerializable(VOA);
            mRankingId = getArguments().getInt(RANKING_ID);
            mRankingName = getArguments().getString(RANKING_NAME);
            mRankingURL = getArguments().getString(RANKING_URL);
            showCommentLoadingLayout();
            //mPresenter.loadComment(mVoa.voaId(), mRankingId, PAGE_NUM, PAGE_SIZE);
        }
        return view;
    }

    private void init() {
        mRecorder = new Recorder();
        mMediaPlayer = new MediaPlayer();
        mLoadingDialog = new LoadingDialog(getContext());
        mRootLayout.setOnResizeListener(new MyRelativeLayout.OnResizeListener() {
            @Override
            public void OnResize(int w, int h, int oldw, int oldh) {
                mIsShowInputMethod = h < oldh;
            }
        });
        int shareSize = (int) getResources().getDimension(R.dimen.comment_share_image_size);
        setTextDrawable(mShareTv, R.drawable.ic_share_yellow, shareSize, shareSize);
    }

    public void setTextDrawable(TextView textView, int resId, int width, int height) {
        Drawable drawable = ContextCompat.getDrawable(getActivity(), resId);
        drawable.setBounds(0, 0, width, height);
        textView.setCompoundDrawables(drawable, null, null, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        mTvComment.setVisibility(View.GONE);
        mLoadingDialog.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    public void initCommentInputView() {
        mCommentInput.setOnCommentSendListener(mSendListener);
        mCommentInput.setInputMethodCallback(new CommentInputView.InputMethodCallback() {

            @Override
            public void show() {
                toggleInputMethod(true);
            }

            @Override
            public void hide() {
                toggleInputMethod(false);
            }
        });
        try {
            mRecorderFilePath = StorageUtil.getCommentVoicePath(getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCommentInput.setRecordFilePath(mRecorderFilePath);
        mCommentInput.setRecorderListener(mRecorderListener);
        mCommentInput.setRequestPermissionCallback(mPermissionCB);
        mCommentInput.setMediaPlayer(mMediaPlayer);
        mCommentInput.setRecorder(mRecorder);
    }

    public void initRecyclerView() {
        mRefreshLayout.setOnRefreshListener(mOnRefreshListener);
//        mAdapter.setCallback(callback);
//        mAdapter.setMediaPlayer(mMediaPlayer);
//        mAdapter.setOnReplyListener(mOnReplyListener);
//        mAdapter.setOnItemClickListener(new CommentAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick() {
//
//            }
//        });
        //   mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        mRecyclerView.addItemDecoration(new LinearItemDivider(getActivity(), LinearItemDivider.VERTICAL_LIST));
        mRecyclerView.setLayoutManager(layoutManager);
//        mRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        mRecyclerView.addOnScrollListener(mOnScrollListener);
    }

//    private CommentAdapter.OnReplyListener mOnReplyListener = new CommentAdapter.OnReplyListener() {
//        @Override
//        public void onReply(String targetUsername) {
//            mDubbingView.setVisibility(View.GONE);
//            mCommentInput.setVisibility(View.VISIBLE);
//            mCommentInput.replyToSomeone(targetUsername);
//        }
//    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        // mPresenter.detachView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mPresenter.detachView();
        if (mMediaPlayer != null) mMediaPlayer.release();
        if (mRecorder != null) mRecorder.release();
//        mCommentInput.deleteRecordFile();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStopEvent(StopEvent stopEvent) {
        switch (stopEvent.source) {
            case StopEvent.SOURCE.VOICE:
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                }
                break;
            default:
                break;
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onCommentEvent(CommentEvent event) {
//        switch (event.status) {
//            case CommentEvent.Status.GONE:
//                onClickOthers();
//                break;
//            default:
//                break;
//        }
//    }

    private void toggleInputMethod(boolean show) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mIsShowInputMethod) {
            if (!show) inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            if (show) inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 刷新监听
     */
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    //mPresenter.loadComment(mVoa.voaId(), mRankingId, PAGE_NUM, PAGE_SIZE);
                }
            });
        }
    };

    /**
     * 加载更多
     */
    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!recyclerView.canScrollVertically(1)) {// 手指不能向上滑动了
                // TODO 这里有个注意的地方，如果你刚进来时没有数据，但是设置了适配器，
                // TODO 这个时候就会触发加载更多，需要开发者判断下是否有数据，如果有数据才去加载更多。
                pageNum++;
                // mPresenter.loadComment(mVoa.voaId(), mRankingId, pageNum, PAGE_SIZE);
            }
        }
    };

    /**
     * 我要配音按钮，如果mVoa 是只有ID的，而且下载文件还是没有的，就不能跳转
     */
    public void onClickDubbing() {

        if (!AccountManager.getInstance().checkUserLogin()) {
            CustomToast.getInstance().showToast(R.string.article_dub_request);
            return;
        }
        String mDir = StorageUtil.getMediaDir(mContext, mVoa.voaId).getAbsolutePath();
        if (mVoa.title == null && mVoa.descCn == null && !StorageUtil.checkFileExist(mDir, mVoa.voaId)) {
            ToastUtil.showToast(mContext, "请先下载课程！");
        } else {
            EventBus.getDefault().post(new StopEvent(StopEvent.SOURCE.VIDEO));
            EventBus.getDefault().post(new StopEvent(StopEvent.SOURCE.VOICE));
            //mPresenter.getVoa(mVoa.voaId());
            long timestamp = TimeUtil.getTimeStamp();
            Intent intent = DubbingActivity.buildIntent(getActivity(), mVoa, timestamp);
            startActivity(intent);
        }
    }

    public void onClickShare() {
        if (!AccountManager.getInstance().checkUserLogin()) {
            CustomToast.getInstance().showToast(R.string.article_share_request);
            return;
        }
        IntegralService integralService = IntegralService.Creator.newIntegralService();
        Share.prepareDubbingMessage(getContext(), mVoa, mRankingId, mRankingName, integralService,
                Integer.parseInt(AccountManager.getInstance().getUserId()));
    }

    public void onClickComment() {
        if (AccountManager.getInstance().checkUserLogin()) {
            mDubbingView.setVisibility(View.GONE);
            mCommentInput.setVisibility(View.VISIBLE);
            mCommentInput.initState();
        } else {
            ToastUtil.showToast(mContext, "请先登录");
        }
    }

    public void onClickOthers() {
        if (mIsShowInputMethod) {
            toggleInputMethod(false);
        } else {
            if (mDubbingView.getVisibility() == View.GONE) {
                mDubbingView.setVisibility(View.VISIBLE);
                mCommentInput.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void showComments(List<Comment> commentList) {
        mEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        //mAdapter.setList(commentList);
        //mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyComment() {
        mEmptyTextTv.setText("此配音还没有\n评论，快来抢\n占沙发吧~");
        mEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showToast(int id) {
        Toast.makeText(getContext(), id, Toast.LENGTH_LONG).show();
    }

    @Override
    public void clearInputText() {
        mCommentInput.clearInputText();
    }

    @Override
    public void setCommentNum(int num) {
        mCommentTitleTv.setText(MessageFormat.format(getString(R.string.comment_num), num));
    }

    @Override
    public void startDubbingActivity(MusicVoa voa) {
        long timestamp = TimeUtil.getTimeStamp();
        Intent intent = DubbingActivity.buildIntent(getActivity(), voa, timestamp);
        startActivity(intent);
    }

    @Override
    public void showLoadingDialog() {
        //mLoadingDialog.show();
    }

    @Override
    public void dismissLoadingDialog() {
        mLoadingDialog.dismiss();
    }

    @Override
    public void showCommentLoadingLayout() {
        //mLoadingLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissCommentLoadingLayout() {
        mLoadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void dismissRefreshingView() {
        mRefreshLayout.setRefreshing(false);
    }

    private CommentInputView.RequestPermissionCallback mPermissionCB = new CommentInputView.RequestPermissionCallback() {
        @Override
        public void requestRecordPermission() {
//            MPermissions.requestPermissions(CommentFragment.this, REQUEST_RECORD_PERMISSION,
//                    Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    };

    //@PermissionGrant(REQUEST_RECORD_PERMISSION)
    void onRecordPermissionGranted() {
        mCommentInput.onRecordPermissionGranted();
    }

    //@PermissionDenied(REQUEST_RECORD_PERMISSION)
    void onRecordPermissionDenied() {
        Toast.makeText(getContext(), "Record Permission Denied! Can't make audio comment", Toast
                .LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private CommentInputView.RecorderListener mRecorderListener = new CommentInputView.RecorderListener() {
        @Override
        public void onBegin() {
            mVolumeView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onVolumeChanged(int db) {
            mVolumeView.setVolume(db);
        }

        @Override
        public void onEnd() {
            mVolumeView.setVisibility(View.GONE);
        }

        @Override
        public void onError() {
            mVolumeView.setVisibility(View.GONE);
        }
    };

    private CommentInputView.OnCommentSendListener mSendListener = new CommentInputView.OnCommentSendListener() {

        @Override
        public void onTextSend(String comment) {
            showLoadingDialog();
            //mPresenter.senTextComment(mVoa.voaId(), mRankingId, comment);
        }

        @Override
        public void onVoiceSend(File record) {
            showLoadingDialog();
            //mPresenter.sendVoiceComment(mVoa.voaId(), mRankingId, record);
        }
    };
}
