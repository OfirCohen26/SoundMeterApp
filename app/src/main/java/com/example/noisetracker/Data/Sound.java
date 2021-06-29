package com.example.noisetracker.Data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.DatabaseView;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "weekly_sound_measure")
public class Sound implements Comparable<Sound> {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "day")
    public int day;

    @ColumnInfo(name = "daily_amount_decibel")
    public double dailyAmountDb;

    @ColumnInfo(name = "max_decibel")
    public double maxDb;

    @ColumnInfo(name = "average_decibel")
    public double avgDb;

    @Ignore
    public Sound(String date, int day, double dailyAmountDb, double maxDb, double avgDb) {
        this.date = date;
        this.day = day;
        this.dailyAmountDb = dailyAmountDb;
        this.maxDb = maxDb;
        this.avgDb = avgDb;
    }

    @Ignore
    public Sound(String date, int day, double dailyAmountDb) {
        this.date = date;
        this.day = day;
        this.dailyAmountDb = dailyAmountDb;
        this.avgDb = 0;
        this.maxDb = 0;
    }

    public Sound(int day,String date) {
        this.date = date;
        this.day = day;
        this.dailyAmountDb = 0;
        this.avgDb = 0;
        this.maxDb = 0;
    }
    public int getUid() {
        return uid;
    }

    public String getDate() {
        return date;
    }

    public int getDay() {
        return day;
    }

    public double getDailyAmountDb() {
        return dailyAmountDb;
    }

    public double getMaxDb() {
        return maxDb;
    }

    public double getAvgDb() {
        return avgDb;
    }

    @Override
    public int compareTo(Sound sound) {
        return this.day > sound.day ? 1 : -1;
    }
}
