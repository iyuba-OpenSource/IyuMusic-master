package com.iyuba.music.dubbing;

import android.util.Base64;

import com.iyuba.module.mvp.BasePresenter;
import com.iyuba.music.data.Constant;
import com.iyuba.music.data.DataManager;
import com.iyuba.music.data.model.IntegralBean;
import com.iyuba.music.data.model.PdfResponse;
import com.iyuba.music.dubbing.utils.RxUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

public class LessonPlayPresenter extends BasePresenter<LessonPlayMvpView> {

    public static int TYPE_DOWNLOAD = 1000;
    public static int PDF_ENG = 1001;
    public static int PDF_CN = 1002;
    public static int PDF_BOTH = 1003;

    private DataManager mDataManager;

    private Disposable mIntegralSub;
    private Disposable mGetPdfSub;

    public LessonPlayPresenter() {
        mDataManager = DataManager.getInstance();
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mIntegralSub);
    }


    public void deductIntegral(final int type, int uId, int voaId) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        String flag = null;
        try {
            flag = Base64.encodeToString(
                    URLEncoder.encode(df.format(new Date(System.currentTimeMillis())), "UTF-8").getBytes(), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        checkViewAttached();
        RxUtil.unsubscribe(mIntegralSub);
        mIntegralSub = mDataManager.deductIntegral(flag,uId, Constant.APP_ID, voaId)
                .compose(com.iyuba.module.toolbox.RxUtil.<IntegralBean>applySingleIoScheduler())
                .subscribe(new Consumer<IntegralBean>() {
                    @Override
                    public void accept(IntegralBean response) throws Exception {
                        if (isViewAttached()) {
                            getMvpView().onDeductIntegralSuccess(type);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        if (isViewAttached()) {
                            throwable.printStackTrace();
                            getMvpView().showToast(" 积分扣取失败！");
                        }
                    }
                });
    }

    public void getPdf(int voaId , int type ){
        RxUtil.unsubscribe(mGetPdfSub);
        mGetPdfSub = mDataManager.getPdf("voa" , voaId, type)
                .compose(com.iyuba.module.toolbox.RxUtil.<PdfResponse>applySingleIoScheduler())
                .subscribe(new Consumer<PdfResponse>() {
                    @Override
                    public void accept(PdfResponse response) throws Exception {
                        if (isViewAttached()) {
                            getMvpView().showPdfFinishDialog(response.path);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                        if (isViewAttached()) {
                            throwable.printStackTrace();
                            getMvpView().showToast("生成pdf失败");
                        }
                    }
                });
    }
}
