package com.android.micros.sistemaandroidmicros;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import static com.android.micros.sistemaandroidmicros.Clases.Usuario.ip;

public class IpActivity extends AppCompatActivity {

    EditText ipText;
    String ipt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ipText = (EditText)findViewById(R.id.editText);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ipt = ipText.getText().toString();
                ip = "http://"+ipt+":8080";
                Intent  intent = new Intent(IpActivity.this, CheckSessionActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
