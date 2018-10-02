package com.imba.kelompol.panicbutton;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.imba.kelompol.panicbutton.API.RoutesConf;

public class LandingActivity extends AppCompatActivity {

    private Button btnLoginG, btnLoginF, btnLoginT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        btnLoginF = (Button)findViewById(R.id.btnLoginFB);
        btnLoginG = (Button)findViewById(R.id.btnLoginGoogle);
        btnLoginT = (Button)findViewById(R.id.btnLoginTwitter);

        btnLoginG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginWebView(0);
            }
        });
        btnLoginF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginWebView(1);
            }
        });
        btnLoginT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginWebView(2);
            }
        });
    }


    public void openLoginWebView(int type){
        //type[0: g, 1: f, 2: t]
        String[] types = {"auth/google/","auth/facebook/","auth/twitter/"};
        String url = RoutesConf.API_BASE_URL+types[type];
        Log.d("OPEN_URL",url);
        Intent login = new Intent(this, LoginWebActivity.class);
        login.putExtra("url",url);
        startActivity(login);
    }
}
