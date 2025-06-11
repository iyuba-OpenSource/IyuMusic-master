package com.iyuba.music.view;



import com.iyuba.music.model.BaseModel;
import com.iyuba.music.model.bean.AdEntryBean;
import com.iyuba.music.presenter.IBasePresenter;

import java.util.List;

import io.reactivex.disposables.Disposable;

public interface SplashContract {


    interface SplashView extends LoadingView {

        void getAdEntryAllComplete(AdEntryBean adEntryBean);
    }

    interface SplashPresenter extends IBasePresenter<SplashView> {

        void getAdEntryAll(String appId, int flag, String uid);
    }

    interface SplashModel extends BaseModel {

        Disposable getAdEntryAll(String appId, int flag, String uid, Callback callback);
    }

    interface Callback {

        void success(List<AdEntryBean> adEntryBeans);

        void error(Exception e);
    }
}
