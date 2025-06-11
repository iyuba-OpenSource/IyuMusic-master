package com.iyuba.music.download;

import android.util.Base64;
import android.util.Log;

import com.iyuba.music.R;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.retrofit.IyumusicRetrofitNetwork;
import com.iyuba.music.retrofit.result.DownLoadJFResult;
import com.iyuba.music.widget.CustomToast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by 10202 on 2016/3/7.
 */
public class DownloadUtil {
    public static boolean checkVip() {
        return AccountManager.getInstance().checkUserLogin() && (AccountManager.getInstance().getUserId().equals("46738") ||
                ((AccountManager.getInstance().getUserInfo() != null) && Integer.valueOf(AccountManager.getInstance().getUserInfo().getVipStatus())>0));
    }

    public static String getAnnouncerUrl(int id, String sound) {
        StringBuilder url = new StringBuilder();
        if (checkVip()) {
            if (id < 1000) {
                url.append(ConstantManager.oldSoundVipUrl).append(sound);
            } else {
                url.append(ConstantManager.vipUrl).append(sound);
            }
        } else {
            if (id < 1000) {
                url.append(ConstantManager.oldSoundUrl).append(sound);
            } else {
                url.append(ConstantManager.songUrl).append(sound);
            }
        }
        return url.toString();
    }

    public static String getSongUrl(String app, String song) {
        StringBuilder url = new StringBuilder();
        if (checkVip()) {
            switch (app) {
                case "209":
                    url.append(ConstantManager.vipUrl).append(song);
                    break;
                case "215":
                case "221":
                case "231":
                    url.append("http://staticvip." + ConstantManager.IYUBA_CN + "sounds/minutes/").append(song);
                    break;
                default:
                    url.append("http://staticvip." + ConstantManager.IYUBA_CN + "sounds/voa").append(song);
                    break;
            }
        } else {
            switch (app) {
                case "209":
                    url.append(ConstantManager.songUrl).append(song);
                    break;
                case "215":
                case "221":
                case "231":
                    url.append("http://staticvip." + ConstantManager.IYUBA_CN + "sounds/minutes/").append(song);
                    break;
                default:
                    url.append("http://staticvip." + ConstantManager.IYUBA_CN + "sounds/voa").append(song);
                    break;
            }
        }
        return url.toString();
    }

    public static void checkScore(int articleId, final IOperationResult iOperationResult) {
        if (checkVip()) {
            iOperationResult.success(null);
            return;
        }
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);//设置日期格式
        try {
            IyumusicRetrofitNetwork.getDeductPointsForDownloadApi().DuctPointsForDownload("39","1",
                    Base64.encodeToString(URLEncoder.encode(df.format(new Date(System.currentTimeMillis())), "UTF-8").getBytes(), Base64.DEFAULT),
                    AccountManager.getInstance().getUserId(),"209",articleId+"").enqueue(new Callback<DownLoadJFResult>() {
                @Override
                public void onResponse(Call<DownLoadJFResult> call, retrofit2.Response<DownLoadJFResult> response) {
                    Log.e("Onresponse","----year!");
                    if(response.isSuccessful()){
                        DownLoadJFResult result = response.body();
                        Log.e("tag",result.toString());
                        if(result!=null&&"200".equals(result.getResult())){
                            CustomToast.getInstance().showToast("积分已经扣除" + Math.abs(Integer.parseInt(result.getAddcredit()))
                                    + "，现在剩余积分为" + result.getTotalcredit());
                            iOperationResult.success(null);
                        }else {
                            iOperationResult.fail(null);
                            CustomToast.getInstance().showToast("积分剩余不足，不能够下载文章了。");
                        }

                    }else {
                        iOperationResult.fail(null);
                        CustomToast.getInstance().showToast("请求异常。");
                    }
                }

                @Override
                public void onFailure(Call<DownLoadJFResult> call, Throwable t) {
                    iOperationResult.fail(null);
                    Log.e("ONFAILure",t.toString());
                    CustomToast.getInstance().showToast("网络错误，请稍后尝试");
                }
            });

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            iOperationResult.fail(null);
            CustomToast.getInstance().showToast(RuntimeManager.getString(R.string.data_error));
        }
        }else {
            iOperationResult.fail(null);
            CustomToast.getInstance().showToast(RuntimeManager.getString(R.string.no_internet));
        }
//        ScoreOperRequest.exeRequest(ScoreOperRequest.generateUrl(AccountManager.getInstance().getUserId(), articleId, 40), new IProtocolResponse() {
//            @Override
//            public void onNetError(String msg) {
//                iOperationResult.fail(null);
//                CustomToast.getInstance().showToast("网络错误，请稍后尝试");
//            }
//
//            @Override
//            public void onServerError(String msg) {
//                iOperationResult.fail(null);
//                Log.e("ServerError",msg+"");
//                CustomToast.getInstance().showToast("积分剩余不足，不能够下载文章了。");
//            }
//
//            @Override
//            public void response(Object object) {
//                BaseApiEntity apiEntity = (BaseApiEntity) object;
//                if (apiEntity.getState() == BaseApiEntity.SUCCESS) {
//                    CustomToast.getInstance().showToast("积分已经扣除" + Math.abs(Integer.parseInt(apiEntity.getMessage()))
//                            + "，现在剩余积分为" + apiEntity.getValue());
//                    iOperationResult.success(null);
//                } else {
//                    CustomToast.getInstance().showToast("积分剩余不足，不能够下载文章了。");
//                    iOperationResult.fail(null);
//                }
//                Log.e("response","---");
//            }
//        });
    }
}
