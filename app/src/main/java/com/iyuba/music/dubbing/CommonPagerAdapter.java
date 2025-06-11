package com.iyuba.music.dubbing;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class CommonPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragments;
    private String[] mTitles;

    public CommonPagerAdapter(FragmentManager fm, @NonNull List<Fragment> list, String[] titles) {
        super(fm);
        this.mFragments = list;
        if (titles != null) {
            this.mTitles = titles;
        }

    }
    public void setFragments( List<Fragment> list) {
        this.mFragments = list;

    }
    public Fragment getItem(int position) {
        return (Fragment)this.mFragments.get(position);
    }

    public int getCount() {
        return this.mFragments.size();
    }

    public CharSequence getPageTitle(int position) {
        return this.mTitles[position];
    }

    public Parcelable saveState() {
        return null;
    }
}
