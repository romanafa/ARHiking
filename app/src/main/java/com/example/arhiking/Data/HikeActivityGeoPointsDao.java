package com.example.arhiking.Data;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.arhiking.Models.HikeActivityGeoPoint;


@Dao
public interface HikeActivityGeoPointsDao {

    @Insert
    void insertAll(HikeActivityGeoPoint... geoPoints);

}