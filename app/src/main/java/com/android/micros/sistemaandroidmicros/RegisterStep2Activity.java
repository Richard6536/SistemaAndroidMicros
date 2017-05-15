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

import com.android.micros.sistemaandroidmicros.Clases.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class RegisterStep2Activity extends AppCompatActivity {

    private Button btnPaso3;
    private EditText txtCorreo;
    String nombre;
    private TextView lblNombre, mensaje;
    private Boolean resultadoBool;
    String messaje="Error fatal, el correo ya existe.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step2);

        Bundle bundle = getIntent().getExtras();
        nombre=bundle.getString("name").toString();

        mensaje = (TextView)findViewById(R.id.mensaje8);
        txtCorreo = (EditText)findViewById(R.id.txtCorreo);
        btnPaso3 = (Button)findViewById(R.id.btnPaso3);

        btnPaso3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ClickPasoTres();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void ClickPasoTres() throws JSONException {
        /*
        Intent intent = new Intent(RegisterStep2Activity.this, RegisterStep3Activity.class);
        Bundle bundle = new Bundle();
        bundle.putString("correo", txtCorreo.getText().toString());
        bundle.putString("name", nombre);
        intent.putExtras(bundle);
        startActivity(intent);
        */

        if(txtCorreo.getText().toString().length() >= 3 && txtCorreo.getText().toString().length() <= 50)
        {
            JSONObject params = new JSONObject();
            params.put("Email", txtCorreo.getText().toString());


            new Usuario.ValidarEmail().execute(params.toString());
        }
    }
    public void resultadoValidacion(boolean value)
    {
        boolean resultBool = value;

        if(resultBool != true)
        {

            Intent intent = new Intent(RegisterStep2Activity.this, RegisterStep3Activity.class);
            startActivity(intent);
        }
        else
        {
            mensaje.setText(messaje);
        }
    }

    public void getResponse(Boolean resp)
    {

    }
}
