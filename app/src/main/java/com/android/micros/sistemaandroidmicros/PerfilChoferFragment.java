package com.android.micros.sistemaandroidmicros;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PerfilChoferFragment extends Fragment {

    TextView misDatosNombre, misDatosEmail, misDatosPatente, misDatosLinea;
    String nombreText, emailText, patenteText, lineaText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil_chofer, container, false);

        misDatosNombre = (TextView)view.findViewById(R.id.txtMiNombre);
        misDatosEmail = (TextView)view.findViewById(R.id.txtMiEmail);
        misDatosPatente = (TextView)view.findViewById(R.id.txtMiPatente);
        misDatosLinea = (TextView)view.findViewById(R.id.txtMiLinea);

        nombreText = this.getArguments().getString("nombre");
        emailText = this.getArguments().getString("email");
        patenteText = this.getArguments().getString("patente");
        lineaText = this.getArguments().getString("linea");

        misDatosNombre.setText(nombreText);
        misDatosEmail.setText(emailText);
        misDatosPatente.setText(patenteText);
        misDatosLinea.setText(lineaText);

        return view;
    }
}
