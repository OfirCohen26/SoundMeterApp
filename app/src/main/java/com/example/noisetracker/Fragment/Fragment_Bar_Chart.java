package com.example.noisetracker.Fragment;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.noisetracker.Data.DatabaseClient;
import com.example.noisetracker.Data.Sound;
import com.example.noisetracker.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Fragment_Bar_Chart extends Fragment {

    private final int INIT_BAR_CHART_DELAY_LENGTH = 300;

    private final int OFFSET = 0;

    private BarChart bar_chart_BAR_barChart;

    private Thread mThread;

    //Sound sounds[];

    List<Sound> sounds;

    private Handler mHandler = new Handler();


    @Nullable
    @Override
    // creates and returns the view hierarchy associated with the fragment.
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bar_chart, container, false);
    }

    @Override
    //Called immediately after "onCreateView" has returned,
    // but before any saved state has been restored in to the view.
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        findViews(view);
        sounds = new ArrayList<Sound>();
        start();
    }

    private void findViews(View view) {
        bar_chart_BAR_barChart = (BarChart) view.findViewById(R.id.bar_chart_BAR_barChart);
    }

    private Runnable mSleepTask = new Runnable() {
        public void run() {
            sounds = DatabaseClient.getInstance(getActivity().getApplicationContext()).getAppDatabase().soundDao().getAll();
            //Arrays.sort(sounds);

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    initBarChart();
                }
            }, INIT_BAR_CHART_DELAY_LENGTH);
        }
    };


    private void start() {
        //Attach tasks to do in thread
        mThread = new Thread(mSleepTask);
        // Start running thread
        mThread.start();
    }

    private void initBarChart() {
        String[] labels = {"Sun", "Mond", "Tues", "Wed", "Thus", "Fri", "sat"};
        ArrayList<BarEntry> soundLevel = new ArrayList<>();

        Log.d(" ", "sound lengh" + sounds.size());

        if (sounds.size() > 0) {
            for (int i = 0; i < 7; i++) {
                soundLevel.add(new BarEntry(i, (int) sounds.get(i).getDailyAmountDb()));
                Log.d("in lop", "" +(int) sounds.get(i).getDailyAmountDb());
            }
        } else {
            for (int i = 0; i < 7; i++) {
                soundLevel.add(new BarEntry(i, 0));
            }
        }

        BarDataSet barDataSet = new BarDataSet(soundLevel, "Sound Level");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.WHITE);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);

        bar_chart_BAR_barChart.setFitBars(true);
        bar_chart_BAR_barChart.getDescription().setText("");
        bar_chart_BAR_barChart.animateY(2000);

        XAxis xAxis = bar_chart_BAR_barChart.getXAxis();
        YAxis yAxisL = bar_chart_BAR_barChart.getAxisLeft();
        YAxis yAxisR = bar_chart_BAR_barChart.getAxisRight();

        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        yAxisL.setTextColor(Color.WHITE);
        yAxisR.setTextColor(Color.WHITE);

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(7);
        xAxis.setTextColor(Color.WHITE);

        bar_chart_BAR_barChart.setData(barData);

        if (sounds.size() > 0) {
            onChartValueSelected();
        }
    }

    private void onChartValueSelected() {
        bar_chart_BAR_barChart.setOnChartValueSelectedListener(
                new OnChartValueSelectedListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onValueSelected(Entry e, Highlight h) {
                        BarEntry pe = (BarEntry) e;
                        if(sounds.get((int) pe.getX()).dailyAmountDb > 0)
                        {
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                                    "Date: " +sounds.get((int) pe.getX()).getDate() + "\nMax dB: " + (int) sounds.get((int) pe.getX()).getMaxDb() + " dB"
                                            + "\nAvg dB: " + (int) sounds.get((int) pe.getX()).dailyAmountDb / 24 + " dB", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, OFFSET, OFFSET);
                            toast.show();
                        }
                    }
                    @Override
                    public void onNothingSelected() {
                    }
                }
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mSleepTask); // Remove pending posts
        mThread.interrupt(); // Kill thread
    }

}
