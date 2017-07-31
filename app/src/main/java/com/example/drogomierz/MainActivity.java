package com.example.drogomierz;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements OdometerService.Callbacks {

    private OdometerService odometer;
    public static boolean sIsRunning = false;
    private Intent serviceIntent;
    private TextView tvDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();
        serviceIntent = new Intent(MainActivity.this,OdometerService.class);
        setViewsWidgets();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings: {
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                break;
            }
        }
        return true;
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode != 1){
//            Toast toast = Toast.makeText(this, "Bez pozwoleń to nie poleci kolego", Toast.LENGTH_LONG);
//        }
//    }

    private void setViewsWidgets(){
        ImageView startPauseImageView = findViewById(R.id.imageStartPause);
        startPauseImageView.setTag(R.drawable.ic_play);
        startPauseImageView.setOnClickListener(imageViewListener);
        tvDistance = findViewById(R.id.tv_distance);
    }

    private void checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},1);
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
            if (view.getId() == R.id.imageStartPause) {

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
                    unbindService(connection);
                    stopService(serviceIntent);
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
