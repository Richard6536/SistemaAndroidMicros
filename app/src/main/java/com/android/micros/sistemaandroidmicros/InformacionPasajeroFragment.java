package com.android.micros.sistemaandroidmicros;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class InformacionPasajeroFragment extends Fragment {

    TextView nombreView, emailView;
    String nombre, email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_informacion_pasajero, container, false);

        nombreView = (TextView)view.findViewById(R.id.txtMiNombrePasajero);
        emailView = (TextView)view.findViewById(R.id.txtMiEmailPasajero);

        nombre = this.getArguments().getString("nombre");
        email = this.getArguments().getString("email");

        nombreView.setText(nombre);
        emailView.setText(email);

        return view;
    }
}
