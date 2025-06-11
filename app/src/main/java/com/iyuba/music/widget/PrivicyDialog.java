package com.iyuba.music.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.iyuba.imooclib.ui.web.Web;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.activity.WelcomeActivity;
import com.iyuba.music.data.Constant;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.util.ToastUtil;

import org.jetbrains.annotations.NotNull;

public class PrivicyDialog {

    private Context mContext;
    private String url;
    public PrivicyDialog(Context mContext) {
        this.mContext = mContext;
    }

    public void initPrivicyDialog() {
        if (ConfigManager.getInstance().isFirstPrivacy()) {
            showFirstOpenAlertDialog(null);
        }
    }

    public void showFirstOpenAlertDialog(Handler handler) {

        String str = "1.为了更方便您使用我们的软件，我们会根据您使用具体功能时，申请必要的权限，如相机，位置，麦克风，存储权限等；获取用户的软件安装列表及收集您的设备Mac地址以提供统计分析服务，可以点击下面的链接了解详情。\n" +
//                "2.我们的产品集成友盟+SDK，友盟+SDK需要收集您的设备Mac地址、唯一设备识别码（IMEI/android ID/IDFA/OPENUDID/GUID、SIM 卡IMSI信息）以提供统计分析服务，并通过地理位置校准报表数据准确性，提供基础反作弊能力。\n" +
//                "3.我们使用了第三方（上海游昆信息技术有限公司，以下称“MobTech”）MobTech SDK 服务为您提供一键登录，短信验证码，一键分享，推送服务等功能。\n" +
                "2.使用本app需要您了解并同意用户协议及隐私政策，点击同意即代表您已阅读并同意该协议。";
        if (Constant.APP_TYPE_PRIVACY == 1) {
            str = "1.为了更方便您使用我们的软件，我们会根据您使用具体功能时，申请必要的权限，如相机，位置，麦克风，存储权限等。\n" +
                    "2.我们的产品集成友盟+SDK，友盟+SDK需要收集您的设备Mac地址、获取用户的软件安装列表以提供统计分析服务。\n" +
                    "3.我们的产品集成有道SDK，有道SDK需要收集您的设备Mac地址、DEVICEID、获取用户的软件安装列表及地理位置信息以提供统计分析服务。\n" +
                    "4.使用本app需要您了解并同意用户协议及隐私政策，点击同意即代表您已阅读并同意该协议。";
        }
        int begin = str.indexOf("用户协议及隐私政策");

        url = Constant.PROTOCOL_URL_IYUBA + Constant.APP_Name;
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_privacy, null);
        TextView textView = view.findViewById(R.id.remindText);
//        TextView textView = new TextView(mContext);
        textView.setPadding(30, 0, 30, 0);
        textView.setTextSize(15);
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(mContext.getResources().getColor(R.color.colorPrimary));
                ds.setUnderlineText(true);
            }

            @Override
            public void onClick(View widget) {
                // 单击事件处理
                mContext.startActivity(Web.buildIntent(mContext, url, "用户隐私协议"));
            }
        };
        SpannableString spannableString = new SpannableString(str);
        if (Constant.APP_NAME_PRIVACY.equalsIgnoreCase("隐私政策")) {
            url = Constant.PROTOCOL_VIVO_PRIVACY + Constant.APP_Name;
            begin = str.indexOf("隐私政策");
            spannableString.setSpan(span, begin, begin + 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            ClickableSpan clickableUsage = new ClickableSpan() {
                @Override
                public void onClick(@NotNull View widget) {
                    mContext.startActivity(Web.buildIntent(mContext, Constant.PROTOCOL_VIVO_USAGE + Constant.APP_Name, "用户协议"));
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(mContext.getResources().getColor(R.color.colorPrimary));
                    ds.setUnderlineText(true);
                }
            };
            spannableString.setSpan(clickableUsage, str.indexOf("用户协议"), str.indexOf("用户协议") + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            spannableString.setSpan(span, begin, begin + 9, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle("个人信息保护政策")
                .setView(view)
                .setCancelable(false)
                .create();
        TextView agree = view.findViewById(R.id.text_agree);
//        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "同意", (v, u) -> {
        agree.setOnClickListener(v -> {
            dialog.dismiss();
            ConfigManager.getInstance().setFirstPrivacy(false);
            if (Constant.APP_TENCENT_PRIVACY) {
//                MusicApplication.initUMMob();
                if (handler != null) {
                    handler.sendEmptyMessage(101);
                }
            }
        });
//        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "不同意", (v, u) -> {
//            dialog.dismiss();
//            ((Activity) mContext).finish();
//        });
        TextView agreeNo = view.findViewById(R.id.text_no_agree);
        agreeNo.setOnClickListener(v -> {
//            ToastUtil.showToast("请同意~");
            if (handler != null) {
                handler.sendEmptyMessage(WelcomeActivity.SET_DEFAULT_EXIT);
            }else {
                System.exit(0);
            }
        });
        dialog.show();
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> ToastUtil.showToast(mContext, "请同意~"));
    }
}
