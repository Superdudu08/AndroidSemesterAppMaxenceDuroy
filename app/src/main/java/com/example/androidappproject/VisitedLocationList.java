package com.example.androidappproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class VisitedLocationList extends AppCompatActivity {

    Toolbar toolbar;
    TextView displayList;
    VisitedLocationDBHelper visitedLocationDBHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visitedlocationlistactivity);

        displayList = findViewById(R.id.textViewLocation);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Since this activity is only here to show the list, I don't need to call the database in a background thread
        visitedLocationDBHelper = new VisitedLocationDBHelper(this);
        db = visitedLocationDBHelper.getReadableDatabase();

        String[] projection = {
                VisitedLocationContract.VisitedLocationEntry.ID,
                VisitedLocationContract.VisitedLocationEntry.COLUMN_LOCATION_TITLE,
                VisitedLocationContract.VisitedLocationEntry.COLUMN_LOCATION_DATE};

        Cursor cursor = db.query(
                VisitedLocationContract.VisitedLocationEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        try {

            int titleColumnIndex = cursor.getColumnIndex(VisitedLocationContract.VisitedLocationEntry.COLUMN_LOCATION_TITLE);
            int dateColumnIndex = cursor.getColumnIndex(VisitedLocationContract.VisitedLocationEntry.COLUMN_LOCATION_DATE);

            while(cursor.moveToNext()){


                String currentTitle = cursor.getString(titleColumnIndex);
                String currentDate = cursor.getString(dateColumnIndex);


                displayList.append(("\n"+currentTitle + " on  " + currentDate));
            }
        }
        finally {
            cursor.close();
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

    public void goToPictureActivity(MenuItem menuItem){
        Intent intent = new Intent(this, PictureActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        visitedLocationDBHelper.close();
        super.onDestroy();
    }

    public void clearDatabase(View view){
        db.delete(VisitedLocationContract.VisitedLocationEntry.TABLE_NAME,null,null);
        finish();
    }
}
