package com.android.micros.sistemaandroidmicros;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Richard on 31/05/2017.
 */

public class AsyncTaskServerPosition
{

    private static final String TAG = "ASYNCTASK";
    private static UserSessionManager session;


    public static class SendToServer extends AsyncTask<String, String, String>
    {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... coordenadas) {

            try {

                HashMap<String, String> user = session.obtenerRolyId();
                String usuarioId = user.get(UserSessionManager.KEY_ID);

                Log.d(TAG, "Location: " +coordenadas);
                HttpURLConnection urlConnection = null;
                String posicionActual = coordenadas[0];
                BufferedReader reader = null;
                OutputStream os = null;
                InputStream inputStream = null;

                URL url = new URL("http://localhost:8081/odata/Usuarios("+usuarioId+")/ActualizarPosicion");
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

            return "call";
        }

        @Override
        protected void onPostExecute(String result) {
        }

    }
}
