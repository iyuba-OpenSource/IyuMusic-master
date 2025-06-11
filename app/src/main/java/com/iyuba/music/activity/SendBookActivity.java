package com.iyuba.music.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.iyuba.music.R;
import com.iyuba.music.manager.ConfigManager;

public class SendBookActivity extends BaseActivity {

    private TextView text;
    private TextView commit;
    private TextView cancel;

    private RelativeLayout relativeLayout_title_and_back;


    /**
     * 设置添加屏幕的背景透明度
     **/
    public void backgroundAlpaha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_sendbook);
        text = (TextView) findViewById(R.id.text);
        commit = (TextView) findViewById(R.id.commit);
        cancel = (TextView) findViewById(R.id.cancel);

        if (ConfigManager.getInstance().isNight()) {
            backgroundAlpaha(0.7f);
        }

        initWidget();
        setListener();
        changeUIByPara();
        title.setText("送书啦");

        SpannableString spanText = new SpannableString("送书啦！\n" +
                "只要在手机应用商店中对本应用进行五星好评，并截图发送给QQ：3099007489，即可获得十本英文短篇名著+最适合学英语的20部经典英文电影。\n机会难得，不容错过，小伙伴们赶快行动吧!");


        spanText.setSpan(new ClickableSpan() {

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.skin_app_color));       //设置文件颜色
                ds.setUnderlineText(true);      //设置下划线
            }

            @Override
            public void onClick(View view) {
//                if (isQQClientAvailable(SendBookActivity.this)) {
                    String url = "mqqwpa://im/chat?chat_type=wpa&uin=";
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url + "3099007489")));
//                } else {
//
//                    ToastUtil.showToast(SendBookActivity.this, "未安装qq客户端");
//
//                }

            }
        }, 35, 45, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        text.setHighlightColor(Color.TRANSPARENT); //设置点击后的颜色为透明，否则会一直出现高亮
        text.setText(spanText);
        text.setMovementMethod(LinkMovementMethod.getInstance());//开始响应点击事件


        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    //去评价后标记，再也不弹出好评送书弹框
                    ConfigManager.getInstance().putBoolean("firstSendbook", true);


                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);


                } catch (Exception e) {
                    AlertDialog dialog = new AlertDialog.Builder(SendBookActivity.this).create();
                    dialog.setIcon(android.R.drawable.ic_dialog_alert);
                    dialog.setTitle("提示");
                    dialog.setMessage(getResources().getString(R.string.about_market_error));
                    dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    dialog.show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    /**
     * 判断qq是否可用
     *
     * @return
     */
//    public static boolean isQQClientAvailable(Context context) {
//        final PackageManager packageManager = context.getPackageManager();
//        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
//        if (pinfo != null) {
//            for (int i = 0; i < pinfo.size(); i++) {
//                String pn = pinfo.get(i).packageName;
//                if (pn.equals("com.tencent.mobileqq")) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

    private int getStatusBarHeight() {
        //状态栏高度
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
