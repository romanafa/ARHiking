package com.example.arhiking.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GyroscopeSensorData {

    @PrimaryKey(autoGenerate = true)
    public int gyroscopeSensorDataId;

    @ColumnInfo(name = "hike_activity_id", defaultValue = "0")
    public long hike_activity_id;

    @ColumnInfo(name = "time_registered")
    public Long timeRegistered;

    @ColumnInfo(name = "x_value")
    public Float xValue;

    @ColumnInfo(name = "y_value")
    public Float yValue;

    @ColumnInfo(name = "z_value")
    public Float zValue;
}
