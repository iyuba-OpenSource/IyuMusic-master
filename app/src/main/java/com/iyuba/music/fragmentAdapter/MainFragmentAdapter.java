package com.iyuba.music.fragmentAdapter;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/11/9.
 */
public class MainFragmentAdapter extends FragmentPagerAdapter {
    public FragmentManager fm;
    public ArrayList<Fragment> list;

    public MainFragmentAdapter(FragmentManager fm, ArrayList<Fragment> list) {
        super(fm);
        this.fm = fm;
        this.list = list;

    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public Fragment instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        fm.beginTransaction().show(fragment).commitAllowingStateLoss();
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Fragment fragment = list.get(position);
        fm.beginTransaction().hide(fragment).commitAllowingStateLoss();
    }

    @Override
    public int getCount() {
        return list.size();
    }

}
