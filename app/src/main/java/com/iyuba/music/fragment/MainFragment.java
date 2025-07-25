package com.iyuba.music.fragment;

import static com.iyuba.music.manager.RuntimeManager.getApplication;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.iyuba.headlinelibrary.HeadlineType;
import com.iyuba.headlinelibrary.ui.title.TitleFragment;
import com.iyuba.headlinelibrary.ui.video.VideoMiniContentFragment;
import com.iyuba.music.BuildConfig;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.event.LoginOutEventbus;
import com.iyuba.music.event.MainFragmentEvent;
import com.iyuba.music.fragmentAdapter.MainFragmentAdapter;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.local_music.LocalMusicActivity;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.receiver.ChangeUIBroadCast;
import com.iyuba.music.request.account.AuditDateRequest;
import com.iyuba.music.service.PlayerService;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.util.ToastUtil;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.MusicListPop;
import com.iyuba.music.widget.RoundProgressBar;
import com.iyuba.music.widget.imageview.MorMusicButton;
import com.iyuba.music.widget.imageview.MorphButton;
import com.iyuba.music.widget.imageview.TabIndicator;
import com.iyuba.music.widget.player.StandardPlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * 主页面
 */
public class MainFragment extends BaseFragment {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private ViewPager viewPager;
    private Context context;
    private StandardPlayer player;
    private TabIndicator viewPagerIndicator;
    //控制栏
    private CircleImageView pic;
    private RoundProgressBar progressBar;
    private TextView curArticleTitle, curArticleInfo;
    private MorphButton pause;
    private Animation operatingAnim;
    private MainChangeUIBroadCast broadCast;
    private PlayerService playerService;
    private MusicListPop musicListPop;
    private String auditResult;  //审核结果
    private MorMusicButton morMusicButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    /**
     * 退出后触发
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginOutEventbus loginOutEventbus) {


        LocalInfoOp localInfoOp = new LocalInfoOp();
        localInfoOp.resetFavourite();
        if (musicListPop != null) {

            musicListPop.setPopMusicList(requireContext());
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main, null);
        ArrayList<String> title = new ArrayList<>();
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPagerIndicator = (TabIndicator) view.findViewById(R.id.tab_indicator);
        initPlayControl(view);

        AuditDateRequest.exeRequest(AuditDateRequest.generateUrl(), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg);
                if ((System.currentTimeMillis() / 1000) > BuildConfig.SMALL_VIDEO_TIME) {//正常展示

                    title.addAll(Arrays.asList(context.getResources().getStringArray(R.array.main_tab_title)));
                } else {//去掉小视频

                    title.addAll(Arrays.asList(context.getResources().getStringArray(R.array.main_tab_title_no_sv)));
                }
                viewPagerIndicator.setTabItemTitles(title);
                viewPagerIndicator.setHighLightColor(GetAppColor.getInstance().getAppColor());
                viewPagerIndicator.setViewPager(viewPager, 0);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg);

                if ((System.currentTimeMillis() / 1000) > BuildConfig.SMALL_VIDEO_TIME) {//正常展示

                    title.addAll(Arrays.asList(context.getResources().getStringArray(R.array.main_tab_title)));
                } else {//去掉小视频

                    title.addAll(Arrays.asList(context.getResources().getStringArray(R.array.main_tab_title_no_sv)));
                }
                viewPagerIndicator.setTabItemTitles(title);
                viewPagerIndicator.setViewPager(viewPager, 0);
            }

            @Override
            public void response(Object object) {
                auditResult = (String) object;
                if ("1".equals(auditResult)) {                    //审核中
                    title.addAll(Arrays.asList(context.getResources().getStringArray(R.array.main_tab_title2)));
                    viewPagerIndicator.setTabItemTitles(title);
                    viewPagerIndicator.setHighLightColor(GetAppColor.getInstance().getAppColor());
                    viewPagerIndicator.setViewPager(viewPager, 0);
                    morMusicButton.setVisibility(View.GONE);

                } else {

                    if ((System.currentTimeMillis() / 1000) > BuildConfig.SMALL_VIDEO_TIME) {//正常展示

                        title.addAll(Arrays.asList(context.getResources().getStringArray(R.array.main_tab_title)));
                    } else {//去掉小视频

                        title.addAll(Arrays.asList(context.getResources().getStringArray(R.array.main_tab_title_no_sv)));
                    }
                    viewPagerIndicator.setTabItemTitles(title);
                    viewPagerIndicator.setHighLightColor(GetAppColor.getInstance().getAppColor());
                    viewPagerIndicator.setViewPager(viewPager, 0);
                    morMusicButton.setVisibility(View.VISIBLE);
                }
            }
        });
        playerService = getApplication().getPlayerService();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<Fragment> list = new ArrayList<>();
        AuditDateRequest.exeRequest(AuditDateRequest.generateUrl(), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg);
                initMusic(list);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg);
                initMusic(list);
            }

            @Override
            public void response(Object object) {
                String result = (String) object;
                if ("1".equals(result)) {                    //审核中
                    list.add(new OlympicFragment(36));
                    list.add(new OlympicFragment(37));
                    list.add(new OlympicFragment(38));
                    list.add(new OlympicFragment(39));
                    MainFragmentAdapter mainFragmentAdapter = new MainFragmentAdapter(getActivity().getSupportFragmentManager(), list);
                    viewPager.setAdapter(mainFragmentAdapter);
                } else {
                    initMusic(list);
                }
            }
        });
        broadCast = new MainChangeUIBroadCast(this);
        IntentFilter intentFilter = new IntentFilter("com.iyuba.music.main");
        context.registerReceiver(broadCast, intentFilter);
    }

    private void initMusic(ArrayList<Fragment> list) {


        list.add(new NewsFragment());
        list.add(new ClassifyFragment());
        //list.add(new AnnouncerFragment());

        if ((System.currentTimeMillis() / 1000) > BuildConfig.SMALL_VIDEO_TIME) {

            TitleFragment music = TitleFragment.newInstance(TitleFragment.buildArguments(10, HeadlineType.SMALLVIDEO, "Music", 12));
            list.add(music);
        }

        //list.add(new OlympicFragment());
        list.add(new MTVFragment());
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.EXCEPT_2G)) {
            list.add(new SongCategoryFragment());
        } else {
            list.add(new MusicFragment());
        }
        list.add(new DubbingFragment());
        MainFragmentAdapter mainFragmentAdapter = new MainFragmentAdapter(getActivity().getSupportFragmentManager(), list);
        viewPager.setAdapter(mainFragmentAdapter);
        viewPager.setOffscreenPageLimit(1);
    }


    @Override
    public void onResume() {
        super.onResume();
        setContent();
        setImageState();
        handler.sendEmptyMessage(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseAnimation();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        context.unregisterReceiver(broadCast);
        pause.setOnClickListener(null);

//        mainFragmentAdapter.destroy();
    }

    private void initPlayControl(View root) {
        pic = root.findViewById(R.id.song_image);
        progressBar = root.findViewById(R.id.progressbar);
        progressBar.setCricleProgressColor(GetAppColor.getInstance().getAppColor());
        progressBar.setMax(100);
        curArticleTitle = (TextView) root.findViewById(R.id.curarticle_title);
        curArticleInfo = (TextView) root.findViewById(R.id.curarticle_info);
        ImageView former = (ImageView) root.findViewById(R.id.main_former);
        morMusicButton = (MorMusicButton) root.findViewById(R.id.main_latter);
        morMusicButton.setForegroundColorFilter(GetAppColor.getInstance().getAppColor(), PorterDuff.Mode.SRC_IN);
        pause = (MorphButton) root.findViewById(R.id.main_play);
        pause.setForegroundColorFilter(GetAppColor.getInstance().getAppColor(), PorterDuff.Mode.SRC_IN);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseClick();


            }
        });
        former.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formerClick();
            }
        });
        morMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                latterClick();


                if (StudyManager.getInstance().getListFragmentPos() == null) {

                    ToastUtil.showToast(context, "当前无播放列表");
                    return;
                }
                try {
                    musicListPop = new MusicListPop(getActivity());
                    musicListPop.showPop(context, pic);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        root.findViewById(R.id.rotate_image_layout).setOnClickListener(v -> {
            if ("101".equals(StudyManager.getInstance().getApp())) {
                context.startActivity(new Intent(context, LocalMusicActivity.class));
            } else {
                if (StudyManager.getInstance().getCurArticle() != null) {

                    if (StudyManager.getInstance().getListFragmentPos() == null) {
                        StudyManager.getInstance().setListFragmentPos("NewsFragment.class");
                    }
                    context.startActivity(new Intent(context,
                            StudyActivity.class));

                }
            }
        });
        root.findViewById(R.id.song_info_layout).setOnClickListener(v -> {
            if ("101".equals(StudyManager.getInstance().getApp())) {
                context.startActivity(new Intent(context, LocalMusicActivity.class));
            } else {
                if (StudyManager.getInstance().getCurArticle() != null) {
                    if (StudyManager.getInstance().getListFragmentPos() == null) {
                        StudyManager.getInstance().setListFragmentPos("NewsFragment.class");
                    }

                    context.startActivity(new Intent(context,
                            StudyActivity.class));

                }
            }
        });
        if (ConfigManager.getInstance().isAutoRound()) {
            initAnimation();
        }
    }

    private void setContent() {
        Article curArticle = StudyManager.getInstance().getCurArticle();
        if (curArticle == null || TextUtils.isEmpty(curArticle.getTitle())) {
            curArticleTitle.setText(R.string.app_name);
            curArticleInfo.setText(R.string.app_intro);
            pic.setImageResource(R.mipmap.ic_launcher);
        } else {
            switch (StudyManager.getInstance().getApp()) {
                case "101":
                    curArticleTitle.setText(curArticle.getTitle());
                    curArticleInfo.setText(curArticle.getSinger());
                    pic.setImageResource(R.mipmap.ic_launcher);
                    break;
                case "209":
                    curArticleTitle.setText(curArticle.getTitle());
                    curArticleInfo.setText(curArticle.getSinger());
                    ImageUtil.loadImage("http://staticvip." + ConstantManager.IYUBA_CN + "images/song/" + curArticle.getPicUrl(), pic, R.mipmap.ic_launcher);
                    break;
                default:
                    curArticleTitle.setText(curArticle.getTitle());
                    curArticleInfo.setText(curArticle.getTitle_cn());
                    ImageUtil.loadImage(curArticle.getPicUrl(), pic, R.mipmap.ic_launcher);
                    break;
            }
        }
    }

    private void initAnimation() {
        operatingAnim = AnimationUtils.loadAnimation(context, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
    }

    private void startAnimation() {
        if (ConfigManager.getInstance().isAutoRound()) {
            if (operatingAnim == null) {
                initAnimation();
            }
            pic.startAnimation(operatingAnim);
        }
    }

    private void pauseAnimation() {
        if (ConfigManager.getInstance().isAutoRound()) {
            pic.clearAnimation();
        }
    }

    private void pauseClick() {

        if (StudyManager.getInstance().getListFragmentPos() == null) {
            StudyManager.getInstance().setListFragmentPos("NewsFragment.class");
        }
        context.sendBroadcast(new Intent("iyumusic.pause"));

    }

    private void formerClick() {
        context.sendBroadcast(new Intent("iyumusic.before"));
        pause.postDelayed(new Runnable() {
            @Override
            public void run() {
                setContent();
            }
        }, 500);
        if (!player.isPlaying()) {
            context.sendBroadcast(new Intent("iyumusic.pause"));
        }
    }

    private void latterClick() {
        context.sendBroadcast(new Intent("iyumusic.next"));
        pause.postDelayed(new Runnable() {
            @Override
            public void run() {
                setContent();
            }
        }, 500);
        if (!player.isPlaying()) {
            context.sendBroadcast(new Intent("iyumusic.pause"));
        }
    }

    private void setImageState() {
        if (getApplication().getPlayerService() == null) {
            pause.setState(MorphButton.PAUSE_STATE);
        } else {
            player = getApplication().getPlayerService().getPlayer();
            if (player == null) {
                pause.setState(MorphButton.PAUSE_STATE);
            } else if (player.isPlaying()) {
                pause.setState(MorphButton.PLAY_STATE);
            } else {
                pause.setState(MorphButton.PAUSE_STATE);
            }
        }


    }

    public void setShowItem(int pos) {
        viewPagerIndicator.setPosDirect(pos);
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<MainFragment> {
        @Override
        public void handleMessageByRef(final MainFragment fragment, Message msg) {
            switch (msg.what) {
                case 0:
                    if (fragment.player != null && fragment.player.getDuration() != 0) {
                        fragment.progressBar.setProgress(fragment.player.getCurrentPosition() * 100 / fragment.player.getDuration());
                        if (fragment.player.isPlaying()) {
                            if (fragment.pic.getAnimation() == null) {
                                fragment.startAnimation();
                            }
                            if (fragment.pause.getState() == MorphButton.PAUSE_STATE) {
                                fragment.pause.setState(MorphButton.PLAY_STATE);
                            }
                        } else if (!fragment.player.isPlaying()) {
                            if (fragment.pic.getAnimation() != null) {
                                fragment.pauseAnimation();
                            }
                            if (fragment.pause.getState() == MorphButton.PLAY_STATE) {
                                fragment.pause.setState(MorphButton.PAUSE_STATE);
                            }
                        }
                    }
                    fragment.handler.sendEmptyMessageDelayed(0, 500);
                    break;
            }
        }
    }

    public static class MainChangeUIBroadCast extends ChangeUIBroadCast {
        private final WeakReference<MainFragment> mWeakReference;

        public MainChangeUIBroadCast(MainFragment fragment) {
            mWeakReference = new WeakReference<>(fragment);
        }

        @Override
        public void refreshUI(String message) {
            if (!TextUtils.isEmpty(message) && mWeakReference.get() != null) {
                switch (message) {
                    case "change":
                        mWeakReference.get().setContent();
                        if (mWeakReference.get().musicListPop != null) {
                            mWeakReference.get().musicListPop.handler.sendEmptyMessage(3);
                        }
                        break;
                    case "pause":
                        mWeakReference.get().setImageState();
                        break;
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(MainFragmentEvent event) {

        if (StudyManager.getInstance().getCurArticle().getId() == event.articles.get(event.indexd).getId() &&
                StudyManager.getInstance().getListFragmentPos() == event.type) {


        } else {

            StudyManager.getInstance().setListFragmentPos(event.type);
            StudyManager.getInstance().setStartPlaying(true);
            StudyManager.getInstance().setSourceArticleList((ArrayList<Article>) event.articles);
            StudyManager.getInstance().setLesson("music");
            StudyManager.getInstance().setCurArticle(event.articles.get(event.indexd));

            ((MusicApplication) getApplication()).getPlayerService().startPlay(StudyManager.getInstance().getCurArticle(), false);
            ((MusicApplication) getApplication()).getPlayerService().setCurArticleId(StudyManager.getInstance().getCurArticle().getId());
            setContent();
            handler.sendEmptyMessage(0);
            if (musicListPop != null) {
                musicListPop.handler.sendEmptyMessage(3);
            }


        }


    }
}
