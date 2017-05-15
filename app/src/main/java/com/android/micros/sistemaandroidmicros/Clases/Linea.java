package com.android.micros.sistemaandroidmicros.Clases;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Richard on 04/05/2017.
 */

public class Linea {

     public int idLinea;
    public String nombreLinea;
    public int idRutaIda;
    public int idRutaVuelta;
    public static ArrayList<Linea> listaLineas = new ArrayList<Linea>();
    String URL = "http://localhost:8081/odata/Lineas";

    public Linea(){}

    public int getIdLineas() {
        return idLinea;
    }

    public void setIdLineas(int idLineas) {
        this.idLinea = idLineas;
    }

    public String getNombreLineas() {
        return nombreLinea;
    }

    public void setNombreLineas(String nombreLineas) {
        this.nombreLinea = nombreLineas;
    }

    public int getIdRutaIda() {
        return idRutaIda;
    }

    public void setIdRutaIda(int idRutaIda) {
        this.idRutaIda = idRutaIda;
    }

    public int getIdRutaVuelta() {
        return idRutaVuelta;
    }

    public void setIdRutaVuelta(int idRutaVuelta) {
        this.idRutaVuelta = idRutaVuelta;
    }


    public static class ObtenerLineas extends AsyncTask<String, String, ArrayList<Linea>>
    {

        HttpURLConnection connection = null;
        BufferedReader reader = null;


        @Override
        protected ArrayList<Linea> doInBackground(String... params) {
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

                    Linea line = new Linea();
                    line.idLinea = jsonobject.getInt("Id");
                    line.nombreLinea = jsonobject.getString("Nombre");
                    line.idRutaIda = jsonobject.getInt("RutaIdaId");
                    line.idRutaVuelta = jsonobject.getInt("RutaVueltaId");

                    listaLineas.add(line);
                }

                return listaLineas;


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
        protected void onPostExecute(ArrayList<Linea> result) {
            super.onPostExecute(result);

        }

        @Override
        protected void onPreExecute() {
            Log.i("ServicioRest", "onPreExecute");
        }
    }

    public ArrayList<String> obtenerNombres(){

        Linea linea = null;
        ArrayList<String> nombres = new ArrayList<String>();

        for(int i = 0; i < listaLineas.size(); i++){

            linea = listaLineas.get(i);
            nombres.add(linea.nombreLinea);
        }

        return nombres;
    }

    //Se recive el nombre de la linea y busca su id
    public int buscarLineaSpinner(String nombre)
    {
        String nombreLinea = nombre;
        int idLineaSelecionada = 0;
        Linea lineaSeleccionada = null;
        for(int i = 0; i<listaLineas.size(); i++)
        {
            lineaSeleccionada = listaLineas.get(i);
            if(lineaSeleccionada.nombreLinea == nombreLinea)
            {
                idLineaSelecionada = lineaSeleccionada.idLinea;
                break;
            }
        }

        return idLineaSelecionada;
    }

    //Recive el id de la linea y retorna la linea completa
    public static Linea BuscarLineaPorId(int id)
    {
        for(int i = 0; i < listaLineas.size(); i++)
        {
            if(id == listaLineas.get(i).idLinea)
            {
                return listaLineas.get(i);
            }
        }
        return null;
    }

}
