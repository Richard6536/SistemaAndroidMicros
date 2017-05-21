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

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class RegisterStep1Activity extends AppCompatActivity {

    private Button btnPaso2;
    private EditText nombre;
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step1);

        nombre = (EditText)findViewById(R.id.txtNombre);


        btnPaso2 = (Button)findViewById(R.id.btnPaso2);
        btnPaso2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickPasoDos();
            }
        });

    }

    protected void onResume()
    {
        super.onResume();
        ActivityController.activiyAbiertaActual = this;
    }
    private void ClickPasoDos()
    {

        if(nombre.getText().toString().length() >= 3 && nombre.getText().toString().length() <= 25)
        {
            Intent intent = new Intent(RegisterStep1Activity.this, RegisterStep2Activity.class);
            Bundle bundle = new Bundle();
            bundle.putString("name", nombre.getText().toString());
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
