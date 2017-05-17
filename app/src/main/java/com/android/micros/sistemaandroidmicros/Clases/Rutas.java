package com.android.micros.sistemaandroidmicros.Clases;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.micros.sistemaandroidmicros.InfoUserActivity;
import com.android.micros.sistemaandroidmicros.UserMapActivity;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Richard on 03/05/2017.
 */

public class Rutas
{
    public int idRuta;
    public String nombreRuta;
    public int tipoRuta;
    public int idInicio;
    public int idLinea;
    public ArrayList<Coordenada> listaCoordenadas;
    public ArrayList<Paradero> listaParaderos;



    public static ArrayList<Rutas> listaRutas = new ArrayList<>();

    public Rutas(){}

    public int getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(int idRuta) {
        this.idRuta = idRuta;
    }

    public String getNombreRuta() {
        return nombreRuta;
    }

    public void setNombreRuta(String nombreRuta) {
        this.nombreRuta = nombreRuta;
    }

    public int getTipoRuta() {
        return tipoRuta;
    }

    public void setTipoRuta(int tipoRuta) {
        this.tipoRuta = tipoRuta;
    }

    public int getIdInicio() {
        return idInicio;
    }

    public void setIdInicio(int idInicio) {
        this.idInicio = idInicio;
    }

    public int getIdLinea() {
        return idLinea;
    }

    public void setIdLinea(int idLinea) {
        this.idLinea = idLinea;
    }


    //Obtiene todas las rutas de la base de datos, usando el web service
    //pero aun no les asigna su lista de coordenadas y tampoco sus paraderos a cada una de ellas
     public static class ObtenerRutas extends AsyncTask<String, String, ArrayList<Rutas>>
     {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            @Override
            protected ArrayList<Rutas> doInBackground(String... params) {

                try {
                    URL url = new URL(params[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    InputStream stream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer resultado = new StringBuffer();

                    String linea = "";
                    while ((linea = reader.readLine()) != null) {
                        resultado.append(linea);
                    }

                    String resultadoString = resultado.toString();
                    JSONObject resultadoJSON = new JSONObject(resultadoString);

                    JSONArray listaJson = resultadoJSON.getJSONArray("value");
                    for (int i = 0; i < listaJson.length(); i++) {
                        JSONObject jsonobject = listaJson.getJSONObject(i);

                        Rutas ruta = new Rutas();
                        ruta.idRuta = jsonobject.getInt("Id");
                        ruta.nombreRuta = jsonobject.getString("Nombre");
                        ruta.tipoRuta = jsonobject.getInt("TipoDeRuta");
                        ruta.idInicio = jsonobject.getInt("InicioId");
                        ruta.idLinea = jsonobject.getInt("LineaId");

                        listaRutas.add(ruta);
                    }

                    return listaRutas;

                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<Rutas> result) {
               super.onPostExecute(result);

                Rutas.CargarCoordenadasRutas();
                Paradero.cargarParaderosPorRuta();

            }

            @Override
            protected void onPreExecute() {
                Log.i("ServicioRest", "onPreExecute");
            }
    }

    //Recorre la lista estatica de todas las rutas y por cada una de ellas llama al metodo ObtenerCoordenadas
    public static void CargarCoordenadasRutas()
    {
        for(int c = 0; c < listaRutas.size(); c++)
        {
            String idRuta = listaRutas.get(c).idRuta+"";
            //String URLCoordenadas = "http://localhost:8081/odata/Rutas("+IdLatLng+")/ListaCoordenadas";
            new ObtenerCoordenadas().execute(idRuta);
        }

    }

    //Por cada ruta que se envia a este metodo se le rellena su lista de coordenadas
    private static class ObtenerCoordenadas extends AsyncTask <String,String,String>
    {


        @Override
        protected String doInBackground(String... params) {


            String JsonResponse = "";
            HttpURLConnection urlConnection = null;
            String idRuta =params[0];

            Rutas rutaARellenar = null;

            for (int i = 0; i < listaRutas.size(); i++)
            {
                if(listaRutas.get(i).idRuta == Integer.parseInt(params[0]))
                {
                    rutaARellenar = listaRutas.get(i);
                }
            }

            BufferedReader reader = null;

            try {
                URL url = new URL("http://localhost:8081/odata/Rutas("+idRuta+")/ListaCoordenadas");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");


                InputStream inputStream = urlConnection.getInputStream();

                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {

                    return "Error 1";
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine = "";
                while ((inputLine = reader.readLine()) != null)
                {
                    buffer.append(inputLine);
                }

                if (buffer.length() == 0) {
                    return "Error 2";
                }
                JsonResponse = buffer.toString();

                JSONObject resultadoJSON = new JSONObject(JsonResponse);

                int idruta = Integer.parseInt(idRuta);
                JSONArray listaJson = resultadoJSON.getJSONArray("value");

                ArrayList<Coordenada> coordenadasRuta = new ArrayList<>();

                for (int i = 0; i < listaJson.length(); i++) {
                    JSONObject jsonobject = listaJson.getJSONObject(i);

                    Coordenada latLng = new Coordenada();
                    latLng.idRuta = idruta;
                    latLng.latitud = jsonobject.getDouble("Latitud");
                    latLng.longitud = jsonobject.getDouble("Longitud");

                    coordenadasRuta.add(latLng);

                }

                rutaARellenar.listaCoordenadas = coordenadasRuta;

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

        protected void onPostExecute(Boolean result)
        {

        }
    }

    //Se envia la id de la linea y retorna una lista de objetos ruta asociadas a ella
    public ArrayList<Rutas> obtenerRutasPorLinea(int id)
    {
        ArrayList<Rutas> rutas = new ArrayList<>();

        for(int r = 0; r < listaRutas.size(); r++)
        {
            if(id == listaRutas.get(r).idLinea)
            {
               rutas.add(listaRutas.get(r));
            }
        }
        return rutas;
    }

    public static Rutas BuscarRutaPorId(int _idRuta)
    {
        for (int i = 0; i < listaRutas.size(); i++)
        {
            if(listaRutas.get(i).idRuta == _idRuta)
            {
                return listaRutas.get(i);
            }
        }

        return null;
    }



}


