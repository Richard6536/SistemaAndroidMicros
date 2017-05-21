package com.android.micros.sistemaandroidmicros.Clases;

/**
 * Created by Richard on 05/05/2017.
 */

public class Coordenada {

    public int idRuta;
    public Double latitud;
    public Double longitud;

    public Coordenada() {}

    public int getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(int idLatLng) {
        this.idRuta = idLatLng;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }


}
