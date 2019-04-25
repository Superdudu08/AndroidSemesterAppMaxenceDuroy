package com.example.androidappproject;

import android.provider.BaseColumns;

public final class VisitedLocationContract {

    private VisitedLocationContract(){}

    public class VisitedLocationEntry implements BaseColumns {
        public static final String TABLE_NAME = "visitedLocation";

        public static final String ID = BaseColumns._ID;
        public static final String COLUMN_LOCATION_TITLE ="title";
        public static final String COLUMN_LOCATION_DATE = "date";

    }
}
