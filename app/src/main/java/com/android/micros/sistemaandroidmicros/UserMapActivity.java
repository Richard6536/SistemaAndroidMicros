package com.android.micros.sistemaandroidmicros;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.micros.sistemaandroidmicros.Clases.Coordenada;
import com.android.micros.sistemaandroidmicros.Clases.Linea;
import com.android.micros.sistemaandroidmicros.Clases.Paradero;
import com.android.micros.sistemaandroidmicros.Clases.Rutas;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class UserMapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private Button btnBuscar;
    private EditText etOrigin, etDestination;

    //NavHeader
    private TextView nombreNavHeader, correoNavHeader;

    Polyline polylineIda;
    Polyline polylineVuelta;

    private List<Marker> paraderosRutaIda = new ArrayList<>();
    private List<Marker> paraderosRutaVuelta = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Spinner spinner;
    ArrayAdapter<String> adapter;

    //Datos de Facebook
    private String nombreFacebook, correoFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        spinner = (Spinner)findViewById(R.id.spLineas);

        cargarSpinner();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {


                if(polylineIda != null && polylineVuelta != null && paraderosRutaIda !=  null && paraderosRutaVuelta != null)
                {
                    polylineIda.remove();
                    polylineVuelta.remove();
                    removerParaderos();

                }
                Object item = parent.getItemAtPosition(pos);
                String itemStr = item.toString();
                Linea linea = new Linea();
                Rutas rutaIda = new Rutas();
                Rutas rutaVuelta = new Rutas();


                //Retorna el Id de la linea que se selecciona en el spinner
                int idLinea = linea.buscarLineaSpinner(itemStr);

                //Envío el Id de la linea y recibo la linea completa
                linea = Linea.BuscarLineaPorId(idLinea);


                rutaIda = Rutas.BuscarRutaPorId(linea.idRutaIda);
                rutaVuelta = Rutas.BuscarRutaPorId(linea.idRutaVuelta);

                //Envío la ruta de ida y vuelta con su lista de coordenadas y paraderos
                //para que sean dibujados en el mapa
                crearRutaIda(rutaIda.listaCoordenadas, rutaIda.listaParaderos);
                crearRutaVuelta(rutaVuelta.listaCoordenadas, rutaVuelta.listaParaderos);

            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_map, menu);
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng hcmus = new LatLng(-40.5769389,-73.1260218);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmus, 18));


        //TODO: Dar permisos para utilizar la ubicación de GPS
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }
    public void crearRutaIda(ArrayList<Coordenada> coordenadas, ArrayList<Paradero> paraderosIda)
    {

        Bitmap icon = markerIcon();
        PolylineOptions polylinesIda = new PolylineOptions();
        for (Coordenada c : coordenadas)
        {

            polylinesIda.color(Color.RED);
            polylinesIda.add(new LatLng(c.latitud, c.longitud));


        }
        polylineIda = mMap.addPolyline(polylinesIda);

        for(Paradero p : paraderosIda)
        {
            paraderosRutaIda.add(mMap.addMarker(new MarkerOptions().position(new LatLng(p.latitud,p.longitud))
                    .icon(BitmapDescriptorFactory.fromBitmap(icon))
                    .title("Paradero")));
            //marker.position(new LatLng(p.latitud,p.longitud));
            //marcadorIda = mMap.addMarker(marker);
        }

    }

    public void crearRutaVuelta(ArrayList<Coordenada> coordenadas, ArrayList<Paradero> paraderosVuelta)
    {

        Bitmap icon = markerIcon();
        PolylineOptions polylinesVuelta = new PolylineOptions();
        for (Coordenada c : coordenadas)
        {

            polylinesVuelta.color(Color.BLUE);
            polylinesVuelta.add(new LatLng(c.latitud, c.longitud));

        }
        polylineVuelta = mMap.addPolyline(polylinesVuelta);

        for(Paradero p : paraderosVuelta)
        {
            paraderosRutaVuelta.add(mMap.addMarker(new MarkerOptions().position(new LatLng(p.latitud,p.longitud))
                    .icon(BitmapDescriptorFactory.fromBitmap(icon))
                    .title("Paradero")));
            //marker.position(new LatLng(p.latitud,p.longitud));
            //marcadorVuelta = mMap.addMarker(marker);
        }

    }

    public void cargarSpinner()
    {
        Linea linea = new Linea();
        ArrayList<String> items = linea.obtenerNombres();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    //Obtengo desde "drawable" el diseño del marcador y lo envío al 'crearRuta'
    public Bitmap markerIcon()
    {

        Bitmap smallMarker;

        int largo = 68;
        int ancho = 42;
        BitmapDrawable bitmapdraw=(BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.map_marker);
        Bitmap b=bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, ancho, largo, false);

        return smallMarker;
    }

    //Remuevo la lista de Paraderos
    private void removerParaderos() {
        for (Marker paradero: paraderosRutaIda) {
            paradero.remove();
        }
        paraderosRutaIda.clear();

        for (Marker paradero: paraderosRutaVuelta) {
            paradero.remove();
        }
        paraderosRutaVuelta.clear();
    }
}
