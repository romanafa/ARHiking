package com.example.arhiking;


import android.graphics.Bitmap;
import android.net.Uri;

public class Tour {

    public String name;
    public String hikeDate;
    public String hikeLocation;
    public String hikeLength;
    public String hikeAscent;
    public String hikeTime;
    public String hikeElevation;
    public String hikeDifficulty;
    public String hikeCategory;

    public int poster;
    public int image1;
    public int image2;
    public int image3;

    public Bitmap btmpPoster;
    public Bitmap btmpImage1;
    public Bitmap btmpImage2;
    public Bitmap btmpImage3;

    public float rating;

    public String getName() {
        return name;
    }
}
