package com.iyuba.music.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.iyuba.music.R;
import com.iyuba.music.dubbing.views.LoadingDialog;
import com.umeng.analytics.MobclickAgent;

public class WebActivity extends BaseActivity {
    private static final String URL = "url";
    private static final String TITLE = "title";
    private static final String BUTTON = "button_right";
    private static final String BUTTON_URL = "button_right_url";

    public static Intent buildIntent(Context context, String url) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(URL, url);
        return intent;
    }

    public static Intent buildIntent(Context context, String url, String title) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(URL, url);
        intent.putExtra(TITLE, title);
        return intent;
    }

    public static Intent buildIntent(Context context, String url, String right, String rightUrl) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(URL, url);
        intent.putExtra(BUTTON, right);
        intent.putExtra(BUTTON_URL, rightUrl);
        return intent;
    }

    private LoadingDialog mLoadingDialog;
    private Toolbar toolbar;
    private TextView tvRight;
    private WebView webWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        toolbar = findViewById(R.id.web_toolbar);
        webWebview = findViewById(R.id.web_webview);
        setSupportActionBar(toolbar);
        mLoadingDialog = new LoadingDialog(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Intent intent = this.getIntent();
        String url = intent.getStringExtra(URL);
//        String url ="https://wxpay.wxutil.com/mch/pay/h5.v2.php";
        String title = intent.getStringExtra(TITLE);
        String right = intent.getStringExtra(BUTTON);
        String rightUrl = intent.getStringExtra(BUTTON_URL);
        if (!TextUtils.isEmpty(title)) {
            toolbar.setTitle(title);
        }

        if (!TextUtils.isEmpty(right)) {
            tvRight.setVisibility(View.VISIBLE);
            tvRight.setText(right);
            final String finalRightUrl = rightUrl;
            tvRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(WebActivity.buildIntent(context, finalRightUrl));
                }
            });
        }

//        mWebView.addJavascriptInterface(this, "Android");
        webWebview.getSettings().setJavaScriptEnabled(true);
        if (!TextUtils.isEmpty(url)) {
            webWebview.loadUrl(url);
        }
        webWebview.requestFocus();
        webWebview.getSettings().setBuiltInZoomControls(true);// 显示放大缩小
        webWebview.getSettings().setSupportZoom(true);// 可放大
        webWebview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);// 提高渲染,加快加载速度
        webWebview.getSettings().setUseWideViewPort(true);
        webWebview.getSettings().setLoadWithOverviewMode(true);
        webWebview.getSettings().setDomStorageEnabled(true);
        webWebview.getSettings().setDatabaseEnabled(true);
        webWebview.getSettings().setGeolocationEnabled(true);

        webWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                toolbar.setTitle(title);
            }
        });
        webWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (!mLoadingDialog.isShowing() && !isDestroyed()) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                mLoadingDialog.dismiss();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                url = "https://wxpay.wxutil.com/mch/pay/h5jumppage.php";
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mLoadingDialog.dismiss();
            }
        });
        webWebview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onKeyDown(int keyCoder, KeyEvent event) {
        if (webWebview.canGoBack() && keyCoder == KeyEvent.KEYCODE_BACK) {
            webWebview.goBack();
            return true;
        } else if (!webWebview.canGoBack() && keyCoder == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void finish() {
        if (mLoadingDialog != null)
            mLoadingDialog.dismiss();
        super.finish();
    }

    @Override
    protected void onDestroy() {
        if (mLoadingDialog != null)
            mLoadingDialog.dismiss();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }

}
