package com.iyuba.music.adapter;

import com.iyuba.module.mvp.MvpView;
import com.iyuba.music.data.model.OfficialResponse;

import java.util.List;

/**
 * Created by carl shen on 2021/8/18
 * English Music, new study experience.
 */
public interface OfficialMvpView extends MvpView {

    void showLoadingLayout();
    void dismissLoadingLayout();

    void setEmptyAccount();
    void setDataAccount(List<OfficialResponse.OfficialAccount> data);
    void setMoreAccount(List<OfficialResponse.OfficialAccount> data);

    void showToast(int resId);

}
