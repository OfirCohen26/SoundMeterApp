package com.example.noisetracker.Screen;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.noisetracker.Fragment.Fragment_Graph;
import com.example.noisetracker.Fragment.Fragment_Menu;
import com.example.noisetracker.R;
import com.example.noisetracker.Utils.Activity_Base;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Main_Screen extends Activity_Base {

    private BottomNavigationView main_BTN_Navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_BTN_Navigation = findViewById(R.id.main_BTN_Navigation);
        main_BTN_Navigation.setItemIconTintList(null);

        main_BTN_Navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_FRG_Content, new Fragment_Menu()).commit();
    }


    // Set bottom navigation bar
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.nav_BTN_menu:
                    selectedFragment = new Fragment_Menu();
                    break;
                case R.id.nav_BTN_bar_chart:
                    selectedFragment = new Fragment_Graph();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.main_FRG_Content, selectedFragment).commit();

            return true;
        }
    };
}