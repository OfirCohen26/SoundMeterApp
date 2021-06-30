package com.example.noisetracker.Screen;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.noisetracker.Utils.Activity_Base;
import com.example.noisetracker.R;
import com.google.android.material.button.MaterialButton;

public class Splash_Screen extends Activity_Base {
     private final int REQUEST_CODE = 123;
    /**
     * Duration of wait
     **/
    private final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen);

        boolean isGranted = checkPermission();
        if (!isGranted) {
            requestPermission();
            return;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToMainActivity();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }


    private void navigateToMainActivity(){
        /* Create an Intent that will start the Main-Activity. */
        Splash_Screen.this.startActivity( new Intent(Splash_Screen.this, Main_Screen.class));
        Splash_Screen.this.finish();
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(Splash_Screen.this,
                new String[]{Manifest.permission.RECORD_AUDIO
                }, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults.length == 0 ||
                            grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        requestPermissionWithRationaleCheck();
                        return;
                    }
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        navigateToMainActivity();
                    }
                }, SPLASH_DISPLAY_LENGTH);

            }
        }
    }


    private void requestPermissionWithRationaleCheck() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            // Show user description for what we need the permission
            requestPermission();
        } else {
            openPermissionSettingDialog();
        }
    }

    private void openPermissionSettingDialog() {
        String message = "The Application needs some permission, please enable all permission required.";
        AlertDialog alertDialog =
                new AlertDialog.Builder(this)
                        .setMessage(message)
                        .setPositiveButton(getString(android.R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                                        intent.setData(uri);
                                        dialog.cancel();
                                    }
                                }).show();
        alertDialog.setCanceledOnTouchOutside(true);
    }


}