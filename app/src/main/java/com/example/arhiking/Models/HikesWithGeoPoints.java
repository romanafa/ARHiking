package com.example.arhiking.Models;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class HikesWithGeoPoints {

    @Embedded
    public Hike hike;
    @Relation(
            parentColumn = "hikeId",
            entityColumn = "hikeGeoPointId"
    )
    public List<HikeGeoPoint> hikeGeoPoints;
}
