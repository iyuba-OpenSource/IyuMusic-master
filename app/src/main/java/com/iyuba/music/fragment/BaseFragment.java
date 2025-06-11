package com.iyuba.music.fragment;

import androidx.fragment.app.Fragment;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by 10202 on 2016/1/1.
 */
public  class BaseFragment extends Fragment {

    public boolean onBackPressed() {
        return false;
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();

    }



}
