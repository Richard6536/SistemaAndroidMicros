package com.android.micros.sistemaandroidmicros;

import android.Manifest;
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
import android.view.KeyEvent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.micros.sistemaandroidmicros.Clases.ActivityController;
import com.android.micros.sistemaandroidmicros.Clases.Coordenada;
import com.android.micros.sistemaandroidmicros.Clases.Linea;
import com.android.micros.sistemaandroidmicros.Clases.Micro;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
import java.util.logging.LogRecord;

public class ChoferMapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback{

    private GoogleMap cMap;
    private int idChofer;
    private int idMicro;
    private int idLineaActual;

    private TextView lblMensaje;

    UserSessionManager session;
    private String name;
    private String email;
    private String idSession;
    Polyline polylineIda;
    Polyline polylineVuelta;
    Micro microActual;

    private Button btnStart, btnStop;

    private List<Marker> paraderosRutaIda = new ArrayList<>();
    private List<Marker> paraderosRutaVuelta = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chofer_map);
        ActivityController.activiyAbiertaActual = this;

        session = new UserSessionManager(getApplicationContext());

        if(session.checkLogin())
            finish();

        HashMap<String, String> user = session.obtenerDetallesUsuario();
        HashMap<String, String> chofer = session.obtenerRolyId();

        idSession = chofer.get(UserSessionManager.KEY_ID);
        name = user.get(UserSessionManager.KEY_NAME);
        email = user.get(UserSessionManager.KEY_EMAIL);

        btnStart = (Button) findViewById(R.id.btnComenzar);
        btnStop = (Button)findViewById(R.id.btnParar);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarServicio();

            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(getBaseContext(), ServicePosition.class));
            }
        });

        lblMensaje = (TextView)findViewById(R.id.lblMensaje);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        new Micro.ObtenerMicroDeChofer().execute(idSession);

    }

    public void validarLinea(Micro micro)
    {

        if(micro.id == -1)
        {
            lblMensaje.setText("No está asociado a una micro");
            //No está asociado a una micro
        }
        else if(micro.lineaId == null)
        {
            lblMensaje.setText("La micro no está asociada a una linea.");
            //La micro no está asociada a una linea
        }
        else
        {

            microActual = micro;

            Rutas rutaIda = new Rutas();
            Rutas rutaVuelta = new Rutas();

            Linea linea = new Linea();
            linea = Linea.BuscarLineaPorId(micro.lineaId);
            idLineaActual = linea.idLinea;

            rutaIda = Rutas.BuscarRutaPorId(linea.idRutaIda);
            rutaVuelta = Rutas.BuscarRutaPorId(linea.idRutaVuelta);

            polylineIda = crearRuta(rutaIda, paraderosRutaIda, Color.RED);
            polylineVuelta = crearRuta(rutaVuelta, paraderosRutaVuelta, Color.BLUE);

        }
    }
    public Polyline crearRuta(Rutas ruta, List<Marker> marcadoresParaderos, int _color)
    {


        ArrayList<Coordenada> coordenadas = ruta.listaCoordenadas;
        ArrayList<Paradero> paraderos = ruta.listaParaderos;

        Bitmap icon = markerIcon();
        PolylineOptions polyLineaNueva = new PolylineOptions();
        for (Coordenada c : coordenadas)
        {

            polyLineaNueva.color(_color);
            polyLineaNueva.add(new LatLng(c.latitud, c.longitud));

        }
        Polyline nuevaPolyline = cMap.addPolyline(polyLineaNueva);


        for(Paradero p : paraderos)
        {
            Marker paradero = cMap.addMarker(new MarkerOptions().position(new LatLng(p.latitud,p.longitud))
                    .icon(BitmapDescriptorFactory.fromBitmap(icon))
                    .title("Paradero"));

            paradero.setTag(p.id);
            marcadoresParaderos.add(paradero);
            //marker.position(new LatLng(p.latitud,p.longitud));
            //marcadorVuelta = mMap.addMarker(marker);
        }

        return nuevaPolyline;

    }

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
        getMenuInflater().inflate(R.menu.chofer_map, menu);
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
            detenerServicio();
            session.logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_DOWN)
        {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                    homeIntent.addCategory( Intent.CATEGORY_HOME );
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homeIntent);
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
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
        cMap = googleMap;

        LatLng position = new LatLng(-40.5769389, -73.1260218);
        cMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 18));

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
        cMap.setMyLocationEnabled(true);

        cMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker paraderoSeleccionado) {
                Toast.makeText(ChoferMapActivity.this, "click en paradero Id : " + paraderoSeleccionado.getTag(), Toast.LENGTH_SHORT).show();

                String id = microActual.id+"";
                new Paradero.AsociarParaderoChofer().execute(id, paraderoSeleccionado.getTag().toString());
            }
        });
    }

    public void detenerServicio()
    {
        stopService(new Intent(getBaseContext(), ServicePosition.class));
        new AsyncTaskServerPosition.StopPosition().execute(idSession);
    }

    public void iniciarServicio()
    {
        Intent intent = new Intent(getBaseContext(), ServicePosition.class);
        intent.putExtra("usuarioId", idSession);
        startService(intent);
    }
    @Override
    protected void onStop()
    {
        Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
        super.onStop();
        detenerServicio();
    }

    @Override
    protected void onRestart()
    {
        Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();
        super.onRestart();
        iniciarServicio();
    }
}
