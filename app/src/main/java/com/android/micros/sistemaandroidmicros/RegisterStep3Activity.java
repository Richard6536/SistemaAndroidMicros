package com.android.micros.sistemaandroidmicros;

import android.annotation.SuppressLint;
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
public class RegisterStep3Activity extends AppCompatActivity {

    private TextView txtPass;
    private Button btnTerminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register_step3);
        txtPass = (TextView)findViewById(R.id.txtPass);

        btnTerminar = (Button)findViewById(R.id.btnTerminar);
    }

}