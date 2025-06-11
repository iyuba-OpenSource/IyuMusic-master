package com.iyuba.music.dubbing.views;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.iyuba.music.R;


public class DownloadDialog extends BaseDialog {

    private OnDownloadListener mOnDownloadListener;

    TextView continue_loading_tv;

    TextView cancel_loading_tv;

    public DownloadDialog(Context context) {
        super(context, R.style.DialogTheme);
    }

    public DownloadDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public void setmOnDownloadListener(OnDownloadListener listener) {
        this.mOnDownloadListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_download);

        continue_loading_tv = findViewById(R.id.continue_loading_tv);
        cancel_loading_tv = findViewById(R.id.cancel_loading_tv);

        continue_loading_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onContinueLoadingClick();
            }
        });
        cancel_loading_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onCancelLoadingClick();
            }
        });

        WindowManager m = getOwnerActivity().getWindowManager();
        Display defaultDisplay = m.getDefaultDisplay();
        Window window = getWindow();
        window.setBackgroundDrawableResource(R.drawable.dialog_bkg);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = (int) (defaultDisplay.getWidth() * 0.78);
        window.setAttributes(layoutParams);
    }

    void onContinueLoadingClick() {
        if (mOnDownloadListener != null) {
            mOnDownloadListener.onContinue();
        }
    }

    void onCancelLoadingClick() {
        if (mOnDownloadListener != null) {
            mOnDownloadListener.onCancel();
        }
    }

    public interface OnDownloadListener {
        void onContinue();

        void onCancel();
    }
}
