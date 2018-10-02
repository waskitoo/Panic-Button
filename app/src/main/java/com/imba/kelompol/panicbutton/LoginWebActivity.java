package com.imba.kelompol.panicbutton;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.imba.kelompol.panicbutton.API.EndpointEmeract;
import com.imba.kelompol.panicbutton.API.RetrofitClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginWebActivity extends AppCompatActivity {

    private WebView webViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_web);

        webViewLogin = (WebView) findViewById(R.id.webviewLogin);

        // Get Passing Param
        Intent ini = getIntent();
        String url = ini.getStringExtra("url");

        // WebView
        WebSettings webSettings = webViewLogin.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 6.4; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2225.0 Safari/537.36");
        webViewLogin.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("URL_OPEN_WEB", url);
                String key = "/auth/user/success";
                if (url.contains(key)) {
                    Toast.makeText(LoginWebActivity.this, "You're Already Sign-in", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    LoginWebActivity.this.finish();
                    return true;
                }
                return false;
                //return super.shouldOverrideUrlLoading(view, url);
            }
        });
        webViewLogin.loadUrl(url);
    }
}
