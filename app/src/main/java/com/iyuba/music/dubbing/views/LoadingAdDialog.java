package com.iyuba.music.dubbing.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iyuba.music.R;
import com.youdao.sdk.nativeads.NativeResponse;

/**
 * 自定义加载进度对话框
 * Created by Administrator on 2016-10-28.
 */

public class LoadingAdDialog extends Dialog {
    ImageView ivAnimLoading;
    ImageView ivHint;
    TextView tvTitle;
    TextView tvRetry;
    TextView tvMessage;
    View adView;
    ImageView adImage;
    TextView adTitle;

    public ImageView close;

//    @BindView(R.id.progress_loading)
//    public ProgressBar progressBar;

    //AdInfoFlowUtil adInfoFlowUtil;
    NativeResponse nativeResponse;

    AnimationDrawable animationDrawable;

    public LoadingAdDialog(final Context context) {
        super(context, R.style.DialogTheme);
        /**设置对话框背景透明*/
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_loading_with_ad);


        ivAnimLoading = findViewById(R.id.iv_anim_loading);
        ivHint = findViewById(R.id.iv_hint);
        tvTitle = findViewById(R.id.tv_title);
        tvMessage = findViewById(R.id.tv_message);
        tvRetry = findViewById(R.id.tv_retry);
        adView = findViewById(R.id.layout_ad);
        adImage = findViewById(R.id.iv_ad);
        adTitle = findViewById(R.id.tv_ad);
        close = findViewById(R.id.iv_close);


        setCanceledOnTouchOutside(false);
        tvTitle.setText("正在加载~");

//        adInfoFlowUtil = new AdInfoFlowUtil(context, false, new AdInfoFlowUtil.Callback() {
//            @Override
//            public void onADLoad(List ads) {
//                if (ads != null && ads.size() > 0) {
//                    nativeResponse = (NativeResponse) ads.get(0);
//                }
//            }
//        }).setAdRequestSize(1);
//        adInfoFlowUtil.refreshAd();


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        // 把动画资源设置为imageView的背景,也可直接在XML里面设置
        ivAnimLoading.setBackgroundResource(R.drawable.publish_loading_anim);
        animationDrawable = (AnimationDrawable) ivAnimLoading.getBackground();
    }


    public void show(boolean vip) {
        if (!vip) {
            if (nativeResponse != null) {
                showAd();
            }
        }
        super.show();

        animationDrawable.start();
    }

    private void showAd() {
        adView.setVisibility(View.VISIBLE);
        adView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nativeResponse.handleClick(adView);
            }
        });
        nativeResponse.recordImpression(adView);
        Glide.with(getContext()).load(nativeResponse.getMainImageUrl())
                .placeholder(R.drawable.loading).into(adImage);
        adTitle.setText(nativeResponse.getTitle());
    }

    @Override
    public void dismiss() {
        //  adInfoFlowUtil.destroy();
        super.dismiss();
    }


    public void setRetryOnClick(View.OnClickListener listener) {
        tvRetry.setOnClickListener(listener);
    }

    CharSequence title;

    public LoadingAdDialog setMessageText(CharSequence message) {
        tvMessage.setText(message);
        return this;
    }

    public void setTitleText(CharSequence message) {
        title = message;
        tvTitle.setText(message);
    }

    public void showSuccess(CharSequence message) {
        animationDrawable.stop();
        ivAnimLoading.setVisibility(View.GONE);

        ivHint.setVisibility(View.VISIBLE);
        ivHint.setImageResource(R.drawable.publish_success);
        setTitleText(message);
    }

    public void showFailure(String message) {
        animationDrawable.stop();
        ivAnimLoading.setVisibility(View.GONE);

        ivHint.setVisibility(View.VISIBLE);
        ivHint.setImageResource(R.drawable.publish_error);
//        setTitleText(message);
    }

    public void showRetryButton() {
        tvRetry.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.GONE);
    }

    public void retry() {
        animationDrawable.start();
        ivAnimLoading.setVisibility(View.VISIBLE);

        ivHint.setVisibility(View.GONE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(title);
        tvRetry.setVisibility(View.GONE);
    }
}
