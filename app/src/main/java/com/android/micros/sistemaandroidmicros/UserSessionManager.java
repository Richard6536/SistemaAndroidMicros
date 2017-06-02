package com.android.micros.sistemaandroidmicros;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.android.micros.sistemaandroidmicros.Clases.Usuario;

import java.util.HashMap;

/**
 * Created by Richard on 19/05/2017.
 */

public class UserSessionManager
{
    SharedPreferences pref;
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    private static final String PREFER_NAME = "AndroidExamplePref";
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";

    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ROL = "rol";

    public UserSessionManager(Context context)
    {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void crearSesionUsuario(Usuario usuario)
    {
        String rol = usuario.rol+"";
        String id = usuario.id+"";
        editor.putBoolean(IS_USER_LOGIN, true);

        editor.putString(KEY_ID, id);
        editor.putString(KEY_NAME, usuario.nombre);
        editor.putString(KEY_EMAIL, usuario.email);
        editor.putString(KEY_ROL, rol);

        editor.commit();
    }

    public boolean checkLogin()
    {
        if(!this.isUserLoggedIn())
        {

          	// user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities from stack
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Staring Login Activity
            _context.startActivity(i);

            return true;
        }

        return false;
    }

    public HashMap<String, String> obtenerDetallesUsuario()
    {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        //user.put(KEY_ROL, pref.getString(KEY_ROL, null));
        return user;
    }

    public HashMap<String, String> obtenerRolyId()
    {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_ROL, pref.getString(KEY_ROL, null));
        user.put(KEY_ID, pref.getString(KEY_ID, null));
        return user;
    }

    public void logoutUser()
    {
        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public boolean isUserLoggedIn(){
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

}
