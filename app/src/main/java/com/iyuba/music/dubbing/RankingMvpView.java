package com.iyuba.music.dubbing;


import com.iyuba.module.mvp.MvpView;
import com.iyuba.music.data.model.Ranking;

import java.util.List;

/**
 * Created by Administrator on 2016/11/28 0028.
 */

public interface RankingMvpView extends MvpView {
    void showRankings(List<Ranking> rankingList);

    void showEmptyRankings();

    void showToast(int id);

    void showToast(String msg);

    void showLoadingLayout();

    void dismissLoadingLayout();

    void dismissRefreshingView();
}
