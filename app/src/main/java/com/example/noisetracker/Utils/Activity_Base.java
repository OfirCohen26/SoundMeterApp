package com.example.noisetracker.Utils;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_Base extends AppCompatActivity {

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            My_Screen_Utils.hideSystemUI(this);
        }
    }
}
