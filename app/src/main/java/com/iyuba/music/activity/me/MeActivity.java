package com.iyuba.music.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.headlinelibrary.ui.content.AudioContentActivity;
import com.iyuba.headlinelibrary.ui.content.TextContentActivity;
import com.iyuba.headlinelibrary.ui.content.VideoContentActivity;
import com.iyuba.headlinelibrary.ui.content.VideoContentActivityNew;
import com.iyuba.module.dl.BasicDLPart;
import com.iyuba.module.dl.DLActivity;
import com.iyuba.module.dl.DLItemEvent;
import com.iyuba.module.favor.data.model.BasicFavorPart;
import com.iyuba.module.favor.event.FavorItemEvent;
import com.iyuba.module.favor.ui.BasicFavorActivity;
import com.iyuba.module.movies.ui.series.SeriesActivity;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.CampaignActivity;
import com.iyuba.music.activity.LoginActivity;
import com.iyuba.music.activity.RankActivity;
import com.iyuba.music.activity.WebViewActivity;
import com.iyuba.music.activity.main.DownloadSongActivity;
import com.iyuba.music.activity.main.FavorSongActivity;
import com.iyuba.music.activity.main.ListenSongActivity;
import com.iyuba.music.adapter.me.MeAdapter;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.util.MD5;
import com.iyuba.music.widget.dialog.CustomDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by 10202 on 2015/12/2.
 */
public class MeActivity extends BaseActivity {
    private MeAdapter meAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discover);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
        EventBus.getDefault().register(this);
    }


    @Override
    protected void initWidget() {
        super.initWidget();
        RecyclerView discover = (RecyclerView) findViewById(R.id.discover_list);
        discover.setLayoutManager(new LinearLayoutManager(context));
        meAdapter = new MeAdapter(context);
        discover.setAdapter(meAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void setListener() {
        super.setListener();
        meAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 1:
                        if (AccountManager.getInstance().checkUserLogin()) {
                            SocialManager.getInstance().pushFriendId(AccountManager.getInstance().getUserId());
                            Intent intent = new Intent(context, FriendCenter.class);
                            intent.putExtra("type", "0");
                            intent.putExtra("needPop", true);
                            startActivity(intent);
                        } else {
                            CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                                @Override
                                public void finish() {
                                    SocialManager.getInstance().pushFriendId(AccountManager.getInstance().getUserId());
                                    Intent intent = new Intent(context, FriendCenter.class);
                                    intent.putExtra("type", "0");
                                    intent.putExtra("needPop", true);
                                    startActivity(intent);
                                }
                            });
                        }
                        break;
                    case 2:
                        if (AccountManager.getInstance().checkUserLogin()) {
                            SocialManager.getInstance().pushFriendId(AccountManager.getInstance().getUserId());
                            Intent intent = new Intent(context, FriendCenter.class);
                            intent.putExtra("type", "1");
                            intent.putExtra("needPop", true);
                            startActivity(intent);
                        } else {
                            CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                                @Override
                                public void finish() {
                                    SocialManager.getInstance().pushFriendId(AccountManager.getInstance().getUserId());
                                    Intent intent = new Intent(context, FriendCenter.class);
                                    intent.putExtra("type", "1");
                                    intent.putExtra("needPop", true);
                                    startActivity(intent);
                                }
                            });
                        }
                        break;
                    case 3:
                        if (AccountManager.getInstance().checkUserLogin()) {
                            startActivity(new Intent(context, FindFriendActivity.class));
                        } else {
                            CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                                @Override
                                public void finish() {
                                    startActivity(new Intent(context, FindFriendActivity.class));
                                }
                            });
                        }
                        break;
                    case 5:
                        //我的下载

//                        startActivity(DLActivity.buildIntent(MeActivity.this));
                        startActivity(new Intent(context, DownloadSongActivity.class));
                        break;
                    case 6:
                        //我的收藏
                        startActivity(new Intent(context, FavorSongActivity.class));

