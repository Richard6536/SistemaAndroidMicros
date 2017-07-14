package com.android.micros.sistemaandroidmicros;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;


public class InformacionMicroFragment extends Fragment {

    String patente, nombreLinea;
    Double calificacionGlobal;
    Button btnCalificarMicro;
    TextView patenteView, nombreLineaView;
    RatingBar ratingBarView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_informacion_micro, container, false);

        patenteView = (TextView)view.findViewById(R.id.txtPatente);
        nombreLineaView = (TextView)view.findViewById(R.id.txtLinea);
        ratingBarView = (RatingBar)view.findViewById(R.id.ratingBarGlobalInfo);

        patente = this.getArguments().getString("patente");
        calificacionGlobal = this.getArguments().getDouble("cGlobal");
        nombreLinea = this.getArguments().getString("nombreLinea");

        btnCalificarMicro = (Button)view.findViewById(R.id.btnCalificarMicro);

        btnCalificarMicro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((UserMapActivity)getActivity()).redireccionarFragments();
            }
        });

        patenteView.setText(patente);
        nombreLineaView.setText(nombreLinea);
        ratingBarView.setRating(calificacionGlobal.floatValue());

        return view;
    }
}
