package com.example.arhiking.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.osmdroid.util.GeoPoint;

import java.util.List;

@Entity
public class Hike {
    @PrimaryKey(autoGenerate = true)
    public int hikeId;

    @ColumnInfo(name = "hike_name")
    public String hikeName;

    @ColumnInfo(name = "hike_description")
    public String hikeDescription;

    @ColumnInfo(name = "user_creator_id", defaultValue = "0")
    public long userCreatorId;

    @ColumnInfo(name = "starting_point")
    public GeoPoint startingPoint;

    /*@ColumnInfo(name = "path")
    public List<GeoPoint> path;*/

}