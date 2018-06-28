package com.example.mikolez.automaticcameraselection;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class TimeDataStart extends AsyncTask<String, Void, String> {

    public static final int TIMEOUT = 10;
    public static final String TIME_SERVER = "1.pool.ntp.org";
    private long timeFromServer;
    private Context context;

    public TimeDataStart(Context context) {
        this.context = context;
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
        DataActivity.dataStartTime = timeFromServer;
        Toast.makeText(context, "Start time received", Toast.LENGTH_SHORT).show();
    }
}