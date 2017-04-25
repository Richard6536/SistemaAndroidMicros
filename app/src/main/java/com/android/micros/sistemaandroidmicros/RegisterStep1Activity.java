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
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class RegisterStep1Activity extends AppCompatActivity {

    private Button btnPaso2;
    private TextView txtNombre, txtApellido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register_step1);
        txtNombre = (TextView)findViewById(R.id.txtNombre);
        txtApellido = (TextView)findViewById(R.id.txtApellido);



        btnPaso2 = (Button)findViewById(R.id.btnPaso2);
        btnPaso2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickPasoDos();
            }
        });

    }
    private void ClickPasoDos()
    {
        Intent in = new Intent(RegisterStep1Activity.this, RegisterStep2Activity.class);
        startActivity(in);
    }
}
