package com.example.arhiking.Data;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.arhiking.Models.HikeGeoPoint;


@Dao
public interface HikeGeoPointsDao {

    @Insert
    void insertAll(HikeGeoPoint... geoPoints);

}