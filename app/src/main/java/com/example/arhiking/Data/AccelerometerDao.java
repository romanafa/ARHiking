package com.example.arhiking.Data;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.arhiking.Models.AccelerometerData;

@Dao
public interface AccelerometerDao {

    @Insert
    void insertAll(AccelerometerData... accelerometerData);

}