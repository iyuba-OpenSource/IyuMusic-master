package com.iyuba.music.dubbing;

import android.content.Context;

import com.iyuba.module.mvp.BasePresenter;
import com.iyuba.module.toolbox.RxUtil;
import com.iyuba.music.R;
import com.iyuba.music.data.DataManager;
import com.iyuba.music.data.model.GetRankingResponse;
import com.iyuba.music.data.model.Ranking;
import com.iyuba.music.data.model.ThumbsResponse;
import com.iyuba.music.dubbing.utils.NetStateUtil;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

public class RankPresenter extends BasePresenter<RankingMvpView> {
    private final DataManager mDataManager;


    private Disposable mGetRankingSub;
    private Disposable mDoAgreeSub;

    public RankPresenter() {
        mDataManager = DataManager.getInstance();
    }

    public RankPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    public void getRanking(int voaId, int pageNum, int pageSize) {
        checkViewAttached();
        RxUtil.dispose(mGetRankingSub);
        getMvpView().showLoadingLayout();
        mGetRankingSub = mDataManager.getThumbRanking(voaId, pageNum, pageSize)
                .compose(RxUtil.<GetRankingResponse>applySingleIoScheduler())
                .subscribe(new Consumer<GetRankingResponse>() {
                    @Override
                    public void accept(GetRankingResponse list) throws Exception {
                        if (isViewAttached()) {
                            getMvpView().dismissLoadingLayout();
                            getMvpView().dismissRefreshingView();
                                List<Ranking> rankingList = list.data;
                                if (rankingList != null && !rankingList.isEmpty()) {
                                    getMvpView().showRankings(rankingList);
                                } else {
                                    getMvpView().showEmptyRankings();
                                }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        if (isViewAttached()) {
                            getMvpView().dismissLoadingLayout();
                            getMvpView().dismissRefreshingView();
                            getMvpView().showToast(R.string.request_fail);
                        }
                    }
                });
    }

    public void doAgree(int id) {
        checkViewAttached();
        RxUtil.dispose(mDoAgreeSub);
        mDoAgreeSub = mDataManager.doAgree(id)
                .compose(RxUtil.<ThumbsResponse>applySingleIoScheduler())
                .subscribe(new Consumer<ThumbsResponse>() {
                    @Override
                    public void accept(ThumbsResponse list) throws Exception {
                        if (isViewAttached()) {
                            getMvpView().showToast("点赞成功");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        if (isViewAttached()) {
                            if(!NetStateUtil.isConnected((Context) getMvpView())) {
                                getMvpView().showToast(R.string.please_check_network);
                            } else {
                                getMvpView().showToast(R.string.request_fail);
                            }
                        }
                    }
                });
    }


    @Override
    public void detachView() {
        super.detachView();
        RxUtil.dispose(mGetRankingSub);
        RxUtil.dispose(mDoAgreeSub);
    }
}
