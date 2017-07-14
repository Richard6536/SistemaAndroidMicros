package com.android.micros.sistemaandroidmicros.Clases;

import android.os.AsyncTask;
import android.util.Log;

import com.android.micros.sistemaandroidmicros.ChoferMapActivity;
import com.android.micros.sistemaandroidmicros.FirstTimeActivity;
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
import java.util.concurrent.ExecutionException;

import static com.android.micros.sistemaandroidmicros.Clases.Usuario.ip;

/**
 * Created by Richard on 17/05/2017.
 */

public class Micro {


    public int id;
    public String patente;
    public float calificacion;
    public int numeroCalificaciones;
    public Integer lineaId;
    public Integer sigCoordenadaId;

    public Integer getSigCoordenadaId() {
        return sigCoordenadaId;
    }

    public void setSigCoordenadaId(Integer sigCoordenadaId) {
        this.sigCoordenadaId = sigCoordenadaId;
    }
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
                URL url = new URL(ip+"/odata/Usuarios("+idUsuario+")/ObtenerMicro");
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
            try {
            Micro micro = new Micro();

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

    public static class ObtenerMiMicroConstanteMente extends AsyncTask<String,String,JSONObject>
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
                URL url = new URL(ip+"/odata/Usuarios("+idUsuario+")/ObtenerMicro");
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
            try {

                boolean esNull = false;

                Micro micro = new Micro();

                micro.id = microJson.getInt("Id");
                micro.patente = microJson.getString("Patente");
                String calificacion = microJson.getString("Calificacion");
                micro.calificacion = Float.valueOf(calificacion);
                micro.numeroCalificaciones = microJson.getInt("NumeroCalificaciones");


                try {
                    micro.sigCoordenadaId = microJson.getInt("SiguienteVerticeId");
                    esNull = false;
                }
                catch (Exception ex)
                {
                    //Esconder boton de iniciar Recorrido
                    esNull = true;
                }

                ChoferMapActivity cma = (ChoferMapActivity)ActivityController.activiyAbiertaActual;
                cma.validarSigCoord(esNull);

            } catch (JSONException e) {
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

            Log.e("LINEID", idLinea);

            try {
                URL url = new URL(ip+"/odata/Lineas("+idLinea+")/ObtenerChoferes");
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
            try {


                    UserMapActivity uMap = (UserMapActivity) ActivityController.activiyAbiertaActual;
                    uMap.agregarMicros(micros);
                }
                catch (Exception e)
                {

                }
        }

    }

    public static class CambiarPosicion extends AsyncTask<String,String,JSONObject>
    {
        @Override
        protected JSONObject doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            String idUsuario =params[0];
            BufferedReader reader = null;
            OutputStream os = null;
            InputStream inputStream = null;

            Log.e("CAMBIARPOSICIONXD", idUsuario);
            try {

                URL url = new URL(ip+"/odata/Usuarios("+idUsuario+")/ObtenerPosicion");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.connect();

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
                JSONObject posicion = new JSONObject(value);


                return posicion;

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
        protected void onPostExecute(JSONObject posicion)
        {
            try
            {
                String activityActual = ActivityController.activiyAbiertaActual.getClass().getSimpleName();

                if(activityActual.equals("UserMapActivity"))
                {
                    UserMapActivity uMap = (UserMapActivity) ActivityController.activiyAbiertaActual;
                    uMap.recibirPosicion(posicion);
                }
                else if(activityActual.equals("ChoferMapActivity"))
                {
                    ChoferMapActivity cMap = (ChoferMapActivity) ActivityController.activiyAbiertaActual;
                    cMap.recibirPosicion(posicion);
                }
            }
            catch (Exception e)
            {

            }
        }

    }

    public static class IniciarRecorrido extends AsyncTask<String,String,JSONObject>
    {
        @Override
        protected JSONObject doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            String idMicro =params[0];
            BufferedReader reader = null;
            OutputStream os = null;
            InputStream inputStream = null;

            Log.e("190854531", idMicro);

            try {
                URL url = new URL(ip+"/odata/Micros("+idMicro+")/IniciarRecorrido");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.connect();

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
                JSONObject posicion = new JSONObject(value);


                return posicion;

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
        protected void onPostExecute(JSONObject posicion)
        {

        }

    }

    public static class CambiarMiPosicion extends AsyncTask<String,String,JSONObject>
    {

        @Override
        protected JSONObject doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            String idUsuario =params[0];
            BufferedReader reader = null;
            OutputStream os = null;
            InputStream inputStream = null;

            Log.e("OBTENERMIPOSICIONASDF", idUsuario);

            try {
                URL url = new URL(ip+"/odata/Usuarios("+idUsuario+")/ObtenerPosicion");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.connect();

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
                JSONObject posicion = new JSONObject(value);


                return posicion;

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
        protected void onPostExecute(JSONObject posicion)
        {
            try
            {
                ChoferMapActivity cMap = (ChoferMapActivity)ActivityController.activiyAbiertaActual;
                cMap.recibirPosicion(posicion);
            }
            catch (Exception e)
            {

            }

        }

    }

}
