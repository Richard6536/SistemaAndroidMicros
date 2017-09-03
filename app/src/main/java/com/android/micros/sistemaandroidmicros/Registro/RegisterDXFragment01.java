package com.android.micros.sistemaandroidmicros.Registro;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.micros.sistemaandroidmicros.Clases.ActivityController;
import com.android.micros.sistemaandroidmicros.HistorialActivity;
import com.android.micros.sistemaandroidmicros.R;
import com.android.micros.sistemaandroidmicros.RegisterStep1Activity;
import com.android.micros.sistemaandroidmicros.RegisterStep2Activity;

public class RegisterDXFragment01 extends Fragment {


    private Button btnSiguienteRegister01;
    private EditText nombre;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_dxfragment01, container, false);
        btnSiguienteRegister01 = (Button)view.findViewById(R.id.btnSiguienteRegister01);
        btnSiguienteRegister01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickPasoDos();
            }
        });

        return view;
    }

    private void ClickPasoDos()
    {

        if(nombre.getText().toString().length() >= 3 && nombre.getText().toString().length() <= 25)
        {
            ((RegisterDXActivity)getActivity()).registerFragment01(nombre.getText().toString());
        }
    }
}
