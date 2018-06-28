package com.example.mikolez.automaticcameraselection;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DataActivity extends AppCompatActivity {

    /* Button to start data collection */
    private Button startButton;

    /* Arraylist to store rotation matrix data accumulated */
    private ArrayList<float[]> rotationMatrixData;

    /* Arraylist of timestamps for the data collection */
    private ArrayList<Long> timeData;

    /* File to store rotation matrix accumulated */
    private File rotationMatrixFile;

    /* File that contains data collection start time received from the server */
    private File dataStartTimeFile;

    /* Sensor manager and listener for sensor data accumulation */
    private static SensorManager mSensorManager;
    private static SensorEventListener sensorEventListener;

    /* A flag that indicates whether data is being recorded or not */
    private boolean isRecording;

    /* Data collection start time field */
    public static long dataStartTime;

    /* Field for the activity context */
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);

        startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    onStartButton();
                } else {
                    onStopButton();
                }
            }
        });

        /* Initialization of fields */
        context = getApplicationContext();
        isRecording = false;
        rotationMatrixData = new ArrayList<>();
        timeData = new ArrayList<>();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    /* Method invoked after pressing the start button */
    private void onStartButton() {
        startButton.setText("Stop");
        isRecording = true;

        /* Request a timestamp from the server for the data start time */
        new TimeDataStart(this).execute();

        Toast.makeText(this, "Data collection started!", Toast.LENGTH_SHORT).show();

        /* Create a folder for data collection instance */
        String root = Environment.getExternalStorageDirectory().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateAndTime = sdf.format(new Date());
        File myDir = new File(root + "/RMD/" + currentDateAndTime);
        myDir.mkdirs();

        /* Initialize data files */
        rotationMatrixFile = new File(myDir, "rotationMatrixFile.txt");
        dataStartTimeFile = new File(myDir, "dataStartTimeFile.txt");

        /* Start time of data acquisition */
        final long startTime = System.currentTimeMillis();

        sensorEventListener = new SensorEventListener() {

            float[] mGravity;
            float[] mGeomagnetic;
            float[] R;
            @Override
            public void onSensorChanged(SensorEvent event) {

                /* Time at which a new instance of rotation matrix is recorded */
                long currentTime = System.currentTimeMillis();

                /* Rotation Matrix computation */
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                    mGravity = event.values;
                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                    mGeomagnetic = event.values;
                if (mGravity != null && mGeomagnetic != null) {
                    R = new float[9];
                    float I[] = new float[9];
                    SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                }

                /* Adding new data to lists */
                rotationMatrixData.add(R);
                timeData.add(currentTime - startTime);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        mSensorManager.registerListener(sensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(sensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_FASTEST);
    }

    /* Method invoked after pressing the stop button */
    private void onStopButton() {
        startButton.setText("Start");
        isRecording = false;

        /* Turn off the sensor listener */
        mSensorManager.unregisterListener(sensorEventListener);

        /* Do the expensive computation on the background thread */
        new WriteRotationMatrixDataOnBackground(this, rotationMatrixData, timeData, dataStartTime, rotationMatrixFile, dataStartTimeFile).execute();
    }
}
