package com.example.drogomierz;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends Activity {

    private OdometerService odometer;
    private boolean bound = false;
    private static boolean sIsRunning = false;
    private AsyncTask asyncTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView startPauseImageView = (ImageView) findViewById(R.id.imageStartPause);
        startPauseImageView.setTag(R.drawable.ic_play);
        startPauseImageView.setOnClickListener(imageViewListener);

        watchMilage();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, OdometerService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            unbindService(connection);
            bound = false;
        }
    }

    private void watchMilage() {

        final TextView tvDistance = findViewById(R.id.tv_distance);

        asyncTask = new AsyncTask<Void,String,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                while (sIsRunning){
                    double distance = 0.;
                    if (odometer != null) {
                        distance = odometer.getDistance();
                    }
                    String distanceString = String.format(Locale.getDefault(), "%1$,.2f", distance);
                    publishProgress(distanceString);
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                tvDistance.setText((values[0]));
            }
        };


    }

    /**
     * ServiceConnection used to bind MainActivity with OdometerService
     */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            OdometerService.OdometerBinder odometerBinder = (OdometerService.OdometerBinder) iBinder;
            odometer = odometerBinder.getOdometer();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            bound = false;
        }
    };


    View.OnClickListener imageViewListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            ImageView mImageView = (ImageView) view;
            if (view.getId() == R.id.imageStartPause) {

                if ((int) mImageView.getTag() == R.drawable.ic_play) {
                    mImageView.setImageResource(R.drawable.ic_pause);
                    mImageView.setTag(R.drawable.ic_pause);
                    sIsRunning = true;

                } else if ((int) mImageView.getTag() == R.drawable.ic_pause) {
                    mImageView.setImageResource(R.drawable.ic_play);
                    mImageView.setTag(R.drawable.ic_play);
                    sIsRunning = false;
                }
            }
        }
    };
}
