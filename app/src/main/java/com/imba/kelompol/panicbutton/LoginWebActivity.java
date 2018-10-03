package com.imba.kelompol.panicbutton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.imba.kelompol.panicbutton.API.EndpointEmeract;
import com.imba.kelompol.panicbutton.API.RetrofitClient;
import com.imba.kelompol.panicbutton.API.RoutesConf;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginWebActivity extends AppCompatActivity {

    private WebView webViewLogin;
    private SharedPreferences.Editor prefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_web);

        webViewLogin = (WebView) findViewById(R.id.webviewLogin);
        prefEditor = getSharedPreferences(RoutesConf.API_SHARED_USER,0).edit();

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
                    CookieManager cookieManager = CookieManager.getInstance();
                    String cookie = cookieManager.getCookie(url);
                    Log.d("COOKIE_MANAGEMENT",cookie);
                    String userProviderID="";
                    try {
                        userProviderID = cookie.substring(cookie.indexOf("userProviderId"));
                        if(userProviderID.indexOf(";")>=0){
                            userProviderID = userProviderID.substring(userProviderID.indexOf("=") + 1, userProviderID.indexOf(";"));
                        }else{
                            userProviderID = userProviderID.substring(userProviderID.indexOf("=") + 1);
                        }
                        prefEditor.putString("USER_COOKIE",""+cookie);
                        prefEditor.putString("USER_PROVIDER_ID",""+userProviderID);
                        prefEditor.apply();
                        Toast.makeText(LoginWebActivity.this, "You're Already Sign-in", Toast.LENGTH_SHORT).show();
                    }catch (Exception ex){
                        Log.e("COOKIE_MANAGEMENT","Error: "+ex.getMessage());
                    }
                    Log.d("COOKIE_MANAGEMENT_UPID",userProviderID);

                    startActivity(new Intent(LoginWebActivity.this, MainActivity.class));
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
