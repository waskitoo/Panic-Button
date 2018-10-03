package com.imba.kelompol.panicbutton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.imba.kelompol.panicbutton.API.RoutesConf;

public class LogoutWebActivity extends AppCompatActivity {

    private WebView webView;
    private SharedPreferences.Editor prefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_web);

        webView = (WebView) findViewById(R.id.webviewLogin);
        prefEditor = getSharedPreferences(RoutesConf.API_SHARED_USER, 0).edit();

        // Get Passing Param
        String url = RoutesConf.API_BASE_URL + "auth/user/logout";

        // WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 6.4; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2225.0 Safari/537.36");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
//                String key = "/auth/user/logout";
//                if(url.contains(key)){
                Toast.makeText(LogoutWebActivity.this, "You're Already Sign-out. Thanks.", Toast.LENGTH_SHORT).show();
                prefEditor.clear();
                prefEditor.apply();
                startActivity(new Intent(LogoutWebActivity.this, MainActivity.class));
                LogoutWebActivity.this.finish();
//                }

            }
        });
        webView.loadUrl(url);
    }
}
