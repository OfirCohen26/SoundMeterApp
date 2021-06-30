package com.example.noisetracker.Data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SoundDao {
    @Insert
    void insertAll(Sound... sounds);

    @Query("SELECT * FROM weekly_sound_measure")
    Sound[] getAll();

    @Query("UPDATE weekly_sound_measure SET daily_amount_decibel = daily_amount_decibel + :value, max_decibel = :maxDb WHERE day = :day")
    public void incrementDbValue(int day, double value, double maxDb);

    @Query("DELETE FROM weekly_sound_measure")
    public void deleteAll();
}
