package com.android.micros.sistemaandroidmicros;

import android.app.Service;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.android.micros.sistemaandroidmicros.Clases.ActivityController;
import com.android.micros.sistemaandroidmicros.Clases.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import static com.android.micros.sistemaandroidmicros.Clases.Usuario.ip;
import static com.android.micros.sistemaandroidmicros.UserMapActivity.idLineaSeleccionada;

public class LineaFusionService extends Service {

    String TAG = "LineaFusionService";
    static String userId;

    public LineaFusionService() {
    }

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
            obtenerTodoPrueba();
            Log.e(TAG, "usuario start: " + userId);
        }
        return START_NOT_STICKY;
    }
    @Override
    public void onCreate()
    {
        Log.e(TAG, "onCreate");
        //obtenerTodoPrueba();
    }

    public static void obtenerTodoPrueba() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {

                Log.e("IDLINEAXDXDXD", idLineaSeleccionada+"");
                new Usuario.ObtenerDatosLineaFusion().execute(userId, idLineaSeleccionada+"");

            }
        }, 0, 1000);
    }
}
