package com.imba.kelompol.panicbutton;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.imba.kelompol.panicbutton.API.EndpointEmeract;
import com.imba.kelompol.panicbutton.API.RetrofitClient;
import com.imba.kelompol.panicbutton.Models.API.History;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap map;
    private LocationManager locationManager;
    private Location _location;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if(location!=null){
                _location = location;
                updateMapsLocationChanged(location);
                Log.d("LOCATION__INFO", "Location founded. Lat "+location.getLatitude()+" Lon "+location.getLongitude());
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

    // Component
    private RecyclerView listHistory;
    private HistoryAdapter mAdapter;
    private List<History> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setTitle("History Panggilan");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "LOCATION PERMISSON DENIED.", Toast.LENGTH_LONG).show();
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,10,mLocationListener);
            //
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapMaps);
        mapFragment.getMapAsync(this);

        listHistory = (RecyclerView) findViewById(R.id.listMapsHistory);
        listHistory.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<>();
        mAdapter = new HistoryAdapter(dataList);
        listHistory.setAdapter(mAdapter);
        loadData();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
    }

    @Override
    public void onLocationChanged(Location location) {
        updateMapsLocationChanged(location);
        locationManager.removeUpdates(this);
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

    private void updateMapsLocationChanged(Location location){
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        map.animateCamera(cameraUpdate);
    }

    private void loadData(){
        EndpointEmeract service = RetrofitClient.getRetrofitInstance().create(EndpointEmeract.class);
        Call<List<Map<String, Object>>> call = service.getListPanic();
        call.enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                List<Map<String, Object>> res = response.body();
                List<History> data = new ArrayList<>();
                for(Map<String,Object> item: res){
                    History h = new History();
                    h.setUuid(item.get("uuid").toString());
                    h.setTime(item.get("created_at").toString());
                    h.setLatlon(item.get("latlon").toString());
                    h.setType(item.get("type").toString());
                    h.setStatus(item.get("resolved_id")==null?"PENDING":item.get("resolved_id").toString());
                    data.add(h);
                }
                Log.d("PANIC_LIST",""+res);
                dataList=data;
                mAdapter = new HistoryAdapter(dataList);
                listHistory.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Log.d("PANIC_LIST",""+t.getMessage());
            }
        });
    }
}
