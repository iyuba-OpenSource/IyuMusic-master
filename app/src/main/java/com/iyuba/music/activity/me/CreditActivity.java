package com.iyuba.music.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.WebViewActivity;
import com.iyuba.music.data.Constant;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.merequest.GradeRequest;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.SwipeRefreshLayout.CustomSwipeToRefresh;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;
import com.iyuba.music.widget.boundnumber.RiseNumberTextView;

/**
 * Created by 10202 on 2016/3/31.
 */
public class CreditActivity extends BaseActivity implements MySwipeRefreshLayout.OnRefreshListener {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private RiseNumberTextView counts, rank;
    private View creditDetail, creditExchange;
    private CustomSwipeToRefresh swipeRefreshLayout;
    private String rankPos, duration;
    private TextView creditDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credits);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        counts = (RiseNumberTextView) findViewById(R.id.credit_counts);
        rank = (RiseNumberTextView) findViewById(R.id.credit_rank);
        creditDetail = findViewById(R.id.credit_detail);
        creditExchange = findViewById(R.id.credit_exchange);
        creditDuration = (TextView) findViewById(R.id.credit_duration);
        swipeRefreshLayout = (CustomSwipeToRefresh) findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setColorSchemeColors(0xff259CF7, 0xff2ABB51, 0xffE10000, 0xfffaaa3c);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    protected void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, WebViewActivity.class);
                intent.putExtra("url", "http://m." + ConstantManager.IYUBA_CN + "mall/ruleOfintegral.jsp");
                intent.putExtra("title", context.getString(R.string.credits_helper));
                startActivity(intent);
            }
        });
        creditDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, WebViewActivity.class);
                intent.putExtra("url", "http://api." + ConstantManager.IYUBA_CN + "credits/useractionrecordmobileList1.jsp?uid=" + AccountManager.getInstance().getUserId());
                intent.putExtra("title", context.getString(R.string.credits_details));
                startActivity(intent);
            }
        });
        creditExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AccountManager.getInstance().checkUserLogin()) {


                    Intent intent = new Intent();
                    intent.setClass(context, WebViewActivity.class);
                    intent.putExtra("url", "http://m." + ConstantManager.IYUBA_CN + "mall/index.jsp?uid=" + AccountManager.getInstance().getUserId() + "&appid=" + Constant.APPID + "&username=" + AccountManager.getInstance().getUserInfo().getUsername() + "&sign=" + MD5.getMD5ofStr("iyuba" + AccountManager.getInstance().getUserId() + "camstory"));
                    intent.putExtra("title", context.getString(R.string.campaign_exchange));
                    startActivity(intent);
                } else {

                    CustomToast.getInstance().showToast("临时账户无法进入积分商城");
                }
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.credits_title);
        toolbarOper.setText(R.string.credits_helper);
        title.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    counts.withNumber(Integer.parseInt(AccountManager.getInstance().getUserInfo().getIcoins())).start();
                } catch (Exception e) {
                    e.printStackTrace();
                    counts.setText("0");
                }
                rank.setText(R.string.credits_loading);
                onRefresh(0);
            }
        }, 700);
    }

    /**
     * 下拉刷新
     *
     * @param index 当前分页索引
     */
    @Override
    public void onRefresh(int index) {
        getData();
    }

    /**
     * 加载更多
     *
     * @param index 当前分页索引
     */
    @Override
    public void onLoad(int index) {

    }

    private void getData() {
        GradeRequest.exeRequest(GradeRequest.generateUrl(AccountManager.getInstance().getUserId()), new IProtocolResponse() {
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
                String result = object.toString();
                String[] para = result.split("@@@");
                rankPos = para[0];
                duration = para[1];
                swipeRefreshLayout.setRefreshing(false);
                handler.sendEmptyMessage(1);
            }
        });
    }

    private String exeStudyTime(int time) {
        StringBuilder sb = new StringBuilder();
        int minus = time % 60;
        int minute = time / 60 % 60;
        int hour = time / 60 / 60;
        if (hour > 0) {
            sb.append(hour).append(':');
        }
        if (minute > 0 || hour > 0) {
            sb.append(minute).append(':');
        }
        sb.append(minus);
        if (sb.toString().contains(":")) {
            return sb.toString();
        } else {
            return "0:" + sb.toString();
        }
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<CreditActivity> {
        @Override
        public void handleMessageByRef(final CreditActivity activity, Message msg) {
            switch (msg.what) {
                case 1:

                    activity.rank.withNumber(Integer.parseInt(activity.rankPos)).start();
                    activity.creditDuration.setText(activity.getString(R.string.credits_study_time, activity.exeStudyTime(Integer.parseInt(activity.duration))));

                    try {
                        if (AccountManager.getInstance().getUserInfo().getIcoins() != null) {
                            activity.counts.withNumber(Integer.parseInt(AccountManager.getInstance().getUserInfo().getIcoins())).start();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
}
