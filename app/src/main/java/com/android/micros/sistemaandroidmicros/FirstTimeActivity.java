package com.android.micros.sistemaandroidmicros;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.micros.sistemaandroidmicros.Clases.ActivityController;
import com.android.micros.sistemaandroidmicros.Clases.Linea;
import com.android.micros.sistemaandroidmicros.Clases.Paradero;
import com.android.micros.sistemaandroidmicros.Clases.Rutas;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FirstTimeActivity extends AppCompatActivity {

    UserSessionManager session;
    private final int DURACION_SPLASH = 15000; // 3 segundos

    private boolean terminoCargarLineas = false;
    private boolean terminoCargarRutas = false;
    private boolean terminoCargarParaderos = false;
    private boolean terminoCargarCoordenadas = false;

    private int numeroRutas;
    private int rutasAsignadas;

    private boolean terminoProgressBar = false;

    protected boolean mbActive;
    protected ProgressBar mProgressBar;
    int progressStatusCounter = 0;
    Handler progressHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_first_time);
        ActivityController.activiyAbiertaActual = this;

        session = new UserSessionManager(getApplicationContext());

        mProgressBar = (ProgressBar)findViewById(R.id.progressBarFT);

        new InternetConnection.hasInternetAccess().execute(getApplicationContext());

        //Start progressing
        new Thread(new Runnable() {
            public void run() {
                while (progressStatusCounter < 100)
                {
                    progressHandler.post(new Runnable() {
                        public void run() {
                            mProgressBar.setProgress(progressStatusCounter);
                        }
                    });
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    public void internetAlert()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(FirstTimeActivity.this);
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

    public void resultInternetConnection(boolean isConnected)
    {
        if(isConnected == false)
        {
            internetAlert();
        }
        else
        {
            String URLRutas = "http://stapp.ml/odata/Rutas";
            String URLLineas = "http://stapp.ml/odata/Lineas";
            String URLParaderos = "http://stapp.ml/odata/Paraderos";

            new Rutas.ObtenerRutas().execute(URLRutas);
            new Linea.ObtenerLineas().execute(URLLineas);
            new Paradero.ObtenerParaderos().execute(URLParaderos);
        }
    }

    protected void onResume()
    {
        super.onResume();
        ActivityController.activiyAbiertaActual = this;
    }

    public void RecibirCargaDeLineas()
    {

            int aumentarProgreso = 20;
            progreso(aumentarProgreso);

            terminoCargarLineas = true;
            EsperarWebService();

    }

    public void RecibirCargaDeRutas()
    {
        int aumentarProgreso = 20;
        progreso(aumentarProgreso);

        //String status = AsyncRutas.getStatus().toString();
            terminoCargarRutas = true;
            EsperarWebService();

    }

    public void RecibirCargaDeParaderos()
    {
        int aumentarProgreso = 20;
        progreso(aumentarProgreso);

        //String status = AsyncRutas.getStatus().toString();
        terminoCargarParaderos = true;
        EsperarWebService();

    }

    public void RecibirCargaDeCoordenadas()
    {

        terminoCargarCoordenadas = true;
        EsperarCoordenadas();
    }

    public void updateProgress(final int time)
    {
        if(null != mProgressBar)
        {
            final int progress = mProgressBar.getMax() * time /DURACION_SPLASH;
            mProgressBar.setProgress(progress);
        }
    }

    public void EsperarCoordenadas()
    {

        rutasAsignadas++;

        if(rutasAsignadas == numeroRutas)
        {
            int aumentarProgreso = 20;
            progreso(aumentarProgreso);

                if(session.checkLogin() == false)
                {


                    HashMap<String, String> user = session.obtenerRolyId();

                    String rolUsuario = user.get(UserSessionManager.KEY_ROL);

                    if(rolUsuario.equals("0"))
                    {
                        Intent intent = new Intent(FirstTimeActivity.this, UserMapActivity.class);
                        startActivity(intent);
                        finish();

                    }
                    else if(rolUsuario.equals("1"))
                    {
                        Intent intent = new Intent(FirstTimeActivity.this, ChoferMapActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
                else
                {

                    Intent intent = new Intent(FirstTimeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
        }
    }

    public void EsperarWebService()
    {


        if(terminoCargarLineas == true && terminoCargarRutas == true && terminoCargarParaderos == true)
        {

            int aumentarProgreso = 20;
            progreso(aumentarProgreso);

            //Necesito asociar los paraderos por ruta cuando haya cargado los paraderos y rutas.
            Paradero paradero = new Paradero();
            paradero.CargarParaderosPorRutas();

            numeroRutas = Rutas.listaRutas.size();
            rutasAsignadas = 0;

            Rutas.CargarCoordenadasRutas();

            //Se reinician los bool
            terminoCargarRutas = false;
            terminoCargarLineas = false;
            terminoCargarParaderos = false;


        }
    }

    public void progreso(int contadorProgreso)
    {
        progressStatusCounter += contadorProgreso;
    }
}