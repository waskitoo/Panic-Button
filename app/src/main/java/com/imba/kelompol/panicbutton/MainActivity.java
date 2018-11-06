package com.imba.kelompol.panicbutton;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.imba.kelompol.panicbutton.API.EndpointEmeract;
import com.imba.kelompol.panicbutton.API.RetrofitClient;
import com.imba.kelompol.panicbutton.API.RoutesConf;
import com.imba.kelompol.panicbutton.Models.API.Article.Article;
import com.imba.kelompol.panicbutton.Models.API.Article.ArticleResponse;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private SlidingUpPanelLayout mLayout;
    boolean doubleBackToExitPressedOnce = false;

    private final String NEWS_DATA_RESPONSE = "NEWS_DATA_RESPONSE";
    private final String WEATHER_DATA_RESPONSE = "WEATHER_DATA_RESPONSE";
    private final String APP_PERMISSION = "APP_PERMISSONS";

    private List<String> sumberBerita;
    private List<String> isiBerita;
    private BeritaAdapter mAdapter;

    // Activity Comp.
    private TextView lblWTemp1, lblWTemp0, lblWLoc0, lblWLoc1;
    // Nav Comp.
    private NavigationView navigationView;
    private TextView lblNavUserName, lblNavUserEmail;
    private RecyclerView recyclerView;
    private View btnPanic;
    private PopupWindow menuPanicOptions;
    private AlertDialog.Builder alertPanic;
    private View popupPanic;

    private ProgressDialog progressDialog;
    private SharedPreferences prefShared;
    private EndpointEmeract service;

    // Information
    private Double lat, lon;
    private Location _location;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if(location!=null){
                lat = location.getLatitude();
                lon = location.getLongitude();
                _location = location;
                Log.d("LOCATION__INFO", "Location founded. Lat "+lat+" Lon "+lon);
                if(_location!=null){
                    loadWeather(TextUtils.join(",",new String[]{""+_location.getLatitude(),""+_location.getLongitude()}));
                    Log.d("LOCATION__INFO", "Weather is Updated");
                }
            }else{
                Log.d("LOCATION", "Location not found.");
            }
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
        @Override
        public void onProviderEnabled(String provider) {

        }
        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    private LocationManager locationManager;

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
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Sign In/Out Menu
        navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
        navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
        View headerView = navigationView.getHeaderView(0);
        lblNavUserName = headerView.findViewById(R.id.navLblUserName);
        lblNavUserEmail = headerView.findViewById(R.id.navLblUserEmail);

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

        prefShared = getSharedPreferences(RoutesConf.API_SHARED_USER, 0);
        lblWTemp1 = findViewById(R.id.weatherTemp1);
        lblWLoc1 = findViewById(R.id.weatherLoc1);
        lblWTemp0 = findViewById(R.id.wheatherTemp);
        lblWLoc0 = findViewById(R.id.wheatherLoc);

        // Debug
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();
        Log.d(NEWS_DATA_RESPONSE, "=== BEGIN ===");
        service = RetrofitClient.getRetrofitInstance().create(EndpointEmeract.class);

        isiBerita = Arrays.asList(getResources().getStringArray(R.array.berita));
        sumberBerita = Arrays.asList(getResources().getStringArray(R.array.sumber));
        recyclerView = (RecyclerView) findViewById(R.id.RVnews);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BeritaAdapter(isiBerita, sumberBerita, this);
        recyclerView.setAdapter(mAdapter);

        // User Mount
        //callService(

        loadInformation();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "LOCATION PERMISSON DENIED.", Toast.LENGTH_LONG).show();
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,10,mLocationListener);
            //
        }

        popupPanic=this.getLayoutInflater().inflate(R.layout.content_choice_panic,null);
