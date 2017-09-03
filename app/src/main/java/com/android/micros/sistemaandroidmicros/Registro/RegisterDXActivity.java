package com.android.micros.sistemaandroidmicros.Registro;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.android.micros.sistemaandroidmicros.HistorialBaseFragment;
import com.android.micros.sistemaandroidmicros.R;

public class RegisterDXActivity extends AppCompatActivity {

    String nombreRegistro, correoRegistro, passwordRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_dx);

        FragmentManager FM = getSupportFragmentManager();
        FragmentTransaction FT = FM.beginTransaction();

        Fragment fragment = new RegisterDXFragment01();
        FT.replace(R.id.fragment_content_Register, fragment);
        FT.commit();
    }

    public void registerFragment01(String nombre)
    {
        nombreRegistro = nombre;

        FragmentManager FM = getSupportFragmentManager();
        FragmentTransaction FT = FM.beginTransaction();

        Fragment fragment = new RegisterDXFragment02();
        FT.replace(R.id.fragment_content_Register, fragment);
        FT.commit();
    }

    public void registerFragment02(String correo)
    {

        FragmentManager FM = getSupportFragmentManager();
        FragmentTransaction FT = FM.beginTransaction();

        Fragment fragment = new RegisterDXFragment02();
        FT.replace(R.id.fragment_content_Register, fragment);
        FT.commit();
    }

}
