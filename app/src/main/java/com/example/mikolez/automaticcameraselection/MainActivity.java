package com.example.mikolez.automaticcameraselection;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v13.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    /* Button for a camera view */
    private Button Camera;

    /* Button for a Rotation Matrix data collection */
    private Button Data;

    /* Permissions to ask */
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Buttons initialization */
        Camera = findViewById(R.id.cameraButton);
        Data = findViewById(R.id.dataButton);

        /* Set up button listeners */
        Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCameraButton();
            }
        });

        Data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDataButton();
            }
        });

        /* Check permissions and ask them if necessary */
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    /* On camera button action */
    private void onCameraButton() {

        /* Change to an activity with a camera view to shoot videos */
        Intent myIntent = new Intent(this, CameraActivity.class);
        startActivity(myIntent);
    }

    /* On data button action */
    private void onDataButton() {

        /* Change to an activity to calibrate cameras' orientation */
        Intent myIntent = new Intent(this, DataActivity.class);
        startActivity(myIntent);
    }

    /* Check permissions and ask them if necessary */
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
