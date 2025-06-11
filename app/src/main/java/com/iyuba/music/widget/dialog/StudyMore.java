package com.iyuba.music.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.iyuba.music.R;
import com.iyuba.music.activity.LoginActivity;
import com.iyuba.music.activity.study.ReadActivity;
import com.iyuba.music.activity.study.RecommendSongActivity;
import com.iyuba.music.activity.study.StudySetActivity;
import com.iyuba.music.adapter.study.StudyMenuAdapter;
import com.iyuba.music.download.DownloadFile;
import com.iyuba.music.download.DownloadManager;
import com.iyuba.music.download.DownloadTask;
import com.iyuba.music.download.DownloadUtil;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.entity.mainpanel.Announcer;
import com.iyuba.music.entity.mainpanel.AnnouncerOp;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.receiver.ChangePropertyBroadcast;
import com.iyuba.music.request.account.ScoreOperRequest;
import com.iyuba.music.request.mainpanelrequest.AnnouncerRequest;
import com.iyuba.music.request.newsrequest.FavorRequest;
import com.iyuba.music.request.newsrequest.StudyPDFRequest;
import com.iyuba.music.util.ChangePropery;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.widget.CustomToast;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;

/**
 * Created by 10202 on 2015/10/28.
 */
public class StudyMore {
    private View root;
    private Activity activity;
    private Context context;
    private boolean shown;
    private int[] menuDrawable;
    private String[] menuText;
    private StudyMenuAdapter studyMenuAdapter;
    private IyubaDialog iyubaDialog;
    private String app;
    private IyubaDialog waittingDialog;

    private Article article;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:

                    if (AccountManager.getInstance().checkUserLogin()) {
                        getScore(39);
                    }
                    break;
                case 1:

