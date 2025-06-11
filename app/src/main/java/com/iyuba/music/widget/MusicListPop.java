package com.iyuba.music.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.adapter.PopMusicAdapter;
import com.iyuba.music.adapter.PopMusicPageAdapter;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfo;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.event.ChangePlayMode;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.newsrequest.FavorRequest;
import com.iyuba.music.util.ScreenUtils;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.iyuba.music.widget.imageview.PageIndicator;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by iyuba on 2018/2/1.
 */

public class MusicListPop extends PopupWindow {

    public static String LISTEN_TYPE = "com.iyuba.music.activity.main.ListenSongActivity" + "new";
    public static String FAVOR_TYPE = "com.iyuba.music.activity.main.FavorSongActivity" + "new";

    private PopupWindow window;

    private ViewPager viewPager;


    private IyubaDialog iyubaDialog;


    private List<View> list = new ArrayList<>();

    private float lastChange = 0;

    private PopMusicPageAdapter popMusicPageAdapter;


    private List<Article> newsList, localList, listOut = new ArrayList<>();
    private LocalInfoOp localInfoOp;
    private ArticleOp articleOp;

    public ImageView image_style_favor, image_style_listen, image_style_out, image_delete_all;

    public TextView tv_style, tv_songnum1, tv_songnum2, tv_songnumOut;

    private PopMusicAdapter popMusicAdapter1, popMusicAdapter2, popMusicAdapterOut;

    private RecyclerView recyclerView1, recyclerView2, recyclerViewout;
    private String type;

    public MusicListPop(Context context) {
        super(context);
        setPopMusicList(context);
        iyubaDialog = WaitingDialog.create(context, null);
    }

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:
                    tv_songnum1.setText("(" + msg.arg1 + "首)");
                    break;
                case 2:
                    tv_songnum2.setText("(" + msg.arg1 + "首)");
                    break;
                case 3:

                    getDataOut();
                    viewPager.setCurrentItem(0);
                    popMusicAdapterOut.setType(type);
                    popMusicAdapterOut.notifyDataSetChanged();

                    tv_songnumOut.setText("(" + listOut.size() + "首)");
                    int index = 0;
                    for (int i = 0; i < listOut.size(); i++) {

                        if (StudyManager.getInstance().getCurArticle().getId() == listOut.get(i).getId()) {
                            index = i;

                        }
                    }
                    LinearLayoutManager llm = (LinearLayoutManager) recyclerViewout.getLayoutManager();
                    llm.scrollToPositionWithOffset(index, 0);
                    llm.setStackFromEnd(false);
                    break;

                case 4:
                    tv_songnumOut.setText("(" + msg.arg1 + "首)");
                    break;

