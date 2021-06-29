package com.example.noisetracker.Utils;

import android.app.Application;

import androidx.room.Room;

import com.example.noisetracker.Data.AppDatabase;

public class App extends Application {
    AppDatabase appDatabase;

    @Override
    public void onCreate() {
        super.onCreate();

        //MyClockTickerV4.initHelper();

    }
}
