package com.iyuba.music.activity;

import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.iyuba.imooclib.ui.web.Web;
import com.iyuba.music.R;
import com.iyuba.music.listener.IOperationResultInt;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.ContextMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10202 on 2015/11/23.
 */
public class WebViewActivity extends BaseInputActivity {
    private WebView web;
    private String url, titleText;
    private ProgressBar loadProgress;
    private ContextMenu menu;
    private TextView source;
    private ValueAnimator progressBarAnimator;
    private int mCurrentProgress;
    private View progressLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();

    }

    @Override
    public void onBackPressed() {
        if (menu.isShown()) {
            menu.dismiss();
        } else if (web.canGoBack()) {
            web.goBack(); // goBack()表示返回webView的上一页面
            title.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String titleContent = web.getTitle();
                    if (!TextUtils.isEmpty(titleContent)) {
                        title.setText(titleContent.length() > 12 ? titleContent.substring(0, 9) + "..." : titleContent);
                    }
                }
            }, 500);
        } else if (!web.canGoBack()) {
            if (!mipush) {
                super.onBackPressed();
            } else {
                startActivity(new Intent(context, MainActivity.class));
            }
        }
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        web = (WebView) findViewById(R.id.webview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // [bug:86726] android 5.0增强安全机制，不允许https http混合，加此配置解决
            web.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        loadProgress = (ProgressBar) findViewById(R.id.load_progress);
        progressLayout = findViewById(R.id.load_progress_layout);
        progressBarAnimator = new ValueAnimator().setDuration(300);
        progressBarAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (int) animation.getAnimatedValue();
                loadProgress.getLayoutParams().width = (int) (progress / 100f * RuntimeManager.getWindowWidth());
                loadProgress.requestLayout();
            }
        });
        source = (TextView) findViewById(R.id.source);
        initContextMenu();
    }

    private void initContextMenu() {
        menu = new ContextMenu(context);
        ArrayList<String> list = new ArrayList<>();
        list.add(context.getString(R.string.webview_on_other));
        list.add(context.getString(R.string.webview_copy));
        menu.setInfo(list, new IOperationResultInt() {
            @Override
            public void performance(int index) {
                if (index == 0) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                } else if (index == 1) {
                    ClipData clip = ClipData.newPlainText("url link", url);
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setPrimaryClip(clip);
                    CustomToast.getInstance().showToast(R.string.webview_clip_board);
                }
            }
        });
    }

    @Override
    protected void setListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        web.setWebChromeClient(
                new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        startProgressBarAnim(newProgress);
                        super.onProgressChanged(view, newProgress);
                    }

                    @Override
                    public void onReceivedTitle(WebView view, String titleContent) {
                        super.onReceivedTitle(view, titleContent);
                        if (TextUtils.isEmpty(titleText))
                            title.setText(titleContent.length() > 12 ? titleContent.substring(0, 9) + "..." : titleContent);
                    }
                }
        );
        web.setDownloadListener(new DownloadListener() {
            @Override

            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null) return false;

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

                if (url.contains("http://weixin")) {
                    startActivity(intent);

                    return true;
                }

                if ((isUrlMarketOrNonHttp(url)) && (isIntentAvailable(WebViewActivity.this, intent))) {
                    startActivity(intent);
                    finish();
                    return true;
                }


                return false;
            }


        });

        web.getSettings().setBlockNetworkImage(false);//解决图片不显示

        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setSupportZoom(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setDisplayZoomControls(false);
        web.getSettings().setDomStorageEnabled(true);
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.show();
            }
        });
    }

    protected void changeUIByPara() {
        backIcon.setState(MaterialMenuDrawable.IconState.X);
        url = getIntent().getStringExtra("url");

//        url = "http://www.xkb365.com/index.php?app=w3g&mod=About&act=wxggz118";

        Log.e("url", url);
        titleText = getIntent().getStringExtra("title");
        source.setText(context.getString(R.string.webview_source, hideMessage(url)));
        web.loadUrl(url);
        title.setText(titleText);
        toolbarOper.setText(R.string.app_more);
        loadProgress.setMax(100);
        loadProgress.setProgress(0);
    }

    @Override
    public void onDestroy() {
        web.destroy();
        super.onDestroy();
    }

    private String hideMessage(String url) {
        if (url.contains("?")) {
            return url.substring(url.indexOf("://") + 3, url.indexOf("?"));
        } else {
            return url.substring(url.indexOf("://") + 3);
        }
    }

    private void startProgressBarAnim(int newProgress) {
        if (mCurrentProgress == newProgress) {
            return;
        }
        if (progressBarAnimator.isRunning()) {
            progressBarAnimator.end();
        }
        progressBarAnimator.setIntValues(mCurrentProgress, newProgress);
        if (newProgress >= 100) {
            progressLayout.setVisibility(View.GONE);
        } else {
            progressLayout.setVisibility(View.VISIBLE);
        }
        progressBarAnimator.start();
        mCurrentProgress = newProgress;
    }


    private static boolean isUrlHttpOrHttps(String url) {
        if (url == null) {
            return false;
        }

        String str = Uri.parse(url).getScheme();
        return ("http".equals(str)) || ("https".equals(str));
    }

    private static boolean isUrlMarket(String url) {
        if (url == null) {
            return false;
        }

        Uri uri = Uri.parse(url);
        String scheme = uri.getScheme();
        String host = uri.getHost();

        if (("play.google.com".equals(host)) || ("market.android.com".equals(host))) {
            return true;
        }

        if ("market".equals(scheme)) {
            return true;
        }

        return false;
    }

    private static boolean isUrlMarketOrNonHttp(String url) {

        return (isUrlMarket(url)) || (!isUrlHttpOrHttps(url));
    }


    private static boolean isIntentAvailable(Context context, Intent intent) {
        try {
            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
            return !resolveInfos.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
