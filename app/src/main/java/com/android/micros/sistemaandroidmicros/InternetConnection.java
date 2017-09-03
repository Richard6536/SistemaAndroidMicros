package com.android.micros.sistemaandroidmicros;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.micros.sistemaandroidmicros.Clases.ActivityController;
import com.android.micros.sistemaandroidmicros.Clases.FragmentController;
import com.android.micros.sistemaandroidmicros.Registro.RegisterDXActivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Richard on 03/06/2017.
 */

public class InternetConnection
{

    static String LOG_TAG = "InternetAccess";
    public static class hasInternetAccess extends AsyncTask<Context, Context, Boolean>
    {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(Context... c) {
            Context context = c[0];

            if (isNetworkAvailable(context)) {
                try {
                    HttpURLConnection urlc = (HttpURLConnection)(new URL("http://clients3.google.com/generate_204").openConnection());
                    urlc.setRequestProperty("User-Agent", "Android");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(1500);
                    urlc.connect();
                    return (urlc.getResponseCode() == 204 &&
                            urlc.getContentLength() == 0);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error checking internet connection", e);
                }
            } else {
                Log.d(LOG_TAG, "No network available!");
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            String activityActual = ActivityController.activiyAbiertaActual.getClass().getSimpleName();

            if(activityActual.equals("LoginActivity"))
            {
                LoginActivity login = (LoginActivity) ActivityController.activiyAbiertaActual;
                login.validar(result);
            }
            else if(activityActual.equals("FirstTimeActivity"))
            {
                FirstTimeActivity fta = (FirstTimeActivity) ActivityController.activiyAbiertaActual;
                fta.resultInternetConnection(result);
            }
            else if(activityActual.equals("RegisterStep2Activity"))
            {
                RegisterStep2Activity fta = (RegisterStep2Activity) ActivityController.activiyAbiertaActual;
                fta.resultInternetConnection(result);
            }
            else if(activityActual.equals("RegisterStep3Activity"))
            {
                RegisterStep3Activity fta = (RegisterStep3Activity) ActivityController.activiyAbiertaActual;
                fta.finalizarRegistro(result);
            }
            else if(activityActual.equals("RegisterDXActivity"))
            {
                RegisterDXActivity rdxa = (RegisterDXActivity) ActivityController.activiyAbiertaActual;

            }
        }

    }

    private static boolean isNetworkAvailable(Context co) {
        ConnectivityManager connectivityManager = (ConnectivityManager)co.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}
