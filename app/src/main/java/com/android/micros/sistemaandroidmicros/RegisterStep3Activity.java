package com.android.micros.sistemaandroidmicros;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.micros.sistemaandroidmicros.Clases.ActivityController;
import com.android.micros.sistemaandroidmicros.Clases.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class RegisterStep3Activity extends AppCompatActivity {

    private EditText txtPass;
    private Button btnTerminar;
    Intent intent = getIntent();
    String nombre, email;

    ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step3);
        bar = (ProgressBar)findViewById(R.id.progressBar3);
        bar.setVisibility(View.GONE);

        Bundle bundle = getIntent().getExtras();
        email =  bundle.getString("correo").toString();
        nombre = bundle.getString("name").toString();
        txtPass = (EditText)findViewById(R.id.txtPass);

        btnTerminar = (Button)findViewById(R.id.btnTerminar);

        btnTerminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarConeccionInternet();
            }
        });
    }
    protected void onResume()
    {
        super.onResume();
        ActivityController.activiyAbiertaActual = this;
    }
    public void verificarConeccionInternet()
    {
        new InternetConnection.hasInternetAccess().execute(getApplicationContext());
    }
    public void finalizarRegistro(boolean internetConectado)
    {
        if(internetConectado == true)
        {
            bar.setVisibility(View.VISIBLE);
            JSONObject parametros = new JSONObject();

            try {

                parametros.put("Nombre", nombre);
                parametros.put("Email", email);
                parametros.put("Password", txtPass.getText().toString());
                parametros.put("Rol", 0);

                new Usuario.CrearUsuario().execute(parametros.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            internetAlert();
        }
    }

    public void fin()
    {
        bar.setVisibility(View.GONE);
        AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterStep3Activity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Cuenta creada con éxito");
        dialog.setMessage("Presione Aceptar para iniciar sesión con su nueva cuenta." );
        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                Intent intent = new Intent(RegisterStep3Activity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        final AlertDialog alert = dialog.create();
        alert.show();
    }
    public void internetAlert()
    {
        bar.setVisibility(View.GONE);
        AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterStep3Activity.this);
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
    }
}