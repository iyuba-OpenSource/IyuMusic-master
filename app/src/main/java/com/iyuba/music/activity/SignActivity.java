package com.iyuba.music.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;
import com.iyuba.music.R;
import com.iyuba.music.data.Constant;
import com.iyuba.music.entity.SignBean;
import com.iyuba.music.entity.StudyTimeBeanNew;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.merequest.SignBeanRequest;
import com.iyuba.music.request.merequest.StudyTimeBeanRequest;
import com.iyuba.music.util.Base64Coder;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.wechat.moments.WechatMoments;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * 打卡页面
 */

public class SignActivity extends Activity {


    private ImageView imageView;
    private ImageView qrImage;
    private TextView tv1, tv2, tv3;
    private Context mContext;
    private TextView sign;
    private ImageView userIcon;
    private TextView tvShareMsg;
    private int signStudyTime = 3 * 60;
    private String loadFiledHint = "打卡加载失败";

    String shareTxt;
    String getTimeUrl;
    LinearLayout ll;
    IyubaDialog mWaittingDialog;
    String addCredit = "";//Integer.parseInt(bean.getAddcredit());
    String days = "";//Integer.parseInt(bean.getDays());
    String totalCredit = "";//bean.getTotalcredit();
    String money = "";
//    private int QR_HEIGHT = 77;
//    private int QR_WIDTH = 77;

    private TextView tvUserName;
    private TextView tvAppName;
    private TextView tv_finish;

    private ImageView btn_close;
    private MaterialDialog dialog, dialog_share;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mContext = this;

//        mWaittingDialogBuilder.wettingDialog(mContext);
        mWaittingDialog = WaitingDialog.create(this, "请稍后");
        setContentView(R.layout.activity_sign);

        //状态栏处理
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {

            //android手机小于5.0的直接全屏显示，防止截图留白边
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        initView();

        initData();

        initBackGround();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void initData() {

        mWaittingDialog.show();

        String uid = AccountManager.getInstance().getUserId();


        final String url = String.format(Locale.CHINA,
                "http://daxue." + ConstantManager.IYUBA_CN + "ecollege/getMyTime.jsp?uid=%s&day=%s&flg=1", uid, getDays());
        Log.d("dddd", url);
        getTimeUrl = url;


        StudyTimeBeanRequest.exeRequest(url, new IProtocolResponse() {
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
                try {
                    if (null != mWaittingDialog) {
                        if (mWaittingDialog.isShowing()) {
                            mWaittingDialog.dismiss();
                        }
                    }
                    Log.e("ppp====", object.toString());
/*
                    JSONObject jsonObject = new JSONObject(object.toString());

                    String result = jsonObject.optString("result");
                    String totalTime = jsonObject.optString("totalTime");
                    String totalDaysTime = jsonObject.optString("totalDaysTime");
                    String totalWords = jsonObject.optString("totalWords");
                    String totalDays = jsonObject.optString("totalDays");
                    String sentence = jsonObject.optString("sentence");
                    String totalUser = jsonObject.optString("totalUser");
                    String ranking = jsonObject.optString("ranking");
                    String totalWord = jsonObject.optString("totalWord");*/

                    final StudyTimeBeanNew bean = new Gson().fromJson(object.toString(), StudyTimeBeanNew.class);

                    Log.e("ppp====", bean.toString());
                    if ("1".equals(bean.getResult())) {
                        final int time = Integer.parseInt(bean.getTotalTime());
                        int totaltime = Integer.parseInt(bean.getTotalDaysTime());

                        tv1.setText(bean.getTotalDays() + ""); //学习天数
                        tv2.setText(bean.getTotalWord() + "");//今日单词f
                        //TODO
                        int nowRank = Integer.parseInt(bean.getRanking());
                        double allPerson = Double.parseDouble(bean.getTotalUser());
                        double carry;
                        String over = null;
                        if (allPerson != 0) {
                            carry = 1 - nowRank / allPerson;
                            DecimalFormat df = new DecimalFormat("0.00");
                            Log.e("百分比", df.format(carry) + "--" + nowRank + "--" + allPerson);

                            over = df.format(carry).substring(2, 4);
                        }

                        tv3.setText(over + "%同学"); //超越了
                        shareTxt = bean.getSentence() + "我在爱语吧坚持学习了" + bean.getTotalDays() + "天,积累了" + bean.getTotalWords()
                                + "单词如下";

                        if (time < signStudyTime) {
                            sign.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    toast(String.format(Locale.CHINA, "打卡失败，当前已学习%d秒，\n满%d分钟可打卡", time, signStudyTime / 60));

                                }
                            });
                        } else {
                            sign.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    qrImage.setVisibility(View.VISIBLE);

                                    sign.setVisibility(View.GONE);

                                    tvShareMsg.setVisibility(View.VISIBLE);
                                    tv_finish.setVisibility(View.VISIBLE);
                                    tvShareMsg.setText("长按图片识别二维码");
                                    tvShareMsg.setBackground(getResources().getDrawable(R.drawable.sign_bg_yellow));

                                    writeBitmapToFile();


                                    showShareOnMoment(mContext, AccountManager.getInstance().getUserId(), ConstantManager.appId);

//                                        shareUrl(shareDownloadUrl,shareTxt,bitmap,"我的学习记录", SendMessageToWX.Req.WXSceneTimeline);
                                }
                            });
//                            startShareInterface(); //TODO 票圈分享
                        }
                    } else {
                        toast(loadFiledHint + bean.getResult());
                    }
                } catch (Exception e) {
                    Log.e("异常", e.toString());
                    e.printStackTrace();
                    toast(loadFiledHint + "！！");
                }
            }
        });

    }

    private void toast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    private long getDays() {
        //东八区;
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        cal.set(1970, 0, 1, 0, 0, 0);
        Calendar now = Calendar.getInstance(Locale.CHINA);
        long intervalMilli = now.getTimeInMillis() - cal.getTimeInMillis();
        long xcts = intervalMilli / (24 * 60 * 60 * 1000);
        return xcts;
    }

    private void initView() {

        imageView = (ImageView) findViewById(R.id.iv);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);

        sign = (TextView) findViewById(R.id.tv_sign);
        ll = (LinearLayout) findViewById(R.id.ll);
        qrImage = (ImageView) findViewById(R.id.tv_qrcode);
        userIcon = (ImageView) findViewById(R.id.iv_userimg);
        tvUserName = (TextView) findViewById(R.id.tv_username);
        tvAppName = (TextView) findViewById(R.id.tv_appname);
        tvShareMsg = (TextView) findViewById(R.id.tv_sharemsg);

        btn_close = (ImageView) findViewById(R.id.btn_close);
        tv_finish = (TextView) findViewById(R.id.tv_finish);
        tv_finish.setText(" 刚刚在『" + ConstantManager.appName + "』上完成了打卡");
        tv_finish.setVisibility(View.INVISIBLE);

        //关闭打卡页面弹出提示
        dialog = new MaterialDialog(SignActivity.this);
        dialog.setTitle("温馨提示");
        dialog.setMessage("点击下边的打卡按钮，成功分享至微信朋友圈才算成功打卡，才能领取红包哦！确定退出么？");
        dialog.setPositiveButton("继续打卡", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("去意已决", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });


        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();

            }
        });


        //当天再次打卡成功后显示
        dialog_share = new MaterialDialog(SignActivity.this);
        dialog_share.setTitle("提醒");
