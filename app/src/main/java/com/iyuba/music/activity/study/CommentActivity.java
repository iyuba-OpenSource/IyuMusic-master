package com.iyuba.music.activity.study;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.MaterialMenuView;
import com.balysv.materialripple.MaterialRippleLayout;
import com.buaa.ct.comment.CommentView;
import com.buaa.ct.comment.ContextManager;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseInputActivity;
import com.iyuba.music.adapter.study.CommentAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.comment.Comment;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOnDoubleClick;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.newsrequest.CommentDeleteRequest;
import com.iyuba.music.request.newsrequest.CommentExpressRequest;
import com.iyuba.music.request.newsrequest.CommentRequest;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.util.ThreadPoolUtil;
import com.iyuba.music.util.UploadFile;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by 10202 on 2016/2/13.
 * 歌曲评论
 */

public class CommentActivity extends BaseInputActivity implements MySwipeRefreshLayout.OnRefreshListener, IOnClickListener {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private Article curArticle;
    private ImageView img;
    private TextView articleTitle, singer, announcer, count;
    private RecyclerView commentRecycleView;
    private ArrayList<Comment> comments;
    private CommentAdapter commentAdapter;
    private MySwipeRefreshLayout swipeRefreshLayout;
    private CommentView commentView;
    private int commentPage;
    private boolean isLastPage = false;


