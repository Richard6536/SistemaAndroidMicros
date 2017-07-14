package com.android.micros.sistemaandroidmicros.Clases;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.android.micros.sistemaandroidmicros.ChoferMapActivity;
import com.android.micros.sistemaandroidmicros.LoginActivity;
import com.android.micros.sistemaandroidmicros.RegisterStep2Activity;
import com.android.micros.sistemaandroidmicros.RegisterStep3Activity;
import com.android.micros.sistemaandroidmicros.UserMapActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Richard on 09/05/2017.
 */

public class Usuario {

    public int id;
    public String nombre;   //Mínimo: 3, Máximo 25
    public String email;    // Mínimo 3, Máximo 50
    public int rol;         //Enviar rol con 0

    public Usuario() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRol() {
        return rol;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }


    public static String ip = "http://192.168.8.103:8080";

    //Verificar si existe el emai al crear un usuario
    public static class ValidarEmail extends AsyncTask<String,Void,Boolean>
    {
        @Override
        protected Boolean doInBackground(String... params) {


            HttpURLConnection urlConnection = null;
            String JsonData =params[0];
            BufferedReader reader = null;
            OutputStream os = null;
            InputStream inputStream = null;
            Boolean boolea = false;

            try {
                URL url = new URL(ip+"/odata/Usuarios/ExisteMail");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");


                urlConnection.connect();

                os = new BufferedOutputStream(urlConnection.getOutputStream());
                os.write(JsonData.getBytes());

                os.flush();
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

                boolea = Boolean.parseBoolean(resultadoJSON.getString("value"));

                return boolea;

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
        protected void onPostExecute(Boolean resultBool)
        {
            try
            {
                RegisterStep2Activity reg = (RegisterStep2Activity)ActivityController.activiyAbiertaActual;
                reg.resultadoValidacion(resultBool);
            }
            catch (Exception e)
            {

            }
        }

    }

    //Agregar Usuario a la base de datos
    public static class CrearUsuario extends AsyncTask <String,String,String>
    {

        @Override
        protected String doInBackground(String... usuario) {

            String JsonResponse = "";
            HttpURLConnection urlConnection = null;
            String JsonUsuario = usuario[0];
            OutputStream os = null;
            InputStream inputStream = null;

            BufferedReader reader = null;

            try {
                URL url = new URL(ip+"/odata/Usuarios");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");

                urlConnection.setFixedLengthStreamingMode(JsonUsuario.getBytes().length);

                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

                urlConnection.connect();

                os = new BufferedOutputStream(urlConnection.getOutputStream());
                os.write(JsonUsuario.getBytes());
                os.flush();

                inputStream = urlConnection.getInputStream();
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
            finally {
                //clean up
                try {
                    os.close();
                    inputStream.close();
                    urlConnection.disconnect();

                    return inputStream.toString();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(String result)
        {
            try
            {
                RegisterStep3Activity reg = (RegisterStep3Activity)ActivityController.activiyAbiertaActual;
                reg.fin();
            }
            catch (Exception e)
            {

            }
        }
    }

    //Validar usuario al iniciar sesión
    public class ValidarUsuario extends AsyncTask <String,String,JSONObject>
    {

        @Override
        protected JSONObject doInBackground(String... user) {

            String JsonResponse = "";
            HttpURLConnection urlConnection = null;
            String JsonUsuario = user[0];
            OutputStream outputstream = null;
            InputStream inputStream = null;

            BufferedReader reader = null;

            Log.e("usuarioU", JsonUsuario);

            try {
                URL url = new URL(ip+"/odata/Usuarios/EsValido");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");

                urlConnection.setFixedLengthStreamingMode(JsonUsuario.getBytes().length);

                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

                urlConnection.connect();

                //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                //writer.write(JsonUsuario);
                //writer.close();

                outputstream = new BufferedOutputStream(urlConnection.getOutputStream());
                outputstream.write(JsonUsuario.getBytes());
                outputstream.flush();

                try
                {
                    inputStream = urlConnection.getInputStream();
                }
                catch(Exception e)
                {

                }

                 StringBuffer buffer = new StringBuffer();

                 reader = new BufferedReader(new InputStreamReader(inputStream));

                 String inputLine = "";
                 while ((inputLine = reader.readLine()) != null)
                 {
                     buffer.append(inputLine);
                 }

                 String value = buffer.toString();
                 JSONObject usuarioJson = new JSONObject(value);


                return usuarioJson;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                //clean up
                try {
                    outputstream.close();
                    inputStream.close();
                    urlConnection.disconnect();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(JSONObject usuarioJSON)
        {

                Usuario usuario = new Usuario();
            try {
                usuario.id = usuarioJSON.getInt("Id");
                usuario.nombre = usuarioJSON.getString("Nombre");
                usuario.email = usuarioJSON.getString("Email");
                usuario.rol = usuarioJSON.getInt("Rol");

                LoginActivity login = (LoginActivity)ActivityController.activiyAbiertaActual;
                login.recibirValidacion(usuario);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static class EditarDatos extends AsyncTask<String,Void,Boolean>
    {
        @Override
        protected Boolean doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            String JsonData =params[0];
            BufferedReader reader = null;
            OutputStream os = null;
            InputStream inputStream = null;
            Boolean boolea = false;

            try {
                URL url = new URL(ip+"/odata/Usuarios(5)/EditarDatos");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");


                urlConnection.connect();

                os = new BufferedOutputStream(urlConnection.getOutputStream());
                os.write(JsonData.getBytes());

                os.flush();
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

                boolea = Boolean.parseBoolean(resultadoJSON.getString("value"));

                return boolea;

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
        protected void onPostExecute(Boolean resultBool)
        {
            try
            {
                RegisterStep2Activity reg = (RegisterStep2Activity)ActivityController.activiyAbiertaActual;
                reg.resultadoValidacion(resultBool);
            }
            catch (Exception e)
            {

            }
        }

    }

    public static class ObtenerDatosLineaFusion extends AsyncTask<String,String,JSONObject>
    {
        @Override
        protected JSONObject doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            //Parámetros
            String idUsuario =params[0];
            String idLinea = params[1];
            BufferedReader reader = null;
            OutputStream os = null;
            InputStream inputStream = null;

            Log.e("LINEAFUSION ",idUsuario + " "+ idLinea);

            try {

                JSONObject idLineaJSON = new JSONObject();
                try {

                    idLineaJSON.put("IdLinea",idLinea);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                URL url = new URL(ip+"/odata/Usuarios("+idUsuario+")/ObtenerDatosLineaFusion");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

                urlConnection.connect();

                os = new BufferedOutputStream(urlConnection.getOutputStream());
                os.write(idLineaJSON.toString().getBytes());
                os.flush();

                inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine = "";
                while ((inputLine = reader.readLine()) != null)
                {
                    buffer.append(inputLine);
                }

                String value = buffer.toString();

                JSONObject parametros = new JSONObject(value);

                // MicroParaderoCercano
                //MicroAboradada
                //Choferes : Lista
                //IdLineaChoferes

                return parametros;

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
        protected void onPostExecute(JSONObject parametros)
        {
            try
            {
                UserMapActivity uMap = (UserMapActivity)ActivityController.activiyAbiertaActual;
                uMap.obtenerParametrosFusionados(parametros);
            }
            catch(Exception e)
            {

            }
        }

    }

    public static class ObtenerDatosRecorridoFusion extends AsyncTask<String,String,JSONObject>
    {
        @Override
        protected JSONObject doInBackground(String... params) {

            HttpURLConnection urlConnection = null;

            //Parámetros
            String idUsuario =params[0];
            //String idLinea = params[1];
            BufferedReader reader = null;
            OutputStream os = null;
            InputStream inputStream = null;

            Log.e("RECORRIDOFUSIONXD ",idUsuario);

            try {


                URL url = new URL(ip+"/odata/Usuarios("+idUsuario+")/ObtenerDatosRecorridoFusion");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

                urlConnection.connect();

                /*
                os = new BufferedOutputStream(urlConnection.getOutputStream());
                os.write(idLineaJSON.toString().getBytes());
                os.flush();
                */

                inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine = "";
                while ((inputLine = reader.readLine()) != null)
                {
                    buffer.append(inputLine);
                }

                String value = buffer.toString();

                JSONObject parametros = new JSONObject(value);

                return parametros;

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
        protected void onPostExecute(JSONObject parametros)
        {
            try
            {
                ChoferMapActivity cMap = (ChoferMapActivity)ActivityController.activiyAbiertaActual;
                cMap.obtenerParametrosFusionadosChofer(parametros);
            }
            catch(Exception e)
            {

            }
        }

    }

    public static class MiMicroAbordada extends AsyncTask<String,String,JSONObject>
    {
        @Override
        protected JSONObject doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            String idUsuario = params[0];
            BufferedReader reader = null;
            OutputStream os = null;
            InputStream inputStream = null;

            Log.e("IDUSUARIO", idUsuario);
            try {

                URL url = new URL(ip+"/odata/Usuarios("+idUsuario+")/ObtenerMiMicroAbordada");
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
                JSONObject miMicro = new JSONObject(value);

                return miMicro;

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
        protected void onPostExecute(JSONObject miMicro)
        {
            try
            {
                UserMapActivity uMap = (UserMapActivity)ActivityController.activiyAbiertaActual;
                //uMap.verificarMiMicroAbordada(miMicro);
            }
            catch (Exception e)
            {

            }
        }

    }

    public static class DetenerPosicion extends AsyncTask<String,String,String>
    {
        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            String idUsuario = params[0];
            BufferedReader reader = null;
            OutputStream os = null;
            InputStream inputStream = null;

            try {

                URL url = new URL(ip+"/odata/odata/Usuarios("+idUsuario+")/DetenerPosicionUpdate");
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
                JSONObject miMicro = new JSONObject(value);

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
        protected void onPostExecute(String miMicro)
        {

        }

    }
}
