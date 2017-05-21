package com.android.micros.sistemaandroidmicros;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.android.micros.sistemaandroidmicros.Clases.ActivityController;
import com.android.micros.sistemaandroidmicros.Clases.Linea;
import com.android.micros.sistemaandroidmicros.Clases.Paradero;
import com.android.micros.sistemaandroidmicros.Clases.Rutas;

import java.util.ArrayList;

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

    private boolean terminoProgressBar = false;

    protected boolean mbActive;
    protected ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//set content view AFTER ABOVE sequence (to avoid crash)
        this.setContentView(R.layout.activity_first_time);
        session = new UserSessionManager(getApplicationContext());

        mProgressBar = (ProgressBar)findViewById(R.id.progressBarFT);

        String URLRutas = "http://localhost:8081/odata/Rutas";
        String URLLineas = "http://localhost:8081/odata/Lineas";
        String URLParaderos = "http://localhost:8081/odata/Paraderos";

        new Rutas.ObtenerRutas().execute(URLRutas);
        new Linea.ObtenerLineas().execute(URLLineas);
        new Paradero.ObtenerParaderos().execute(URLParaderos);
    }

    protected void onResume()
    {
        super.onResume();
        ActivityController.activiyAbiertaActual = this;
    }

    public void RecibirCargaDeLineas()
    {
        //String status = AsyncLineas.getStatus().toString();
            //aqui llega del onPostExecute del validar usuario
            terminoCargarLineas = true;
            EsperarWebService();

    }

    public void RecibirCargaDeRutas()
    {
        //String status = AsyncRutas.getStatus().toString();
            terminoCargarRutas = true;
            EsperarWebService();

    }

    public void RecibirCargaDeParaderos()
    {
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
        //if(terminoCargarCoordenadas == true && terminoCargarParaderos == true)
        if(terminoCargarCoordenadas == true)
        {
            terminoCargarCoordenadas = false;
            //terminoCargarParaderos = false;

            if(session.checkLogin() == false)
            {

                Intent intent = new Intent(FirstTimeActivity.this, UserMapActivity.class);
                startActivity(intent);
                finish();
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

            //Necesito asociar los paraderos por ruta cuando haya cargado los paraderos y rutas.
            Paradero paradero = new Paradero();
            paradero.CargarParaderosPorRutas();
            Rutas.CargarCoordenadasRutas();

            //Se reinician los bool
            terminoCargarRutas = false;
            terminoCargarLineas = false;
            terminoCargarParaderos = false;


        }
    }
}