    private MaterialRippleLayout new_back;
    private TextView new_title;
    private RelativeLayout re_new;
    private MaterialMenuView new_back_material;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextManager.setInstance(this);//评论模块初始化
        setContentView(R.layout.comment);
        context = this;
        isLastPage = false;
        initWidget();
        setListener();
        changeUIByPara();




    }

    @Override
    protected void initWidget() {
        super.initWidget();
        initNewTopBar();

        img = (ImageView) findViewById(R.id.article_img);
        articleTitle = (TextView) findViewById(R.id.article_title);
        announcer = (TextView) findViewById(R.id.article_announcer);
        singer = (TextView) findViewById(R.id.article_singer);
        count = (TextView) findViewById(R.id.article_comment_count);
        commentRecycleView = (RecyclerView) findViewById(R.id.comment_recyclerview);
        swipeRefreshLayout = (MySwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setColorSchemeColors(0xff259CF7, 0xff2ABB51, 0xffE10000, 0xfffaaa3c);
        swipeRefreshLayout.setFirstIndex(0);
        swipeRefreshLayout.setOnRefreshListener(this);
        commentRecycleView.setLayoutManager(new LinearLayoutManager(context));
        ((SimpleItemAnimator) commentRecycleView.getItemAnimator()).setSupportsChangeAnimations(false);
        commentAdapter = new CommentAdapter(context, true);
        commentAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (AccountManager.getInstance().checkUserLogin()) {
                    if (AccountManager.getInstance().getUserId().equals(comments.get(position).getUserid())) {//是自己，删除
                        delDialog(position);
                    } else {//不是自己  回复
                        if (comments.get(position).getUserName() == null || comments.get(position).getUserName().equals("")) {

                            //选择临时账号，没有名字，回复id
                            commentView.getmEtText().setText(getResources().getString(R.string.comment_reply,
                                    comments.get(position).getUserid()));
                            commentView.getmEtText().setSelection(commentView.getmEtText().length());
                        } else {
                            commentView.getmEtText().setText(getResources().getString(R.string.comment_reply,
                                    comments.get(position).getUserName()));
                            commentView.getmEtText().setSelection(commentView.getmEtText().length());
                        }

                    }
                } else {
                    final int pos = position;
                    CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                        @Override
                        public void finish() {
                            if (AccountManager.getInstance().getUserId().equals(comments.get(pos).getUserid())) {//是自己，删除
                                delDialog(pos);
                            } else {//不是自己  回复

                                if (comments.get(pos).getUserName() == null || comments.get(pos).getUserName().equals("")) {

                                    commentView.getmEtText().setText(getResources().getString(R.string.comment_reply,
                                            comments.get(pos).getUserid()));
                                    commentView.getmEtText().setSelection(commentView.getmEtText().length());

                                } else {


                                    commentView.getmEtText().setText(getResources().getString(R.string.comment_reply,
                                            comments.get(pos).getUserName()));
                                    commentView.getmEtText().setSelection(commentView.getmEtText().length());
                                }

                            }
                        }
                    });
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        commentRecycleView.setAdapter(commentAdapter);
        commentRecycleView.addItemDecoration(new DividerItemDecoration());
        swipeRefreshLayout.setRefreshing(true);
        commentView = (CommentView) findViewById(R.id.comment_view);
    }

    @Override
    protected void setListener() {
        super.setListener();
        toolBarLayout.setOnTouchListener(new IOnDoubleClick(this, context.getString(R.string.list_double)));
        commentView.setOperationDelegate(new CommentView.OnComposeOperationDelegate() {
            @Override
            public void onSendText(String s) {
                if (AccountManager.getInstance().checkUserLogin()) {
                    sendComment(s);
                } else {
                    final String string = s;
                    CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                        @Override
                        public void finish() {

                            sendComment(string);
                        }
                    });
                }

                try {


                    commentView.hideEmojiOptAndKeyboard();
                } catch (Exception e) {

                    e.printStackTrace();
                }
//
            }

            @Override
            public void onSendVoice(String s, int i) {
                if (i == 0) {
                    CustomToast.getInstance().showToast(R.string.comment_sound_short);
                } else {
                    if (AccountManager.getInstance().checkUserLogin()) {
                        handler.obtainMessage(1, s).sendToTarget();
                    } else {
                        final String string = s;
                        CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                            @Override
                            public void finish() {
                                handler.obtainMessage(1, string).sendToTarget();
                            }
                        });
                    }
                }
            }

            @Override
            public void onSendImageClicked(View view) {

            }

            @Override
            public void onSendLocationClicked(View view) {

            }
        });
    }

    private void sendComment(String s) {


        try {


            CommentExpressRequest.exeRequest(CommentExpressRequest.generateUrl(
                    String.valueOf(curArticle.getId()), AccountManager.getInstance().getUserId(),
                    AccountManager.getInstance().getUserInfo().getUsername(), s), new IProtocolResponse() {
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
                    if (object.toString().equals("501")) {
                        commentView.clearText();
                        handler.sendEmptyMessage(2);
                    } else {
                        CustomToast.getInstance().showToast(R.string.comment_send_fail);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.comment_title);
        curArticle = StudyManager.getInstance().getCurArticle();
        ImageUtil.loadImage("http://staticvip." + ConstantManager.IYUBA_CN + "images/song/" + curArticle.getPicUrl(), img, R.drawable.default_music);
        articleTitle.setText(curArticle.getTitle());
        announcer.setText(context.getString(R.string.article_announcer, curArticle.getBroadcaster()));
        singer.setText(context.getString(R.string.article_singer, curArticle.getSinger()));
        count.setText(context.getString(R.string.article_commentcount, "0"));
        onRefresh(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        commentAdapter.onDestroy();
        ContextManager.destory();
    }

    @Override
    public void onBackPressed() {
        if (commentView.onBackPressed()) {
            super.onBackPressed();
        }
    }


    /**
     * 下拉刷新
     *
     * @param index 当前分页索引
     */
    @Override
    public void onRefresh(int index) {
        commentPage = 1;
        comments = new ArrayList<>();
        isLastPage = false;
        getCommentData();
    }

    /**
     * 加载更多
     *
     * @param index 当前分页索引
     */
    @Override
    public void onLoad(int index) {
        if (comments.size() == 0) {

        } else if (!isLastPage) {
            commentPage++;
            getCommentData();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            CustomToast.getInstance().showToast(R.string.comment_get_all);
        }
    }

    @Override
    public void onClick(View view, Object message) {
        commentRecycleView.scrollToPosition(0);
    }

    private void delDialog(final int position) {
        final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
        materialDialog.setTitle(R.string.comment_title);
        materialDialog.setMessage(R.string.comment_del_msg);
        materialDialog.setPositiveButton(R.string.comment_del, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentDeleteRequest.exeRequest(CommentDeleteRequest.generateUrl(comments.get(position).getId()), new IProtocolResponse() {
                    @Override
                    public void onNetError(String msg) {

                    }

                    @Override
                    public void onServerError(String msg) {

                    }

                    @Override
                    public void response(Object object) {
                        if (object.toString().equals("1")) {
                            commentAdapter.removeData(position);
                        } else {
                            CustomToast.getInstance().showToast(R.string.comment_del_fail);
                        }
                    }
                });
                materialDialog.dismiss();
            }
        });
        materialDialog.setNegativeButton(R.string.app_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }

    private void getCommentData() {
        CommentRequest.exeRequest(CommentRequest.generateUrl(curArticle.getId(), commentPage), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void response(Object object) {
                swipeRefreshLayout.setRefreshing(false);
                BaseListEntity listEntity = (BaseListEntity) object;
                isLastPage = listEntity.isLastPage();
                if (listEntity.getTotalCount() == 0) {
                    findViewById(R.id.no_comment).setVisibility(View.VISIBLE);
                } else {
                    comments.addAll((ArrayList<Comment>) listEntity.getData());
                    findViewById(R.id.no_comment).setVisibility(View.GONE);
                    handler.obtainMessage(0, listEntity.getTotalCount()).sendToTarget();
                    if (listEntity.getCurPage() == 1) {

                    } else {
                        CustomToast.getInstance().showToast(listEntity.getCurPage() + "/" + listEntity.getTotalPage(), 800);
                    }
                }
            }
        });
    }

    private void startUploadVoice(final String url) {
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                String sb = "http://daxue." + ConstantManager.IYUBA_CN + "appApi/UnicomApi?protocol=60003&platform=android&appName=music&format=json" + "&userid=" +
                        AccountManager.getInstance().getUserId() +
                        "&shuoshuotype=" + 1 +
                        "&voaid=" + curArticle.getId();
                final File file = new File(url);
                UploadFile.postSound(sb, file, new IOperationResult() {
                    @Override
                    public void success(Object object) {
                        handler.sendEmptyMessage(2);
                        file.delete();
                    }

                    @Override
                    public void fail(Object object) {
                        handler.sendEmptyMessage(3);
                    }
                });
            }
        });
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<CommentActivity> {
        @Override
        public void handleMessageByRef(final CommentActivity activity, Message msg) {
            switch (msg.what) {
                case 0:
                    activity.commentAdapter.setDataSet(activity.comments);
                    activity.count.setText(activity.getString(R.string.article_commentcount, msg.obj.toString()));
                    break;
                case 1:
                    activity.startUploadVoice(msg.obj.toString());
                    break;
                case 2:
                    activity.onRefresh(0);
                    activity.commentRecycleView.scrollToPosition(0);
                    break;
                case 3:
                    CustomToast.getInstance().showToast(R.string.comment_send_fail);
                    break;
            }
        }
    }


    private int getStatusBarHeight() {
        //状态栏高度
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void initNewTopBar() {
        //重复topbar，未使用
        new_back = (MaterialRippleLayout) findViewById(R.id.new_back);
        new_title = (TextView) findViewById(R.id.new_title);
        re_new = (RelativeLayout) findViewById(R.id.new_res);
        re_new.setPadding(0, getStatusBarHeight(), 0, 0);

        new_back_material = (MaterialMenuView) findViewById(R.id.new_back_material);
        new_back_material.setState(MaterialMenuDrawable.IconState.ARROW);

        new_title.setText("评论");
        new_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
