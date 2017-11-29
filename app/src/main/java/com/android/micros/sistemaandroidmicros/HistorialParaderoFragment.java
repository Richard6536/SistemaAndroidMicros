package com.android.micros.sistemaandroidmicros;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.android.micros.sistemaandroidmicros.HistorialIdaVueltaActivity.historialIdaVeultaActual;
import static com.android.micros.sistemaandroidmicros.HistorialParaderoActivity.historialParaderosActual;

public class HistorialParaderoFragment extends Fragment {

    String horaLlegada, fechaLlegada, segundosDetenidos, minutosDetenidos;
    int pasajerosRecibidos;
    int idParadero;

    TextView txtHoraLlegada, txtTiempoDetenido, txtPasajerosRecibidos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_historial_paradero, container, false);

        idParadero = Integer.parseInt(this.getArguments().getString("id"));

        txtHoraLlegada = (TextView)view.findViewById(R.id.txtHoraLlegadaParaderos);
        txtTiempoDetenido = (TextView)view.findViewById(R.id.txtTiempoDetenido);
        txtPasajerosRecibidos = (TextView)view.findViewById(R.id.txtPasajerosRecibidos);

        for(int i = 0; i<historialParaderosActual.length(); i++)
        {
            try {

                JSONObject h = null;
                h = historialParaderosActual.getJSONObject(i);
                int id = h.getInt("Id");

                if(id == idParadero)
                {
                    String hLlegada = h.getString("HoraLlegada");
                    String[] fechaHoraInicioSplit = hLlegada.split("T");
                    String[] horaCompleta = fechaHoraInicioSplit[1].split("\\."); //0

                    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = null;

                    horaLlegada = horaCompleta[0];

                    String tiempoDetenidoCompleto = h.getString("TiempoDetenido");
                    String[] sinDecimals = tiempoDetenidoCompleto.split("\\.");

                    boolean existenMinutos = ordenarDuracionRecorrido(sinDecimals[0]);
                    String[] sinPT = sinDecimals[0].split("T");

                    if(existenMinutos == true)
                    {
                        String[] minutosSplit = sinPT[1].split("M");
                        minutosDetenidos = minutosSplit[0];
                        segundosDetenidos = minutosSplit[1];
                    }
                    else
                    {
                        minutosDetenidos = "0";
                        segundosDetenidos = sinPT[1];
                    }

                    pasajerosRecibidos = h.getInt("PasajerosRecibidos");
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        txtHoraLlegada.setText(horaLlegada);
        txtTiempoDetenido.setText(minutosDetenidos + " minutos "+ segundosDetenidos+" segundos");
        txtPasajerosRecibidos.setText(pasajerosRecibidos+"");

        return view;
    }

    public boolean ordenarDuracionRecorrido(String tiempoDetenido)
    {
        boolean existenMinutos = false;
        for(int caracter = 0; caracter < tiempoDetenido.length(); caracter++)
        {
            char caracterObtenido = tiempoDetenido.charAt(caracter);

            if(caracterObtenido == 'M')
            {
                existenMinutos = true;
            }
        }

        return existenMinutos;
    }

}
