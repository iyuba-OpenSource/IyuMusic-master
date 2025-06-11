package com.iyuba.music;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

import com.iyuba.music.databinding.ActivityMyWebBinding;


/**
 * 主要是显示隐私协议的
 */
public class MyWebActivity extends AppCompatActivity {

    private ActivityMyWebBinding activityMyWebBinding;

    private String url;

    private String toolbarName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMyWebBinding = ActivityMyWebBinding.inflate(getLayoutInflater());
        setContentView(activityMyWebBinding.getRoot());

        getBundle();

        activityMyWebBinding.toolbar.toolbarIvRight.setVisibility(View.GONE);

        activityMyWebBinding.toolbar.toolbarIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        activityMyWebBinding.toolbar.toolbarTvTitle.setText(toolbarName);

        activityMyWebBinding.webWv.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });

        activityMyWebBinding.webWv.setWebChromeClient(new WebChromeClient(){

        });

        activityMyWebBinding.webWv.getSettings().setJavaScriptEnabled(true);
      /*  activityMyWebBinding.webWv.getSettings().setAllowFileAccess(true);
        activityMyWebBinding.webWv.getSettings().setAllowContentAccess(true);
        activityMyWebBinding.webWv.getSettings().setDomStorageEnabled(true);
        activityMyWebBinding.webWv.getSettings().setDatabaseEnabled(true);*/
//        activityMyWebBinding.webWv.loadDataWithBaseURL(url, "", "text/html", "utf-8", null);
        activityMyWebBinding.webWv.loadUrl(url);
    }


    private void getBundle() {

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            url = bundle.getString("URL");
            toolbarName = bundle.getString("TOOLBAR_NAME");
        }
    }

    public static void startActivity(Activity activity, String url, String toolbarName) {

        Intent intent = new Intent(activity, MyWebActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("URL", url);
        bundle.putString("TOOLBAR_NAME", toolbarName);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }
}