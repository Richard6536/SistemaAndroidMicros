package com.android.micros.sistemaandroidmicros.Clases;

import com.google.android.gms.maps.model.Polyline;

import java.util.List;

/**
 * Created by Richard on 13/09/2017.
 */

public class SesionPasajero
{
    public static int idParaderoGuardado = -1;
    public static int idLineaPosicionGuardada;
    public static Rutas rutaIda;
    public static Rutas rutaVuelta;


    public static Rutas getRutaIda() {
        return rutaIda;
    }

    public static void setRutaIda(Rutas rutaIda) {
        SesionPasajero.rutaIda = rutaIda;
    }

    public static Rutas getRutaVuelta() {
        return rutaVuelta;
    }

    public static void setRutaVuelta(Rutas rutaVuelta) {
        SesionPasajero.rutaVuelta = rutaVuelta;
    }

    public static int getIdParaderoGuardado() {
        return idParaderoGuardado;
    }

    public static void setIdParaderoGuardado(int idParaderoGuardado) {
        SesionPasajero.idParaderoGuardado = idParaderoGuardado;
    }

    public static int getIdLineaPosicionGuardada() {
        return idLineaPosicionGuardada;
    }

    public static void setIdLineaPosicionGuardada(int idLineaPosicionGuardada) {
        SesionPasajero.idLineaPosicionGuardada = idLineaPosicionGuardada;
    }
}
