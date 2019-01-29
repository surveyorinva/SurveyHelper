package com.survlogic.surveyhelper.activity.staffCompany.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.utils.BaseActivity;

public class CompanyWebActivity extends BaseActivity {
    private static final String TAG = "CardFeedEventWebActivit";

    private Context mContext;

    private String webViewURLFromBreadcrumb, webViewActivityTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_company_item_web_activity);

        mContext = CompanyWebActivity.this;

        initView();
        initWebView();

    }

    private void getIntentDelivery(){
        webViewURLFromBreadcrumb = getIntent().getStringExtra(getString(R.string.KEY_EVENT_WEB_URL));
        webViewActivityTitle = getIntent().getStringExtra(getString(R.string.KEY_EVENT_HEADER_TITLE));

    }

    private void initView(){
        ImageButton ibAppBarBack = findViewById(R.id.appBar_top_action_nav_back);
        ibAppBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ibAppBarBack.setVisibility(View.VISIBLE);

        getIntentDelivery();
    }

    private void initWebView(){
        TextView tvAppBarTitle = findViewById(R.id.appBar_top_title);
        tvAppBarTitle.setText(webViewActivityTitle);

        WebView webView = findViewById(R.id.webView_event);
        webView.setWebViewClient(new MyBrowser());

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        webView.loadUrl(webViewURLFromBreadcrumb);

    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }


}

