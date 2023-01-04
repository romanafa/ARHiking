package com.example.arhiking.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.arhiking.Data.HikeActivityDao_Impl;
import com.example.arhiking.Repository.UserDataRepository;

public class SettingsViewModel extends AndroidViewModel {

    public UserDataRepository userDataRepository;

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        userDataRepository = new UserDataRepository(application);
    }

    public LiveData<Integer> getCount() {
        return userDataRepository.getCount();
    }

    public LiveData<Double> getTotalDistanceSum(){
        return userDataRepository.getTotalDistanceSum();
    }

}