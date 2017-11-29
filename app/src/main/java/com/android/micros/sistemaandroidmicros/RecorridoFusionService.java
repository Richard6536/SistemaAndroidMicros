package com.android.micros.sistemaandroidmicros;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.android.micros.sistemaandroidmicros.Clases.Usuario;

import java.util.Timer;
import java.util.TimerTask;

import static com.android.micros.sistemaandroidmicros.UserMapActivity.idLineaSeleccionada;

public class RecorridoFusionService extends Service {
    public RecorridoFusionService() {
    }

    String TAG = "LineaFusionService";

    static String userId;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

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

                Log.e("IDUSUARIOXDXDDX",userId);
                //new Usuario.ObtenerDatosRecorridoFusion().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userId);

            }
        }, 0, 1000);
    }
}
