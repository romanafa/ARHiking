package com.example.arhiking.Models;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class HikeActivitiesWithGeoPoints {

    @Embedded
    public Hike_Activity hikeActivity;
    @Relation(
            parentColumn = "hikeActivityId",
            entityColumn = "hikeActivityGeoPointId"
    )
    public List<HikeActivityGeoPoint> hikeActivityGeoPoints;
}

//
