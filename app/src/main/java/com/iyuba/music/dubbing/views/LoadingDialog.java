package com.iyuba.music.dubbing.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import com.iyuba.music.R;


/**
 * 自定义加载进度对话框
 * Created by Administrator on 2016-10-28.
 */

public class LoadingDialog extends Dialog {
    TextView mTextTv;

    public LoadingDialog(Context context) {
        super(context, R.style.DialogTheme);
        /**设置对话框背景透明*/
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_loading);

        mTextTv = findViewById(R.id.loading_tv);
        setCanceledOnTouchOutside(false);
//        int dividerId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
//        View divider = findViewById(dividerId);
//        divider.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));

        mTextTv.setText("正在加载~");
    }

    /**
     * 为加载进度个对话框设置不同的提示消息
     *
     * @param message 给用户展示的提示信息
     * @return build模式设计，可以链式调用
     */
    public LoadingDialog setMessage(String message) {
        mTextTv.setText(message);
        return this;
    }


}
