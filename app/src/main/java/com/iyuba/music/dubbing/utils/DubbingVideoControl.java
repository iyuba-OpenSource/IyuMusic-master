package com.iyuba.music.dubbing.utils;

import android.content.Context;
import android.util.AttributeSet;

import com.iyuba.music.R;
import com.iyuba.music.dubbing.utils.NormalVideoControl;

/**
 * Created by Administrator on 2017/1/17/017.
 */

public class DubbingVideoControl extends NormalVideoControl {
    public DubbingVideoControl(Context context) {
        super(context);
    }

    public DubbingVideoControl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DubbingVideoControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dubbing_video_control;
    }
}
