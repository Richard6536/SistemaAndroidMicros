package com.android.micros.sistemaandroidmicros.Clases;

import android.os.AsyncTask;
import android.util.Log;

import com.android.micros.sistemaandroidmicros.ChoferMapActivity;
import com.android.micros.sistemaandroidmicros.FirstTimeActivity;
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
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Richard on 12/05/2017.
 */

public class Paradero
{
    public int id;
    public double latitud;
    public double longitud;
    public int rutaId;

    public static ArrayList<Paradero> paraderos = new ArrayList<>();

    public Paradero() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }


    //Se guardan todos los paraderos en una lista estática (paraderos)
    public static class ObtenerParaderos extends AsyncTask<String,String,ArrayList<Paradero>>
    {


        @Override
        protected ArrayList<Paradero> doInBackground(String... parametros) {


            String JsonResponse = "";
            HttpURLConnection urlConnection = null;

            BufferedReader reader = null;

            try {
                URL url = new URL(parametros[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();


                InputStream inputStream = urlConnection.getInputStream();

                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {


                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine = "";
                while ((inputLine = reader.readLine()) != null)
                {
                    buffer.append(inputLine);
                }

                JsonResponse = buffer.toString();

                JSONObject resultadoJSON = new JSONObject(JsonResponse);
                JSONArray listaJson = resultadoJSON.getJSONArray("value");

                for (int i = 0; i < listaJson.length(); i++) {
                    JSONObject jsonobject = listaJson.getJSONObject(i);

                    Paradero paradero = new Paradero();
                    paradero.id = jsonobject.getInt("Id");
                    paradero.latitud = jsonobject.getDouble("Latitud");
                    paradero.longitud = jsonobject.getDouble("Longitud");
                    paradero.rutaId = jsonobject.getInt("RutaId");
                    paraderos.add(paradero);

                }

                return paraderos;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
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
            }
            return null;

        }
        @Override
        protected void onPostExecute(ArrayList<Paradero> result)
        {
            FirstTimeActivity ft = (FirstTimeActivity)ActivityController.activiyAbiertaActual;
            ft.RecibirCargaDeParaderos();
        }
    }

    public void CargarParaderosPorRutas()
    {
        for(int i = 0; i < Rutas.listaRutas.size(); i++)
        {
            Rutas rutaActual = Rutas.listaRutas.get(i);
            rutaActual.listaParaderos = new ArrayList<>();

            for(Paradero p : paraderos)
            {
                if(p.rutaId == rutaActual.idRuta)
                {
                    rutaActual.listaParaderos.add(p);
                }
            }
        }
    }

    public static class AsociarParaderoChofer extends AsyncTask<String,String,String>
    {
        @Override
        protected String doInBackground(String... params) {


            HttpURLConnection urlConnection = null;

            //Parámetros
            String idMicro =params[0];
            String idParadero = params[1];

            BufferedReader reader = null;
            OutputStream os = null;
            InputStream inputStream = null;

            try {
                URL url = new URL("http://localhost:8081/odata/Micros("+idMicro+")/SeleccionarParadero");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");


                urlConnection.connect();

                os = new BufferedOutputStream(urlConnection.getOutputStream());
                os.write(idParadero.getBytes());
                os.flush();


                JSONObject resultadoJSON = new JSONObject("");

                return "";

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
        protected void onPostExecute(String p)
        {

        }

    }

    public static class MicrosQueSeleccionaronParadero extends AsyncTask<String,String,JSONObject>
    {
        @Override
        protected JSONObject doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            String idParadero =params[0];
            BufferedReader reader = null;
            OutputStream os = null;
            InputStream inputStream = null;

            try {
                URL url = new URL("http://localhost:8081/odata/Paraderos("+idParadero+")/MicroParaderoMasCercano");
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
                JSONObject microParadero = new JSONObject(value);

                return microParadero;

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
        protected void onPostExecute(JSONObject microParadero)
        {

            UserMapActivity uMap = (UserMapActivity)ActivityController.activiyAbiertaActual;
            uMap.obtenerMicrosDelParadero(microParadero);
        }

    }

    public static class ObtenerMiParadero extends AsyncTask<String,String,JSONObject>
    {
        @Override
        protected JSONObject doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            String idMicro =params[0];
            BufferedReader reader = null;
            OutputStream os = null;
            InputStream inputStream = null;

            try {
                URL url = new URL("http://localhost:8081/odata/Micros("+idMicro+")/ObtenerMiParadero");
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
                JSONObject miParadero = new JSONObject(value);

                return miParadero;

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
        protected void onPostExecute(JSONObject miParadero)
        {

            ChoferMapActivity cMap = (ChoferMapActivity)ActivityController.activiyAbiertaActual;
            cMap.recibirMiParadero(miParadero);
        }

    }

    public static class UsuariosQueSeleccionaronParadero extends AsyncTask<String,String,JSONArray>
    {
        @Override
        protected JSONArray doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            String idParadero =params[0];
            BufferedReader reader = null;
            OutputStream os = null;
            InputStream inputStream = null;

            try {
                URL url = new URL("http://localhost:8081/odata/Paraderos("+idParadero+")/UsuariosQueSeleccionaron");
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
                JSONObject usuarios = new JSONObject(value);
                JSONArray usuariosArray = usuarios.getJSONArray("value");

                return usuariosArray;

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
        protected void onPostExecute(JSONArray usuarios)
        {

            ChoferMapActivity cMap = (ChoferMapActivity)ActivityController.activiyAbiertaActual;
            cMap.recibirUsuariosParadero(usuarios);
        }

    }
}
//ASDF