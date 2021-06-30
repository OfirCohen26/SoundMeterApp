package com.example.noisetracker.Data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Sound.class},version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SoundDao soundDao();
}