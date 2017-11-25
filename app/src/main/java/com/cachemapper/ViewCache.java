package com.cachemapper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ViewCache extends AppCompatActivity {

    private Button backButton;
    private TextView cacheNameTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cache);
        backButton = (Button) findViewById(R.id.backbutton);
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
    }
}
