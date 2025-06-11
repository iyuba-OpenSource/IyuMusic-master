package com.iyuba.music.dubbing;

import com.iyuba.module.mvp.MvpView;

/**
 * Created by Administrator on 2016/12/10 0010.
 */

public interface WatchDubbingMvpView extends MvpView {
    void updateThumbIv(int action);

    void updateThumbNumTv(String id);

    void showToast(int resId);
}
