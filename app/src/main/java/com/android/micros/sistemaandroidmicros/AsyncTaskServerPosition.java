package com.android.micros.sistemaandroidmicros;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.micros.sistemaandroidmicros.Clases.ActivityController;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import static com.android.micros.sistemaandroidmicros.Clases.Usuario.ip;

/**
 * Created by Richard on 31/05/2017.
 */

public class AsyncTaskServerPosition
{

    private static final String TAG = "ASYNCTASK";


    public static class SendPosition extends AsyncTask<String, String, String>
    {
        String latLng;
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "Asynctask iniciado");

        }

        @Override
        protected String doInBackground(String... parametros) {

            try {
                String usuarioId = parametros[1];
                String posicionActual = parametros[0];

                latLng = posicionActual;

                Log.d(TAG, "Coordenadas: " + posicionActual);
                Log.d(TAG, "Usuario: " + usuarioId);

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                OutputStream os = null;
                InputStream inputStream = null;

                URL url = new URL(ip+"/Usuarios("+usuarioId+")/ActualizarPosicion");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

                urlConnection.connect();

                os = new BufferedOutputStream(urlConnection.getOutputStream());
                os.write(posicionActual.getBytes());

                os.flush();
                os.close();

                int serverResponse = urlConnection.getResponseCode();
                String serverMsg = urlConnection.getResponseMessage();
                urlConnection.disconnect();

                Log.d(TAG, "Code: " + serverResponse + " - Menssage: " + serverMsg);


            } catch (Exception e) {
                Log.i("error", e.toString());
            }

            return latLng;
        }

        @Override
        protected void onPostExecute(String latLng)
        {
            try
            {
                String coor = latLng;
            }
            catch (Exception e)
            {

            }
        }

    }

    public static class StopPosition extends AsyncTask<String, String, String>
    {

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "Asynctask funcionando");

        }

        @Override
        protected String doInBackground(String... parametros) {

            try {

                String usuarioId = parametros[0];

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                OutputStream os = null;
                InputStream inputStream = null;

                URL url = new URL(ip+"/Usuarios("+usuarioId+")/DetenerPosicionUpdate");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

                urlConnection.connect();

                int serverResponse = urlConnection.getResponseCode();
                String serverMsg = urlConnection.getResponseMessage();
                urlConnection.disconnect();

                Log.d(TAG, "Code: " + serverResponse + " - Menssage: " + serverMsg);


            } catch (Exception e) {
                Log.i("error", e.toString());
            }

            return "call";
        }

        @Override
        protected void onPostExecute(String result)
        {

        }

    }
}
