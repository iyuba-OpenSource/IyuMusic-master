package com.iyuba.music.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;

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
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfo;
import com.iyuba.music.entity.article.LocalInfoOp;
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

import java.util.ArrayList;
import java.util.List;


/**
 * Created by iyuba on 2018/2/1.
 */

public class MusicListPopNew extends PopupWindow {

    private PopupWindow window;


    private IyubaDialog iyubaDialog;

    public MusicListPopNew(Context context) {
        super(context);
        setPopMusicList(context);
        iyubaDialog = WaitingDialog.create(context, null);
    }

    private List<View> list = new ArrayList<>();

    private float lastChange = 0;


    private List<Article> newsList, localList;
    private LocalInfoOp localInfoOp;
    private ArticleOp articleOp;

    public ImageView image_style, image_delete_all;
    public TextView tv_style, tv_songnum1, tv_songnum2;

    private PopMusicAdapter popMusicAdapter1;


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
                case  3:
                    changeAdapter();
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

        window = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, ScreenUtils.getScreenHeight(context) / 2, true);
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

        window.setOnDismissListener(new OnDismissListener() {
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
    }

    private void setWidget(View view, Context context) {
        list.clear();

        localInfoOp = new LocalInfoOp();
        articleOp = new ArticleOp();
        getData();
        getDataLast();

        final ViewPager viewPager = view.findViewById(R.id.viewpager);
        final PageIndicator pop_indicator = view.findViewById(R.id.pop_indicator);
        TextView close = view.findViewById(R.id.close);
        lastChange = 0;

        setViewPagerView(context);
        ((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

//        PopMusicPageAdapter popMusicPageAdapter = new PopMusicPageAdapter(list);
//        viewPager.setAdapter(popMusicPageAdapter);

        pop_indicator.setVisibility(View.GONE);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });

    }


    private void setViewPagerView(Context context) {

        View view1 = LayoutInflater.from(context).inflate(R.layout.pop_adapter_item_first, null);
        createRecyclerView(view1, StudyManager.getInstance().getCurArticleList(), "favor", context);

        list.add(view1);

    }

    private void createRecyclerView(View view, List<Article> list, final String type, final Context context) {

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration());



        popMusicAdapter1 = new PopMusicAdapter(list, context, handler);
        recyclerView.setAdapter(popMusicAdapter1);
        popMusicAdapter1.setType(type);
        tv_songnum1 = view.findViewById(R.id.text_songnum);
        tv_songnum1.setText("(" + list.size() + "首)");

        image_style = view.findViewById(R.id.image_style);
        image_delete_all = view.findViewById(R.id.image_delete_all);
        tv_style = view.findViewById(R.id.text_style);



            setStyle(ConfigManager.getInstance().getStudyPlayMode());

            tv_style.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    changeStyle();
                }
            });

            image_style.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeStyle();
                }
            });

    }

    /**
     * 设置添加屏幕的背景透明度
     **/
    public void backgroundAlpaha(Context context, float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
        lp.alpha = bgAlpha;

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
                tv_style.setText("单曲循环");
                image_style.setImageResource(R.drawable.single_replay);
                tv_songnum2.setVisibility(View.GONE);
                break;
            case 1:
                tv_style.setText("顺序播放");
                image_style.setImageResource(R.drawable.list_play);
                tv_songnum2.setVisibility(View.VISIBLE);
                break;
            case 2:
                tv_style.setText("随机播放");
                image_style.setImageResource(R.drawable.random_play);
                tv_songnum2.setVisibility(View.VISIBLE);
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

    public void changeAdapter() {

        popMusicAdapter1.notifyDataSetChanged();
    }

}
