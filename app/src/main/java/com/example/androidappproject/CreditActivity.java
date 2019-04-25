package com.example.androidappproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class CreditActivity extends AppCompatActivity {

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creditactivity);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbarmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void openSettings(MenuItem menuItem){
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }

    public void goHome(MenuItem menuItem){
        Intent homeIntent = new Intent(this,MainActivity.class);
        startActivity(homeIntent);
    }

    public void goToPictureActivity(MenuItem menuItem){
        Intent intent = new Intent(this, PictureActivity.class);
        startActivity(intent);
    }
}
