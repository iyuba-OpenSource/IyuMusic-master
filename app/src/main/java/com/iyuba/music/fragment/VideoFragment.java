package com.iyuba.music.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.headlinelibrary.HeadlineType;
import com.iyuba.headlinelibrary.event.HeadlineGoVIPEvent;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivity;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivityNew;
import com.iyuba.headlinelibrary.ui.content.VideoContentActivityNew;
import com.iyuba.headlinelibrary.ui.title.DropdownTitleFragmentNew;
import com.iyuba.headlinelibrary.ui.title.ITitleRefresh;
import com.iyuba.module.dl.BasicDLPart;
import com.iyuba.module.dl.DLItemEvent;
import com.iyuba.music.R;
import com.iyuba.music.activity.LoginActivity;
import com.iyuba.music.activity.vip.NewVipCenterActivity;
import com.iyuba.music.manager.AccountManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by iyuba on 2017/7/27.
 * 视频fragment
 */

public class VideoFragment extends BaseFragment {
    private TextView tv_title;
    private Context mContext;
    private DropdownTitleFragmentNew mExtraFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        EventBus.getDefault().register(this);
    }

    public VideoFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
//        initVariables();
    }

    protected boolean isVisible;
    private boolean isPrepared;
    private boolean isFirst = true;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isPrepared = true;
        lazyLoad();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_video, null);
        return root;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            lazyLoad();
        } else {
            isVisible = false;
        }

    }

    protected void lazyLoad() {
        if (!isPrepared || !isVisible || !isFirst) {
            return;
        }
        AccountManager.getInstance().setUser();
        initview();
        isFirst = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BasicDLPart event) {
        jumpToCorrectDLActivityByCate(mContext, event);
    }

    public void jumpToCorrectDLActivityByCate(Context context, BasicDLPart basicHDsDLPart) {
        switch (basicHDsDLPart.getType()) {


            case "voa":
            case "csvoa":
            case "bbc":
                startActivity(AudioContentActivityNew.getIntent2Me(context, basicHDsDLPart.getCategoryName(), basicHDsDLPart.getTitle(), basicHDsDLPart.getPic(), basicHDsDLPart.getType(), basicHDsDLPart.getId(), basicHDsDLPart.getTitleCn()));
                break;
            case "song":
                startActivity(AudioContentActivity.getIntent2Me(context, basicHDsDLPart.getCategoryName(), basicHDsDLPart.getTitle(), basicHDsDLPart.getPic(), basicHDsDLPart.getType(), basicHDsDLPart.getId(), basicHDsDLPart.getTitleCn()));
                break;
            case "voavideo":
            case "meiyu":
            case "ted":
            case "bbcwordvideo":
            case "japanvideos":
            case "topvideos":
                startActivity(VideoContentActivityNew.getIntent2Me(context, basicHDsDLPart.getCategoryName(), basicHDsDLPart.getTitle(), basicHDsDLPart.getPic(), basicHDsDLPart.getType(), basicHDsDLPart.getId(), basicHDsDLPart.getTitleCn(), ""));
                break;
        }
    }

    private void initview() {
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        AccountManager.getInstance().setUser();
        String[] types = new String[]{
                HeadlineType.SMALLVIDEO
        };


        Bundle bundle = DropdownTitleFragmentNew.buildArguments(10, types, false);
        mExtraFragment = DropdownTitleFragmentNew.newInstance(bundle);
//        mExtraFragment.setSmallVideoFragment(VideoListFragment.newInstance());
        transaction.replace(R.id.root, mExtraFragment);
        transaction.show(mExtraFragment);
        transaction.commit();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(getActivity());
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DLItemEvent dlEvent) {
        //视频下载后点击
        BasicDLPart dlPart = dlEvent.items.get(dlEvent.position);
        switch (dlPart.getType()) {
            case "voa":
            case "csvoa":
            case "bbc":
                startActivity(AudioContentActivityNew.getIntent2Me(getContext(), dlPart.getCategoryName(),
                        dlPart.getTitle(), dlPart.getTitleCn(), dlPart.getPic(), dlPart.getType(), dlPart.getId()));
                break;
            case "song":
                startActivity(AudioContentActivity.getIntent2Me(getContext(), dlPart.getCategoryName(),
                        dlPart.getTitle(), dlPart.getTitleCn(), dlPart.getPic(), dlPart.getType(), dlPart.getId()));
                break;
            case "voavideo":
            case "meiyu":
            case "ted":
            case "bbcwordvideo":
            case "topvideos":
            case "japanvideos":
                startActivity(VideoContentActivityNew.getIntent2Me(getContext(), dlPart.getCategoryName(), dlPart.getTitle(),
                        dlPart.getTitleCn(), dlPart.getPic(), dlPart.getType(), dlPart.getId(), ""));
                break;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(VIpChangeEvent vIpChangeEvent) {
        AccountManager.getInstance().setUser();
        if (mExtraFragment != null) {
            ((ITitleRefresh) mExtraFragment).refreshTitleContent();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(HeadlineGoVIPEvent event) {
        if (!AccountManager.Instace(mContext).checkUserLogin()) {
            startActivity(new Intent(mContext, LoginActivity.class));
            return;
        }
        Intent intent = new Intent(mContext, NewVipCenterActivity.class);
        startActivity(intent);
    }


}
