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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.android.micros.sistemaandroidmicros.Clases.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Richard on 06/06/2017.
 */

public class ServiceChofer extends Service
{


    private static final String TAG = "TESTGPS";
    private LocationManager mLocationManager = null;
    String userId = "";
    Location ultimaLocalizacion = null;

    UserSessionManager session;

    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            ultimaLocalizacion = location;
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            Log.e(TAG, "onLocationChanged: " + location);

                JSONObject actualPosition = new JSONObject();

                try {

                    actualPosition.put("Latitud", latitude);
                    actualPosition.put("Longitud", longitude);

                    new Usuario.ActualizarPosicionPasajero().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userId, actualPosition.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
            if (ultimaLocalizacion != null) {
                onLocationChanged(ultimaLocalizacion);
            }
        }

        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
            new AsyncTaskServerPosition.StopPosition().execute(userId);

        }
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        Bundle extras = intent.getExtras();

        if (extras == null) {
            Log.d("Service", "null");
        } else {
            Log.d("Service", "not null");
            userId = (String) extras.get("usuarioId");
            Log.e(TAG, "usuario start: " + userId);
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