//        menuPanicOptions=new PopupWindow(popupPanic);
//        menuPanicOptions.setAnimationStyle(android.R.style.Animation_Dialog);

        alertPanic = new AlertDialog.Builder(this);
        alertPanic.setView(popupPanic);
        alertPanic.create();
        //menuPanicOptions.showAtLocation(popupPanic, Gravity.CENTER,0,0);
        btnPanic=findViewById(R.id.btnPanic);
        btnPanic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clicked
                //sendPanicAct(type);
                //menuPanicOptions.showAtLocation(popupPanic, Gravity.CENTER,0,0);
                alertPanic.show();
            }
        });
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
        } else if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        }

        this.doubleBackToExitPressedOnce = true;


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 500);


    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            startActivity(new Intent(this, LandingActivity.class));
        }
        else if (id == R.id.nav_logout) {
            startActivity(new Intent(this, LogoutWebActivity.class));
        }
        else if (id == R.id.nav_maps) {
            startActivity(new Intent(this, MapsActivity.class));
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
        switch (item.getItemId()) {
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
    private void generateRecyclerNews(List<Article> list) {
        mAdapter = new BeritaAdapter(list, this);
        recyclerView.setAdapter(mAdapter);
    }


    private void callService() {
        EndpointEmeract service = RetrofitClient.getRetrofitInstance().create(EndpointEmeract.class);
        String userProviderId = prefShared.getString("USER_PROVIDER_ID", null);
        Log.d("USER_PROVIDER_ID", "" + userProviderId);
        if (userProviderId != null) {
            Call<Map<String, Object>> call = service.getUserLogged(userProviderId);
            call.enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    Map<String, Object> res = response.body();
                    if (res.get("status") != null && (Boolean) res.get("status") == true && res.get("user") != null) {
                        Map<String, Object> user = (Map) res.get("user");
                        String html = "";
                        html += "Name: " + user.get("name");
                        html += "\nEmail: " + user.get("email");
                        html += "\nID: " + user.get("provider_id");
                        Log.d("USER_LOGIN", "Info: " + html);
                        //showDialog(html, "User Login");
                        setPropUserNav("" + user.get("name"), "" + user.get("email"));

                        navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
                        navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
                    }
                    Log.d("USER_LOGIN", "Info: " + res);
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Log.e("USER_LOGIN", "Error: " + t.getMessage());
                }
            });
        }
    }


    private void showDialog(String msg, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setTitle(title);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setPropUserNav(String name, String email) {
        lblNavUserEmail.setText(email);
        lblNavUserName.setText(name);
    }

    /*
    * Load Information
    * */
    private void loadInformation(){
        loadNews();
        loadWeather(null);
        callService();
        //loadLocation();
        //loadLocationInfo();

        progressDialog.dismiss();
    }


    private void loadNews() {
        Call<ArticleResponse> call = service.getListNews();
        call.enqueue(new Callback<ArticleResponse>() {
            @Override
            public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                Log.d(NEWS_DATA_RESPONSE, response.body().toString());
                generateRecyclerNews(response.body().getArticles());
            }

            @Override
            public void onFailure(Call<ArticleResponse> call, Throwable t) {
                Log.d(NEWS_DATA_RESPONSE, "Failed! Cause: " + t.getMessage());
            }
        });
    }

    private void loadWeather(String latlon){
        Call<Map<String, Object>> weatherCall = service.getCurrentWeather();
        if(latlon!=null && !latlon.equals("")){weatherCall = service.getCurrentWeather(latlon);}
        weatherCall.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                Map<String, Object> item = (Map) response.body().get("data");
                Log.d(WEATHER_DATA_RESPONSE, response.body().toString());
                String temp, loc;
                temp = ((Map) item.get("main")).get("temp").toString();
                loc = item.get("name").toString();
                lblWTemp1.setText(temp + "℃");
                lblWLoc1.setText(loc);

                lblWTemp0.setText(temp + "℃");
                lblWLoc0.setText(loc);
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.d(WEATHER_DATA_RESPONSE, "Failed! Cause: " + t.getMessage());
            }
        });
    }

    private void loadLocationInfo(){
        SmartLocation.with(this)
                .location()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        Log.d("LOCATION__INFO_2","LatLon "+location.getLatitude()+", "+location.getLongitude());
                        Log.d("LOCATION__INFO_2","LatLon "+location.getProvider());
                    }
                });
    }

    private void loadLocation(){
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(_location!=null){
                    loadWeather(TextUtils.join(",",new String[]{""+_location.getLatitude(),""+_location.getLongitude()}));
                    Log.d("LOCATION__INFO","Location has Updated. Lat:"+lat+" Lon:"+lon);
                }
            }
        },0,5000);
    }

    private void sendPanicAct(String type){
        String userProviderId = prefShared.getString("USER_PROVIDER_ID", null);
        if(userProviderId!=null){
            EndpointEmeract service = RetrofitClient.getRetrofitInstance().create(EndpointEmeract.class);
            String latlon = "";
            if(_location!=null){latlon=TextUtils.join(",",new String[]{""+_location.getLatitude(),""+_location.getLongitude()});}
            Call<Map<String,Object>> call = service.sendPanic(""+userProviderId,latlon,type);
            call.enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    Log.d("SEND_PANIC",""+response.body());
                    Toast.makeText(MainActivity.this, "Request Sent", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {

                }
            });
        }
    }
}
