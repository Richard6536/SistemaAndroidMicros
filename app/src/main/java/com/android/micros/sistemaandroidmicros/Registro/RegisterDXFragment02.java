package com.android.micros.sistemaandroidmicros.Registro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.micros.sistemaandroidmicros.Clases.Usuario;
import com.android.micros.sistemaandroidmicros.InternetConnection;
import com.android.micros.sistemaandroidmicros.R;
import com.android.micros.sistemaandroidmicros.RegisterStep2Activity;
import com.android.micros.sistemaandroidmicros.RegisterStep3Activity;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterDXFragment02 extends Fragment {

    private Button btnSiguienteRegister02;
    private EditText txtCorreo;
    String nombre;
    private TextView lblNombre, mensaje;
    private Boolean resultadoBool;
    String messaje="Error fatal, el correo ya existe.";
    private ProgressDialog espera;
    private ProgressBar bar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_dxfragment02, container, false);

        mensaje = (TextView)view.findViewById(R.id.mensaje8);
        bar = (ProgressBar)view.findViewById(R.id.progressBar2);
        bar.setVisibility(View.GONE);
        txtCorreo = (EditText)view.findViewById(R.id.txtCorreo);

        btnSiguienteRegister02 = (Button)view.findViewById(R.id.btnPaso3);
        btnSiguienteRegister02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ClickPasoTres();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    private void ClickPasoTres() throws JSONException {

        new InternetConnection.hasInternetAccess().execute(this.getContext());
    }

    public void resultInternetConnection(boolean internetConectado)
    {
        if(internetConectado == true)
        {
            if(txtCorreo.getText().toString().length() >= 3 && txtCorreo.getText().toString().length() <= 50)
            {
                bar.setVisibility(View.VISIBLE);
                btnSiguienteRegister02.setEnabled(false);
                try
                {
                    JSONObject params = new JSONObject();
                    params.put("Email", txtCorreo.getText().toString());

                    new Usuario.ValidarEmail().execute(params.toString());

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        else
        {
           // internetAlert();
        }
    }

    public void resultadoValidcion(boolean value)
    {
        boolean result = value;
        if(result != true)
        {
            //bar.setVisibility(View.GONE);
            ((RegisterDXActivity)getActivity()).registerFragment02(txtCorreo.getText().toString());

        }
        else
        {
            mensaje.setText(messaje);
        }
    }
/*
    public void internetAlert()
    {
        bar.setVisibility(View.GONE);
        AlertDialog.Builder dialog = new AlertDialog.Builder();
        dialog.setCancelable(false);
        dialog.setTitle("Error de conexión");
        dialog.setMessage("Por favor, verifique su conexión a internet e intente nuevamente." );
        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                dialog.cancel();
            }
        });
        dialog.setPositiveButton("Config.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        final AlertDialog alert = dialog.create();
        alert.show();
    }*/
}
