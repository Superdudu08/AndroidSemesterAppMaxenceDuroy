package com.example.androidappproject;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    Toolbar toolbar;
    private GoogleMap gMap;
    LocationRequest mLocationRequest;
    FusedLocationProviderClient mFusedLocationClient;
    LocationCallback mLocationCallback;
    LatLng targetLoc;
    Location targetLocation;
    LatLng userLoc;
    Marker lastUserMarker;
    boolean alreadyVisited;
    TextView distanceTextView;
    TextView titleTextView;
    TextView descriptionTextView;

    VisitedLocationDBHelper visitedLocationDBHelper;
    SQLiteDatabase writeabledb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapactivity);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        visitedLocationDBHelper = new  VisitedLocationDBHelper((this));

        distanceTextView = findViewById(R.id.mapDistance);
        titleTextView = findViewById(R.id.mapTitle);
        descriptionTextView =  findViewById(R.id.mapDescription);

        // Get the data from previous activity
        Bundle bundle  = getIntent().getExtras();
        titleTextView.setText(bundle.getString("title","No title found"));
        descriptionTextView.setText(bundle.getString("description","No description found"));
        targetLocation = new Location("");
        targetLocation.setLongitude(bundle.getDouble("longitude"));
        targetLocation.setLatitude(bundle.getDouble("latitude"));
        targetLoc = new LatLng(targetLocation.getLatitude(),targetLocation.getLongitude());

        new CheckIfVisitedTask().execute();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult result){
                Location currentLocation = result.getLastLocation();
                userLoc = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                if(lastUserMarker != null){
                    lastUserMarker.remove();
                }
                MarkerOptions newUserPos = new MarkerOptions().position(userLoc).title("You");
                //Sometimes the map wasn't ready yet when I tried to add the marker
                if(gMap != null) {
                    lastUserMarker = gMap.addMarker(newUserPos);
                }

                //Update the distanceTextView
                Location userLocation = new Location("");
                userLocation.setLatitude(userLoc.latitude);
                userLocation.setLongitude(userLoc.longitude);
                Float distance = userLocation.distanceTo(targetLocation);
                distanceTextView.setText("" + Math.round(distance) + " meters");
                super.onLocationResult(result);
            }
        };
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},42);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback,null);
        }
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.addMarker(new MarkerOptions().position(targetLoc).title("Destination"));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLoc,14));
    }

    public void goToPictureActivity(MenuItem menuItem){
        Intent intent = new Intent(this, PictureActivity.class);
        startActivity(intent);
    }

    public void addToLocalDatabase(View view){
        writeabledb = visitedLocationDBHelper.getWritableDatabase();

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM");
        String formattedDate = format.format(date);

        ContentValues values = new ContentValues();
        values.put(VisitedLocationContract.VisitedLocationEntry.COLUMN_LOCATION_TITLE,titleTextView.getText().toString());
        values.put(VisitedLocationContract.VisitedLocationEntry.COLUMN_LOCATION_DATE, formattedDate);

        writeabledb.insert(VisitedLocationContract.VisitedLocationEntry.TABLE_NAME,null,values);
        finish();
    }

    @Override
    protected void onDestroy() {
        visitedLocationDBHelper.close();
        super.onDestroy();
    }


    private class CheckIfVisitedTask extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void ... voids) {
            Boolean visited;
            SQLiteDatabase readabledb = visitedLocationDBHelper.getReadableDatabase();
            String[] projection = {
                    VisitedLocationContract.VisitedLocationEntry.COLUMN_LOCATION_TITLE
            };
            String[] selectWhere = {titleTextView.getText().toString()};
            Cursor cursor = readabledb.query(
                    VisitedLocationContract.VisitedLocationEntry.TABLE_NAME,
                    projection,
                    VisitedLocationContract.VisitedLocationEntry.COLUMN_LOCATION_TITLE + "=?",
                    selectWhere,
                    null,
                    null,
                    null);

            if(cursor.getCount()==0){
                visited = false;
            }
            else{
                visited = true;
            }
            return visited;
        }

        protected void onPostExecute (Boolean result) {
            alreadyVisited = result;
            if(alreadyVisited) {
                Toast.makeText(MapActivity.this, "You already visited this street art, consider picking another.", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
