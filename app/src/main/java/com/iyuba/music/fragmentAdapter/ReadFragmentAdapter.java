package com.iyuba.music.fragmentAdapter;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.iyuba.music.activity.study.ReadFragment;
import com.iyuba.music.activity.study.ReadNewFragment;
import com.iyuba.music.activity.study.ReadTopFragment;
import com.iyuba.music.fragment.BaseFragment;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/12/17.
 */
public class ReadFragmentAdapter extends FragmentPagerAdapter {
    public FragmentManager fm;
    public ArrayList<BaseFragment> list;

    public ReadFragmentAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
        this.list = new ArrayList<>(3);
        list.add(new ReadFragment());
//        list.add(new ReadTopFragment());
        list.add(new ReadNewFragment());
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
        return list.size();
    }
}

