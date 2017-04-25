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
public class RegisterStep2Activity extends AppCompatActivity {

    private Button btnPaso3;
    private TextView txtCorreo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register_step2);
        btnPaso3 = (Button)findViewById(R.id.btnPaso3);

        txtCorreo = (TextView)findViewById(R.id.txtCorreo);

        btnPaso3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickPasoTres();
            }
        });
    }

    private void ClickPasoTres()
    {
        Intent in = new Intent(RegisterStep2Activity.this, RegisterStep3Activity.class);
        startActivity(in);
    }
}
