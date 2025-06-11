package com.iyuba.music.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.widget.MusicListPop;
import com.iyuba.music.widget.imageview.PageIndicator;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class MusicPopFragmentRoot extends DialogFragment {


    private List<MusicPopFragment> list = new ArrayList<>();

    private float lastChange = 0;

    private ViewPager viewPager;

    private Context context;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pop_musiclist, null);

        initView(view);

        return view;
    }

    private void initView(View view) {
        list.clear();

        viewPager = view.findViewById(R.id.viewpager);
        final PageIndicator pop_indicator = view.findViewById(R.id.pop_indicator);
        TextView close = view.findViewById(R.id.close);
        lastChange = 0;

        setViewPagerView();

        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();

//        PopMusicPageAdapter popMusicPageAdapter = new PopMusicPageAdapter(fragmentManager, list);
//        viewPager.setAdapter(popMusicPageAdapter);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (lastChange != 0 && positionOffset != 0) {
                    if (lastChange > positionOffset) {//左滑
                        pop_indicator.setDirection(PageIndicator.LEFT);
                        pop_indicator.setMovePercent(position + 1, positionOffset);
                    } else {
                        pop_indicator.setDirection(PageIndicator.RIGHT);
                        pop_indicator.setMovePercent(position, positionOffset);
                    }
                }
                lastChange = positionOffset;
            }

            @Override
            public void onPageSelected(int position) {
                pop_indicator.setDirection(PageIndicator.NONE);
                pop_indicator.setCurrentItem(viewPager.getCurrentItem());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

    }


    private void setViewPagerView() {


        MusicPopFragment popFragmentFavor = new MusicPopFragment().newInstance(MusicListPop.FAVOR_TYPE);
        MusicPopFragment popFragmentListen = new MusicPopFragment().newInstance(MusicListPop.LISTEN_TYPE);
        MusicPopFragment popFragmentout = new MusicPopFragment().newInstance("");

        list.add(popFragmentFavor);
        list.add(popFragmentListen);
        list.add(popFragmentout);

    }

}
