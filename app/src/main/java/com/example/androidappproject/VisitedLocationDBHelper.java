package com.example.androidappproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class VisitedLocationDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "visitedlocations.db";
    private static final int DATABASE_VERSION = 1;

    public VisitedLocationDBHelper(Context context){
        super(context, DATABASE_NAME,  null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_VISITED_LOCATIONS_TABLE = "CREATE TABLE "+ VisitedLocationContract.VisitedLocationEntry.TABLE_NAME + "("
                + VisitedLocationContract.VisitedLocationEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + VisitedLocationContract.VisitedLocationEntry.COLUMN_LOCATION_TITLE + " TEXT NOT NULL, "
                + VisitedLocationContract.VisitedLocationEntry.COLUMN_LOCATION_DATE + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_VISITED_LOCATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
