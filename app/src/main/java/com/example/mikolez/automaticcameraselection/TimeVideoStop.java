package com.example.mikolez.automaticcameraselection;

import android.app.Activity;
import android.graphics.Camera;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class TimeVideoStop extends AsyncTask<String, Void, String> {

    public static final int TIMEOUT = 10;
    public static final String TIME_SERVER = "1.pool.ntp.org";
    private long timeFromServer;
    private Activity activity;

    public TimeVideoStop(Activity activity) {
        this.activity = activity;
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
        final String filePath = Camera2VideoFragment.videoPath;

        /* Retrieve a video duration */
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(filePath);
        long duration = Long.parseLong(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

        /* Write general data to the file */
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(Camera2VideoFragment.generalDataFile, true), 1024);
            String entry = "Stop: " + timeFromServer + " Duration: " + duration;
            out.write(entry);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Camera2VideoFragment.IS_MOVING_CAMERA) {
            /* Write the data from "rotationMatrixData" arraylist to the file "rotationMatrixFile" */
            for (int i = 0; i < Camera2VideoFragment.rotationMatrixData.size(); i++) {
                try {
                    BufferedWriter out = new BufferedWriter(new FileWriter(Camera2VideoFragment.rotationMatrixDataFile, true), 1024);
                    String entry = "";
                    if (Camera2VideoFragment.rotationMatrixData.get(i) == null) {
                        continue;
                    }
                    for (int j = 0; j < Camera2VideoFragment.rotationMatrixData.get(i).length; j++) {
                        entry += Camera2VideoFragment.rotationMatrixData.get(i)[j] + " ";
                    }
                    entry += Camera2VideoFragment.timeData.get(i) + "\n";
                    out.write(entry);
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Toast.makeText(activity, "Files are saved!", Toast.LENGTH_SHORT).show();
        Camera2VideoFragment.mIsCameraCalibrated = false;
    }
}