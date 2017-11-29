package com.android.micros.sistemaandroidmicros;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.micros.sistemaandroidmicros.Clases.ActivityController;
import com.android.micros.sistemaandroidmicros.Clases.Coordenada;
import com.android.micros.sistemaandroidmicros.Clases.Linea;
import com.android.micros.sistemaandroidmicros.Clases.Paradero;
import com.android.micros.sistemaandroidmicros.Clases.Rutas;
import com.android.micros.sistemaandroidmicros.Clases.Usuario;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

public class RecomendarRutaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;
    boolean primeraVez = true;
    boolean busquedaDetallada  = false;

    //Alertas de dialogo
    AlertDialog alert = null;
    AlertDialog alertNoExisteRuta = null;
    AlertDialog alerta = null;
    //Marcadores
    private Marker marcadorInicio;
    private Marker marcadorTermino;
    private List<Marker> paraderosRutaIda = new ArrayList<>();
    private List<Marker> paraderosRutaVuelta = new ArrayList<>();
    private boolean enFalse = false;
    String estadoPunto;

    //Polylineas
    Polyline polyRuta;
    Polyline polylineIda;
    Polyline polylineVuelta;

    //Ruta webService
    private JSONArray rutaWS;

    Rutas rutaIda;
    Rutas rutaVuelta;
    int idLineaSeleccionada;
    String nombreLinea = "";

    private TextView txtnombreLinea, txtMsjLinea;

    private String TAG = "RecomendarRuta";

    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomendar_ruta);

        //ActivityController.activiyAbiertaActual = this;

        txtnombreLinea = (TextView)findViewById(R.id.txtNombreLinea);
        txtMsjLinea = (TextView)findViewById(R.id.txtMsjLinea);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(marcadorInicio != null && marcadorTermino != null)
                {
                    if(polylineIda != null && polylineVuelta != null)
                    {
                        polylineIda.remove();
                        polylineVuelta.remove();
                        polyRuta.remove();
                        removerParaderos();
                        txtMsjLinea.setEnabled(false);
                        txtnombreLinea.setText("");
                    }

                    LatLng latLngInicio = marcadorInicio.getPosition();
                    LatLng latLngTermino = marcadorTermino.getPosition();

                    JSONObject coordenadas = new JSONObject();

                    try {

                        creandoRutaDialog();
                        double latInicio = latLngInicio.latitude;
                        double lngInicio = latLngInicio.longitude;

                        double latTermino = latLngTermino.latitude;
                        double lngTermino = latLngTermino.longitude;

                        coordenadas.put("BusquedaDetallada", busquedaDetallada);
                        coordenadas.put("latInicio", latInicio);
                        coordenadas.put("lngInicio", lngInicio);
                        coordenadas.put("latFinal", latTermino);
                        coordenadas.put("lngFinal", lngTermino);

                        new Linea.RecomendarRuta().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, coordenadas.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Snackbar.make(view, "Debe haber un inicio y llegada.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                direccionAlert(place);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("MENSAJEPLACE", "An error occurred: " + status);
            }
        });

        initializeLocationManager();
        permisos();

    }

    protected void onResume()
    {
        super.onResume();
        ActivityController.activiyAbiertaActual = this;
    }

    public void direccionAlert(final Place place)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(RecomendarRutaActivity.this);
        // Set the dialog title
        builder.setTitle("¿Cómo desea agregar esta dirección?").setMessage(place.getName()+",\n"+
                place.getAddress() +"\n"
                + place.getPhoneNumber())
                .setPositiveButton("inicio", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        estadoPunto = "inicio";
                        agregarDireccion(estadoPunto, place);

                    }
                })
                .setNegativeButton("término", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        estadoPunto = "termino";
                        agregarDireccion(estadoPunto, place);
                    }
                });

        builder.create();
        builder.show();
    }

    public void agregarDireccion(String punto, Place place)
    {
        Bitmap flag = flagMarker();

        String nombre = place.getName().toString();
        String direccion = place.getAddress().toString();

        if(punto.equals("termino"))
        {
            if(marcadorTermino == null)
            {

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 14));
                marcadorTermino = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title("Punto de término")
                        .snippet(nombre+"\n"+direccion)
                        .icon(BitmapDescriptorFactory.fromBitmap(flag))
                        .draggable(true));
                marcadorTermino.setTag("llegada");
                marcadorTermino.showInfoWindow();
            }
            else
            {
                marcadorTermino.setPosition(place.getLatLng());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 14));
            }

        }
        else if(punto.equals("inicio"))
        {

            if(marcadorInicio == null)
            {

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 14));
                marcadorInicio = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title("Punto de Inicio")
                        .snippet(nombre+"\n"+direccion)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .draggable(true));
                marcadorInicio.setTag("inicio");
                marcadorInicio.showInfoWindow();
            }
            else
            {
                marcadorInicio.setPosition(place.getLatLng());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 14));
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng hcmus = new LatLng(-40.5769389, -73.1260218);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmus, 14));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                Bitmap flag = flagMarker();

                if(marcadorInicio == null)
                {
                    marcadorDeInicio(latLng);
                }
                else
                {
                    if(marcadorTermino == null)
                    {
                        marcadorTermino = mMap.addMarker(new MarkerOptions().position(latLng).title("Punto de llegada")
                                .icon(BitmapDescriptorFactory.fromBitmap(flag))
                                .draggable(true));
                        marcadorTermino.setTag("llegada");
                        marcadorTermino.showInfoWindow();
                    }
                    else
                    {
                        marcadorTermino.setPosition(latLng);
                    }
                }
            }
        });


        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng nuevaPosicion = marker.getPosition();
                String tag = marker.getTag().toString();

                if(tag.equals("inicio"))
                {
                    marcadorInicio.setPosition(nuevaPosicion);
                }
                else if(tag.equals("llegada"))
                {
                    marcadorTermino.setPosition(nuevaPosicion);
                }
            }
        });
    }

    public void permisos()
    {
        try {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "No puede solicitar la actualización de la ubicacion", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "El proveedor de gps no existe, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "No puede solicitar la actualización de la ubicacion", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "El proveedor de gps no existe " + ex.getMessage());
        }
    }

    private class LocationListener implements android.location.LocationListener{

        Location mLastLocation;

        public LocationListener(String gpsProvider)
        {
            mLastLocation = new Location(gpsProvider);
        }

        @Override
        public void onLocationChanged(Location location) {

            double lat = location.getLatitude();
            double lng = location.getLongitude();

            LatLng miPosicion = new LatLng(lat, lng);
            Log.e(TAG, "onLocationChanged: " + location);

            if(primeraVez == true)
            {
                if(marcadorInicio != null)
                {
                    marcadorInicio.setPosition(miPosicion);
                }
                else
                {
                    marcadorDeInicio(miPosicion);
                }

                primeraVez = false;
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void mejorRuta(JSONArray ruta)
    {

        if(ruta.length() != 0)
        {
            rutaWS = ruta;

            for(int i=0; i<ruta.length(); i++)
            {
                try {

                    JSONObject coordenadas = ruta.getJSONObject(i);
                    idLineaSeleccionada = coordenadas.getInt("Id");
                    break;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(idLineaSeleccionada == -1)
            {
                dibujarRutaParaderoCercano();
                caminarDialog();
                alert.cancel();

            }
            else
            {

                Linea linea;


                linea = Linea.BuscarLineaPorId(idLineaSeleccionada);
                nombreLinea = linea.nombreLinea;

                rutaIda = Rutas.BuscarRutaPorId(linea.idRutaIda);
                rutaVuelta = Rutas.BuscarRutaPorId(linea.idRutaVuelta);

                polylineIda = dibujarMejorLinea(rutaIda, paraderosRutaIda, Color.RED);
                polylineVuelta = dibujarMejorLinea(rutaVuelta, paraderosRutaVuelta, Color.BLUE);

                dibujarRutaParaderoCercano();
                txtMsjLinea.setVisibility(View.VISIBLE);
                txtnombreLinea.setText(nombreLinea);



                alert.cancel();
            }
        }
        else
        {
            noExisteRutaDialog();
            alert.cancel();
        }
    }

    public void dibujarRutaParaderoCercano()
    {
        PolylineOptions polyLineaRuta = new PolylineOptions();
        for(int i=0; i<rutaWS.length(); i++)
        {
            try {

                JSONObject coordenadas = null;
                coordenadas = rutaWS.getJSONObject(i);

                double lat = coordenadas.getDouble("Latitud");
                double lng = coordenadas.getDouble("Longitud");
                idLineaSeleccionada = coordenadas.getInt("Id");

                polyLineaRuta.color(Color.GREEN);
                polyLineaRuta.add(new LatLng(lat, lng));


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        polyRuta = mMap.addPolyline(polyLineaRuta);

    }
    public Polyline dibujarMejorLinea(Rutas ruta, List<Marker> marcadoresParaderos, int _color)
    {
        ArrayList<Coordenada> coordenadas = ruta.listaCoordenadas;
        ArrayList<Paradero> paraderos = ruta.listaParaderos;

        Bitmap icon = markerIcon();
        PolylineOptions polyLineaNueva = new PolylineOptions();

        for (Coordenada c : coordenadas) {

            polyLineaNueva.color(_color);
            polyLineaNueva.width(16);
            polyLineaNueva.add(new LatLng(c.latitud, c.longitud));

        }
        Polyline nuevaPolyline = mMap.addPolyline(polyLineaNueva);

        for (Paradero p : paraderos) {

            Marker paradero = mMap.addMarker(new MarkerOptions().position(new LatLng(p.latitud, p.longitud))
                    .icon(BitmapDescriptorFactory.fromBitmap(icon)));

            paradero.setTag(p.id);
            marcadoresParaderos.add(paradero);
            //marker.position(new LatLng(p.latitud,p.longitud));
            //marcadorVuelta = mMap.addMarker(marker);
        }


        return nuevaPolyline;
    }

    public Bitmap markerIcon() {

        Bitmap smallMarker;

        int largo = 68;
        int ancho = 42;
        BitmapDrawable bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.stop2);
        Bitmap b = bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, ancho, largo, false);

        return smallMarker;
    }

    public Bitmap flagMarker() {

        Bitmap smallMarker;

        int largo = 68;
        int ancho = 42;
        BitmapDrawable bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.flag_finish);
        Bitmap b = bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, ancho, largo, false);

        return smallMarker;
    }


    private void removerParaderos() {
        for (Marker paradero : paraderosRutaIda) {
            paradero.remove();
        }
        paraderosRutaIda.clear();

        for (Marker paradero : paraderosRutaVuelta) {
            paradero.remove();
        }
        paraderosRutaVuelta.clear();
    }



    //TODO:-------------------------------- Navigator Drawer -----------------------------------------------------------------------
    // ------------------------------------

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void noExisteRutaDialog()
    {

        AlertDialog.Builder dialog = new AlertDialog.Builder(RecomendarRutaActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Puede existir un problema con su conexión a internet o que no haya una ruta recomendada.");
        dialog.setPositiveButton("Intentar nuevamente", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                alertNoExisteRuta.cancel();
            }
        });
        alertNoExisteRuta = dialog.create();
        alertNoExisteRuta.show();
    }

    public void caminarDialog()
    {

        AlertDialog.Builder dialog = new AlertDialog.Builder(RecomendarRutaActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Caminar hasta el siguiente punto, es lo más recomendado.");
        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                alerta.cancel();
            }
        });
        alerta = dialog.create();
        alerta.show();
    }

    public void creandoRutaDialog()
    {

        AlertDialog.Builder dialog = new AlertDialog.Builder(RecomendarRutaActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Creando la ruta, por favor espere..." );
        alert = dialog.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.recomendar_ruta, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera)
        {
            Intent in = new Intent(RecomendarRutaActivity.this, UserMapActivity.class);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            startActivity(in);

        } else if (id == R.id.nav_gallery) {

            if(ActivityController.activiyAbiertaActual != this)
            {
                Intent in = new Intent(RecomendarRutaActivity.this, RecomendarRutaActivity.class);
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                startActivity(in);
            }

        }

        else if (id == R.id.nav_false)
        {
            busquedaDetallada = false;
        }
        else if(id == R.id.nav_true)
        {
            busquedaDetallada = true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void marcadorDeInicio(LatLng posicion)
    {
        marcadorInicio = mMap.addMarker(new MarkerOptions().position(posicion).title("Punto de inicio")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .draggable(true));
        marcadorInicio.setTag("inicio");
        marcadorInicio.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion, 14));
    }
}





