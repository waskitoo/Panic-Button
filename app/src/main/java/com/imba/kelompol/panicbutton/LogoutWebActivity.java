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
        prefEditor = getSharedPreferences(RoutesConf.API_SHARED_USER,0).edit();

        // Get Passing Param
        String url = RoutesConf.API_BASE_URL+"logout";

        // WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 6.4; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2225.0 Safari/537.36");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                Log.i("URL_OPEN_WEB", url);
//                String key = "/auth/user/success";
//                if (url.contains(key)) {
//                    Toast.makeText(LoginWebActivity.this, "You're Already Sign-in", Toast.LENGTH_SHORT).show();
//                    CookieManager cookieManager = CookieManager.getInstance();
//                    String cookie = cookieManager.getCookie(url);
//                    String userProviderID = cookie.substring(cookie.indexOf("userProviderId"));
//                    userProviderID = userProviderID.substring(userProviderID.indexOf("=")+1,userProviderID.indexOf(";"));
//                    Log.d("COOKIE_MANAGEMENT",cookie);
//                    Log.d("COOKIE_MANAGEMENT_UPID",userProviderID);
//
//                    prefEditor.putString("USER_COOKIE",cookie);
//                    prefEditor.putString("USER_PROVIDER_ID",userProviderID);
//                    prefEditor.apply();
//                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                    LoginWebActivity.this.finish();
//                    return true;
//                }
                return false;
//                //return super.shouldOverrideUrlLoading(view, url);
            }
        });
        webView.loadUrl(url);
    }
}