                default:
                    break;
            }
        }
    };

    public void setPopMusicList(final Context context) {

        Log.e("pop来源", context.getClass().getName().toString());
        // 用于PopupWindow的View
        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_musiclist, null, false);
        // 创建PopupWindow对象，其中：
        // 第一个参数是用于PopupWindow中的View，第二个参数是PopupWindow的宽度，
        // 第三个参数是PopupWindow的高度，第四个参数指定PopupWindow能否获得焦点

        window = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, ScreenUtils.getScreenHeight(context) / 5 * 3, true);
        // 设置PopupWindow的背景
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置PopupWindow是否能响应外部点击事件
        window.setOutsideTouchable(true);
        // 设置PopupWindow是否能响应点击事件
        window.setTouchable(true);
        // 显示PopupWindow，其中：
        // 第一个参数是PopupWindow的锚点，第二和第三个参数分别是PopupWindow相对锚点的x、y偏移
        // 或者也可以调用此方法显示PopupWindow，其中：
        // 第一个参数是PopupWindow的父View，第二个参数是PopupWindow相对父View的位置，
        // 第三和第四个参数分别是PopupWindow相对父View的x、y偏移
        // window.showAtLocation(parent, gravity, x, y);
        setWidget(contentView, context);

        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpaha((Activity) context, 1.0f);
            }
        });
        window.setAnimationStyle(R.style.PopupAnimation);
    }

    public void showPop(Context context, View viewParent) {

        backgroundAlpaha((Activity) context, 0.5f);
        window.showAtLocation(viewParent, Gravity.BOTTOM, 0, 0);
        handler.sendEmptyMessage(3);
        setStyle(ConfigManager.getInstance().getStudyPlayMode());


    }

    private void setWidget(View view, Context context) {
        list.clear();
        localInfoOp = new LocalInfoOp();
        articleOp = new ArticleOp();
        getData();
        getDataLast();
        getDataOut();

        viewPager = view.findViewById(R.id.viewpager);
        final PageIndicator pop_indicator = view.findViewById(R.id.pop_indicator);
        TextView close = view.findViewById(R.id.close);
        lastChange = 0;

        setViewPagerView(context);

        popMusicPageAdapter = new PopMusicPageAdapter(list);
        viewPager.setAdapter(popMusicPageAdapter);


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

                if (position == 1 && popMusicAdapter1 != null) {
                    getDataLast();
                    popMusicAdapter1.setList(localList);
                    popMusicAdapter1.notifyDataSetChanged();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });

    }


    private void setViewPagerView(Context context) {

        View viewOut = LayoutInflater.from(context).inflate(R.layout.pop_adapter_item_first, null);

        View view1 = LayoutInflater.from(context).inflate(R.layout.pop_adapter_item_first, null);

        View viewfavor = LayoutInflater.from(context).inflate(R.layout.pop_adapter_item_first, null);

        createFavor(viewfavor, context, newsList);

        createLocal(view1, context, localList);

        createOut(viewOut, context, listOut);

        list.add(viewOut);
        list.add(view1);
        list.add(viewfavor);

    }

    private void createFavor(View viewfavor, Context context, List<Article> list) {
        recyclerView2 = viewfavor.findViewById(R.id.recyclerview);
        recyclerView2.setLayoutManager(new LinearLayoutManager(context));
        recyclerView2.addItemDecoration(new DividerItemDecoration());
        popMusicAdapter2 = new PopMusicAdapter(list, context, handler);
        recyclerView2.setAdapter(popMusicAdapter2);
        popMusicAdapter2.setType(FAVOR_TYPE);
        tv_songnum2 = viewfavor.findViewById(R.id.text_songnum);
        tv_songnum2.setText("(" + list.size() + "首)");
        image_style_favor = viewfavor.findViewById(R.id.image_style);
        image_style_favor.setVisibility(View.INVISIBLE);
        TextView tv_style_favor = viewfavor.findViewById(R.id.text_style);
        tv_style_favor.setText("收藏列表");


        ImageView image_delete_all_favor = viewfavor.findViewById(R.id.image_delete_all);
        image_delete_all_favor.setVisibility(View.INVISIBLE);


    }


    private void createLocal(View viewLocal, Context context, List<Article> list) {


        recyclerView1 = viewLocal.findViewById(R.id.recyclerview);
        recyclerView1.setLayoutManager(new LinearLayoutManager(context));
        recyclerView1.addItemDecoration(new DividerItemDecoration());

        popMusicAdapter1 = new PopMusicAdapter(list, context, handler);
        recyclerView1.setAdapter(popMusicAdapter1);
        popMusicAdapter1.setType(LISTEN_TYPE);

        tv_songnum1 = viewLocal.findViewById(R.id.text_songnum);
        tv_songnum1.setText("(" + list.size() + "首)");
        image_style_listen = viewLocal.findViewById(R.id.image_style);
        image_style_listen.setVisibility(View.INVISIBLE);

        TextView tv_style_listen = viewLocal.findViewById(R.id.text_style);
        tv_style_listen.setText("最近播放");

        ImageView image_delete_all_Local = viewLocal.findViewById(R.id.image_delete_all);
        image_delete_all_Local.setVisibility(View.INVISIBLE);
    }


    private void createOut(View viewOut, final Context context, List<Article> list) {

        recyclerViewout = viewOut.findViewById(R.id.recyclerview);
        recyclerViewout.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewout.addItemDecoration(new DividerItemDecoration());
        popMusicAdapterOut = new PopMusicAdapter(list, context, handler, true);
        recyclerViewout.setAdapter(popMusicAdapterOut);

        type = StudyManager.getInstance().getListFragmentPos();
        popMusicAdapterOut.setType(type);
        tv_songnumOut = viewOut.findViewById(R.id.text_songnum);
        tv_songnumOut.setText("(" + list.size() + "首)");


        image_style_out = viewOut.findViewById(R.id.image_style);
        setOnclic(image_style_out);


        ImageView image_delete_all_out = viewOut.findViewById(R.id.image_delete_all);
        image_delete_all_out.setVisibility(View.INVISIBLE);


        image_delete_all_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals(FAVOR_TYPE)) {
                    delFavor(context);
                } else if (type.equals(LISTEN_TYPE)) {
                    delListen(context);
                }

            }
        });

        //设置播放模式
        setStyle(ConfigManager.getInstance().getStudyPlayMode());

        TextView tv_style_out = viewOut.findViewById(R.id.text_style);
        tv_style_out.setText("试听列表");
        //切换播放模式
        tv_style_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStyle();
                EventBus.getDefault().post(new ChangePlayMode());
            }
        });

    }


    private void setOnclic(View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStyle();
                EventBus.getDefault().post(new ChangePlayMode());
            }
        });
    }

    /**
     * 设置添加屏幕的背景透明度
     **/
    public void backgroundAlpaha(Context context, float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
        lp.alpha = 1.0f;
        ((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ((Activity) context).getWindow().setAttributes(lp);
    }


    /**
     * 收藏数据
     */
    private void getData() {

        newsList = new ArrayList<>();
        ArrayList<LocalInfo> temp = localInfoOp.findDataByFavourite();
        Article article;
        for (LocalInfo local : temp) {
            article = articleOp.findById(local.getApp(), local.getId());
            article.setExpireContent(local.getFavTime());
            newsList.add(article);
        }
    }

    /**
     * 最近播放
     */
    private void getDataLast() {


        localList = new ArrayList<>();
        ArrayList<LocalInfo> temp = localInfoOp.findDataByListen();
        Article article;
        for (LocalInfo local : temp) {
            article = articleOp.findById(local.getApp(), local.getId());
            article.setExpireContent(local.getSeeTime());
            localList.add(article);
        }

    }


    private void changeStyle() {
        int nextMusicType = ConfigManager.getInstance().getStudyPlayMode();
        nextMusicType = (nextMusicType + 1) % 3;
        ConfigManager.getInstance().setStudyPlayMode(nextMusicType);
        StudyManager.getInstance().generateArticleList();

        setStyle(nextMusicType);
    }

    private void setStyle(int nextMusicType) {
        switch (nextMusicType) {
            case 0:
                image_style_out.setImageResource(R.drawable.single_replay);
                tv_songnumOut.setVisibility(View.GONE);
                break;
            case 1:
                image_style_out.setImageResource(R.drawable.list_play);
                tv_songnumOut.setVisibility(View.VISIBLE);
                break;
            case 2:

                image_style_out.setImageResource(R.drawable.random_play);
                tv_songnumOut.setVisibility(View.VISIBLE);
                break;


        }
    }

    /**
     * 删除最近听过
     */
    private void delListen(Context context) {

        final MyMaterialDialog dialog = new MyMaterialDialog(context);
        dialog.setTitle(R.string.article_clear_all);
        dialog.setMessage(R.string.article_clear_hint);
        dialog.setPositiveButton(R.string.article_search_clear_sure, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalInfoOp localInfoOp = new LocalInfoOp();
                localInfoOp.clearSee();
                dialog.dismiss();

                localList.clear();
                popMusicAdapter1.notifyDataSetChanged();
                tv_songnum1.setText("(0首)");
            }
        });
        dialog.setNegativeButton(R.string.app_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
        backgroundAlpaha(context, 1.0f);

    }

    /**
     * 删除收藏
     */
    private void delFavor(Context context) {


        final MyMaterialDialog dialog = new MyMaterialDialog(context);
        dialog.setTitle(R.string.article_clear_all);
        dialog.setMessage(R.string.article_clear_hint);
        dialog.setPositiveButton(R.string.article_search_clear_sure, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                cancelFavor();


            }
        });
        dialog.setNegativeButton(R.string.app_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
        backgroundAlpaha(context, 1.0f);


    }

    private void cancelFavor() {
        final LocalInfoOp localInfoOp = new LocalInfoOp();
        final String app = StudyManager.getInstance().getApp();


        if (newsList.size() > 0) {
            if (!iyubaDialog.isShowing()) {
                iyubaDialog.show();
            }
            for (int i = 0; i < newsList.size(); i++) {

                final int position = i;
                if (localInfoOp.findDataById(app, newsList.get(position).getId()).getFavourite() == 1) {
                    FavorRequest.exeRequest(FavorRequest.generateUrl(AccountManager.getInstance().getUserId(), newsList.get(position).getId(), "del"), new IProtocolResponse() {
                        @Override
                        public void onNetError(String msg) {

                        }

                        @Override
                        public void onServerError(String msg) {

                        }

                        @Override
                        public void response(Object object) {
                            if (object.toString().equals("del")) {
                                localInfoOp.updateFavor(newsList.get(position).getId(), app, 0);

                                if (position == newsList.size() - 1) {

                                    newsList.clear();
                                    popMusicAdapter2.notifyDataSetChanged();
                                    tv_songnum2.setText("(0首)");
                                    if (iyubaDialog.isShowing()) {
                                        iyubaDialog.dismiss();
                                    }
                                }

                            }
                        }
                    });
                }
            }


        }


    }


    public void getDataOut() {

        listOut.clear();
        listOut.addAll(StudyManager.getInstance().getCurArticleList());
        type = StudyManager.getInstance().getListFragmentPos();
    }
}
