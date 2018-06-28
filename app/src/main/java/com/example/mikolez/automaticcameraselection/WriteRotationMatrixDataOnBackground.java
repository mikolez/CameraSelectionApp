package com.example.mikolez.automaticcameraselection;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class WriteRotationMatrixDataOnBackground extends AsyncTask<String, Void, String> {


    private Context context;
    private ArrayList<float[]> rotationMatrixData;
    private ArrayList<Long> timeData;
    private long dataStartTime;
    private File rotationMatrixFile;
    private File dataStartTimeFile;


    public WriteRotationMatrixDataOnBackground(Context context, ArrayList<float[]> rotationMatrixData,
                                               ArrayList<Long> timeData, long dataStartTime, File rotationMatrixFile,
                                               File dataStartTimeFile) {
        this.context = context;
        this.rotationMatrixData = rotationMatrixData;
        this.timeData = timeData;
        this.dataStartTime = dataStartTime;
        this.rotationMatrixFile = rotationMatrixFile;
        this.dataStartTimeFile = dataStartTimeFile;
    }

    @Override
    protected String doInBackground(String... urls) {

        /* Write the data from "rotationMatrixData" arraylist to the file "rotationMatrixFile" */
        for (int i = 0; i < rotationMatrixData.size(); i++) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(rotationMatrixFile, true), 1024);
                String entry = "";
                if (rotationMatrixData.get(i) == null) {
                    continue;
                }
                for (int j = 0; j < rotationMatrixData.get(i).length; j++) {
                    entry += rotationMatrixData.get(i)[j] + " ";
                }
                entry += timeData.get(i) + "\n";
                out.write(entry);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /* Write the data collection start timestamp and duration to the file */
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(dataStartTimeFile, true), 1024);
            String entry = "Start: " + dataStartTime + " Duration: " + timeData.get(timeData.size() - 1);
            out.write(entry);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* Clear the arraylists for the next data accumulation */
        rotationMatrixData.clear();
        timeData.clear();

        return null;
    }

    @Override
    protected void onPostExecute(String feed) {
        Toast.makeText(context, "Data is saved", Toast.LENGTH_SHORT).show();
    }
}