package com.example.arhiking.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.arhiking.Data.AppDatabase_v2;

public class LibraryViewModel extends ViewModel {

    private AppDatabase_v2 database;
    private final MutableLiveData<String> mText;

    public LibraryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is library fragment");
    }


    public LiveData<String> getText() {
        return mText;
    }
}
