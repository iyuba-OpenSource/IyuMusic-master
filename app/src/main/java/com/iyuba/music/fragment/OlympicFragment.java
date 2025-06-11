package com.iyuba.music.fragment;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.iyuba.music.R;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.download.DownloadUtil;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfo;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.ground.GroundNewsAdapter;
import com.iyuba.music.ground.VideoPlayerActivity;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.mainpanelrequest.MTVRequest;
import com.iyuba.music.request.mainpanelrequest.OlympicRequest;
//import com.iyuba.music.util.AD.ADutils;
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
 * Created by 10202 on 2017/11/4.
 */
public class OlympicFragment extends BaseRecyclerViewFragment implements MySwipeRefreshLayout.OnRefreshListener {
    private ArrayList<Article> MTVList;
    private GroundNewsAdapter MTVAdapter;
    private ArticleOp articleOp;
    private LocalInfoOp localInfoOp;
    private int curPage;
    private boolean isLastPage = false;
    private int icid = 0;
    //有道广告
    private YouDaoRecyclerAdapter mAdAdapter;

    public OlympicFragment() {
    }

    public OlympicFragment(int icid) {
        this.icid = icid;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLastPage = false;
        localInfoOp = new LocalInfoOp();
        articleOp = new ArticleOp();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        swipeRefreshLayout.setOnRefreshListener(this);
        MTVList = new ArrayList<>();
        MTVAdapter = new GroundNewsAdapter(context, false);
        if (DownloadUtil.checkVip()) {
            MTVAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    StudyManager.getInstance().setListFragmentPos(OlympicFragment.this.getClass().getName());
                    StudyManager.getInstance().setStartPlaying(true);
                    StudyManager.getInstance().setLesson("music");
                    StudyManager.getInstance().setSourceArticleList(MTVList);
                    StudyManager.getInstance().setCurArticle(MTVList.get(position));
                    StudyManager.getInstance().setApp("101");
                    context.startActivity(new Intent(context, StudyActivity.class));
                }

                @Override
                public void onItemLongClick(View view, int position) {
                }
            });
            recyclerView.setAdapter(MTVAdapter);
        } else {
            mAdAdapter = new YouDaoRecyclerAdapter(getActivity(), MTVAdapter, YouDaoNativeAdPositioning.clientPositioning().addFixedPosition(4).enableRepeatingPositions(5));
            MTVAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    StudyManager.getInstance().setListFragmentPos(OlympicFragment.this.getClass().getName());
                    StudyManager.getInstance().setStartPlaying(true);
                    StudyManager.getInstance().setLesson("music");
                    StudyManager.getInstance().setSourceArticleList(MTVList);
                    StudyManager.getInstance().setCurArticle(MTVList.get(position));
                    StudyManager.getInstance().setApp("101");
                    context.startActivity(new Intent(context, StudyActivity.class));
                }

                @Override
                public void onItemLongClick(View view, int position) {
                }
            });

//             绑定界面组件与广告参数的映射关系，用于渲染广告
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
            recyclerView.setAdapter(mAdAdapter);

         /*   if (AdTimeCheck.setAd()) {
                ADutils aDutils = new ADutils(getActivity(), MTVAdapter, recyclerView);
                aDutils.getLiuAd();
//                mAdAdapter.loadAds(ConstantManager.YOUDAOSECRET, mRequestParameters);
            }*/

        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout.setRefreshing(true);
        onRefresh(0);
    }

    private void getData() {
        OlympicRequest.exeRequest(OlympicRequest.generateUrl(curPage, icid), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg + context.getString(R.string.article_local));
                getDbData();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg + context.getString(R.string.article_local));
                getDbData();
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
                    MTVList.addAll(netData);
                    MTVAdapter.setData(MTVList);
                    if (curPage == 1) {
                    } else {
                        CustomToast.getInstance().showToast(curPage + "/" + (listEntity.getTotalCount() / 20 + (listEntity.getTotalCount() % 20 == 0 ? 0 : 1)), 800);
                    }
                    if (!StudyManager.getInstance().isStartPlaying()) {
                        StudyManager.getInstance().setLesson("music");
                        StudyManager.getInstance().setSourceArticleList(MTVList);
                        if (MTVList.size() != 0) {
                            StudyManager.getInstance().setCurArticle(MTVList.get(0));
                        }
                        StudyManager.getInstance().setApp("101");
                    } else if (OlympicFragment.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
                        StudyManager.getInstance().setSourceArticleList(MTVList);
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
                    articleOp.saveData(MTVList);
                }
            }
        });
    }

    /**
     * 下拉刷新
     *
     * @param index 当前分页索引
     */
    @Override
    public void onRefresh(int index) {
        curPage = 1;
        MTVList = new ArrayList<>();
        isLastPage = false;
        getData();
    }

    /**
     * 加载更多
     *
     * @param index 当前分页索引
     */
    @Override
    public void onLoad(int index) {
        if (MTVList.size() == 0) {

        } else if (!isLastPage) {
            curPage++;
            getData();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            CustomToast.getInstance().showToast(R.string.article_load_all);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdAdapter != null) {
            mAdAdapter.destroy();
        }
    }

    private void getDbData() {
        MTVList.addAll(articleOp.findDataByCategory(ConstantManager.appId, 401, MTVList.size(), 20));
        MTVAdapter.setData(MTVList);
    }
}
