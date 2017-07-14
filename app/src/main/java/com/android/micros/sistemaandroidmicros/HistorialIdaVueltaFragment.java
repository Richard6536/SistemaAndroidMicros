package com.android.micros.sistemaandroidmicros;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.micros.sistemaandroidmicros.Clases.HistorialIdaVuelta;

import org.json.JSONException;
import org.json.JSONObject;

import static com.android.micros.sistemaandroidmicros.HistorialActivity.historialActual;
import static com.android.micros.sistemaandroidmicros.HistorialIdaVueltaActivity.historialIdaVeultaActual;

public class HistorialIdaVueltaFragment extends Fragment {

    int idIdaVuelta;
    int pasajerosTransportados;
    String horaInicio, horaTermino, duracionRecorridoComp;
    Button btnHistorialParadero;

    TextView txtPasajerosTrans, txtHoraInicio, txtHoraTermino, txtDuracionRecorrido;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_historial_ida_vuelta, container, false);
        idIdaVuelta = this.getArguments().getInt("id");

        txtPasajerosTrans = (TextView) v.findViewById(R.id.txtPasajerosTransp);
        txtHoraInicio = (TextView) v.findViewById(R.id.txtHoraInicioIdaVuelta);
        txtHoraTermino = (TextView) v.findViewById(R.id.textView16);
        txtDuracionRecorrido = (TextView) v.findViewById(R.id.textView18);

        btnHistorialParadero = (Button) v.findViewById(R.id.btnHistorialParadero);
        btnHistorialParadero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HistorialIdaVueltaActivity)getActivity()).redireccionarFragments(idIdaVuelta);
            }
        });

        for(int i = 0; i<historialIdaVeultaActual.length(); i++)
        {
            try {

                JSONObject h = null;
                h = historialIdaVeultaActual.getJSONObject(i);
                int id = h.getInt("Id");

                if(id == idIdaVuelta)
                {
                    pasajerosTransportados = h.getInt("PasajerosTransportados");
                    horaInicio = h.getString("HoraInicio");
                    horaTermino = h.getString("HoraTermino");
                    duracionRecorridoComp = h.getString("DuracionRecorrido");

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        txtPasajerosTrans.setText(pasajerosTransportados+"");
        txtHoraInicio.setText(horaInicio);
        txtHoraTermino.setText(horaTermino);
        txtDuracionRecorrido.setText(duracionRecorridoComp);

        return v;
    }
}
