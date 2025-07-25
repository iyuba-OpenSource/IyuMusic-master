package com.iyuba.music.fragment;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.iyuba.music.R;
import com.iyuba.music.data.local.DubDBManager;
import com.iyuba.music.download.DownloadUtil;
import com.iyuba.music.dubbing.LessonPlayActivity;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.MusicVoa;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfo;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.fragmentAdapter.DubbingAdapter;
import com.iyuba.music.ground.VideoPlayerActivity;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.mainpanelrequest.DubbingRequest;
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
 * Created by carl shen on 2020/9/18.
 */
public class DubbingFragment extends BaseRecyclerViewFragment implements MySwipeRefreshLayout.OnRefreshListener {
    private ArrayList<MusicVoa> MTVList;
    private DubbingAdapter DubAdapter;
    private ArticleOp articleOp;
    private LocalInfoOp localInfoOp;
    private int curPage;
    private boolean isLastPage = false;
    //有道广告
    private YouDaoRecyclerAdapter mAdAdapter;

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
        DubAdapter = new DubbingAdapter(context, false);
        if (DownloadUtil.checkVip()) {
            DubAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
//                    Intent intent = new Intent(context, VideoPlayerActivity.class);
//                    intent.putExtra("pos", position);
//                    intent.putExtra("articleList", MTVList);
//                    context.startActivity(intent);
                    context.startActivity(LessonPlayActivity.buildIntent(context, MTVList.get(position), MTVList));
                }

                @Override
                public void onItemLongClick(View view, int position) {
                }
            });
            recyclerView.setAdapter(DubAdapter);
        } else {
            mAdAdapter = new YouDaoRecyclerAdapter(getActivity(), DubAdapter, YouDaoNativeAdPositioning.clientPositioning().addFixedPosition(4).enableRepeatingPositions(5));
            DubAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
//                    Intent intent = new Intent(context, VideoPlayerActivity.class);
//                    intent.putExtra("pos", position);
//                    intent.putExtra("articleList", MTVList);
//                    context.startActivity(intent);
                    context.startActivity(LessonPlayActivity.buildIntent(context, MTVList.get(position), MTVList));
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

          /*  if (AdTimeCheck.setAd()) {
                ADutils aDutils = new ADutils(getActivity(), DubAdapter, recyclerView);
                aDutils.getLiuAd();
                mAdAdapter.loadAds(ConstantManager.YOUDAOSECRET, mRequestParameters);
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
        DubbingRequest.exeRequest(DubbingRequest.generateUrl(curPage), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg + context.getString(R.string.article_local));
//                getDbData();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg + context.getString(R.string.article_local));
//                getDbData();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void response(Object object) {
                swipeRefreshLayout.setRefreshing(false);
                BaseListEntity listEntity = (BaseListEntity) object;
                ArrayList<MusicVoa> netData = (ArrayList<MusicVoa>) listEntity.getData();
                isLastPage = listEntity.isLastPage();
                if (isLastPage) {
                    CustomToast.getInstance().showToast(R.string.article_load_all);
                } else {
                    if (netData == null || netData.size() < 1) {
                        return;
                    }
                    Log.e("DubbingFragment", "response netData.size(): " + netData.size());
                    MTVList.addAll(netData);
                    DubAdapter.setData(MTVList);
                    if (curPage == 1) {
                    } else {
                        CustomToast.getInstance().showToast(curPage + "/" + (listEntity.getTotalCount() / 20 + (listEntity.getTotalCount() % 20 == 0 ? 0 : 1)), 800);
                    }
                    String mUid = AccountManager.getInstance().getUserId();
                    final DubDBManager dubDBManager = DubDBManager.getInstance();
                    for (MusicVoa mLesson : netData) {
                        boolean ok = dubDBManager.setCollect("" + mLesson.voaId, mUid, mLesson.title, mLesson.descCn,
                                mLesson.pic, "" + mLesson.category);
                        Log.e("DubbingFragment", "response save mLesson: " + mLesson.toString());
                        Log.e("DubbingFragment", "response save ok: " + ok);
                    }
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
//        MTVList.addAll(articleOp.findDataByCategory(ConstantManager.appId, 401, MTVList.size(), 20));
//        MTVAdapter.setData(MTVList);
    }
}
