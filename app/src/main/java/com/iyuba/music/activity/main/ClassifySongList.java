package com.iyuba.music.activity.main;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.MainActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.adapter.study.SimpleNewsAdapter;
import com.iyuba.music.download.DownloadUtil;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfo;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOnDoubleClick;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.mainpanelrequest.ClassifyNewsRequest;
import com.iyuba.music.util.AdTimeCheck;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;
import com.youdao.sdk.nativeads.RequestParameters;
import com.youdao.sdk.nativeads.ViewBinder;
import com.youdao.sdk.nativeads.YouDaoNativeAdPositioning;
import com.youdao.sdk.nativeads.YouDaoNativeAdRenderer;
import com.youdao.sdk.nativeads.YouDaoRecyclerAdapter;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Created by 10202 on 2016/1/2.
 */
public class ClassifySongList extends BaseActivity implements MySwipeRefreshLayout.OnRefreshListener, IOnClickListener {
    private RecyclerView newsRecycleView;
    private ArrayList<Article> newsList;
    private SimpleNewsAdapter newsAdapter;
    private MySwipeRefreshLayout swipeRefreshLayout;
    private int curPage;
    private boolean isLastPage = false;
    private LocalInfoOp localInfoOp;
    private ArticleOp articleOp;
    private int classify;
    private String classifyName;
    //有道广告
    private YouDaoRecyclerAdapter mAdAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.classify_without_oper);
        context = this;
        classify = getIntent().getIntExtra("classify", 0);
        classifyName = getIntent().getStringExtra("classifyName");
        isLastPage = false;
        localInfoOp = new LocalInfoOp();
        articleOp = new ArticleOp();
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        newsRecycleView = (RecyclerView) findViewById(R.id.news_recyclerview);
        swipeRefreshLayout = (MySwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setColorSchemeColors(0xff259CF7, 0xff2ABB51, 0xffE10000, 0xfffaaa3c);
        swipeRefreshLayout.setFirstIndex(0);
        swipeRefreshLayout.setOnRefreshListener(this);
        newsRecycleView.setLayoutManager(new LinearLayoutManager(context));
        newsAdapter = new SimpleNewsAdapter(context);
        if (DownloadUtil.checkVip()) {
            newsAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    StudyManager.getInstance().setStartPlaying(true);
                    StudyManager.getInstance().setListFragmentPos(ClassifySongList.this.getClass().getName());
                    StudyManager.getInstance().setSourceArticleList(newsList);
                    StudyManager.getInstance().setLesson("music");

                    try {
                        StudyManager.getInstance().setCurArticle(newsList.get(position));
                        context.startActivity(new Intent(context, StudyActivity.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onItemLongClick(View view, int position) {
                }
            });
            newsRecycleView.setAdapter(newsAdapter);
        } else {
            mAdAdapter = new YouDaoRecyclerAdapter(this, newsAdapter, YouDaoNativeAdPositioning.clientPositioning().addFixedPosition(4).enableRepeatingPositions(5));

            // 绑定界面组件与广告参数的映射关系，用于渲染广告
            final YouDaoNativeAdRenderer adRenderer = new YouDaoNativeAdRenderer(
                    new ViewBinder.Builder(R.layout.native_ad_row_music)
                            .titleId(R.id.native_title)
                            .mainImageId(R.id.native_main_image).build());
            mAdAdapter.registerAdRenderer(adRenderer);
            final Location location = null;
            final String keywords = null;
            // 声明app需要的资源，这样可以提供高质量的广告，也会节省网络带宽
            final EnumSet<RequestParameters.NativeAdAsset> desiredAssets = EnumSet.of(
                    RequestParameters.NativeAdAsset.TITLE, RequestParameters.NativeAdAsset.TEXT,
                    RequestParameters.NativeAdAsset.ICON_IMAGE, RequestParameters.NativeAdAsset.MAIN_IMAGE,
                    RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT);
            RequestParameters mRequestParameters = new RequestParameters.RequestParametersBuilder()
                    .location(location).keywords(keywords)
                    .desiredAssets(desiredAssets).build();
            newsRecycleView.setAdapter(mAdAdapter);

            newsAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    StudyManager.getInstance().setStartPlaying(true);
                    StudyManager.getInstance().setListFragmentPos(ClassifySongList.this.getClass().getName() + classify);
                    StudyManager.getInstance().setSourceArticleList(newsList);
                    StudyManager.getInstance().setLesson("music");
                    try {
                        StudyManager.getInstance().setCurArticle(newsList.get(position));
                        context.startActivity(new Intent(context, StudyActivity.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onItemLongClick(View view, int position) {
                }
            });
            newsRecycleView.setAdapter(newsAdapter);
           /* if (AdTimeCheck.setAd()) {
                ADutils aDutils = new ADutils(ClassifySongList.this, newsAdapter, newsRecycleView);
                aDutils.getLiuAd();
                mAdAdapter.loadAds(ConstantManager.YOUDAOSECRET, mRequestParameters);
            }*/

        }
        swipeRefreshLayout.setRefreshing(true);
        onRefresh(0);
    }

    @Override
    protected void setListener() {
        super.setListener();
        toolBarLayout.setOnTouchListener(new IOnDoubleClick(this, context.getString(R.string.list_double)));
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        if (classifyName.length() < 10) {
            title.setText(classifyName);
        } else {
            title.setText(classifyName.substring(0, 8) + "...");
        }
    }

    @Override
    public void onBackPressed() {
        if (!mipush) {
            super.onBackPressed();
        } else {
            startActivity(new Intent(context, MainActivity.class));
        }
    }

    /**
     * 下拉刷新
     *
     * @param index 当前分页索引
     */
    @Override
    public void onRefresh(int index) {
        curPage = 1;
        newsList = new ArrayList<>();
        isLastPage = false;
        getNewsData();
    }

    /**
     * 加载更多
     *
     * @param index 当前分页索引
     */
    @Override
    public void onLoad(int index) {
        if (newsList.size() == 0) {

        } else if (!isLastPage) {
            curPage++;
            getNewsData();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            CustomToast.getInstance().showToast(R.string.article_load_all);
        }
    }

    private void getNewsData() {
        ClassifyNewsRequest.exeRequest(ClassifyNewsRequest.generateUrl(classify, curPage), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg + context.getString(R.string.article_local));
                getDbData();
                if ((ClassifySongList.this.getClass().getName() + classify).equals(StudyManager.getInstance().getListFragmentPos())) {
                    StudyManager.getInstance().setSourceArticleList(newsList);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg + context.getString(R.string.article_local));
                getDbData();
                if (ClassifySongList.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
                    StudyManager.getInstance().setSourceArticleList(newsList);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void response(Object object) {
                swipeRefreshLayout.setRefreshing(false);
                BaseListEntity listEntity = (BaseListEntity) object;
                ArrayList<Article> netData = (ArrayList<Article>) listEntity.getData();
                isLastPage = listEntity.isLastPage();
                if (isLastPage) {
                    CustomToast.getInstance().showToast(R.string.article_load_all);
                } else {
                    newsList.addAll(netData);
                    newsAdapter.setDataSet(newsList);
                    if (curPage != 1) {
                        CustomToast.getInstance().showToast(curPage + "/" + (listEntity.getTotalCount() / 20 + (listEntity.getTotalCount() % 20 == 0 ? 0 : 1)), 800);
                    }
                    if (ClassifySongList.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
                        StudyManager.getInstance().setSourceArticleList(newsList);
                    }
                    LocalInfo localinfo;
                    for (Article temp : netData) {
                        temp.setApp(ConstantManager.appId);
                        localinfo = localInfoOp.findDataById(temp.getApp(), temp.getId());
                        if (localinfo.getId() == 0) {
                            localinfo.setApp(temp.getApp());
                            localinfo.setId(temp.getId());
                            localInfoOp.saveData(localinfo);
                        }
                    }
                    articleOp.saveData(netData);
                }
            }
        });
    }

    @Override
    public void onClick(View view, Object message) {
        newsRecycleView.scrollToPosition(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdAdapter != null) {
            mAdAdapter.destroy();
        }
    }

    private void getDbData() {
        newsList.addAll(articleOp.findDataByCategory(ConstantManager.appId, classify, newsList.size(), 20));
        newsAdapter.setDataSet(newsList);
    }
}
