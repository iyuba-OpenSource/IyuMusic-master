package com.iyuba.music.fragment;

import androidx.fragment.app.Fragment;

/**
 * Created by 10202 on 2016/1/1.
 */
public abstract class BaseRankFragment extends Fragment {

    protected boolean isVisible;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    protected void onInvisible() {

    }

    protected void onVisible() {
        lazyload();
    }

    protected abstract void lazyload();
}
