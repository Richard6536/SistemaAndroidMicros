package com.android.micros.sistemaandroidmicros;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.micros.sistemaandroidmicros.Clases.FragmentController;
import com.android.micros.sistemaandroidmicros.Clases.Historial;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.android.micros.sistemaandroidmicros.HistorialActivity.historialActual;


public class HistorialBaseFragment extends Fragment {

    String nombreChofer;
    String fecha, horaInicio, horaFinal; //DateTime.
    Double kilometrosRecorridos,calificacionesDiarias;
    int calificacionesRecibidas, pasajerosTransportados; //Posiblemente no.
    int numeroVueltas, idMicro;
    int idHistorial;

    TextView txtInicio, txtTermino, txtKilometrosR, txtNumIdaVueltas, txtnCalificaciones, txtCalificacionDiaria, txtPasajerosTrans;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //((ChoferMapActivity) getActivity()).getSupportActionBar().setTitle("Fragment Inbox");
        View v = inflater.inflate(R.layout.fragment_historial_base, container, false);

        txtInicio = (TextView)v.findViewById(R.id.txtInicio);
        txtTermino = (TextView)v.findViewById(R.id.txtKilometrosR);
        txtKilometrosR = (TextView)v.findViewById(R.id.textView21);
        txtnCalificaciones = (TextView)v.findViewById(R.id.textView23);
        txtNumIdaVueltas = (TextView)v.findViewById(R.id.textView24);
        txtCalificacionDiaria = (TextView)v.findViewById(R.id.textView26);
        txtPasajerosTrans = (TextView)v.findViewById(R.id.textView27);

        final Button btnIdaVuelta = (Button)v.findViewById(R.id.btnIdaVuelta);

        idHistorial = this.getArguments().getInt("id");

        btnIdaVuelta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //new Historial.ObtenerHistorialIdaVuelta().execute(idHistorial+"");
                ((HistorialActivity)getActivity()).redireccionarFragments(idHistorial);
            }
        });

        for(int i = 0; i<historialActual.length(); i++)
        {
            try {

                JSONObject h = null;
                h = historialActual.getJSONObject(i);
                int id = h.getInt("Id");

                if(id == idHistorial)
                {
                    nombreChofer = h.getString("NombreChofer"); //??
                    fecha = h.getString("Fecha");           //DATETIME
                    horaInicio = h.getString("HoraInicio"); //DATETIME
                    horaFinal = h.getString("HoraFinal");   //DATETIME
                    kilometrosRecorridos = h.getDouble("KilometrosRecorridos");
                    calificacionesRecibidas = h.getInt("CalificacionesRecibidas");
                    calificacionesDiarias = h.getDouble("CalificacionDiaria");
                    numeroVueltas = h.getInt("NumeroIdaVueltas");
                    pasajerosTransportados = h.getInt("PasajerosTransportados");

                    idMicro = h.getInt("IdMicro");
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        txtInicio.setText(horaInicio);
        txtTermino.setText(horaFinal);
        txtKilometrosR.setText(kilometrosRecorridos+"");
        txtnCalificaciones.setText(calificacionesRecibidas+"");
        txtNumIdaVueltas.setText(numeroVueltas+"");
        txtCalificacionDiaria.setText(calificacionesDiarias+"");
        txtPasajerosTrans.setText(pasajerosTransportados+"");
        return v;
    }

    public void onResume() {
        super.onResume();
        FragmentController.FragmentAbierto = this;
        String a = "";
    }

    public void cargarHistorialIdaVuelta()
    {
        Intent intent = new Intent(getActivity().getBaseContext(), HistorialActivity.class); getActivity().startActivity(intent);
    }
}
