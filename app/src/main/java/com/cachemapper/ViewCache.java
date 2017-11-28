package com.cachemapper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
public class ViewCache extends AppCompatActivity {

    //private Button backButton;
    private TextView usernameTextView;
    private TextView descriptionTextView;
    private ImageView imageView;
    private TextView cacheNameTextView;
    private Toolbar toolbar;

    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cache);
        //backButton = (Button) findViewById(R.id.backbutton);
        descriptionTextView = (TextView) findViewById(R.id.cacheDescription);
        usernameTextView = (TextView) findViewById(R.id.cacheUsername);
        imageView = (ImageView) findViewById(R.id.imageView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        /*backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent retIntent = new Intent(ViewCache.this, MainActivity.class);
                setResult(1, retIntent);
                finish();
            }
        });*/

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Cache Mapper: the app!");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent retIntent = new Intent(ViewCache.this, MainActivity.class);
                setResult(1, retIntent);
                finish();
            }
        });

        cacheNameTextView = (TextView) findViewById(R.id.cacheName);
        cacheNameTextView.setText("Cache Name: " + getIntent().getStringExtra("name"));
        FirebaseDatabase.getInstance().getReference().child("caches/"+getIntent().getStringExtra("name"))
                .addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        cacheLocation cache = dataSnapshot.getValue(cacheLocation.class);
                        usernameTextView.setText("Maker of this cache:" + cache.username);
                        descriptionTextView.setText("Cache Description: " + cache.description);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
        storageReference = FirebaseStorage.getInstance().getReference().child("images/"+getIntent().getStringExtra("name")+".jpg");
        storageReference.getBytes(1024 * 1024 * 5).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                if (bytes.length > 1)
                {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView.setImageBitmap(bitmap);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }
}
