package com.iyuba.music.dubbing.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.iyuba.music.R;
import com.iyuba.music.data.Constant;
import com.iyuba.music.data.model.DownLoadJFResult;
import com.iyuba.music.data.model.TalkLesson;
import com.iyuba.music.data.remote.IntegralService;
import com.iyuba.music.entity.MusicVoa;
import com.iyuba.music.manager.ConstantManager;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class Share {

    public static final String MY_DUBBING_PREFIX = "http://voa.iyuba.cn/voa/talkShowShare.jsp?shuoshuoId=";

    public static void prepareDubbingMessage(Context context, TalkLesson voa, int backId, String user,
                                             IntegralService service, int uid) {
        String descUrl = getMyDubbingUrl(backId);

        shareMessage(context, voa.Pic, getText(context, voa), descUrl, "播音员：" + user + " " + voa.TitleCn, getListener(context, service, uid, backId));
    }

    public static void prepareDubbingMessage(Context context, MusicVoa voa, int backId, String user,
                                             IntegralService service, int uid) {
        String descUrl = getMyDubbingUrl(backId);
        shareMessage(context, voa.pic, getText(context, voa), descUrl, "播音员：" + user + " " + voa.titleCn, getListener(context, service, uid, backId));
    }
    public static void prepareVideoMessage(Context context, MusicVoa voa, IntegralService service, int uid) {
        //String url = VoaMediaUtil.getVideoUrl(voa.category(),voa.voaId());
        //String url = "http://staticvip.iyuba.cn/video/voa/321/" + voa.Id + ".mp4";;
        String url = "http://m.iyuba.cn/voaS/playPY.jsp?id=" + voa.voaId + "&apptype=music";;

        shareMessage(context, voa.pic, getText(context, voa), url, voa.titleCn, getListener(context, service, uid, voa.voaId));
    }
    private static String getText(Context context, MusicVoa voa) {
        return MessageFormat.format(context.getString(R.string.video_share_content),
                voa.titleCn, voa.title, voa.descCn);
    }

    public static void prepareVideoMessage(Context context, TalkLesson voa, IntegralService service, int uid) {
        //String url = VoaMediaUtil.getVideoUrl(voa.category(),voa.voaId());
        //String url = "http://staticvip.iyuba.cn/video/voa/321/" + voa.Id + ".mp4";;
        String url = "http://m.iyuba.cn/voaS/playPY.jsp?id=" + voa.Id + "&apptype=music";;

        shareMessage(context, voa.Pic, getText(context, voa), url, voa.TitleCn, getListener(context, service, uid, voa.voaId()));
    }

    public static void shareMessage(Context context, String imageUrl, String text, String url,
                                    String title, PlatformActionListener listener) {
        OnekeyShare oks = new OnekeyShare();
        oks.setTitle(title);
        oks.setTitleUrl(url);
        oks.setText(text);
        oks.setImageUrl(imageUrl);
        oks.setUrl(url);
        oks.setComment(MessageFormat.format(
                context.getString(R.string.app_share_content), Constant.APP_Name));
        oks.setSite(Constant.APP_Name);
        oks.setSiteUrl(url);
        oks.setSilent(true);
        oks.setLatitude((float) 0);
        oks.setLongitude((float) 0);
        oks.setCallback(listener);
        oks.show(context);
    }

    public static String getMyDubbingUrl(int backId) {
        return MY_DUBBING_PREFIX + backId+"&apptype=music";
    }

    public static PlatformActionListener getListener(final Context mContext, final IntegralService service, final int uid, final int id) {
        return new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Log.e("share", "onComplete");

                String srid = "";
                if (platform.getName().equals("QQ")
                        || platform.getName().equals("Wechat")
                        || platform.getName().equals("WechatFavorite")) {
                    srid = "7";
                } else if (platform.getName().equals("QZone")
                        || platform.getName().equals("WechatMoments")
                        || platform.getName().equals("SinaWeibo")
                        || platform.getName().equals("TencentWeibo")) {
                    srid = "19";
                }

                service.integral(srid, 1, getTime(), uid, Constant.APP_ID, id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<DownLoadJFResult>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(DownLoadJFResult downLoadJFResult) {

                                if (mContext != null) {
                                    if (downLoadJFResult != null && "200" .equals(downLoadJFResult.getResult())) {
                                        Toast.makeText(mContext, "分享成功" + downLoadJFResult.getAddcredit() + "分，当前积分为："
                                                        + downLoadJFResult.getTotalcredit() + "分",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(mContext, "分享成功", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                                Log.e("onError", "分享积分失败");
                                if (mContext != null) {
                                    Toast.makeText(mContext, "分享成功", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.e("share", "onError");
                Toast.makeText(mContext, "分享失败",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.e("share", "onCancel");
                Toast.makeText(mContext, "分享已取消",
                        Toast.LENGTH_SHORT).show();
            }
        };
    }

    public static boolean isWXSmallAvailable(Context context) {
        IWXAPI wxapi = WXAPIFactory.createWXAPI(context, ConstantManager.SMSAPPID);
        if ((wxapi != null) && wxapi.isWXAppInstalled()) {
            return wxapi.getWXAppSupportAPI() > Build.MINIPROGRAM_SUPPORTED_SDK_INT;
        }
        return false;
    }

    public static String getTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        //设置日期格式
        return "1234567890" + df.format(new Date());// new Date()为获取当前系统时间
    }

    private static String getText(Context context, TalkLesson voa) {
        return MessageFormat.format(context.getString(R.string.video_share_content),
                voa.TitleCn, voa.Title, voa.DescCn);
    }

}
