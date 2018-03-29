package com.onistech.user.expandablelistdemo;

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback
        , LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    GoogleMap mGoogleMap;
    private ExpandableListAdapter adapter;
    ExpandableListView expLv;
    List<String> listDataHeader;
    HashMap<String, List<String>> listChildData;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToogle;
    Toolbar toolbar;

    GoogleApiClient googleApiClient;
    LocationManager locationManager;
    public static final int LOCATION_MAPS_PERMISSION = 9999;

  

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (googleServiceAvaliable()) {
            Toast.makeText(this, "perfect", Toast.LENGTH_SHORT).show();
        }
        initViews();


    }

    private boolean googleServiceAvaliable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Can\'t connect to play services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void initViews() {
        initMaps();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        expLv = (ExpandableListView) findViewById(R.id.lvExp);
        prepareListData();
        adapter = new ExpandableListAdapter(this, listDataHeader, listChildData);
        expLv.setAdapter(adapter);
        setDrawerToggle();
        drawerLayout.setDrawerListener(drawerToogle);
        expLv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent,
                                        View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(MainActivity.this, listChildData
                        .get(listDataHeader.get(groupPosition))
                        .get(childPosition) + " is Clicked!", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        expLv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(MainActivity.this, listDataHeader.get(groupPosition) + " is expanded..!!", Toast.LENGTH_SHORT).show();
            }
        });

        expLv.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(MainActivity.this, listDataHeader.get(groupPosition) + " is collapse..!!", Toast.LENGTH_SHORT).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initMaps() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.content_frame);
        mapFragment.getMapAsync(this);


    }

    private void setDrawerToggle() {
        drawerToogle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerToogle.syncState();
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataHeader.add("Top 250");
        listDataHeader.add("Now Showing");
        listDataHeader.add("Coming Soon...");
        List<String> top250 = new ArrayList<>();
        top250.add("The Shawshank Redemption");
        top250.add("The Godfather");
        top250.add("The Godfather: Part II");
        top250.add("Plup Fiction");
        top250.add("The Good, the bad and the Ugly");
        top250.add("The dark Knight");
        top250.add("12 Angry Men");

        List<String> nowShowing = new ArrayList<>();
        nowShowing.add("The Conjuring");
        nowShowing.add("Despicable Me 2");
        nowShowing.add("Turbo");
        nowShowing.add("Grown Ups 2");
        nowShowing.add("Red 2");
        nowShowing.add("The Wolverine");

        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("2 Guns");
        comingSoon.add("The Smurfs 2");
        comingSoon.add("The Spectacular Now");
        comingSoon.add("The Canyons");
        comingSoon.add("Europa Report");

        listChildData = new HashMap<>();
        listChildData.put(listDataHeader.get(0), top250);
        listChildData.put(listDataHeader.get(1), nowShowing);
        listChildData.put(listDataHeader.get(2), comingSoon);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        this.mGoogleMap = googleMap;
        //gotoLocation(26.430796, 80.304423,15);

       if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
           if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
               requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                       Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_MAPS_PERMISSION);
                return;
            }
        }
        mGoogleMap.setMyLocationEnabled(true);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_MAPS_PERMISSION:
                if (grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "permission grant ");
                }else{
                    Log.d(TAG, "permission denied ");
                }
        }
    }

    private void gotoLocation(double lat, double lng) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
        mGoogleMap.moveCamera(update);
    }

    private void gotoLocation(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mGoogleMap.moveCamera(update);

    }

    Marker marker = null;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        final SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: " + query);
                Geocoder gc = new Geocoder(getApplicationContext());
                try {
                    List<Address> list = gc.getFromLocationName(query, 1);
                    Address address = list.get(0);
                    String locality = address.getLocality();
                    Log.d(TAG, "onQueryTextSubmit: " + locality);
                    double lat = address.getLatitude();
                    double lang = address.getLongitude();
                    gotoLocation(lat, lang, 15);
                    setMarker(locality, lat, lang);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void setMarker(String locality, double lat, double lang) {
        if (marker != null){
            marker.remove();
        }
        MarkerOptions options = new MarkerOptions()
                .title(locality)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                .position(new LatLng(lat,lang));
        marker = mGoogleMap.addMarker(options);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.mapTypeNone:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.mapTypeNormal:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeTerrain:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeSatellite:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeHybrid:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    LocationRequest locationRequest;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: ");
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_MAPS_PERMISSION);
                return;
            }
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: ");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: ");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null){
            Toast.makeText(this, "can\'t get location", Toast.LENGTH_SHORT).show();
        }else{
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            Log.d(TAG, "latitude "+lat);
            Log.d(TAG, "longitude "+lng);
            LatLng ll = new LatLng(lat,lng);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll,15);
            mGoogleMap.animateCamera(update);
            Log.d(TAG, "onLocationChanged: ");
/*            Geocoder gc = new Geocoder(getApplicationContext());
            try{
                List<Address> list = gc.getFromLocation(lat,lng,1);
                String str = list.get(0).getLocality()+" ";
                str += list.get(0).getCountryName();
                mGoogleMap.addMarker(new MarkerOptions().position(ll).title(str));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll,15));

            }catch (IOException e){
                e.printStackTrace();
            }*/

        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
    }
}
