package com.iyuba.music.activity.discover;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.headlinelibrary.HeadlineType;
import com.iyuba.headlinelibrary.IHeadline;
import com.iyuba.headlinelibrary.IHeadlineManager;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivityNew;
import com.iyuba.headlinelibrary.ui.content.VideoContentActivityNew;
import com.iyuba.headlinelibrary.ui.title.DropdownTitleFragmentNew;
import com.iyuba.headlinelibrary.ui.title.HolderType;
import com.iyuba.module.dl.BasicDLPart;
import com.iyuba.module.dl.DLItemEvent;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.util.ScreenUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by 10202 on 2016/3/21.
 */
public class Video2Activity extends BaseActivity {
    private boolean needRestart;

    RelativeLayout root;


    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        ScreenUtils.setWindowStatusBarColor(this, R.color.blue_movie);
        context = Video2Activity.this;

        root = findViewById(R.id.root);

        EventBus.getDefault().register(this);

        int statusBarHeight = getStatusBarHeight(context);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) root.getLayoutParams();
        lp.topMargin = statusBarHeight;
        root.setLayoutParams(lp);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        IHeadlineManager.appId = ConstantManager.appId;
        IHeadlineManager.appName = ConstantManager.appEnglishName;

        String[] types = new String[]{
                HeadlineType.SMALLVIDEO,
        };


        Bundle bundle = DropdownTitleFragmentNew.buildArguments(8, HolderType.SMALL, types, true);
        DropdownTitleFragmentNew mExtraFragment = DropdownTitleFragmentNew.newInstance(bundle);

        fragmentTransaction.replace(R.id.root, mExtraFragment);
        fragmentTransaction.show(mExtraFragment);
        fragmentTransaction.commit();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DLItemEvent dlEvent) {
        //视频下载后点击
        BasicDLPart dlPart = dlEvent.items.get(dlEvent.position);
        switch (dlPart.getType()) {
            case "voa":
            case "csvoa":
            case "bbc":
                startActivity(AudioContentActivityNew.getIntent2Me(context,
                        dlPart.getCategoryName(), dlPart.getTitle(), dlPart.getTitleCn(),
                        dlPart.getPic(), dlPart.getType(), dlPart.getId()));
                break;
            case "voavideo":
            case "meiyu":
            case "ted":
            case "bbcwordvideo":
            case "topvideos":
                startActivity(VideoContentActivityNew.getIntent2Me(context,
                        dlPart.getCategoryName(), dlPart.getTitle(), dlPart.getTitle(),
                        dlPart.getPic(), dlPart.getType(), dlPart.getId(), ""));

                break;
        }

    }


    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    private int getStatusBarHeight(Context context) {
        if (Build.VERSION.SDK_INT < 19) {
            return 0;
        }
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }


}
