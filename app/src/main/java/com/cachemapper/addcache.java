package com.cachemapper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class addcache extends AppCompatActivity {
    public static final String PREFS_NAME = "CacheMapperPrefsFile";
    private static final int TAKE_PHOTO_PERMISSION = 1;

    private DatabaseReference mDatabase;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public Button backButton;

    Double currentLat;
    Double currentLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcache);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION }, TAKE_PHOTO_PERMISSION);
        }

        // Add code here to register the listener with the Location Manager to receive location updates
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Add code here to do stuff when the location changes
                currentLat = location.getLatitude();
                currentLon = location.getLongitude();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        backButton = (Button) findViewById(R.id.backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent retIntent = new Intent(addcache.this,MainActivity.class);
                setResult(1,retIntent);
                finish();
            }
        });
    }

    public void uploadFirebaseData(View view) {
        //Check validity of data first!
        mDatabase = FirebaseDatabase.getInstance().getReference();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String name="a";
        String description="b";
        double latitude = currentLat.doubleValue();
        double longitude = currentLon.doubleValue();
        String username = settings.getString("currentUser",null);
        cacheLocation cache = new cacheLocation(username,name,description,latitude,longitude);
        mDatabase.child("caches").child(name).setValue(cache);
    }


}
