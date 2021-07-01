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

    @Query("SELECT * FROM weekly_sound_measure ORDER BY day ASC")
    Sound[] getAll();

    @Query("UPDATE weekly_sound_measure SET daily_amount_decibel = daily_amount_decibel + :value, max_decibel = :maxDb WHERE day = :day")
    public void incrementDbValue(int day, double value, double maxDb);

    @Query("UPDATE weekly_sound_measure SET daily_amount_decibel = 0, max_decibel = 0, date = :date  WHERE day = :day")
    public void zeroDbValue(int day, String date);

    @Query("SELECT date FROM weekly_sound_measure WHERE day = :day")
    String getDateFromDataBase(int day);

    @Query("DELETE FROM weekly_sound_measure")
    public void deleteAll();
}
