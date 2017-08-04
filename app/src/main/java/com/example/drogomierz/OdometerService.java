package com.example.drogomierz;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.preference.PreferenceManager;
import android.widget.Toast;

import static com.example.drogomierz.MainActivity.IMPERIAL;
import static com.example.drogomierz.MainActivity.METRIC;
import static com.example.drogomierz.MainActivity.UNIT_KEY;

public class OdometerService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final IBinder binder = new OdometerBinder();
    private double distanceTravelled;
    private Location lastLocation = null;
    private Callbacks activity = null;

    private String unit = METRIC;


    @Override
    public void onCreate() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        unit = sharedPreferences.getString(UNIT_KEY, METRIC);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (activity != null) {
                    if (location.getAccuracy() < 5) {
                        if (lastLocation == null || !MainActivity.sIsRunning) {
                            lastLocation = location;
                        }

                        String distanceString = "";

                        if (unit.equals(METRIC)){
                            distanceTravelled += location.distanceTo(lastLocation) / 1000;
                            distanceString = String.format("%.3f", distanceTravelled);
                            distanceString += " km";

                        } else if (unit.equals(IMPERIAL)){
                            distanceTravelled += location.distanceTo(lastLocation) / 1000 * 0.621371192;
                            distanceString = String.format("%.3f", distanceTravelled);
                            distanceString += " ml";
                        }

                        lastLocation = location;

                        activity.updateClient(distanceString);
                    }
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
            toast.show();
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


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        String newUnit = sharedPreferences.getString(UNIT_KEY,METRIC);

        if (!unit.equals(newUnit)){

            if (newUnit.equals(IMPERIAL)){
                distanceTravelled *= 0.621371192;
            } else if (newUnit.equals(METRIC)){
                distanceTravelled *= 1.609344;
            }

            unit = newUnit;
        }
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
        void updateClient(String distance);
    }
}
