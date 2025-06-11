package com.iyuba.music.dubbing;

import android.content.Context;

import com.iyuba.module.mvp.BasePresenter;
import com.iyuba.music.R;
import com.iyuba.music.data.DataManager;
import com.iyuba.music.data.model.Thumb;
import com.iyuba.music.data.model.ThumbsResponse;
import com.iyuba.music.dubbing.utils.NetStateUtil;
import com.iyuba.music.dubbing.utils.RxUtil;
import com.iyuba.music.dubbing.utils.ThumbAction;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

public class WatchDubbingPresenter extends BasePresenter<WatchDubbingMvpView> {


    private final DataManager mDataManager;

    private Disposable mGetThumbSub;
    private Disposable mDoAgreeSub;
    private Disposable mDoAgainstSub;

    private Thumb mThumb;

    public int mUid;

    public WatchDubbingPresenter(DataManager dataManager, int uid) {
        this.mDataManager = dataManager;
        mUid = uid;
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mGetThumbSub);
        RxUtil.unsubscribe(mDoAgreeSub);
        RxUtil.unsubscribe(mDoAgainstSub);
    }

    public void doThumb(int id) {
        if (mThumb == null) {
            doAgreeThumb(id);
        }
    }

    private void doAgreeThumb(final int id) {
        checkViewAttached();
        RxUtil.unsubscribe(mDoAgreeSub);
        mDoAgreeSub = mDataManager.doAgree(id)
                .compose(com.iyuba.module.toolbox.RxUtil.<ThumbsResponse>applySingleIoScheduler())
                .subscribe(new Consumer<ThumbsResponse>() {
                    @Override
                    public void accept(ThumbsResponse response) throws Exception {
                        if (isViewAttached()) {
                            getMvpView().updateThumbIv(ThumbAction.THUMB);
                            getMvpView().updateThumbNumTv(String.valueOf(id));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        if (isViewAttached()) {
                            if (!NetStateUtil.isConnected((Context) getMvpView())) {
                                getMvpView().showToast(R.string.please_check_network);
                            } else {
                                getMvpView().showToast(R.string.request_fail);
                            }
                        }
                    }
                });
    }

    private void initThumb(int id) {
        if (mThumb == null) {
            mThumb = new Thumb();
            mThumb.uid = mUid;
            mThumb.commentId = (id);
        }
    }
}
