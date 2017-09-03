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
    String fechaInicio, horaInicio, fechaTermino, horaTermino, minRecorrido, segRecorridos;
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
                    String hi = h.getString("HoraInicio");
                    String hf = h.getString("HoraTermino");

                    if(!hi.equals(hf))
                    {
                        String[] fechaHoraInicioSplit = hi.split("T");
                        String[] fechaHoraTerminoSplit = hf.split("T");
                        String[] horaCompleta = fechaHoraInicioSplit[1].split("\\.");
                        String[] horaFCompleta = fechaHoraTerminoSplit[1].split("\\.");

                        fechaInicio = fechaHoraInicioSplit[0];
                        horaInicio = horaCompleta[0];
                        fechaTermino = fechaHoraTerminoSplit[0];
                        horaTermino = horaFCompleta[0];

                        String duracionRecorridoComp = h.getString("DuracionRecorrido");
                        String[] splitDuracion = duracionRecorridoComp.split("\\.");
                        String[] minutoSegundos = splitDuracion[0].split("M");
                        String[] minutosSplit = minutoSegundos[0].split("T");

                        segRecorridos = minutoSegundos[1];
                        minRecorrido = minutosSplit[1];

                        txtPasajerosTrans.setText(pasajerosTransportados+"");
                        txtHoraInicio.setText(fechaInicio + System.getProperty("line.separator") + horaInicio);
                        txtHoraTermino.setText(fechaTermino + System.getProperty("line.separator") + horaTermino);
                        txtDuracionRecorrido.setText(minRecorrido +" minutos "+segRecorridos+" segundos");
                    }

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return v;
    }
}
