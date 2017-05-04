package com.android.micros.sistemaandroidmicros.Clases;

import android.os.AsyncTask;
import android.util.Log;

import com.android.micros.sistemaandroidmicros.InfoUserActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

    public ArrayList<Rutas> listaRutas = new ArrayList<Rutas>();
    private String URLRutas = "http://localhost:8081/odata/Rutas";

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

    public void obtenerTodasLasRutas()
    {
        new ObtenerRutas().execute(URLRutas);
    }

     private class ObtenerRutas extends AsyncTask<String, String, String>
     {

            HttpURLConnection connection = null;
            BufferedReader reader = null;


            @Override
            protected String doInBackground(String... params) {
                int id;
                String nombre;
                int tipo;
                int idIn;
                int idLi;

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

                        id = jsonobject.getInt("Id");
                        nombre = jsonobject.getString("Nombre");
                        tipo = jsonobject.getInt("TipoDeRuta");
                        idIn = jsonobject.getInt("InicioId");
                        idLi = jsonobject.getInt("LineaId");

                        Rutas ruta = new Rutas();
                        ruta.idRuta = id;
                        ruta.nombreRuta = nombre;
                        ruta.tipoRuta = tipo;
                        ruta.idInicio = idIn;
                        ruta.idLinea = idLi;

                        listaRutas.add(ruta);

                    }

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
            protected void onPostExecute(String result) {
                Log.i("ServicioRest", "onPostExecute");

                // JSONObject resultadoJSON = null;
                //LISTA
                //JSONArray lista = resultadoJSON.getJSONArray("movies");
                //JSONObject finalObject = lista.getJSONObject(0);

            }

            @Override
            protected void onPreExecute() {
                Log.i("ServicioRest", "onPreExecute");
            }
    }



}
