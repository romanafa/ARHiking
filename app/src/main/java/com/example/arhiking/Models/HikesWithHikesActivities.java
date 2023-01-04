package com.example.arhiking.Models;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.arhiking.Data.HikeActivityDao;

import java.util.List;

public class HikesWithHikesActivities {

    @Embedded
    public Hike hike;
    @Relation(
            parentColumn = "hikeId",
            entityColumn = "hike_id"
    )
    public List<Hike_Activity> hikeActivities;
}
