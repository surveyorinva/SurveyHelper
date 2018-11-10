package com.survlogic.surveyhelper.activity.appSettings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.survlogic.surveyhelper.R;
import com.survlogic.surveyhelper.utils.RemoteConfigLoader;

public class LegalTermsActivity extends AppCompatActivity implements RemoteConfigLoader.RemoteConfigLoaderListener {

    private static final String TAG = "LegalTermsActivity";

    @Override
    public void isLoaded(boolean isLoaded) {
        getIntentDelivery();

        initView();
        initWebView();

    }

    private Context mContext;
    private RemoteConfigLoader remoteConfigLoader;

    private String webViewURLFromBreadcrumb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity_legal_policy);

        mContext = LegalTermsActivity.this;
        remoteConfigLoader = new RemoteConfigLoader(mContext, this);

    }

    private void getIntentDelivery(){
        webViewURLFromBreadcrumb = getIntent().getStringExtra(getString(R.string.KEY_PARENT_ACTIVITY));
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
    }

    private void initWebView(){
        String url = "https://www.christopherconsultants.com";
        TextView tvAppBarTitle = findViewById(R.id.appBar_top_title);


        if (webViewURLFromBreadcrumb.equals(getResources().getString(R.string.CLASS_SETTINGS_LEGAL_TERMS))){
            tvAppBarTitle.setText(getResources().getString(R.string.appSettings_general_about_terms_of_use));
            tvAppBarTitle.setVisibility(View.VISIBLE);

            url = remoteConfigLoader.fetchRemoteConfigURLTermsOfUse();

        } else if(webViewURLFromBreadcrumb.equals(getResources().getString(R.string.CLASS_SETTINGS_LEGAL_PRIVACY))){
            tvAppBarTitle.setText(getResources().getString(R.string.appSettings_general_about_privacy_policy));
            tvAppBarTitle.setVisibility(View.VISIBLE);

            url = remoteConfigLoader.fetchRemoteConfigURLPrivacyPolicy();
        }

        WebView webView = findViewById(R.id.webView_legal_terms_of_use);
        webView.setWebViewClient(new MyBrowser());

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(false);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        webView.loadUrl(url);

    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
