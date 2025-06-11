package com.iyuba.music.fragmentAdapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.iyuba.music.fragment.HelpFragment;

public class HelpFragmentAdapter extends FragmentStatePagerAdapter {
    private boolean usePullDown;

    public HelpFragmentAdapter(FragmentManager fm, boolean usePullDown) {
        super(fm);
        this.usePullDown = usePullDown;
    }

    @Override
    public Fragment getItem(int position) {
        return HelpFragment.newInstance(position, usePullDown);
    }

    @Override
    public int getCount() {
        return 5;
    }
}
