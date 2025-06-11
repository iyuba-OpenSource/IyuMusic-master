package com.iyuba.music.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.music.R;
import com.iyuba.music.adapter.LinearItemDivider;
import com.iyuba.music.adapter.OfficialAdapter;
import com.iyuba.music.adapter.OfficialMvpView;
import com.iyuba.music.adapter.OfficialPresenter;
import com.iyuba.music.adapter.OnOfficialClickListener;
import com.iyuba.music.data.Constant;
import com.iyuba.music.data.model.OfficialResponse;
import com.iyuba.music.dubbing.utils.NetStateUtil;
import com.iyuba.music.dubbing.utils.Share;
import com.iyuba.music.widget.CommonProgressDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.List;

/**
 * Created by carl shen on 2021/8/18
 * English Music, new study experience.
 */
public class OfficialActivity extends BaseInputActivity implements OfficialMvpView {
    private static final String TAG = OfficialActivity.class.getSimpleName();
    private static int PAGE_NUM = 1;
    private static final int PAGE_SIZE = 10;
    private OfficialAdapter mAdapter;
    private OfficialPresenter mPresenter;
    private View emptyView;
    private TextView emptyText;
    private RecyclerView officialRecyclerView;
    private SmartRefreshLayout refreshLayout;

    OnOfficialClickListener mListener = new OnOfficialClickListener() {
        @Override
        public void onOfficialClick(OfficialResponse.OfficialAccount collect) {
            if (collect == null) {
                showToast("请检查内容是否正确。");
                return;
            }
            if (Constant.APP_SHARE_WXMINIPROGRAM < 1) {
                showToast("对不起，分享暂时不支持");
                return;
            }
            if (Share.isWXSmallAvailable(context)) {
                IWXAPI api = WXAPIFactory.createWXAPI(context, Constant.WX_KEY);
                WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
                req.userName = Constant.WX_NAME;
                String minipath = String.format("/pages/gzhDetails/gzhDetails?id=%d", collect.id);
                Log.e("ShareSDK", "OfficialActivity MiniProgram path " + minipath);
                req.path = minipath;
                req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;
                api.sendReq(req);
            } else {
                showToast("您的手机暂时不支持跳转到微信小程序，谢谢！");
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        refreshLayout = findViewById(R.id.refresh_layout);
        officialRecyclerView = findViewById(R.id.official_recycler_view);
        emptyView = findViewById(R.id.empty_view);
        emptyText = findViewById(R.id.empty_text);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        title.setText(R.string.user_official);
        mPresenter = new OfficialPresenter();
        mPresenter.attachView(this);
        mAdapter = new OfficialAdapter(context);
        mAdapter.setListener(mListener);
        officialRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        officialRecyclerView.setLayoutManager(layoutManager);
        LinearItemDivider divider = new LinearItemDivider(context, LinearItemDivider.VERTICAL_LIST);
        divider.setDivider(getResources().getDrawable(R.drawable.voa_ranking_divider));
        officialRecyclerView.addItemDecoration(divider);
//        officialRecyclerView.setHasFixedSize(true);
        officialRecyclerView.setItemAnimator(new DefaultItemAnimator());
        refreshLayout.setOnRefreshListener(refreshLayout -> refreshData());
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!NetStateUtil.isConnected(context)) {
                    refreshLayout.finishRefresh();
                    showToast(R.string.please_check_network);
                    return;
                }
                PAGE_NUM++;
                mPresenter.getMoreAccount(PAGE_NUM, PAGE_SIZE);
                refreshLayout.finishLoadMore(2000);
            }
        });
        showLoadingLayout();
        refreshData();
    }

    private void refreshData() {
        if (!NetStateUtil.isConnected(context)) {
            refreshLayout.finishRefresh();
            showToast(R.string.please_check_network);
            return;
        }
        PAGE_NUM = 1;
        mPresenter.getOfficialAccount(PAGE_NUM, PAGE_SIZE);
        refreshLayout.finishRefresh(2000);
    }

    @Override
    public void setEmptyAccount() {
        emptyView.setVisibility(View.VISIBLE);
        emptyText.setText("暂时没有相应的数据");
        refreshLayout.setVisibility(View.GONE);
    }

    @Override
    public void setDataAccount(List<OfficialResponse.OfficialAccount> data) {
        Log.e("OfficialPresenter", " setDataAccount ");
        mAdapter.setData(data);
        mAdapter.notifyDataSetChanged();
        emptyView.setVisibility(View.GONE);
        refreshLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void setMoreAccount(List<OfficialResponse.OfficialAccount> data) {
        mAdapter.addData(data);
        mAdapter.notifyDataSetChanged();
        emptyView.setVisibility(View.GONE);
        refreshLayout.setVisibility(View.VISIBLE);
    }

    public void showLoadingLayout() {
        CommonProgressDialog.showProgressDialog(context);
    }

    public void dismissLoadingLayout() {
        CommonProgressDialog.dismissProgressDialog();
    }

    public void showToast(int resId) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
    }

    public void showToast(String resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }
}
