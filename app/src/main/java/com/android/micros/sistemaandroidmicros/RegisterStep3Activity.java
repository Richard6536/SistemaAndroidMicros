package com.android.micros.sistemaandroidmicros;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step3);
        Bundle bundle = getIntent().getExtras();
        email =  bundle.getString("correo").toString();
        nombre = bundle.getString("name").toString();
        txtPass = (EditText)findViewById(R.id.txtPass);

        btnTerminar = (Button)findViewById(R.id.btnTerminar);

        btnTerminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalizarRegistro();
            }
        });
    }
    protected void onResume()
    {
        super.onResume();
        ActivityController.activiyAbiertaActual = this;
    }
    private void finalizarRegistro()
    {

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

    public void fin()
    {
        Intent intent = new Intent(RegisterStep3Activity.this, LoginActivity.class);
        startActivity(intent);
    }

}