// Some location code take from https://stackoverflow.com/questions/34582370/how-can-i-show-current-location-on-a-google-map-on-android-marshmallow
package com.cachemapper;

//import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.ConnectionResult;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import static java.lang.System.out;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private boolean hasCompletedMap;
    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;
    private LocationRequest locationRequest;
    private DatabaseReference mDatabase;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private Marker currentLocationMarker;
    private Button logoutButton;
    private ChildEventListener myListener;
    private HashMap<String,Marker> markers;
    private static final int TAKE_PHOTO_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hasCompletedMap = false;
        setContentView(R.layout.activity_main);
        logoutButton = (Button) findViewById(R.id.logoutbutton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent retIntent = new Intent(MainActivity.this, LoginActivity.class);
                setResult(1, retIntent);
                finish();
            }
        });

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, TAKE_PHOTO_PERMISSION);
        }

        //only need to recreate the map if activity is starting fresh
        if (savedInstanceState == null)
        {
            mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            mapFragment.setRetainInstance(true);
            markers = new HashMap<String,Marker>();
            FirebaseDatabase.getInstance().getReference().child("caches").keepSynced(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (myListener != null) {
            FirebaseDatabase.getInstance().getReference().child("caches").removeEventListener(myListener);
        }
        //stopping location updates
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //begin requesting location again!
        if (googleApiClient != null) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
            }
        }
        if (myListener != null)
        {
            FirebaseDatabase.getInstance().getReference().child("caches").addChildEventListener(myListener);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
    }


    public void addCache(View view) {
        Intent addIntent = new Intent(this, addcache.class);
        startActivityForResult(addIntent, 1);
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        buildGoogleApiClient();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //permisions should have already be requested in oncreate, this is to prevent possible error otherwise.
            return;
        }
        googleMap.setMyLocationEnabled(true);

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.equals(currentLocationMarker))
                {
                    return false;
                }
                Intent viewIntent = new Intent(MainActivity.this,ViewCache.class);
                viewIntent.putExtra("name",marker.getTitle());
                startActivityForResult(viewIntent,0);
                return true;
            }
        });

        FirebaseDatabase.getInstance().getReference().child("caches")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        {
                            cacheLocation cache = snapshot.getValue(cacheLocation.class);
                            LatLng loc = new LatLng(cache.latitude,cache.longitude);
                            Marker marker = googleMap.addMarker(new MarkerOptions().position(loc).title(cache.name));
                            markers.put(cache.name,marker);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });

        myListener = FirebaseDatabase.getInstance().getReference().child("caches")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {
                        cacheLocation cache = dataSnapshot.getValue(cacheLocation.class);
                        LatLng loc = new LatLng(cache.latitude,cache.longitude);
                        Marker marker = googleMap.addMarker(new MarkerOptions().position(loc).title(cache.name));
                        markers.put(cache.name,marker);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s)
                    {
                        cacheLocation cache = dataSnapshot.getValue(cacheLocation.class);
                        LatLng loc = new LatLng(cache.latitude,cache.longitude);
                        markers.get(cache.name).setPosition(loc);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot)
                    {
                        cacheLocation cache = dataSnapshot.getValue(cacheLocation.class);
                        markers.get(cache.name).remove();
                        markers.remove(cache.name);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        hasCompletedMap = true;
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {

    }

    @Override
    public void onLocationChanged(Location location)
    {
        lastLocation = location;
        if (currentLocationMarker != null)
        {
            currentLocationMarker.remove();
        }
        else
        {
            recenterMap();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        currentLocationMarker = googleMap.addMarker(markerOptions);
    }

    private void recenterMap()
    {
        LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
    }

}
