package com.example.arhiking.Data;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.room.migration.AutoMigrationSpec;

import com.example.arhiking.Models.AccelerometerData;
import com.example.arhiking.Models.GeomagneticSensorData;
import com.example.arhiking.Models.GyroscopeSensorData;
import com.example.arhiking.Models.Hike;
import com.example.arhiking.Models.HikeActivityGeoPoint;
import com.example.arhiking.Models.HikeGeoPoint;
import com.example.arhiking.Models.Hike_Activity;

import com.example.arhiking.Models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.type.LatLng;

import org.osmdroid.util.GeoPoint;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

@Database(entities = {User.class, Hike.class, Hike_Activity.class,
GyroscopeSensorData.class, AccelerometerData.class, GeomagneticSensorData.class,
HikeGeoPoint.class, HikeActivityGeoPoint.class}
        , version = 6,  exportSchema = true,
        autoMigrations = {
        @AutoMigration (from = 5, to = 6 , spec = AppDatabase_v2.MyAutoMigration.class)
})

@TypeConverters({AppDatabase_v2.Converters.class})
public abstract class AppDatabase_v2 extends RoomDatabase {
    public abstract UserDao userDao();

    public abstract HikeDao hikeDao();

    public abstract HikeActivityDao hikeActivityDao();

    public abstract AccelerometerDao accelerometerDao();

    public abstract GeomagneticDao geomagneticDao();

    public abstract GyroscopeDao gyroscopeDao();

    public abstract HikeGeoPointsDao HikeGeoPointsDao();

    public abstract HikeActivityGeoPointsDao HikeActivityGeoPointsDao();
    /*public abstract TrackingDao trackingDao();*/

    //@DeleteTable.Entries(value = @DeleteTable(tableName = "HikeActivityGeoPoint"))

    static class MyAutoMigration implements AutoMigrationSpec {

    }

    public static class Converters {

        @TypeConverter
        public static GeoPoint stringToGeoPoint(String data) {
            return new Gson().fromJson(data, GeoPoint.class);
        }

        @TypeConverter
        public static String geoPointToString(GeoPoint geoPoint) {
            return new Gson().toJson(geoPoint);
        }


       @TypeConverter
        public static LatLng stringToLatLng(String data) {
            return new Gson().fromJson(data, LatLng.class);
       }

       @TypeConverter
        public static String latLngToString(LatLng latLng) {
            return new Gson().toJson(latLng);
       }


    }


}