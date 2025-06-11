package com.iyuba.music.adapter;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by 10202 on 2015/11/30.
 */
public class PopMusicPageAdapter extends PagerAdapter {

    //viewpage的适配器

    private List<View> list;

    public PopMusicPageAdapter(List<View> list) {

        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    //判断是否是否为同一张图片，这里返回方法中的两个参数做比较就可以
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    //设置viewpage内部东西的方法，如果viewpage内没有子空间滑动产生不了动画效果
    @Override
    public Object instantiateItem(ViewGroup container, int position) {


        container.addView(list.get(position));
        //最后要返回的是控件本身
        return list.get(position);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        //         super.destroyItem(container, position, object);
    }


}
