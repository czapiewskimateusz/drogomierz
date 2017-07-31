package com.example.drogomierz;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

public class OdometerService extends Service {

    private final IBinder binder = new OdometerBinder();
    private static double distanceInMeters;
    private static Location lastLocation = null;
    private Callbacks activity = null;

    @Override
    public void onCreate() {

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location.getAccuracy()<5){
                    if (lastLocation == null || !MainActivity.sIsRunning) {
                        lastLocation = location;
                    }
                    distanceInMeters += location.distanceTo(lastLocation) / 1000;
                    lastLocation = location;
                    String distanceString = String.format("%.3f",distanceInMeters);
                    activity.updateClient(distanceString + " km");
                }

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast toast = Toast.makeText(this, "No permissions granted", Toast.LENGTH_LONG);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    public void registerClient(Callbacks activity) {
        this.activity = activity;
    }

    public void unregisterClient() {
        activity = null;
    }

    /**
     * Binder class
     */
    public class OdometerBinder extends Binder {

        OdometerService getOdometer() {
            return OdometerService.this;
        }
    }

    /**
     * Interface to display data in Activity
     */
    public interface Callbacks {
        public void updateClient(String distance);
    }
}
