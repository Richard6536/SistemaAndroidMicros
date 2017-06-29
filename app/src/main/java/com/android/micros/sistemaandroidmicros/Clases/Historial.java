package com.android.micros.sistemaandroidmicros.Clases;

import android.os.AsyncTask;
import android.util.Log;

import com.android.micros.sistemaandroidmicros.ChoferMapActivity;
import com.android.micros.sistemaandroidmicros.HistorialActivity;
import com.android.micros.sistemaandroidmicros.HistorialBaseFragment;
import com.android.micros.sistemaandroidmicros.UserMapActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Richard on 25/06/2017.
 */

public class Historial {

    public static class ObtenerHistorialDiario extends AsyncTask<String,String,JSONArray>
    {
        @Override
        protected JSONArray doInBackground(String... params) {


            HttpURLConnection urlConnection = null;
            String idMicro =params[0];
            BufferedReader reader = null;
            OutputStream os = null;
            InputStream inputStream = null;

            try {
                URL url = new URL("http://localhost:8081/odata/Micros("+idMicro+")/ObtenerHistorialesDiarios");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");


                urlConnection.connect();

                /*
                os = new BufferedOutputStream(urlConnection.getOutputStream());
                os.write(JsonData.getBytes());
                os.flush();
                */

                inputStream = urlConnection.getInputStream();

                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {

                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine = "";
                while ((inputLine = reader.readLine()) != null)
                {
                    buffer.append(inputLine);
                }

                String value = buffer.toString();
                JSONObject resultadoJSON = new JSONObject(value);

                JSONArray historialDirario = resultadoJSON.getJSONArray("value");


                return historialDirario;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Mensaje2", "Error closing stream", e);
                    }
                }
                if(os != null)
                {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray historialDirario)
        {
            try {

                HistorialActivity ha = (HistorialActivity)ActivityController.activiyAbiertaActual;
                ha.listarHistorial(historialDirario);

            }
            catch (Exception e)
            {

            }
        }

    }

    public static class ObtenerHistorialIdaVuelta extends AsyncTask<String,String,JSONArray>
    {
        @Override
        protected JSONArray doInBackground(String... params) {


            HttpURLConnection urlConnection = null;
            String idHistorial =params[0];
            BufferedReader reader = null;
            OutputStream os = null;
            InputStream inputStream = null;

            try {
                URL url = new URL("odata/HistorialesDiarios("+idHistorial+")/ObtenerHistorialesIdaVuelta");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");


                urlConnection.connect();

                /*
                os = new BufferedOutputStream(urlConnection.getOutputStream());
                os.write(JsonData.getBytes());
                os.flush();
                */

                inputStream = urlConnection.getInputStream();

                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {

                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine = "";
                while ((inputLine = reader.readLine()) != null)
                {
                    buffer.append(inputLine);
                }

                String value = buffer.toString();
                JSONObject resultadoJSON = new JSONObject(value);

                JSONArray historialIdaVuelta = resultadoJSON.getJSONArray("value");


                return historialIdaVuelta;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Mensaje2", "Error closing stream", e);
                    }
                }
                if(os != null)
                {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray historialIdaVuelta)
        {
            try {

                HistorialBaseFragment hbf = (HistorialBaseFragment)FragmentController.FragmentAbierto;
                hbf.cargarHistorialIdaVuelta();

            }
            catch (Exception e)
            {

            }
        }

    }

}
