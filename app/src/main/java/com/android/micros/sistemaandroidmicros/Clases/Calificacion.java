package com.android.micros.sistemaandroidmicros.Clases;

import android.os.AsyncTask;

import com.android.micros.sistemaandroidmicros.RegisterStep3Activity;
import com.android.micros.sistemaandroidmicros.UserMapActivity;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.android.micros.sistemaandroidmicros.Clases.Usuario.ip;

/**
 * Created by Richard on 02/07/2017.
 */

public class Calificacion
{
    public static class AgregarCalificacion extends AsyncTask<String,String,String>
    {

        @Override
        protected String doInBackground(String... parametros) {

            String JsonResponse = "";
            HttpURLConnection urlConnection = null;
            String microId = parametros[0];
            String calificacion = parametros[1];
            OutputStream os = null;
            InputStream inputStream = null;

            BufferedReader reader = null;

            try {
                URL url = new URL(ip+"/odata/Micros("+microId+")/NuevaCalificacion");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");

                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

                urlConnection.connect();

                os = new BufferedOutputStream(urlConnection.getOutputStream());
                os.write(calificacion.getBytes());
                os.flush();

                os.close();
                urlConnection.disconnect();

                return "";
                /*
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {

                    return "Error 1";
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                */

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String result)
        {
            try
            {
                UserMapActivity cMap = (UserMapActivity)ActivityController.activiyAbiertaActual;
                cMap.RemoverFragmentCalificacion();

            }
            catch (Exception e)
            {

            }
        }
    }
}
