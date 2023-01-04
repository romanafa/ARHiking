package com.example.arhiking.Data;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.arhiking.Models.GeomagneticSensorData;


@Dao
public interface GeomagneticDao {

    @Insert
    void insertAll(GeomagneticSensorData... geomagneticSensorData);

}