//                        if (AccountManager.getInstance().checkUserLogin()) {
//
//                            int uid;
//                            try {
//                                uid = Integer.parseInt(AccountManager.getInstance().getUserId());
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                uid = 0;
//                            }
//
//                            Intent intent = BasicFavorActivity.buildIntent(MeActivity.this);
//                            startActivity(intent);
//                        } else {
//                            startActivity(new Intent(MeActivity.this, LoginActivity.class));
//                        }

                        break;
                    case 7:
                        startActivity(new Intent(context, ListenSongActivity.class));
                        break;
                    case 9:
                        if (AccountManager.getInstance().checkUserLogin()) {
                            startActivity(new Intent(context, CreditActivity.class));
                        } else {
                            CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                                @Override
                                public void finish() {
                                    startActivity(new Intent(context, CreditActivity.class));
                                }
                            });
                        }
                        break;
                    case 10:
                        if (AccountManager.getInstance().checkUserLogin()) {
                            launchRank();
                        } else {
                            CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                                @Override
                                public void finish() {
                                    launchRank();
                                }
                            });
                        }
                        break;
                    case 11:
                        if (AccountManager.getInstance().checkUserLogin()) {
                            launchIStudy();
                        } else {
                            CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                                @Override
                                public void finish() {
                                    launchIStudy();
                                }
                            });
                        }
                        break;
                    case 12:
                        if (AccountManager.getInstance().checkUserLogin()) {
                            startActivity(new Intent(context, CampaignActivity.class));
                        } else {
                            CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                                @Override
                                public void finish() {
                                    startActivity(new Intent(context, CampaignActivity.class));
                                }
                            });
                        }
                        break;
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private void launchIStudy() {
        String url = "http://m." + ConstantManager.IYUBA_CN + "i/index.jsp?" + "uid=" + AccountManager.getInstance().getUserId() + '&' + "username=" + AccountManager.getInstance().getUserInfo().getUsername() + '&' + "sign=" + MD5.getMD5ofStr("iyuba" + AccountManager.getInstance().getUserId() + "camstory");
        Intent intent = new Intent();
        intent.setClass(context, WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", context.getString(R.string.oper_bigdata));
        startActivity(intent);
    }

    private void launchRank() {

        Intent intent =new Intent(MeActivity.this, RankActivity.class);
        startActivity(intent);
//        String url = "http://m." + ConstantManager.IYUBA_CN + "i/getRanking.jsp?appId=" + ConstantManager.appId + "&uid=" + AccountManager.getInstance().getUserId() + "&sign=" + MD5.getMD5ofStr(AccountManager.getInstance().getUserId() + "ranking" + ConstantManager.appId);
//        Intent intent = new Intent();
//        intent.setClass(context, WebViewActivity.class);
//        intent.putExtra("url", url);
//        intent.putExtra("title", context.getString(R.string.oper_rank));
//        startActivity(intent);
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.oper_myself);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FavorItemEvent fEvent) {
        //收藏页面点击
        BasicFavorPart fPart = fEvent.items.get(fEvent.position);
        goFavorItem(fPart);

    }


    private void goFavorItem(BasicFavorPart part) {
        String userIdStr = AccountManager.getInstance().getUserId();

        int userId;
        boolean isVip;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (Exception e) {
            e.printStackTrace();
            userId = 0;
        }

        UserInfo userInfo2 = AccountManager.getInstance().getUserInfo();
        try {
            int vip = Integer.parseInt(userInfo2.getVipStatus());
            if (vip > 0) {
                isVip = true;
            } else {
                isVip = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            isVip = false;
        }


        switch (part.getType()) {
            case "news":
                startActivity(TextContentActivity.getIntent2Me(MeActivity.this, part.getId(), part.getTitle(), part.getTitle(), part.getType(), part.getCategoryName(), part.getCreateTime(), part.getPic(), part.getSource()));
                break;
            case "voa":
            case "csvoa":
            case "bbc":
                startActivity(AudioContentActivity.getIntent2Me(MeActivity.this,  part.getCategoryName(), part.getTitle(), part.getTitleCn(), part.getPic(), part.getType(), part.getId(), part.getSound()));
                break;
            case "voavideo":
            case "meiyu":
            case "ted":
            case "bbcwordvideo":
            case "topvideos":
            case "japanvideos":

                startActivity(VideoContentActivityNew.getIntent2Me(MeActivity.this,  part.getCategoryName(), part.getTitle(), part.getTitleCn(), part.getPic(), part.getType(), part.getId(),part.getSound()));
                break;
            case "series":
                Intent intent = SeriesActivity.buildIntent(MeActivity.this, part.getSeriesId(), part.getId());
                startActivity(intent);
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DLItemEvent dlEvent) {
        //视频下载后点击
        BasicDLPart dlPart = dlEvent.items.get(dlEvent.position);


        String userIdStr = AccountManager.getInstance().getUserId();

        int userId;
        boolean isVip;


        try {
            userId = Integer.parseInt(userIdStr);
        } catch (Exception e) {
            e.printStackTrace();
            userId = 0;
        }


        UserInfo userInfo2 = AccountManager.getInstance().getUserInfo();

        try {
            int vip = Integer.parseInt(userInfo2.getVipStatus());
            if (vip > 0) {
                isVip = true;
            } else {

                isVip = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            isVip = false;
        }

        switch (dlPart.getType()) {
            case "voa":
            case "csvoa":
            case "bbc":
                startActivity(AudioContentActivity.getIntent2Me(context, dlPart.getCategoryName(), dlPart.getTitle(), dlPart.getTitleCn(), dlPart.getPic(), dlPart.getType(), dlPart.getId()));
                break;
            case "voavideo":
            case "meiyu":
            case "ted":
            case "bbcwordvideo":
            case "topvideos":
            case "japanvideos":
                startActivity(VideoContentActivity.getIntent2Me(context, dlPart.getCategoryName(), dlPart.getTitle(), dlPart.getTitleCn(), dlPart.getPic(), dlPart.getType(), dlPart.getId()));
                break;
        }

    }
}
