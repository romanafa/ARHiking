/*
package com.example.arhiking.Models;


import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

import org.osmdroid.LocationListenerProxy;

@Entity
public class TrackingEntity

 {
     @PrimaryKey long timeStamp;

     @ColumnInfo
     private Double latitude;
     @ColumnInfo
     private Double longitude;
    // The distance in meters between the current Tracking Entity and the previous one in the Room database
    @ColumnInfo
     private float distanceTravelled = 0f;
    // 1
    LatLng asLatLng =  new LatLng(latitude, longitude);
    // 2
    private Float distanceTo(TrackingEntity newTrackingEntity) {
        Location locationA = new Location(LocationManager.GPS_PROVIDER);
        locationA.setLatitude(latitude);
        locationA.setLongitude(longitude);
        Location locationB = new Location(LocationManager.GPS_PROVIDER);
        locationB.setLatitude(newTrackingEntity.latitude);
        locationB.setLongitude(newTrackingEntity.longitude);
        Float distanceInMeter = locationA.distanceTo(locationB);
        return distanceInMeter;
    }
}
*/
