package com.iyuba.music.dubbing;

import com.iyuba.module.mvp.MvpView;

/**
 * Created by Administrator on 2016/12/9 0009.
 */

public interface PreviewMvpView extends MvpView {

    void startLoginActivity();

    void showToast(int resId);

    void showToast(String resId);

    void showShareView(String url);

    void showShareHideReleaseButton();

    void showLoadingDialog();

    void dismissLoadingDialog();

    void showPublishSuccess(int resID);

    void showPublishFailure(int resID);

    void startMainActivity();
}
