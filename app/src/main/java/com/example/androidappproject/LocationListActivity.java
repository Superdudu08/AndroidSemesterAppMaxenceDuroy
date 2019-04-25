package com.example.androidappproject;

import android.content.Intent;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class LocationListActivity extends AppCompatActivity implements LocationAdapter.OnListItemClickListener{

    RecyclerView recyclerView;
    FirebaseDatabase database;
    LocationAdapter locationAdapter;
    Toolbar toolbar;
    Location userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locationlistactivity);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        double latitude = bundle.getDouble("latitude");
        double longitude =  bundle.getDouble("longitude");
        userLocation = new Location("");
        userLocation.setLatitude(latitude);
        userLocation.setLongitude(longitude);

        ArrayList<LocationDBObject> list = new ArrayList<LocationDBObject>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        locationAdapter = new LocationAdapter(list,this);
        database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("locations");
        String distancemax = PreferenceManager.getDefaultSharedPreferences(this).getString("distancelimit","10");
        final int  distancelimit = Integer.parseInt(distancemax);
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                LocationDBObject newLoc = dataSnapshot.getValue(LocationDBObject.class);
                Location location = new Location("");
                location.setLongitude(newLoc.longitude);
                location.setLatitude(newLoc.latitude);
                if(userLocation.distanceTo(location)< distancelimit*1000) {     //*1000 because distanceTo uses meters while distancelimit is in kilometers
                    locationAdapter.add(dataSnapshot.getValue(LocationDBObject.class));
                    locationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addChildEventListener(childEventListener);
        recyclerView.setAdapter(locationAdapter);

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
    public void onListItemClick(int clickedItemIndex) {
        double longitude = locationAdapter.locationList.get(clickedItemIndex).longitude;
        double latitude = locationAdapter.locationList.get(clickedItemIndex).latitude;
        Intent intent = new Intent(this,MapActivity.class);
        intent.putExtra("title",locationAdapter.locationList.get(clickedItemIndex).title);
        intent.putExtra("longitude",longitude);
        intent.putExtra("latitude",latitude);
        intent.putExtra("description",locationAdapter.locationList.get(clickedItemIndex).description);
        startActivity(intent);
    }

    public void goToPictureActivity(MenuItem menuItem){
        Intent intent = new Intent(this, PictureActivity.class);
        startActivity(intent);
    }
}
