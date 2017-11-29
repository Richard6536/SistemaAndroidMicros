package com.android.micros.sistemaandroidmicros;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.android.micros.sistemaandroidmicros.Clases.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import static com.android.micros.sistemaandroidmicros.ChoferMapActivity.ultimaLocalizacion;

public class ServicePositionChofer extends Service {
    private static final String TAG = "TESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 500;
    private static final float LOCATION_DISTANCE = 1;
    String userId="";


    UserSessionManager session;

    private class LocationListener implements android.location.LocationListener{

        Location mLastLocation;
        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }
        @Override
        public void onLocationChanged(Location location)
        {
            ultimaLocalizacion = location;

            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
        }


        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
            new AsyncTaskServerPosition.StopPosition().execute(userId);

            //Abre las configuraciones de GPS del teléfono
            //Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            //startActivity(intent);
        }
        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
            if(ultimaLocalizacion != null)
            {
                onLocationChanged(ultimaLocalizacion);
            }
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }
    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        Bundle extras = intent.getExtras();

        if(extras == null) {
            Log.d("Service","null");
        } else {
            Log.d("Service","not null");
            userId = (String) extras.get("usuarioId");
            Log.e(TAG, "usuario start: " + userId);
        }

        return START_NOT_STICKY;
    }
    @Override
    public void onCreate()
    {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "No puede solicitar la actualización de la ubicacion", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "El proveedor de gps no existe, " + ex.getMessage());
        }

        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "No puede solicitar la actualización de la ubicacion", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "El proveedor de gps no existe " + ex.getMessage());
        }
        //mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,0, );
    }
    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }
    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
