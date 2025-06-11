package com.iyuba.music.activity.study;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.fragmentAdapter.ReadFragmentAdapter;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.widget.imageview.TabIndicator;

import java.util.ArrayList;
import java.util.Arrays;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

/**
 * 首页 - 解说 - 播放主页面 - 跟唱
 */
@RuntimePermissions
public class ReadActivity extends BaseActivity {
    private boolean needRestart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read);
        context = this;
        if (((MusicApplication) getApplication()).getPlayerService().getPlayer().isPlaying()) {
            needRestart = true;
            sendBroadcast(new Intent("iyumusic.pause"));
        } else {
            needRestart = false;
        }
        initWidget();
        setListener();
        changeUIByPara();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            ReadActivityPermissionsDispatcher.initLocationWithPermissionCheck(this);
        }

    }

    @Override
    protected void initWidget() {
        super.initWidget();
        ArrayList<String> tabTitle = new ArrayList<>();
        tabTitle.addAll(Arrays.asList(context.getResources().getStringArray(R.array.read_tab_title)));
        ViewPager viewPager = (ViewPager) findViewById(R.id.read_main);
        TabIndicator viewPagerIndicator = (TabIndicator) findViewById(R.id.tab_indicator);
        viewPager.setAdapter(new ReadFragmentAdapter(getSupportFragmentManager()));
        viewPagerIndicator.setTabItemTitles(tabTitle);
        viewPagerIndicator.setViewPager(viewPager, 0);
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(StudyManager.getInstance().getCurArticle().getTitle());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (needRestart) {
            sendBroadcast(new Intent("iyumusic.pause"));
        }
    }


    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ReadActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO})
    public void initLocation() {

    }

    @OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO})
    public void locationDenied() {

//        CustomToast.getInstance().showToast("存储权限开通才可以正常使用app，请到系统设置中开启", 3000);

    }
}
