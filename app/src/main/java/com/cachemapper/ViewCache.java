package com.cachemapper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
public class ViewCache extends AppCompatActivity {

    //private Button backButton;
    public static final String PREFS_NAME = "CacheMapperPrefsFile";
    private TextView usernameTextView;
    private TextView descriptionTextView;
    private ImageView imageView;
    private TextView cacheNameTextView;
    private TextView visitTextView;
    private TextView countTextView;
    private CheckBox checkBox;
    private Toolbar toolbar;
    private long currentCount;

    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cache);
        currentCount = 0;
        descriptionTextView = (TextView) findViewById(R.id.cacheDescription);
        usernameTextView = (TextView) findViewById(R.id.cacheUsername);
        countTextView = (TextView) findViewById(R.id.countTextView);
        imageView = (ImageView) findViewById(R.id.imageView);
        visitTextView = (TextView) findViewById(R.id.visitTextView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        checkBox = (CheckBox) findViewById(R.id.checkBox);

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
                .addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        cacheLocation cache = dataSnapshot.getValue(cacheLocation.class);
                        usernameTextView.setText("Maker of this cache: " + cache.username);
                        descriptionTextView.setText("Cache Description: " + cache.description);
                        countTextView.setText("Number of users who have visited this cache: " + cache.count);
                        currentCount = cache.count;
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        final String username = settings.getString("currentUser",null);
        FirebaseDatabase.getInstance().getReference().child("users/"+username+"/"+getIntent().getStringExtra("name")).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() == null) {
                    mutableData.setValue(Boolean.toString(false));
                    checkBox.setChecked(false);
                }
                else
                {
                    boolean value = Boolean.parseBoolean( (String) mutableData.getValue());
                    checkBox.setChecked(value);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {

            }
        });


        //now set an onclicked listener for the checkbox.
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkBox.isChecked())
                {
                    currentCount++;
                    FirebaseDatabase.getInstance().getReference().child("caches/"+getIntent().getStringExtra("name")+"/count").setValue(currentCount);
                }
                else
                {
                    currentCount--;
                    FirebaseDatabase.getInstance().getReference().child("caches/"+getIntent().getStringExtra("name")+"/count").setValue(currentCount);
                }

                FirebaseDatabase.getInstance().getReference().child("users/"+username+"/"+getIntent().getStringExtra("name")).setValue(Boolean.toString(checkBox.isChecked()));

            }
        });
        //now getting an image
        storageReference = FirebaseStorage.getInstance().getReference().child("images/"+getIntent().getStringExtra("name")+".jpg");
        storageReference.getBytes(1024 * 1024 * 5).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                if (bytes.length > 1)
                {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView.setImageBitmap(bitmap);
                }
                else
                {
                    //no image chosen. May want to display text...
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
