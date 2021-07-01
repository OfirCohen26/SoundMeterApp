package com.example.noisetracker.Fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.noisetracker.R;
import com.example.noisetracker.Service.Service_Sound_Meter;
import com.google.android.material.button.MaterialButton;

public class Fragment_Menu  extends Fragment {
    // Buttons
    private MaterialButton home_page_BTN_start;
    private MaterialButton home_page_BTN_finish;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        findViews(view);
        initButtons();

    }

    private void initButtons() {
        home_page_BTN_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMyService();
            }
        });

        home_page_BTN_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopMyService();
            }
        });
    }

    private void startMyService() {
        actionToService(Service_Sound_Meter.START_FOREGROUND_SERVICE);
    }

    private void stopMyService() {
        actionToService(Service_Sound_Meter.STOP_FOREGROUND_SERVICE);
    }

    private void actionToService(String action) {
        Intent startIntent = new Intent(getContext(), Service_Sound_Meter.class);
        startIntent.setAction(action);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // startForegroundService(startIntent);
            getActivity().startForegroundService(startIntent);
            // or
            //ContextCompat.startForegroundService(this, startIntent);
        } else {
            getActivity().startService(startIntent);
        }
    }
        private void findViews(View view) {
        home_page_BTN_start = (MaterialButton) view.findViewById(R.id.menu_page_BTN_start);
        home_page_BTN_finish = (MaterialButton) view.findViewById(R.id.menu_page_BTN_finish);
    }
}
