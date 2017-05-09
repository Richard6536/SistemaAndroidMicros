package com.android.micros.sistemaandroidmicros;

import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.micros.sistemaandroidmicros.Clases.Linea;
import com.android.micros.sistemaandroidmicros.Clases.Rutas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class InfoUserActivity extends AppCompatActivity {

    private Button btnBuscar, btnIngreso;
    private TextView lblId, lblNombre, lblCorreo, info, info2;
    private EditText txtId, txtNombre, txtEdad;
    private int identificador;
    private Spinner spinner;
    ArrayAdapter<String> adapter;
    String URL = "http://localhost:8081/odata/Lineas";
    String URLRutas = "http://localhost:8081/odata/Rutas";
    Linea linea = new Linea();

    private String nombre;
    private String edad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_user);
        info = (TextView)findViewById(R.id.textView2);
        spinner = (Spinner)findViewById(R.id.spLineas);


        //TODO: obtengo el item del spinner ------------
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);

                //Retorna el id de la linea seleccionada del spinner.
                Linea line = new Linea();
                int idLinea = line.buscarLineaSpinner(item.toString());

                //Envía id para obtener las rutas de la linea seleccionada.

            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //String idUser = txtId.getText().toString();
        //identificador = Integer.parseInt(idUser);

    }
    private class JsonAlServidor extends AsyncTask <String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            String JsonResponse = "";
            String mensaje;
            String JsonDATA = params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("http://localhost:8081/odata/Usuarios/MensajeParametros");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);

                writer.close();
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
                mensaje = resultadoJSON.getString("value");

                Log.i("Mensaje1",mensaje);
                return mensaje;

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
        protected void onPostExecute(String result)
        {
            info2.setText("Mensaje: " + result);
        }
    }

    private class ObtenerDatosUsuario extends AsyncTask<String, String, String> {
        HttpURLConnection connection = null;
        BufferedReader reader = null;


        @Override
        protected String doInBackground(String... params) {
            int id;
            String nombre;
            String correo;

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
                id = resultadoJSON.getInt("Id");
                nombre = resultadoJSON.getString("Nombre");
                correo = resultadoJSON.getString("Email");

                return "Id: " + id + " - Nombre: " + nombre + " - Correo: " + correo;

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

            lblCorreo.setText(result);


            //LISTA
            //JSONArray lista = resultadoJSON.getJSONArray("movies");
            //JSONObject finalObject = lista.getJSONObject(0);

        }

        @Override
        protected void onPreExecute() {
            Log.i("ServicioRest", "onPreExecute");
            info.setText(URL);
        }
    }


    public void cargarSpinner()
    {
        Linea linea = new Linea();
        ArrayList<String> nombres = linea.obtenerNombres();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}