package com.example.androidappproject;

public class LocationDBObject {
    public String title;
    public String description;
    public double latitude;
    public double longitude;

    public LocationDBObject(){}

    public LocationDBObject(String title, String description, double latitude, double longitude){
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
