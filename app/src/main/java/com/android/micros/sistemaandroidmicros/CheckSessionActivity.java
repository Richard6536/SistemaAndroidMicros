package com.android.micros.sistemaandroidmicros;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.HashMap;

public class CheckSessionActivity extends AppCompatActivity {

    UserSessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_session);
        session = new UserSessionManager(getApplicationContext());

        if(session.checkLogin() == false)
        {

           Intent intent = new Intent(CheckSessionActivity.this, FirstTimeActivity.class);
           startActivity(intent);
           finish();

        }
        else
        {

            Intent intent = new Intent(CheckSessionActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

