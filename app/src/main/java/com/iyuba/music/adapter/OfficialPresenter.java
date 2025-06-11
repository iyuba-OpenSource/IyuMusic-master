package com.iyuba.music.adapter;

import android.util.Log;

import com.iyuba.module.mvp.BasePresenter;
import com.iyuba.module.toolbox.RxUtil;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.data.DataManager;
import com.iyuba.music.data.model.OfficialResponse;
import com.iyuba.music.dubbing.utils.NetStateUtil;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

/**
 * Created by carl shen on 2021/8/17
 * New English Music, new study experience.
 */
public class OfficialPresenter extends BasePresenter<OfficialMvpView> {
    private final DataManager mDataManager;

    private Disposable mGetCollectionSub;

    public OfficialPresenter() {
        mDataManager = DataManager.getInstance();
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.dispose(mGetCollectionSub);
    }

    public void getOfficialAccount(int pageNum, int pageSize) {
        checkViewAttached();
        RxUtil.dispose(mGetCollectionSub);
        getMvpView().showLoadingLayout();
        mGetCollectionSub = mDataManager.getOfficialAccount(pageNum, pageSize)
                .compose(RxUtil.<OfficialResponse>applySingleIoScheduler())
                .subscribe(new Consumer<OfficialResponse>() {
                    @Override
                    public void accept(OfficialResponse response) throws Exception {
                        getMvpView().dismissLoadingLayout();
                        if ((response == null) || (response.data == null)) {
                            getMvpView().setEmptyAccount();
                            Log.e("OfficialPresenter", "getOfficialAccount response == null ");
                            return;
                        }
                        Log.e("OfficialPresenter", "getOfficialAccount data.size " + response.data.size());
                        if(response.result == 200) {
                            getMvpView().setDataAccount(response.data);
                        } else {
                            getMvpView().setEmptyAccount();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        getMvpView().dismissLoadingLayout();
                        if(!NetStateUtil.isConnected(MusicApplication.getApp())) {
                            getMvpView().showToast(R.string.please_check_network);
                        } else {
                            getMvpView().showToast(R.string.request_fail);
                        }
                        if (throwable != null) {
                            Log.e("OfficialPresenter", "getOfficialAccount onError  " + throwable.getMessage());
                        }
                    }
                });
    }

    public void getMoreAccount(int pageNum, int pageSize) {
        checkViewAttached();
        RxUtil.dispose(mGetCollectionSub);
        getMvpView().showLoadingLayout();
        mGetCollectionSub = mDataManager.getOfficialAccount(pageNum, pageSize)
                .compose(RxUtil.<OfficialResponse>applySingleIoScheduler())
                .subscribe(new Consumer<OfficialResponse>() {
                    @Override
                    public void accept(OfficialResponse response) throws Exception {
                        getMvpView().dismissLoadingLayout();
                        if ((response == null) || (response.data == null)) {
                            getMvpView().showToast(R.string.request_no_more);
                            Log.e("OfficialPresenter", "getOfficialAccount response == null ");
                            return;
                        }
                        Log.e("OfficialPresenter", "getOfficialAccount data.size " + response.data.size());
                        if(response.result == 200) {
                            getMvpView().setMoreAccount(response.data);
                        } else {
                            getMvpView().showToast(R.string.request_no_more);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        getMvpView().dismissLoadingLayout();
                        if(!NetStateUtil.isConnected(MusicApplication.getApp())) {
                            getMvpView().showToast(R.string.please_check_network);
                        } else {
                            getMvpView().showToast(R.string.request_fail);
                        }
                        if (throwable != null) {
                            Log.e("OfficialPresenter", "getOfficialAccount onError  " + throwable.getMessage());
                        }
                    }
                });
    }

}
