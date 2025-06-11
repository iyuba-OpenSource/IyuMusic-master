package com.iyuba.music.util;

import android.content.Context;
import androidx.annotation.Nullable;

import android.util.AttributeSet;

/**
 * 跑马灯效果textview，XML中不能同时设置2个
 */

public class Marquee_Textview extends androidx.appcompat.widget.AppCompatTextView {
    public Marquee_Textview(Context context) {
        super(context);
    }

    public Marquee_Textview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Marquee_Textview(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public boolean isFocused(){
        return true;
    }
}