//        dialog_share.setMessage("今日已打卡，不能再次获取红包或积分哦！");
        dialog_share.setPositiveButton("好的", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_share.dismiss();
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initBackGround() {

        int day = Calendar.getInstance(Locale.CHINA).get(Calendar.DAY_OF_MONTH);
        String url = "http://staticvip." + ConstantManager.IYUBA_CN + "images/mobile/" + day + ".jpg";

//        bitmap = returnBitMap(url);
        Glide.with(mContext).load(url).placeholder(R.drawable.sign_background).error(R.drawable.sign_background).into(imageView);
        final String userIconUrl = "http://api." + Constant.IYUBA_COM + "v2/api.iyuba?protocol=10005&uid="
                + AccountManager.getInstance().getUserId() + "&size=middle";


//        Glide.with(mContext).load(userIconUrl).into(userIcon);

        Glide.with(mContext)
                .asBitmap()  //这句不能少，否则下面的方法会报错
                .load(userIconUrl)
                .placeholder(R.drawable.default_photo)
                .error(R.drawable.default_photo)
                .centerCrop()
                .into(new BitmapImageViewTarget(userIcon) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        userIcon.setImageDrawable(circularBitmapDrawable);
                    }
                });


        Log.d("diao", "initBackGround: " + AccountManager.getInstance().getUserId() + ":" + AccountManager.getInstance().getUserInfo().getUsername());
        if (TextUtils.isEmpty(AccountManager.getInstance().getUserInfo().getUsername())) {
            tvUserName.setText(AccountManager.getInstance().getUserId());
        } else {
            tvUserName.setText(AccountManager.getInstance().getUserInfo().getUsername());
        }
        tvAppName.setText("『" + ConstantManager.appName + "』" + "-  英语学习必备软件");

        Glide.with(mContext).load("").placeholder(R.drawable.qrcode).into(qrImage);


    }


    public void writeBitmapToFile() {
        View view = getWindow().getDecorView();
//        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        view.measure(0, 0);
//        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
//        Log.d("diao", "writeBitmapToFile: "+view.getMeasuredWidth()+":"+view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        if (bitmap == null) {
            return;
        }

        bitmap.setHasAlpha(false);
        bitmap.prepareToDraw();

        File newpngfile = new File(getExternalFilesDir(null), "aaa.png");
        if (newpngfile.exists()) {
            newpngfile.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(newpngfile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        tv_finish.setVisibility(View.GONE);
    }


    private void startInterfaceADDScore(String userID, String appid) {

        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        String time = Base64Coder.encode(dateString);

        String url = "http://api." + ConstantManager.IYUBA_CN + "credits/updateScore.jsp?srid=81&mobile=1" + "&uid=" + userID
                + "&appid=" + appid + "&flag=" + time;
        SignBean bean;

        SignBeanRequest.exeRequest(url, new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg + "=");
            }

            @Override
            public void response(Object object) {
                final SignBean bean = new Gson().fromJson(object.toString(), SignBean.class);
                if (bean.getResult().equals("200")) {
                    money = bean.getMoney();
                    addCredit = bean.getAddcredit();
                    days = bean.getDays();
                    totalCredit = bean.getTotalcredit();

                    //打卡成功,您已连续打卡xx天,获得xx元红包,关注[爱语课吧]微信公众号即可提现!
                    runOnUiThread(() -> {
                        float moneyThisTime = Float.parseFloat(money);
                        if (moneyThisTime > 0) {
                            float allmoney = Float.parseFloat(totalCredit);
                            //TODO
                            AccountManager.getInstance().setMoney(totalCredit);
                            MobclickAgent.onEvent(SignActivity.this, "dailybonus"); //友盟打卡统计
                            dialog_share.setMessage("打卡成功," + "您已连续打卡" + days + "天,获得" + floatToString(moneyThisTime) + "元,总计: " + floatToString(allmoney) + "元," + "满十元可在\"爱语课吧\"公众号提现");
                            dialog_share.show();
//                                Toast.makeText(mContext, "打卡成功," + "您已连续打卡" + days + "天,  获得" + moneyThisTime * 0.01 + "元,总计: " + allmoney * 0.01 + "元," + "满十元可在\"爱语课吧\"公众号提现", Toast.LENGTH_LONG).show();
                        } else {
                            dialog_share.setMessage("打卡成功，连续打卡" + days + "天,获得" + addCredit + "积分，总积分: " + totalCredit);
                            dialog_share.show();
//                                Toast.makeText(mContext, "打卡成功，连续打卡" + days + "天,获得" + addCredit + "积分，总积分: " + totalCredit, Toast.LENGTH_LONG).show();
                        }
                    });

                } else {
                    runOnUiThread(() -> {
                        dialog_share.setMessage("今日已打卡，重复打卡不能再次获取红包或积分哦！");
                        dialog_share.show();
//                            Toast.makeText(mContext, "您今天已经打过卡了,", Toast.LENGTH_SHORT).show();
                    });

                }
            }
        });

    }

    public void showShareOnMoment(Context context, final String userID, final String AppId) {

        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();


//        Bitmap bitmap = BitmapFactory.decodeResource(R.drawable.abc_ab_share_pack_holo_dark, )

        oks.setPlatform(WechatMoments.NAME);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setImagePath(getExternalFilesDir(null).getAbsolutePath() + "/aaa.png");


        oks.setSilent(true);

        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Log.e("--分享成功===", "....");
                startInterfaceADDScore(userID, AppId);
                tv_finish.setVisibility(View.INVISIBLE);
                btn_close.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.e("okCallbackonError", "onError");
                Log.e("--分享失败===", throwable.toString());
                tv_finish.setVisibility(View.INVISIBLE);
                btn_close.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.e("okCallbackonError", "onError");
                Log.e("--分享取消===", "....");
                tv_finish.setVisibility(View.INVISIBLE);
                btn_close.setVisibility(View.VISIBLE);


                qrImage.setVisibility(View.GONE);
                sign.setVisibility(View.VISIBLE);
                tvShareMsg.setVisibility(View.VISIBLE);
                tv_finish.setVisibility(View.GONE);
                btn_close.setVisibility(View.VISIBLE);
                tvShareMsg.setText("天天学习，每日红包！");
                tvShareMsg.setBackground(getResources().getDrawable(R.color.transparent));
            }
        });
        // 启动分享GUI
        oks.show(context);
    }


    private String floatToString(float fNumber) {

        fNumber = (float) (fNumber * 0.01);

        DecimalFormat myformat = new java.text.DecimalFormat("0.00");
        String str = myformat.format(fNumber);
        return str;
    }

}



