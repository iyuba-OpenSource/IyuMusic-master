package com.iyuba.music.util.popup;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.iyuba.music.R;

import razerdp.basepopup.BasePopupWindow;


/**
 * 隐私协议的弹窗
 */
public class PrivacyPopup extends BasePopupWindow {

    private TextView privacy_tv_content;
    private TextView privacy_tv_no;
    private TextView privacy_tv_yes;

    private Callback callback;

    public PrivacyPopup(Context context) {
        super(context);
        setContentView(createPopupById(R.layout.popup_privacy));
        setOutSideDismiss(false);
        setBackPressEnable(false);
    }

    @Override
    public void onViewCreated(@NonNull View contentView) {
        super.onViewCreated(contentView);

        initView(contentView);


    }

    /**
     * 初始化控件
     *
     * @param contentView
     */
    private void initView(View contentView) {


        privacy_tv_content = contentView.findViewById(R.id.privacy_tv_content);
        privacy_tv_no = contentView.findViewById(R.id.privacy_tv_no);
        privacy_tv_yes = contentView.findViewById(R.id.privacy_tv_yes);

        //同意
        privacy_tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (callback != null) {

                    callback.yes();
                }
            }
        });
        //不同意
        privacy_tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (callback != null) {

                    callback.no();
                }
            }
        });

        //文字相关
        String str = "1.为了更方便您使用我们的软件，我们会根据您使用具体功能时，申请必要的权限，如摄像头，存储权限等。\n" +
                "2.使用本app需要您了解并同意用户协议及隐私政策，点击同意即代表您已阅读并同意该协议。";

        int begin = str.indexOf("用户协议及隐私政策");

        ClickableSpan spanAgreement = new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#5089F8"));//设置超链接的颜色
                ds.setUnderlineText(true);
            }

            @Override
            public void onClick(View widget) {

                if (callback != null) {

                    callback.user();
                }
            }
        };


        ClickableSpan spanPrivacyPolicy = new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#5089F8"));//设置超链接的颜色
                ds.setUnderlineText(true);
            }

            @Override
            public void onClick(View widget) {

                if (callback != null) {

                    callback.privacy();
                }
            }
        };
        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(spanAgreement, begin, begin + 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(spanPrivacyPolicy, begin + 5, begin + 9, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        privacy_tv_content.setMovementMethod(LinkMovementMethod.getInstance());
        privacy_tv_content.setText(spannableString);
        privacy_tv_content.setHighlightColor(Color.TRANSPARENT);
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {

        /**
         * 同意
         */
        void yes();

        /**
         * 不同意
         */
        void no();

        /**
         * 点击用户协议
         */
        void user();

        /**
         * 点击隐私协议
         */
        void privacy();
    }
}
