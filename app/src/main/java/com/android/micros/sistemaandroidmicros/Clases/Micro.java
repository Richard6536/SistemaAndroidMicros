package com.android.micros.sistemaandroidmicros.Clases;

import android.os.AsyncTask;
import android.util.Log;

import com.android.micros.sistemaandroidmicros.ChoferMapActivity;
import com.android.micros.sistemaandroidmicros.LoginActivity;
import com.android.micros.sistemaandroidmicros.RegisterStep2Activity;
import com.android.micros.sistemaandroidmicros.UserMapActivity;

import org.json.JSONArray;
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
import java.util.ArrayList;

/**
 * Created by Richard on 17/05/2017.
 */

public class Micro {


    public int id;
    public String patente;
    public float calificacion;
    public int numeroCalificaciones;
    public Integer lineaId;
    //public Integer microChoferId;
    //public Integer microParaderoId;

    public Micro()
    {

    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(float calificacion) {
        this.calificacion = calificacion;
    }

    public int getNumeroCalificaciones() {
        return numeroCalificaciones;
    }

    public void setNumeroCalificaciones(int numeroCalificaciones) {
        this.numeroCalificaciones = numeroCalificaciones;
    }

    public Integer getLineaId() {
        return lineaId;
    }

    public void setLineaId(Integer lineaId) {
        this.lineaId = lineaId;
    }

    public static class ObtenerMicroDeChofer extends AsyncTask<String,String,JSONObject>
    {
        @Override
        protected JSONObject doInBackground(String... params) {


            HttpURLConnection urlConnection = null;
            String idUsuario =params[0];
            BufferedReader reader = null;
            OutputStream os = null;
            InputStream inputStream = null;

            try {
                //"http://localhost:8081/odata/Usuarios("+idUsuario+")/ObtenerMicro"
                URL url = new URL("http://localhost:8081/odata/Usuarios("+idUsuario+")/ObtenerMicro");
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



                return resultadoJSON;

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
        protected void onPostExecute(JSONObject microJson)
        {
            Micro micro = new Micro();
            try {
                micro.id = microJson.getInt("Id");
                micro.patente = microJson.getString("Patente");
                String calificacion = microJson.getString("Calificacion");
                micro.calificacion = Float.valueOf(calificacion);
                micro.numeroCalificaciones = microJson.getInt("NumeroCalificaciones");
                try {
                    micro.lineaId = microJson.getInt("LineaId");
                }
                catch (Exception ex)
                {
                    micro.lineaId = null;
                }

                //micro.microParaderoId = microJson.getInt("MicroParaderoId");
                //micro.microChoferId = microJson.getInt("MicroChoferId");

                ChoferMapActivity cma = (ChoferMapActivity)ActivityController.activiyAbiertaActual;
                cma.validarLinea(micro);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public static class ObtenerMicrosPorLinea extends AsyncTask<String,String,JSONArray>
    {
        @Override
        protected JSONArray doInBackground(String... params) {


            HttpURLConnection urlConnection = null;
            String idLinea =params[0];
            BufferedReader reader = null;
            OutputStream os = null;
            InputStream inputStream = null;

            try {
                URL url = new URL("http://localhost:8081/odata/Lineas("+idLinea+")/ObtenerChoferes");
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

                JSONArray micros = resultadoJSON.getJSONArray("value");


                return micros;

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
        protected void onPostExecute(JSONArray micros)
        {
            UserMapActivity uMap = (UserMapActivity)ActivityController.activiyAbiertaActual;
            uMap.agregarMicros(micros);
        }

    }


}
