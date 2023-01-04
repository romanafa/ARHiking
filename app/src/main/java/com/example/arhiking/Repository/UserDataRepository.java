package com.example.arhiking.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.example.arhiking.Data.AppDatabase_v2;
import com.example.arhiking.Data.HikeActivityDao;

public class UserDataRepository {
    public HikeActivityDao hikeActivityDao;
    public static UserDataRepository INSTANCE = null;

    public UserDataRepository() {
    }

    public UserDataRepository getInstance(){
        if(INSTANCE == null)
            return INSTANCE = new UserDataRepository();
        return INSTANCE;
    }

    public UserDataRepository(Application application) {
        AppDatabase_v2 db = Room.databaseBuilder(application.getApplicationContext(),
                AppDatabase_v2.class, "database-v2").allowMainThreadQueries().build();
        this.hikeActivityDao = db.hikeActivityDao();
    }

    public LiveData<Integer> getCount() {
        return hikeActivityDao.getCount();
    }

    public LiveData<Double> getTotalDistanceSum(){
        return hikeActivityDao.getTotalDistanceSum();
    }
}
