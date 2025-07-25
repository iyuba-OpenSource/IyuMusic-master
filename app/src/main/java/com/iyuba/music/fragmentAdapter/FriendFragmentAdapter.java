package com.iyuba.music.fragmentAdapter;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.iyuba.music.fragment.BaseFragment;
import com.iyuba.music.fragment.FanFragment;
import com.iyuba.music.fragment.FollowFragment;
import com.iyuba.music.fragment.RecommendFragment;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/11/9.
 */
public class FriendFragmentAdapter extends FragmentPagerAdapter {
    public FragmentManager fm;
    public ArrayList<BaseFragment> list;

    public FriendFragmentAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
        this.list = new ArrayList<>(3);
        list.add(new FollowFragment());
        list.add(new FanFragment());
        list.add(new RecommendFragment());
    }

    @Override
    public BaseFragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public BaseFragment instantiateItem(ViewGroup container, int position) {
        BaseFragment fragment = (BaseFragment) super.instantiateItem(container, position);
        fm.beginTransaction().show(fragment).commitAllowingStateLoss();
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        BaseFragment fragment = list.get(position);
        fm.beginTransaction().hide(fragment).commitAllowingStateLoss();
    }

    @Override
    public int getCount() {
        return 3;
    }
}
