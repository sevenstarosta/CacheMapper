package com.cachemapper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewCache extends AppCompatActivity {

    private Button backButton;
    private TextView usernameTextView;
    private TextView descriptionTextView;
    private ImageView imageView;
    private TextView cacheNameTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cache);
        backButton = (Button) findViewById(R.id.backbutton);
        descriptionTextView = (TextView) findViewById(R.id.cacheDescription);
        usernameTextView = (TextView) findViewById(R.id.cacheUsername);
        imageView = (ImageView) findViewById(R.id.imageView);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent retIntent = new Intent(ViewCache.this, MainActivity.class);
                setResult(1, retIntent);
                finish();
            }
        });

        cacheNameTextView = (TextView) findViewById(R.id.cacheName);
        cacheNameTextView.setText(getIntent().getStringExtra("name"));
        FirebaseDatabase.getInstance().getReference().child("caches/"+getIntent().getStringExtra("name"))
                .addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        cacheLocation cache = dataSnapshot.getValue(cacheLocation.class);
                        usernameTextView.setText(cache.username);
                        descriptionTextView.setText(cache.description);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
            }
}
