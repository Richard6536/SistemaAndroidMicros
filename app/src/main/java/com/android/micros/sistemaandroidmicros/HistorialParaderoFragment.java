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

import static com.android.micros.sistemaandroidmicros.HistorialIdaVueltaActivity.historialIdaVeultaActual;
import static com.android.micros.sistemaandroidmicros.HistorialParaderoActivity.historialParaderosActual;

public class HistorialParaderoFragment extends Fragment {

    String horaLlegada, tiempoDetenido;
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
                    horaLlegada = h.getString("HoraLlegada");
                    tiempoDetenido = h.getString("TiempoDetenido");
                    pasajerosRecibidos = h.getInt("PasajerosRecibidos");
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        txtHoraLlegada.setText(horaLlegada);
        txtTiempoDetenido.setText(tiempoDetenido);
        txtPasajerosRecibidos.setText(pasajerosRecibidos+"");

        return view;
    }

}
