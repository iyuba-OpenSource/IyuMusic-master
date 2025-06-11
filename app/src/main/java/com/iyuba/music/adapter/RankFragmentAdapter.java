package com.iyuba.music.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class RankFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> list;
    private FragmentManager fm;
    private List<String> tagList;

    public RankFragmentAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.fm = fm;
        this.list = list;
        tagList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            tagList.add(makeFragmentName(-1,i));
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }



    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }




    public static String makeFragmentName(int viewId, long index) {
        return "android:switcher:" + viewId + ":" + index;
    }



}
