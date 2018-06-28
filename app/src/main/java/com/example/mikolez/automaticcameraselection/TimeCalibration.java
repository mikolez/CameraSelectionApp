package com.example.mikolez.automaticcameraselection;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class TimeCalibration extends AsyncTask<String, Void, String> {

    public static final int TIMEOUT = 10;
    public static final String TIME_SERVER = "1.pool.ntp.org";
    private long timeFromServer;

    /* Fields used to write to the file and toast the message */
    private File cameraCalibrationFile;
    private Context context;

    public TimeCalibration(Context context, File cameraCalibrationFile) {
        this.context = context;
        this.cameraCalibrationFile = cameraCalibrationFile;
    }

    @Override
    protected String doInBackground(String... urls) {
        SntpClient client = new SntpClient();

        long start = System.currentTimeMillis();
        while (!client.requestTime(TIME_SERVER, TIMEOUT)) {
        }
        long end = System.currentTimeMillis();
        timeFromServer = client.getNtpTime() - (end - start);
        return null;
    }

    @Override
    protected void onPostExecute(String feed) {

        /* Write the calibration timestamp to the file */
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(cameraCalibrationFile, true), 1024);
            String entry = "";
            entry += timeFromServer + " ";
            out.write(entry);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* Change the flag and toast the message */
        Camera2VideoFragment.mIsCameraCalibrated = true;
        Toast.makeText(context, "Calibration is done", Toast.LENGTH_SHORT).show();
    }
}