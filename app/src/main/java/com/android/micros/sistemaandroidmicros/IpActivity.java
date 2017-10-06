package com.android.micros.sistemaandroidmicros;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static com.android.micros.sistemaandroidmicros.Clases.Usuario.ip;

public class IpActivity extends AppCompatActivity {

    EditText ipText;
    String ipt, ipPrm;
    EditText ipPrimeraParte;
    Button btnStappCF, btnCambiarIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ipText = (EditText)findViewById(R.id.editText);
        ipPrimeraParte = (EditText)findViewById(R.id.txtIpPriemra);
        btnStappCF = (Button)findViewById(R.id.btnStappCF);
        btnStappCF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ip = "http://stapp.cf/odata";
                Intent  intent = new Intent(IpActivity.this, CheckSessionActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnCambiarIp = (Button)findViewById(R.id.btnCambiarIp);
        btnCambiarIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ipPrimeraParte.setEnabled(true);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ipt = ipText.getText().toString();
                ipPrm = ipPrimeraParte.getText().toString();
                ipPrimeraParte.setEnabled(false);

                ip = "http://"+ ipPrm + ipt +":8080/odata";
                Intent  intent = new Intent(IpActivity.this, CheckSessionActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
