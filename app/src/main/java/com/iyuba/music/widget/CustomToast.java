package com.iyuba.music.widget;

import android.util.Log;
import android.widget.Toast;

import com.iyuba.music.MusicApplication;
import com.iyuba.music.manager.RuntimeManager;


/**
 * 重载后toast 可同时触发
 */
public class CustomToast {
    public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;
    public static final int LENGTH_LONG = Toast.LENGTH_LONG;
    private Toast mToast;

    private CustomToast() {

    }

    public static CustomToast getInstance() {
        return SingleInstanceHelper.instance;
    }

    public void showToast(String text) {
        showToast(text, LENGTH_SHORT);
    }

    public void showToast(int resId, int duration) {
        showToast(RuntimeManager.getString(resId), duration);
    }

    public void showToast(int resId) {
        showToast(MusicApplication.getApp().getString(resId), LENGTH_SHORT);
    }

    public void showToast(String text, int duration) {

        if (text == null) {

            return;
        }
        Toast.makeText(MusicApplication.getApp(), text, duration).show();
    }

    private static class SingleInstanceHelper {
        private static CustomToast instance = new CustomToast();
    }
}
