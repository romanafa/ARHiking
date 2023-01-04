package com.example.arhiking.Models;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class HikeActivitiesWithAccelerometerData {

    @Embedded
    public Hike_Activity hikeActivity;
    @Relation(
            parentColumn = "hikeActivityId",
            entityColumn = "hike_activity_id"
    )
    public List<AccelerometerData> accelerometerData;
}
