package com.imba.kelompol.panicbutton;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.imba.kelompol.panicbutton.API.EndpointEmeract;
import com.imba.kelompol.panicbutton.API.RetrofitClient;
import com.imba.kelompol.panicbutton.Models.API.Article.Article;
import com.imba.kelompol.panicbutton.Models.API.Article.ArticleResponse;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private SlidingUpPanelLayout mLayout;
    boolean doubleBackToExitPressedOnce = false;

    private final String NEWS_DATA_RESPONSE = "NEWS_DATA_RESPONSE";
    private final String WEATHER_DATA_RESPONSE = "WEATHER_DATA_RESPONSE";

    private List<String> sumberBerita;
    private List<String> isiBerita;
    private BeritaAdapter mAdapter;

    private TextView lblWTemp1, lblWTemp0, lblWLoc0, lblWLoc1;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //Design
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.mainLayout);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
            }
        });

        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        lblWTemp1 = findViewById(R.id.weatherTemp1);
        lblWLoc1 = findViewById(R.id.weatherLoc1);
        lblWTemp0 = findViewById(R.id.wheatherTemp);
        lblWLoc0 = findViewById(R.id.wheatherLoc);

        // Debug
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();
        Log.d(NEWS_DATA_RESPONSE,"=== BEGIN ===");
        EndpointEmeract service = RetrofitClient.getRetrofitInstance().create(EndpointEmeract.class);
        Call<ArticleResponse> call = service.getListNews();
        call.enqueue(new Callback<ArticleResponse>() {
            @Override
            public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                progressDialog.dismiss();
                Log.d(NEWS_DATA_RESPONSE,response.body().toString());
                generateRecyclerNews(response.body().getArticles());
            }

            @Override
            public void onFailure(Call<ArticleResponse> call, Throwable t) {
                Log.d(NEWS_DATA_RESPONSE, "Failed! Cause: "+t.getMessage());
            }
        });

        Call<Map<String,Object>> weatherCall = service.getCurrentWeather();
        weatherCall.enqueue(new Callback<Map<String,Object>>() {
            @Override
            public void onResponse(Call<Map<String,Object>> call, Response<Map<String,Object>> response) {
                Map<String,Object> item = (Map)response.body().get("data");
                Log.d(WEATHER_DATA_RESPONSE, response.body().toString());
                String temp,loc;
                temp=((Map)item.get("main")).get("temp").toString();
                loc=item.get("name").toString();
                lblWTemp1.setText(temp+"℃");
                lblWLoc1.setText(loc);

                lblWTemp0.setText(temp+"℃");
                lblWLoc0.setText(loc);
            }

            @Override
            public void onFailure(Call<Map<String,Object>> call, Throwable t) {
                Log.d(WEATHER_DATA_RESPONSE, "Failed! Cause: "+t.getMessage());
            }
        });
        isiBerita = Arrays.asList(getResources().getStringArray(R.array.berita));
        sumberBerita = Arrays.asList(getResources().getStringArray(R.array.sumber));
        recyclerView = (RecyclerView)findViewById(R.id.RVnews);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BeritaAdapter(isiBerita,sumberBerita, this);
        recyclerView.setAdapter(mAdapter);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }else {
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        }

        this.doubleBackToExitPressedOnce = true;


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 500);


    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            startActivity(new Intent(this,LandingActivity.class));
        }
//        else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //Swipe UP
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_toggle: {
                if (mLayout != null) {
                    if (mLayout.getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                        item.setTitle("Show Panel");
                    } else {
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        item.setTitle("Hide Panel");


                    }
                }
                return true;
            }
            case R.id.action_anchor: {
                if (mLayout != null) {
                    if (mLayout.getAnchorPoint() == 1.0f) {
                        mLayout.setAnchorPoint(0.7f);
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                        item.setTitle("Enable Anchor Point");
                    } else {
                        mLayout.setAnchorPoint(1.0f);
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        item.setTitle("Disable Anchor Point");
                    }
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    /*
    * Functional
    * */
    private void generateRecyclerNews(List<Article> list){
        mAdapter = new BeritaAdapter(list, this);
        recyclerView.setAdapter(mAdapter);
    }
}