                    if (AccountManager.getInstance().checkUserLogin()) {
                        getScore(38);
                    }
                    break;
            }
        }
    };

    public StudyMore(Activity activity) {
        this.context = activity;
        this.activity = activity;
        LayoutInflater vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root = vi.inflate(R.layout.study_more, null);
        init();
    }

    private void init() {

        waittingDialog = WaitingDialog.create(context, null);
        app = StudyManager.getInstance().getApp();
        article = StudyManager.getInstance().getCurArticle();
        GridView moreGrid = (GridView) root.findViewById(R.id.study_menu);
        if (app.equals("209")) {
            menuDrawable = new int[]{R.drawable.share_study, R.drawable.favor,
                    R.drawable.download, R.drawable.read,
                    R.drawable.recommend, R.drawable.chat,
                    R.drawable.night, R.drawable.play_set, R.drawable.img_pdf};
            menuText = context.getResources().getStringArray(R.array.study_menu);
        } else {
            menuDrawable = new int[]{R.drawable.share_study, R.drawable.favor,
                    R.drawable.download, R.drawable.play_set,};
            menuText = context.getResources().getStringArray(R.array.study_menu_simple);
        }
        studyMenuAdapter = new StudyMenuAdapter(context);
        final LocalInfoOp localInfoOp = new LocalInfoOp();
        final Article curArticle = StudyManager.getInstance().getCurArticle();
        if (localInfoOp.findDataById(app, curArticle.getId()).getFavourite() == 1) {
            menuDrawable[1] = R.drawable.favor_true;
        } else {
            menuDrawable[1] = R.drawable.favor;
        }
        int downloadState = localInfoOp.findDataById(app, curArticle.getId()).getDownload();
        if (DownloadTask.checkFileExists(curArticle)) {
            menuDrawable[2] = R.drawable.down_true;
        } else if (downloadState == 1) {
            menuDrawable[2] = R.drawable.down_true;
        } else if (downloadState == 2) {
            menuDrawable[2] = R.drawable.downloading;
        } else {
            menuDrawable[2] = R.drawable.download;
        }
        if (app.equals("209")) {
            if (ConfigManager.getInstance().isNight()) {
                menuDrawable[6] = R.drawable.night_true;
            } else {
                menuDrawable[6] = R.drawable.night;
            }
        }
        Log.e("数据长度", menuText.length + "");
        Log.e("StudyMore", "app = " + app);
        Log.e("StudyMore", "curArticle.getId() = " + curArticle.getId());
        Log.e("StudyMore", "getDownload() = " + localInfoOp.findDataById(app, curArticle.getId()).getDownload());
        Log.e("StudyMore", "getFavourite() = " + localInfoOp.findDataById(app, curArticle.getId()).getFavourite());
        studyMenuAdapter.setDataSet(menuText, menuDrawable);
        moreGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case 0:
                        dismiss();
                        if (!AccountManager.getInstance().checkUserLogin()) {
                            CustomToast.getInstance().showToast(R.string.article_share_request);
                            return;
                        }
                        share();
                        break;
                    case 1:
                        dismiss();
                        if (!AccountManager.getInstance().checkUserLogin()) {
                            CustomToast.getInstance().showToast(R.string.article_favor_request);
                            return;
                        }
                        if (localInfoOp.findDataById(app, curArticle.getId()).getFavourite() == 1) {
                            FavorRequest.exeRequest(FavorRequest.generateUrl(AccountManager.getInstance().getUserId(), curArticle.getId(), "del"), new IProtocolResponse() {
                                @Override
                                public void onNetError(String msg) {
                                    CustomToast.getInstance().showToast(R.string.article_favor_cancel_fail);
                                }

                                @Override
                                public void onServerError(String msg) {
                                    CustomToast.getInstance().showToast(R.string.article_favor_cancel_fail);
                                }

                                @Override
                                public void response(Object object) {
                                    if (object.toString().equals("del")) {
                                        localInfoOp.updateFavor(curArticle.getId(), app, 0);
                                        CustomToast.getInstance().showToast(R.string.article_favor_cancel);
                                        menuDrawable[1] = R.drawable.favor;
                                        studyMenuAdapter.setDataSet(menuText, menuDrawable);
                                    }
                                }
                            });
                        } else {
                            FavorRequest.exeRequest(FavorRequest.generateUrl(AccountManager.getInstance().getUserId(), curArticle.getId(), "insert"), new IProtocolResponse() {
                                @Override
                                public void onNetError(String msg) {
                                    CustomToast.getInstance().showToast(R.string.article_favor_fail);
                                }

                                @Override
                                public void onServerError(String msg) {
                                    CustomToast.getInstance().showToast(R.string.article_favor_fail);
                                }

                                @Override
                                public void response(Object object) {
                                    if (object.toString().equals("insert")) {
                                        localInfoOp.updateFavor(curArticle.getId(), app, 1);
                                        CustomToast.getInstance().showToast(R.string.article_favor);
                                        menuDrawable[1] = R.drawable.favor_true;
                                        studyMenuAdapter.setDataSet(menuText, menuDrawable);
                                    }
                                }
                            });
                        }
                        break;
                    case 2:
                        dismiss();
                        if (!AccountManager.getInstance().checkUserLogin()) {
                            CustomToast.getInstance().showToast(R.string.article_download_request);
                            return;
                        }
                        if (DownloadTask.checkFileExists(curArticle)) {
                            CustomToast.getInstance().showToast(R.string.article_download_over);
                            return;
                        }
                        int downloadState = localInfoOp.findDataById(app, curArticle.getId()).getDownload();
                        if (downloadState == 1) {
                            CustomToast.getInstance().showToast(R.string.article_download_over);
                        } else if (downloadState == 0) {


                            int songs = localInfoOp.findDataByDownloaded().size() + localInfoOp.findDataByDownloading().size();


                            if (songs < 10) {
                                Toast.makeText(context, "开始下载，请稍等。", Toast.LENGTH_SHORT).show();
                                localInfoOp.updateDownload(curArticle.getId(), app, 2);
                                menuDrawable[2] = R.drawable.downloading;
                                studyMenuAdapter.setDataSet(menuText, menuDrawable);
                                DownloadFile downloadFile = new DownloadFile();
                                downloadFile.id = curArticle.getId();
                                downloadFile.downloadState = "start";
                                DownloadManager.getInstance().fileList.add(downloadFile);
                                new DownloadTask(curArticle).start();
                                return;
                            }

                            if (songs == 10) {
                                showNoNetDialog(curArticle, localInfoOp);
                                return;
                            }

                            DownloadUtil.checkScore(curArticle.getId(), new IOperationResult() {

                                @Override
                                public void success(Object object) {
                                    localInfoOp.updateDownload(curArticle.getId(), app, 2);
                                    menuDrawable[2] = R.drawable.downloading;
                                    studyMenuAdapter.setDataSet(menuText, menuDrawable);
                                    DownloadFile downloadFile = new DownloadFile();
                                    downloadFile.id = curArticle.getId();
                                    downloadFile.downloadState = "start";
                                    DownloadManager.getInstance().fileList.add(downloadFile);
                                    new DownloadTask(curArticle).start();
                                }

                                @Override
                                public void fail(Object object) {

                                }
                            });
                        } else {
                            CustomToast.getInstance().showToast(R.string.article_downloading);
                        }
                        break;
                    case 3:
                        dismiss();
                        if (!AccountManager.getInstance().checkUserLogin()) {
                            CustomToast.getInstance().showToast(R.string.article_sing_request);
                            return;
                        }
                        if (app.equals("209")) {
                            context.startActivity(new Intent(context, ReadActivity.class));
                        } else {
                            context.startActivity(new Intent(context, StudySetActivity.class));
                        }
                        break;
                    case 4:
                        dismiss();
                        context.startActivity(new Intent(context, RecommendSongActivity.class));
                        break;
                    case 5:
                        dismiss();
                        if (AccountManager.getInstance().checkUserLogin()) {
                            goAnnouncerHomePage(curArticle);
                        } else {
                            CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                                @Override
                                public void finish() {
                                    goAnnouncerHomePage(curArticle);
                                }
                            });
                        }
                        break;
                    case 6:
                        ConfigManager.getInstance().setNight(!ConfigManager.getInstance().isNight());
                        ChangePropery.updateNightMode(ConfigManager.getInstance().isNight());
                        intent = new Intent(ChangePropertyBroadcast.FLAG);
                        intent.putExtra(ChangePropertyBroadcast.SOURCE, "StudyActivity.class");
                        context.sendBroadcast(intent);
                        break;
                    case 7:
                        dismiss();
                        context.startActivity(new Intent(context, StudySetActivity.class));
                        break;
                    case 8:
                        dismiss();


                        if (AccountManager.getInstance().checkUserLogin()) {

                            setDialognew();
                        } else {

                            Intent intent1 = new Intent(context, LoginActivity.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent1);
                        }
                        break;
                    default:
                        break;
                }

            }
        });
        moreGrid.setAdapter(studyMenuAdapter);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        iyubaDialog = new IyubaDialog(context, root, true, 0);
        iyubaDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                shown = false;
            }
        });
    }

    private void setdownPDFDialog(final String path) {

        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(path);


        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("PDF链接生成成功!")
                .setMessage(path + "\t(链接已复制)")
                .setNegativeButton("下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri uri = Uri.parse(path);
                        intent.setData(uri);
                        context.startActivity(intent);
                    }
                }).setPositiveButton("发送", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendPDF("http://staticvip." + ConstantManager.IYUBA_CN + "images/song/" + article.getPicUrl(), path, article.getTitle(), article.getSinger());
                    }
                }).create();
        alertDialog.show();


        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(alertDialog);

            //通过反射修改title字体大小和颜色
            Field mTitle = mAlertController.getClass().getDeclaredField("mTitleView");
            mTitle.setAccessible(true);
            TextView mTitleView = (TextView) mTitle.get(mAlertController);
            mTitleView.setTextColor(Color.BLUE);

            //通过反射修改message字体大小和颜色
            Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
            mMessage.setAccessible(true);
            TextView mMessageView = (TextView) mMessage.get(mAlertController);
            mMessageView.setTextSize(14);


        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }


    private void setDialognew() {

        String[] strings = {"导出英文", "导出中英双语"};

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("请选择需要导出的PDF形式").setItems(strings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Article article = StudyManager.getInstance().getCurArticle();
                        Log.e("article.getId()", article.getId() + "");

                        int isenglish;
                        if (which == 0) {
                            isenglish = 1;
                        } else {
                            isenglish = 0;
                        }
                        if (!waittingDialog.isShowing()) {
                            waittingDialog.show();
                        }

                        StudyPDFRequest.exeRequest(StudyPDFRequest.generateUrl(article.getId() + "", isenglish), new IProtocolResponse() {
                            @Override
                            public void onNetError(String msg) {
                                if (waittingDialog.isShowing()) {
                                    waittingDialog.dismiss();
                                }
                                Toast.makeText(context, "PDF生成失败，请稍后重试。", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onServerError(String msg) {
                                if (waittingDialog.isShowing()) {
                                    waittingDialog.dismiss();
                                }
                                Toast.makeText(context, "PDF生成失败，请稍后重试。", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void response(Object object) {
                                if (waittingDialog.isShowing()) {
                                    waittingDialog.dismiss();
                                }

                                BaseApiEntity apiEntity = (BaseApiEntity) object;
                                JSONObject jsonObject = (JSONObject) apiEntity.getData();

                                if (jsonObject != null && "true".equals(jsonObject.optString("exists"))) {
                                    if (DownloadUtil.checkVip()) {

                                        final String url = "http://apps." + ConstantManager.IYUBA_CN + "afterclass" + jsonObject.optString("path");
                                        Log.e("PDFurl", url);


                                        setdownPDFDialog(url);

                                    } else {
//                                pointremove(this, result);

                                        final String url = "http://apps." + ConstantManager.IYUBA_CN + "afterclass" + jsonObject.optString("path");


                                        setCreditDownLoadPDF(url);

                                    }

                                } else {
                                    Toast.makeText(context, "PDF生成失败，请稍后重试。", Toast.LENGTH_SHORT).show();
                                }


                            }
                        });
                    }
                }).create();
        alertDialog.show();

        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(alertDialog);

            //通过反射修改title字体大小和颜色
            Field mTitle = mAlertController.getClass().getDeclaredField("mTitleView");
            mTitle.setAccessible(true);
            TextView mTitleView = (TextView) mTitle.get(mAlertController);
            mTitleView.setTextColor(Color.BLUE);


        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    private void goAnnouncerHomePage(Article curArticle) {
        Intent intent;
        if (curArticle.getSimple() == 0) {
            Announcer announcer = new AnnouncerOp().findById(StudyManager.getInstance().getCurArticle().getStar());
            if (announcer == null || announcer.getId() == 0) {
                getAnnounceList();
            } else {
                SocialManager.getInstance().pushFriendId(announcer.getUid());
                context.startActivity(PersonalHomeActivity.buildIntent(context, Integer.parseInt(announcer.getUid()), "", 0));

            }
        } else {
            SocialManager.getInstance().pushFriendId("928");
            context.startActivity(PersonalHomeActivity.buildIntent(context, Integer.parseInt("928"), "", 0));

        }
    }

    private void getAnnounceList() {
        AnnouncerRequest.exeRequest(new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg);
            }

            @Override
            public void response(Object object) {
                ArrayList<Announcer> announcerList = (ArrayList<Announcer>) ((BaseListEntity) object).getData();
                new AnnouncerOp().saveData(announcerList);
                final Announcer item = new Announcer();
                item.setUid("-1");
                for (Announcer announcer : announcerList) {
                    if (announcer.getName().equals(StudyManager.getInstance().getCurArticle().getStar())) {
                        item.setUid(announcer.getUid());
                        break;
                    }
                }
                if (!item.getUid().equals("-1")) {
                    if (AccountManager.getInstance().checkUserLogin()) {
                        SocialManager.getInstance().pushFriendId(item.getUid());

                        context.startActivity(PersonalHomeActivity.buildIntent(context, Integer.parseInt(item.getUid()), "", 0));

                    } else {
                        CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                            @Override
                            public void finish() {
                                SocialManager.getInstance().pushFriendId(item.getUid());

                                context.startActivity(PersonalHomeActivity.buildIntent(context, Integer.parseInt(item.getUid()), "", 0));

                            }
                        });
                    }
                }
            }
        });
    }

    public void show() {
        iyubaDialog.showAnim(R.anim.bottom_in);
        shown = true;
    }

    public void dismiss() {
        iyubaDialog.dismissAnim(R.anim.bottom_out);
    }

    public boolean isShown() {
        return shown;
    }

    private void showNoNetDialog(final Article curArticle, final LocalInfoOp localInfoOp) {
        final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
        materialDialog.setTitle("升级vip即可享受无限下载");
        materialDialog.setMessage("你已下载十首歌曲！以后每首歌曲需要扣除20积分");

        materialDialog.setPositiveButton("继续下载", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDialog.dismiss();

                DownloadUtil.checkScore(curArticle.getId(), new IOperationResult() {
                    @Override
                    public void success(Object object) {
                        localInfoOp.updateDownload(curArticle.getId(), app, 2);
                        menuDrawable[2] = R.drawable.downloading;
                        studyMenuAdapter.setDataSet(menuText, menuDrawable);
                        DownloadFile downloadFile = new DownloadFile();
                        downloadFile.id = curArticle.getId();
                        downloadFile.downloadState = "start";
                        DownloadManager.getInstance().fileList.add(downloadFile);
                        new DownloadTask(curArticle).start();
                    }

                    @Override
                    public void fail(Object object) {

                    }
                });

            }
        });
        materialDialog.setNegativeButton("暂不下载", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
                return;
            }
        });

        materialDialog.show();

    }


    private void share() {

        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        weibo.removeAccount(true);
        ShareSDK.removeCookieOnAuthorize(true);

        String text = article.getContent();
        String siteUrl = getShareUrl();
        OnekeyShare oks = new OnekeyShare();
        oks.addHiddenPlatform(SinaWeibo.NAME);

        // 关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字
        // oks.setNotification(R.drawable.ic_launcher,
        // getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(article.getTitle());
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(siteUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(text);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//         oks.setImagePath(getResources().getDrawable(R.drawable.ic_launcher));
        // imageUrl是、We、b图片路径，sina需要开通权限
        oks.setImageUrl(getPicUrl());
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(siteUrl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(text);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(article.getTitle());
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(siteUrl);
        // oks.setDialogMode();
        // oks.setSilent(false);

        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                Log.e("okCallbackonError", "onError" + arg2.toString());
            }

            @Override
            public void onComplete(Platform arg0, int arg1,
                                   HashMap<String, Object> arg2) {
                Log.e("okCallbackonComplete", "onComplete");
                if (AccountManager.getInstance().getUserId() != null) {
                    Message msg = new Message();
                    msg.obj = arg0.getName();


                    if (arg0.getName().equals("QQ")
                            || arg0.getName().equals("Wechat")
                            || arg0.getName().equals("WechatFavorite")) {
                        msg.what = 1;
                    } else if (arg0.getName().equals("QZone")
                            || arg0.getName().equals("WechatMoments")
                            || arg0.getName().equals("SinaWeibo")
                    ) {
                        msg.what = 0;
                    }
                    handler.sendMessage(msg);
                } else {
                    handler.sendEmptyMessage(13);
                }
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                Log.e("share_cancel", "" + arg1);
                Log.e("okCallbackonCancel", "onCancel");
            }
        });
        // 启动分享GUI
        oks.show(context);
    }


    private String getShareUrl() {
        String url;
        switch (StudyManager.getInstance().getApp()) {
            case "201":
            case "218":
                url = "http://m." + ConstantManager.IYUBA_CN + "news.html?type=voa&id=" + article.getId();
                break;
            case "209":
                if ("401".equals(article.getCategory())) {
                    url = "http://m." + ConstantManager.IYUBA_CN + "news.html?type=topvideos&id=" + article.getId();
                } else {
                    url = "http://m." + ConstantManager.IYUBA_CN + "news.html?type=song&id=" + article.getId();
                }
                break;
            case "212":
                url = "http://m." + ConstantManager.IYUBA_CN + "news.html?type=csvoa&id=" + article.getId();
                break;
            case "213":
                url = "http://m." + ConstantManager.IYUBA_CN + "news.html?type=meiyu&id=" + article.getId();
                break;
            case "217":
                url = "http://m." + ConstantManager.IYUBA_CN + "news.html?type=voavideo&id=" + article.getId();
                break;
            case "215":
            case "221":
            case "231":
                url = "http://m." + ConstantManager.IYUBA_CN + "news.html?type=bbc&id=" + article.getId();
                break;
            case "229":
                url = "http://m." + ConstantManager.IYUBA_CN + "news.html?type=ted&id=" + article.getId();
                break;
            default:
                url = "";
                break;
        }
        return url;
    }


    private String getPicUrl() {
        switch (StudyManager.getInstance().getApp()) {
            case "209":
                return "http://staticvip." + ConstantManager.IYUBA_CN + "images/song/" + article.getPicUrl();
            default:
                return article.getPicUrl();
        }
    }

    private void getScore(int type) {
        ScoreOperRequest.exeRequest(ScoreOperRequest.generateUrl(AccountManager.getInstance().getUserId(), article.getId(), type), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {

            }

            @Override
            public void onServerError(String msg) {

            }

            @Override
            public void response(Object object) {
                BaseApiEntity apiEntity = (BaseApiEntity) object;
                switch (apiEntity.getState()) {
                    case BaseApiEntity.SUCCESS:
                        CustomToast.getInstance().showToast(context.getString(R.string.article_share_success, apiEntity.getMessage(), apiEntity.getValue()));
                        break;
                    case BaseApiEntity.FAIL:
                        CustomToast.getInstance().showToast(apiEntity.getMessage());
                        break;
                    case BaseApiEntity.ERROR:
                        break;
                }
            }
        });
    }

    void sendPDF(String pdfurl, String siteurl, String title, String star) {
        //ShareSDK.initSDK(this);
        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        weibo.removeAccount(true);
        ShareSDK.removeCookieOnAuthorize(true);

        String imageUrl = pdfurl;
        String text = star;
        String siteUrl = siteurl;

        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.addHiddenPlatform(SinaWeibo.NAME);

        // 分享时Notification的图标和文字
        // oks.setNotification(R.drawable.ic_launcher,
        // getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(siteUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(text);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        // oks.setImagePath("/sdcard/test.jpg");
        // imageUrl是Web图片路径，sina需要开通权限
        oks.setImageUrl(imageUrl);
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(siteUrl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("爱语吧的这款应用" + ConstantManager.appName + "真的很不错啊~推荐！");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(ConstantManager.appName);
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(siteUrl);
        // oks.setDialogMode();
        // oks.setSilent(false);
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                Log.e("okCallbackonError", "onError" + arg2.toString());
            }

            @Override
            public void onComplete(Platform arg0, int arg1,
                                   HashMap<String, Object> arg2) {

            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                Log.e("okCallbackonCancel", "onCancel");
            }
        });
        // 启动分享GUI
        oks.show(context);
    }

    private void setCreditDownLoadPDF(final String url) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("生成PDF每篇文章将消耗20积分，是否生成？").setTitle("提示：").setPositiveButton("生成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                DownloadUtil.checkScore(article.getId(), new IOperationResult() {
                    @Override
                    public void success(Object object) {
                        setdownPDFDialog(url);

                    }

                    @Override
                    public void fail(Object object) {

                    }
                });
            }
        }).setNegativeButton("取消", null).create().show();
    }

}
