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

import com.android.micros.sistemaandroidmicros.Clases.ActivityController;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class RegisterActivity extends AppCompatActivity {

    private Button btnPaso1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        btnPaso1 = (Button)findViewById(R.id.btnPaso1);

        btnPaso1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickPasoUno();
            }
        });
    }
    protected void onResume()
    {
        super.onResume();
        ActivityController.activiyAbiertaActual = this;
    }
    private void ClickPasoUno()
    {
        Intent in = new Intent(RegisterActivity.this, RegisterStep1Activity.class);
        startActivity(in);
    }
}
