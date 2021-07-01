package com.example.noisetracker.Service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.noisetracker.Data.DatabaseClient;
import com.example.noisetracker.Data.Sound;
import com.example.noisetracker.R;
import com.example.noisetracker.Screen.Main_Screen;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Service_Sound_Meter extends Service {

    public static final String START_FOREGROUND_SERVICE = "START_FOREGROUND_SERVICE";
    public static final String STOP_FOREGROUND_SERVICE = "STOP_FOREGROUND_SERVICE";

    public static String CHANNEL_ID = "com.example.noisetracker.CHANNEL_ID_FOREGROUND";
    public static String MAIN_ACTION = "com.example.noisetracker.appService.action.main";

    private final int RECORD_DELAY_LENGTH = 300;

    private final int START_RECORD_DELAY_LENGTH = 600;

    public static int NOTIFICATION_ID = 153;
    private int lastShownNotificationId = -1;
    private boolean isServiceRunningRightNow = false;
    private NotificationCompat.Builder notificationBuilder;

    private MediaRecorder mRecorder;
    private double liveDb = 0;
    private double maxDb = 0.0;

    private Thread mThread;
    private Handler mHandler = new Handler();

    Calendar calendar;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action.equals(START_FOREGROUND_SERVICE)) {
            // if the service is already running
            if (isServiceRunningRightNow) {
                // keep running
                return START_STICKY;
            }
            isServiceRunningRightNow = true;
            notifyToUserForForegroundService();
            startRecording();
            return START_STICKY;
        } else if (action.equals(STOP_FOREGROUND_SERVICE)) {
            stopRecording();
            stopForeground(true);
            stopSelf();
            isServiceRunningRightNow = false;
            return START_NOT_STICKY;
        }
        return START_STICKY;
    }

    private Runnable mPollTask = new Runnable() {
        @Override
        public void run() {
            calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            liveDb = getAmplitude();
            double toSend = Math.round(liveDb);
            if (toSend != 0.0) { // Make sure thread stops sending measurement if no input sound detected
                if (maxDb <= toSend) {   // Find maximum result for final result
                    maxDb = toSend;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("day", "" + day);
                        DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().soundDao().incrementDbValue(day, toSend, maxDb);
                    }
                }).start();
                Log.d("toSend", "" + toSend);
                Log.d("maxDb", "" + maxDb);
            }
            mHandler.postDelayed(mPollTask, RECORD_DELAY_LENGTH);
        }
    };
    private Runnable mSleepTask = new Runnable() {
        public void run() {
            //Clean Database
            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().soundDao().deleteAll();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (int i = 0; i < 7; i++) {
                calendar = new GregorianCalendar();
                calendar.add(Calendar.DATE, i);
                String date = sdf.format(calendar.getTime());
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().soundDao().
                        insertAll(new Sound(dayOfWeek, date));

            }
            // Init media recorder
            startRecorder();
            mHandler.postDelayed(mPollTask, START_RECORD_DELAY_LENGTH);
        }
    };

    private void startRecording() {
        //Attach tasks to do in thread
        mThread = new Thread(mSleepTask);
        // Start running thread
        mThread.start();
    }

    private void stopRecording() {
        if (mRecorder != null) {
            // Stopping the recorder when service is stopped
            stopRecorder();
        }
    }

    public void startRecorder() {
        if (mRecorder == null) {
            // Initialize media recorder
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try {
                mRecorder.prepare();
            } catch (IllegalStateException e) {
                Toast.makeText(getApplicationContext(), "IllegalStateException called", Toast.LENGTH_LONG).show();


            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "prepare() failed", Toast.LENGTH_LONG).show();

            }
            mRecorder.start();
        }
    }

    public void stopRecorder() {
        if (mRecorder != null) {
            mHandler.removeCallbacks(mPollTask); // Remove pending posts
            mHandler.removeCallbacks(mSleepTask); // Remove pending posts
            mThread.interrupt(); // Kill thread
            //Stop media recorder
            mRecorder.stop();

            // Free allocated memory for recorder
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public double getAmplitude() {
        if (mRecorder != null) {
            // Cellphones can catch up to 90 db + -
            // getMaxAmplitude returns a value between 0-32767 (in most phones). that means that if the maximum db is 90, the pressure
            // at the microphone is 0.6325 Pascal.
            // it does a comparison with the previous value of getMaxAmplitude.
            // we need to divide maxAmplitude with (32767/0.6325)
            double f1 = mRecorder.getMaxAmplitude() / 51805.5336;//51805.5336 or if 100db so 46676.6381
            if (f1 > 0) {
                //Assuming that the minimum reference pressure is 0.000085 Pascal (on most phones) is equal to 0 db
                return (Math.abs(20 * Math.log10(f1 / 0.000085)));
            }
            return 0;
        } else
            return 0;

    }

    private void notifyToUserForForegroundService() {
        // On notification click the Main activity is opened
        Intent notificationIntent = new Intent(this, Main_Screen.class);
        notificationIntent.setAction(MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder = getNotificationBuilder(this,
                CHANNEL_ID,
                NotificationManagerCompat.IMPORTANCE_LOW); //Low importance prevent visual appearance for this notification channel on top

        notificationBuilder.setContentIntent(pendingIntent) // Open activity
                .setOngoing(true)
                .setSmallIcon(R.drawable.sound_measure)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                .setContentTitle("App in progress");

        Notification notification = notificationBuilder.build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }

        if (NOTIFICATION_ID != lastShownNotificationId) {
            // Cancel previous notification
            final NotificationManager notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
            notificationManager.cancel(lastShownNotificationId);
        }
        lastShownNotificationId = NOTIFICATION_ID;
    }

    public static NotificationCompat.Builder getNotificationBuilder(Context context, String channelId, int importance) {
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            prepareChannel(context, channelId, importance);
            builder = new NotificationCompat.Builder(context, channelId);
        } else {
            builder = new NotificationCompat.Builder(context);
        }
        return builder;
    }

    @TargetApi(26)
    private static void prepareChannel(Context context, String id, int importance) {
        final String appName = context.getString(R.string.app_name);
        String notifications_channel_description = "Noise Tracker";
        String description = notifications_channel_description;
        final NotificationManager nm = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);

        if (nm != null) {
            NotificationChannel nChannel = nm.getNotificationChannel(id);

            if (nChannel == null) {
                nChannel = new NotificationChannel(id, appName, importance);
                nChannel.setDescription(description);

                // from another answer
                nChannel.enableLights(true);
                nChannel.setLightColor(Color.BLUE);

                nm.createNotificationChannel(nChannel);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
