package com.example.drogomierz;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements OdometerService.Callbacks {

    public static  String UNIT_KEY;
    public static  String METRIC;
    public static  String IMPERIAL;

    public static boolean sIsRunning = false;
    private OdometerService odometer;
    private Intent serviceIntent;
    private TextView tvDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

        serviceIntent = new Intent(MainActivity.this, OdometerService.class);
        setViewsWidgets();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            }
        }
        return true;
    }

    private void setViewsWidgets() {
        ImageView startPauseImageView = findViewById(R.id.imageStartPause);
        startPauseImageView.setTag(R.drawable.ic_play);
        startPauseImageView.setOnClickListener(imageViewListener);

        ImageView resetImageView = findViewById(R.id.imageReset);
        resetImageView.setOnClickListener(imageViewListener);

        tvDistance = findViewById(R.id.tv_distance);


        UNIT_KEY = getString(R.string.pref_units_key);
        METRIC = getString(R.string.pref_unit_metric_value);
        IMPERIAL = getString(R.string.pref_unit_imperial_value);
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }


    /**
     * ServiceConnection used to bind MainActivity with OdometerService
     */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            OdometerService.OdometerBinder odometerBinder = (OdometerService.OdometerBinder) iBinder;
            odometer = odometerBinder.getOdometer();
            odometer.registerClient(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            odometer.unregisterClient();
            odometer = null;
        }
    };


    View.OnClickListener imageViewListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            ImageView mImageView = (ImageView) view;

            switch (view.getId()) {
                case R.id.imageStartPause: {

                    if ((int) mImageView.getTag() == R.drawable.ic_play) {
                        mImageView.setImageResource(R.drawable.ic_pause);
                        mImageView.setTag(R.drawable.ic_pause);
                        sIsRunning = true;
                        startService(serviceIntent);
                        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);

                    } else if ((int) mImageView.getTag() == R.drawable.ic_pause) {
                        mImageView.setImageResource(R.drawable.ic_play);
                        mImageView.setTag(R.drawable.ic_play);
                        sIsRunning = false;
                    }
                    break;
                }

                case R.id.imageReset: {
                    mImageView = findViewById(R.id.imageStartPause);
                    mImageView.setImageResource(R.drawable.ic_play);
                    mImageView.setTag(R.drawable.ic_play);

                    if (null!=odometer){
                        unbindService(connection);
                        stopService(serviceIntent);
                    }

                    tvDistance.setText(getString(R.string.work_finished));
                    break;
                }
            }

        }
    };

    @Override
    public void updateClient(String distance) {
        if (sIsRunning) {
            tvDistance.setText(distance);
        }
    }
}
