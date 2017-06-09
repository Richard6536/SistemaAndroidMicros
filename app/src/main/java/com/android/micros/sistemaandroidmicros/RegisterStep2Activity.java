package com.android.micros.sistemaandroidmicros;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
public class RegisterStep2Activity extends AppCompatActivity {

    private Button btnPaso3;
    private EditText txtCorreo;
    String nombre;
    private TextView lblNombre, mensaje;
    private Boolean resultadoBool;
    String messaje="Error fatal, el correo ya existe.";
    private ProgressDialog espera;
    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step2);

        Bundle bundle = getIntent().getExtras();
        nombre=bundle.getString("name").toString();

        mensaje = (TextView)findViewById(R.id.mensaje8);
        bar = (ProgressBar)findViewById(R.id.progressBar2);
        bar.setVisibility(View.GONE);
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
    protected void onResume()
    {
        super.onResume();
        ActivityController.activiyAbiertaActual = this;
        bar.setVisibility(View.GONE);
        btnPaso3.setEnabled(true);
    }
    private void ClickPasoTres() throws JSONException {
        /*
        Intent intent = new Intent(RegisterStep2Activity.this, RegisterStep3Activity.class);
        startActivity(intent);
        */
        //bar.setVisibility(View.VISIBLE);
        new InternetConnection.hasInternetAccess().execute(getApplicationContext());
    }

    public void resultInternetConnection(boolean internetConectado)
    {
        if(internetConectado == true)
        {
            if(txtCorreo.getText().toString().length() >= 3 && txtCorreo.getText().toString().length() <= 50)
            {
                bar.setVisibility(View.VISIBLE);
                btnPaso3.setEnabled(false);
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
            internetAlert();
        }
    }

    public void resultadoValidacion(boolean value)
    {
        boolean resultBool = value;

        if(resultBool != true)
        {
            //bar.setVisibility(View.GONE);
            Intent intent = new Intent(RegisterStep2Activity.this, RegisterStep3Activity.class);
            Bundle bundle = new Bundle();
            bundle.putString("correo", txtCorreo.getText().toString());
            bundle.putString("name", nombre);
            intent.putExtras(bundle);
            startActivity(intent);

        }
        else
        {
            mensaje.setText(messaje);
        }
    }

    public void internetAlert()
    {
        bar.setVisibility(View.GONE);
        AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterStep2Activity.this);
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
