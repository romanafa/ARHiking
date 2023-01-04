/*
package com.example.arhiking.Data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.arhiking.Models.TrackingEntity;

import java.util.List;

@Dao
interface TrackingDao {
    // 1
    @Query("SELECT * FROM trackingentity")
    LiveData<List> getAllTrackingEntities();
    // 2
    @Query("SELECT * FROM trackingentity")
    List getAllTrackingEntitiesRecord();
    // 3
    @Query("SELECT SUM(distanceTravelled) FROM trackingentity")
    LiveData getTotalDistanceTravelled();
    // 4
    @Query("SELECT * FROM trackingentity ORDER BY timestamp DESC LIMIT 1")
    LiveData<TrackingEntity> getLastTrackingEntity();
    // 5
    @Query("SELECT * FROM trackingentity ORDER BY timestamp DESC LIMIT 1")
    TrackingEntity getLastTrackingEntityRecord();
    // 6
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(TrackingEntity trackingEntity);
    // 7
    @Query("DELETE FROM trackingentity")
    void deleteAll();
}
*/
