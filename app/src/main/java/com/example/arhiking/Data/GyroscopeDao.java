package com.example.arhiking.Data;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.arhiking.Models.GyroscopeSensorData;


@Dao
public interface GyroscopeDao {

    @Insert
    void insertAll(GyroscopeSensorData... gyroscopeSensorData);

}