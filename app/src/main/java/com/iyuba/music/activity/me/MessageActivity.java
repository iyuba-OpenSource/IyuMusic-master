package com.iyuba.music.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.adapter.me.MessageAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.message.MessageLetter;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOnDoubleClick;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.request.merequest.MessageRequest;
import com.iyuba.music.request.merequest.SetMessageReadRequest;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by 10202 on 2016/1/2.
 */
public class MessageActivity extends BaseActivity implements MySwipeRefreshLayout.OnRefreshListener, IOnClickListener {
    private RecyclerView messageRecycleView;
    private ArrayList<MessageLetter> messageLetters;
    private MessageAdapter messageAdapter;
    private MySwipeRefreshLayout swipeRefreshLayout;
    private int messagePage;
    private boolean isLastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);
        context = this;
        isLastPage = false;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    public void onResume() {
        super.onResume();
        changeUIResumeByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        messageRecycleView = (RecyclerView) findViewById(R.id.message_recyclerview);
        swipeRefreshLayout = (MySwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setColorSchemeColors(0xff259CF7, 0xff2ABB51, 0xffE10000, 0xfffaaa3c);
        swipeRefreshLayout.setFirstIndex(0);
        swipeRefreshLayout.setOnRefreshListener(this);
        messageRecycleView.setLayoutManager(new LinearLayoutManager(context));
        ((SimpleItemAnimator) messageRecycleView.getItemAnimator()).setSupportsChangeAnimations(false);
        messageAdapter = new MessageAdapter(context);
        messageAdapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                MessageLetter messageLetter = messageLetters.get(position);
                SocialManager.getInstance().pushFriendId(messageLetter.getFriendid());
                SocialManager.getInstance().pushFriendName(messageLetter.getFriendName());
                Intent intent = new Intent(context, ChattingActivity.class);
                intent.putExtra("needpop", true);
                startActivity(intent);
                SetMessageReadRequest.exeRequest(SetMessageReadRequest.generateUrl(AccountManager.getInstance().getUserId(), messageLetter.getMessageid()));
                messageAdapter.setReaded(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        messageRecycleView.setAdapter(messageAdapter);
        messageRecycleView.addItemDecoration(new DividerItemDecoration());
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    protected void setListener() {
        super.setListener();
        toolBarLayout.setOnTouchListener(new IOnDoubleClick(this, context.getString(R.string.list_double)));
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialManager.getInstance().pushFriendId(AccountManager.getInstance().getUserId());
                Intent intent = new Intent(context, FriendCenter.class);
                intent.putExtra("type", "0");
                intent.putExtra("intenttype", "chat");
                startActivity(intent);
            }
        });
    }

    protected void changeUIByPara() {
        backIcon.setState(MaterialMenuDrawable.IconState.ARROW);
        toolbarOper.setText(R.string.message_oper);
        title.setText(R.string.message_title);
    }

    protected void changeUIResumeByPara() {
        onRefresh(0);
    }

    /**
     * 下拉刷新
     *
     * @param index 当前分页索引
     */
    @Override
    public void onRefresh(int index) {
        messagePage = 1;
        messageLetters = new ArrayList<>();
        isLastPage = false;
        getMessageData();
    }

    /**
     * 加载更多
     *
     * @param index 当前分页索引
     */
    @Override
    public void onLoad(int index) {
        if (messageLetters.size() == 0) {

        } else if (!isLastPage) {
            messagePage++;
            getMessageData();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            CustomToast.getInstance().showToast(R.string.comment_get_all);
        }
    }

    private void getMessageData() {
        MessageRequest.exeRequest(MessageRequest.generateUrl(AccountManager.getInstance().getUserId(), messagePage), new IProtocolResponse() {
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
                BaseListEntity listEntity = (BaseListEntity) object;
                isLastPage = listEntity.isLastPage();
                messageLetters.addAll((ArrayList<MessageLetter>) listEntity.getData());
                swipeRefreshLayout.setRefreshing(false);
                messageAdapter.setMessageList(messageLetters);
                if (messagePage == 1) {
                    if (messageLetters.size() == 0) {
                        findViewById(R.id.no_message).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.no_message).setVisibility(View.GONE);
                    }
                } else {
                    CustomToast.getInstance().showToast(messagePage + "/" + listEntity.getTotalPage(), 800);
                }
            }
        });
    }

    @Override
    public void onClick(View view, Object message) {
        messageRecycleView.scrollToPosition(0);
    }
}
