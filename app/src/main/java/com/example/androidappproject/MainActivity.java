package com.example.androidappproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText locationTitle;
    EditText locationDescription;

    LocationRequest mLocationRequest;
    FusedLocationProviderClient mFusedLocationClient;
    LocationCallback mLocationCallback;
    Location userLocation;

    FirebaseDatabase database;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.opendrawer,R.string.closedrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        database = FirebaseDatabase.getInstance();

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult result){
                Location currentLocation = result.getLastLocation();
                Log.d("location","Location requested, onLocationResult");
                userLocation = currentLocation;
                super.onLocationResult(result);
            }
        };
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},42);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback,null);
        }
        
        
        getFragmentManager().beginTransaction().replace(R.id.maincontainer,new HomeFragment()).commit();
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

    public void goHome (MenuItem menuItem){
        getFragmentManager().beginTransaction().replace(R.id.maincontainer,new HomeFragment()).commit();
    }

    public void goToPictureActivity(MenuItem menuItem){
        Intent intent = new Intent(this, PictureActivity.class);
        startActivity(intent);
    }

    public void goToAddLocationActivity(MenuItem menuItem){
        getFragmentManager().beginTransaction().replace(R.id.maincontainer,new AddLocationFragment()).commit();
    }

    public void addLocationToDatabase (View v){
        locationTitle = findViewById(R.id.locationTitle);
        locationDescription = findViewById(R.id.locationDescription);
        if(userLocation != null) {
            writeNewLocation(locationTitle.getText().toString(),locationDescription.getText().toString(),userLocation.getLatitude(),userLocation.getLongitude());
            Toast.makeText(this, "Location added succesfully", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Your location isn't found yet, try again in a few seconds.", Toast.LENGTH_SHORT).show();
        }
    }

    public void writeNewLocation(String title, String description, double latitude, double longitude){
        LocationDBObject loc = new LocationDBObject(title,description,latitude,longitude);
        database.getReference("locations").push().setValue(loc);

    }

    public void goToLocationListActivity(MenuItem menuItem){
        Intent intent = new Intent(this,LocationListActivity.class);
        if(userLocation != null) {
            intent.putExtra("longitude", userLocation.getLongitude());
            intent.putExtra("latitude", userLocation.getLatitude());
            startActivity(intent);
        }
        else{
            Toast.makeText(this, "We couldn't get your location yet, try again in a few seconds", Toast.LENGTH_SHORT).show();
        }
    }

    public void activateGPS (View view){
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    public void goToVisitedLocationActivity(MenuItem menuItem){
        Intent intent = new Intent(this,VisitedLocationList.class);
        startActivity(intent);
    }
}
