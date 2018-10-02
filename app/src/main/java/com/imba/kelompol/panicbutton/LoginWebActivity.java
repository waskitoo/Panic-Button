package com.imba.kelompol.panicbutton;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class LoginWebActivity extends AppCompatActivity {

    private WebView webViewLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_web);

        webViewLogin=(WebView)findViewById(R.id.webviewLogin);

        // Get Passing Param
        Intent ini = getIntent();
        String url = ini.getStringExtra("url");

        // WebView
        WebSettings webSettings = webViewLogin.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 6.4; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2225.0 Safari/537.36");
        webViewLogin.setWebViewClient(new WebViewClient());
        webViewLogin.loadUrl(url);
    }
}
