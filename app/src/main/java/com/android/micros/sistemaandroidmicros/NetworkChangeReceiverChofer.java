package com.android.micros.sistemaandroidmicros;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import static com.android.micros.sistemaandroidmicros.ChoferMapActivity.dialog;

/**
 * Created by Richard on 09/11/2017.
 */

public class NetworkChangeReceiverChofer extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        try
        {
            if(isOnline(context))
            {
                dialog(true);
            }
            else
            {
                dialog(false);
            }
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}
