package com.android.micros.sistemaandroidmicros;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ActivityCompat;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.micros.sistemaandroidmicros.Clases.ActivityController;
import com.android.micros.sistemaandroidmicros.Clases.Coordenada;
import com.android.micros.sistemaandroidmicros.Clases.Linea;
import com.android.micros.sistemaandroidmicros.Clases.Micro;
import com.android.micros.sistemaandroidmicros.Clases.Paradero;
import com.android.micros.sistemaandroidmicros.Clases.Rutas;
import com.android.micros.sistemaandroidmicros.Clases.SesionPasajero;
import com.android.micros.sistemaandroidmicros.Clases.Usuario;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.internal.IPolylineDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;
import static com.android.micros.sistemaandroidmicros.CalificacionFragment.progressCalificacion;

public class UserMapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;

    ArrayList<String> items;

    public static Button btnCalificarMicro;
    TextView patenteView, nombreLineaView;
    TextView nombreLineaAppbar, tarifaAppbar;
    RatingBar ratingBarView;
    RelativeLayout relativeLayoutInfoMicro, rLayoutTiempo;
    boolean menos80mts = false;
    boolean layoutCerrado = false;
    Dialog alertParadero, alertLineas;
    Timer timerMicroParaderoMasCercano;
    boolean paraderoEncontrado;
    boolean onCreateBool = false;

    Rutas rutaDeIdaActual;
    Rutas rutaDeVueltaActual;

    private int idMicroGlobal = -1;

    Double calificacionGlobal;
    boolean estaAbierto = false;
    boolean paraderoSeleccionado;
    boolean swt = false;

    //NavHeader
    private TextView txtTiempoMicro;

    Polyline polylineIda;
    Polyline polylineVuelta;

    private List<Marker> paraderosRutaIda = new ArrayList<>();
    private List<Marker> paraderosRutaVuelta = new ArrayList<>();

    private List<Marker> microsMarker = new ArrayList<>();

    Marker marcador;
    private Spinner spinner;
    ArrayAdapter<String> adapter;

    private TextView nameHeader, emailHeader;

    UserSessionManager session;
    private String name;
    private String email;
    private String idUser;

    static int idLineaSeleccionada;

    String GPS_FILTER = "MyGPSLocation";

    boolean estabaEnMicro = false;
    public static boolean aCalificado = false;

    int idParaderoSeleccionado, posicionLineaActual;

    AlertDialog alert;
    RelativeLayout spinnerLayout;
    Button btnDeseleccionarParadero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);

        session = new UserSessionManager(getApplicationContext());

        HashMap<String, String> userid = session.obtenerRolyId();
        HashMap<String, String> userDatos = session.obtenerDetallesUsuario();

        JSONObject parametros = new JSONObject();

        idUser = userid.get(UserSessionManager.KEY_ID);
        String emailUsuario = userDatos.get(UserSessionManager.KEY_EMAIL);

        spinner = (Spinner) findViewById(R.id.spLineas);
        spinnerLayout = (RelativeLayout) findViewById(R.id.relativeLayoutSpinner);

        rLayoutTiempo = (RelativeLayout)findViewById(R.id.rLayoutTiempo);
        rLayoutTiempo.setVisibility(View.GONE);
        txtTiempoMicro = (TextView)findViewById(R.id.txtTiempoMicro);

        relativeLayoutInfoMicro = (RelativeLayout) findViewById(R.id.relativeLayoutInfoMicro);
        relativeLayoutInfoMicro.setVisibility(View.GONE);

        btnDeseleccionarParadero = (Button) findViewById(R.id.btnDeseleccionarParadero);
        btnDeseleccionarParadero.setVisibility(View.GONE);
        btnDeseleccionarParadero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                spinnerLayout.setVisibility(View.VISIBLE);
                btnDeseleccionarParadero.setVisibility(View.GONE);
                txtTiempoMicro.setText("");
                rLayoutTiempo.setVisibility(View.GONE);
                deseleccionarParaderoAsync();
            }
        });

        cargarSpinner();

        btnCalificarMicro = (Button)findViewById(R.id.btnCalificarMicro);
        btnCalificarMicro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager FM = getSupportFragmentManager();
                FragmentTransaction FT = FM.beginTransaction();

                Bundle bundle = new Bundle();
                bundle.putInt("idMicro", idMicroGlobal);
                bundle.putDouble("cGlobal", calificacionGlobal);

                Fragment fragment = new CalificacionFragment();
                fragment.setArguments(bundle);
                FT.replace(R.id.frame_content_info, fragment);
                FT.addToBackStack(null);
                FT.commit();
            }
        });

        patenteView = (TextView)findViewById(R.id.txtPatente);
        nombreLineaView = (TextView)findViewById(R.id.txtLinea);
        ratingBarView = (RatingBar)findViewById(R.id.ratingBarGlobalInfo);

        //datosUsuario = (TextView) findViewById(R.id.datosUsuario);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nombreLineaAppbar = (TextView)findViewById(R.id.lineaNombreAppbar);
        tarifaAppbar = (TextView)findViewById(R.id.tarifaAppbar);

        if (session.checkLogin())
            finish();

        HashMap<String, String> user = session.obtenerDetallesUsuario();
        name = user.get(UserSessionManager.KEY_NAME);
        email = user.get(UserSessionManager.KEY_EMAIL);

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

        try {

            parametros.put("Email", emailUsuario);
            Usuario us = new Usuario();
            us.new validacionEmailUsuario().execute(parametros.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void verificacionUsuario(Boolean existe)
    {

        if(existe == false)
        {
            Intent intent = new Intent(UserMapActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {


            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {


                    if (polylineIda != null && polylineVuelta != null) {
                        polylineIda.remove();
                        polylineVuelta.remove();
                        removerParaderos();
                        removerMicros();
                    }

                    posicionLineaActual = pos;
                    Object item = parent.getItemAtPosition(pos);
                    String itemStr = item.toString();
                    Linea linea = new Linea();
                    Rutas rutaIda;
                    Rutas rutaVuelta;

                    //cargandoLinea();

                    //Retorna el Id de la linea que se selecciona en el spinner
                    idLineaSeleccionada = linea.buscarLineaSpinner(itemStr);
                    Log.e("idLineaUSERPMAP", idLineaSeleccionada + "");

                    //Envío el Id de la linea y recibo la linea completa
                    linea = Linea.BuscarLineaPorId(idLineaSeleccionada);

                    //Mostrar tarifa
                    tarifaAppbar.setText(linea.tarifa+"");
                    nombreLineaAppbar.setText(linea.nombreLinea);

                    rutaIda = Rutas.BuscarRutaPorId(linea.idRutaIda);
                    rutaVuelta = Rutas.BuscarRutaPorId(linea.idRutaVuelta);

                    rutaDeIdaActual = rutaIda;
                    rutaDeVueltaActual = rutaVuelta;

                    polylineIda = crearRuta(rutaIda, paraderosRutaIda, Color.RED);
                    polylineVuelta = crearRuta(rutaVuelta, paraderosRutaVuelta, Color.BLUE);
                    //actualizarPosicionMicros();
                    //removerMicros();
                }

                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            if(onCreateBool == false)
            {

                //Métodos que se llaman constantemente -------------------------
                idParaderoSeleccionado = -1;
                //actualizarPosicionMicros();                                            //Lineas(5)/ObtenerChoferes
                //actualizarMiMicroAbordada();                                           //Usuarios(5)/ObtenerMiMicroAbordada
                iniciarServicio();                                                     //Usuarios(5)/ActualizarPosicion
                iniciarServicioLineaFusion();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Permission " + permissions[0] + " granted");
        }
    }

    public void deseleccionarParaderoAsync()
    {
        idParaderoSeleccionado = -1;
        new Paradero.DeseleccionarParadero().execute(idUser);
    }

    protected void onResume() {
        super.onResume();
        ActivityController.activiyAbiertaActual = this;

        if(SesionPasajero.idParaderoGuardado != -1)
        {
            idParaderoSeleccionado = SesionPasajero.idParaderoGuardado;
            posicionLineaActual = SesionPasajero.idLineaPosicionGuardada;

            polylineIda = crearRuta(SesionPasajero.rutaIda, paraderosRutaIda, Color.RED);
            polylineVuelta = crearRuta(SesionPasajero.rutaVuelta, paraderosRutaVuelta, Color.BLUE);

            recibirParaderoSeleccionado();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                    homeIntent.addCategory(Intent.CATEGORY_HOME);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homeIntent);
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
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

        nameHeader = (TextView) findViewById(R.id.nameUser);
        emailHeader = (TextView) findViewById(R.id.emailUser);
        nameHeader.setText(name);
        emailHeader.setText(email);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Cerrar Sesion del usuario
        if (id == R.id.action_settings) {
            new Usuario.DetenerPosicion().execute(idUser);
            session.logoutUser();

            detenerServicio();
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
            if (ActivityController.activiyAbiertaActual != this) {
                Intent in = new Intent(UserMapActivity.this, UserMapActivity.class);
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                startActivity(in);
            }

        } else if (id == R.id.nav_gallery) {

            Intent in = new Intent(UserMapActivity.this, RecomendarRutaActivity.class);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            startActivity(in);
        }

        else if (id == R.id.nav_share) {
            item.setTitle("Desactivar Modo Tester");
            if (ActivityCompat.checkSelfPermission(UserMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(UserMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            mMap.setMyLocationEnabled(false);

            detenerServicio();
            obtenerPosicion();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng hcmus = new LatLng(-40.5769389, -73.1260218);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmus, 14));

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
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker paraderoSeleccionado) {
                if (estabaEnMicro == false) {

                    idParaderoSeleccionado = Integer.parseInt(paraderoSeleccionado.getTag().toString());
                    procesoSeleccionParadero();

                } else {
                    Toast.makeText(UserMapActivity.this, "No puede selecconar un paradero, si está abordo de una micro.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void procesoSeleccionParadero()
    {
        detenerServicioLineaFusion();
        spinnerLayout.setVisibility(View.GONE);
        btnDeseleccionarParadero.setVisibility(View.VISIBLE);

        new Paradero.SeleccionarParadero().execute(idUser, idParaderoSeleccionado+"");
    }

    public void agregarMicros(JSONArray choferes) {
        if (choferes.length() != 0) {
            Bitmap iconMicroActivo = microsIcon();
            for (int i = 0; i < choferes.length(); i++) {

                JSONObject jsonobject = null;
                try {

                    jsonobject = choferes.getJSONObject(i);
                    double lat = jsonobject.getDouble("Latitud");
                    double lng = jsonobject.getDouble("Longitud");
                    int idMicro = jsonobject.getInt("Id");
                    boolean estaActivo = jsonobject.getBoolean("TransmitiendoPosicion");

                    if (estaActivo) {

                        Log.e("MICROESTAACTIVA", lat +" "+lng);
                        //revisar si en la lista microsmarker existe un marcador con tag == id chofer
                        Marker marcadorChofer = obtenerChoferMarcador(idMicro);
                        if (marcadorChofer != null) {
                            marcadorChofer.setPosition(new LatLng(lat, lng));
                            //a ese marker se le cambia la posicion que se recibio
                        } else {
                            //si no existe se crea un nuevo marcador
                            //se le asigna como tag el id del chofer
                            //ese marcador se agrega a la lista

                            try
                            {
                                Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("Soy una micro")
                                        .icon(BitmapDescriptorFactory.fromBitmap(iconMicroActivo)));
                                m.setTag(idMicro+"");
                                microsMarker.add(m);
                                microsMarker.size();
                                String a ="";
                            }
                            catch(Exception e)
                            {
                                String mensaje = e.getMessage();
                                String a ="";
                            }
                        }

                    } else {
                        //chofer esta inactivo
                        //revisar si chofer esta en la lista de marcadores usando el tag
                        Marker marcadorMicro = obtenerChoferMarcador(idMicro);
                        if (marcadorMicro != null)
                        {
                            Marker choferSeleccionado = null;

                            for (Marker chofer : microsMarker)
                            {
                                if (chofer.getTag() == marcadorMicro.getTag())
                                {
                                    choferSeleccionado = chofer;
                                    chofer.remove();
                                }
                            }
                            if (choferSeleccionado != null)
                            {
                                microsMarker.remove(choferSeleccionado);
                            }

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Marker obtenerChoferMarcador(int id) {
        Marker mChofer = null;
        String idString = id + "";
        for (Marker m : microsMarker) {
            if (m.getTag().toString().equals(idString)) {
                mChofer = m;
            }
        }
        return mChofer;
    }


    public void obtenerMicrosDelParadero(JSONObject microParadero, int idLinea) {
        Bitmap iconMicroParadero = microsParaderoIcon();
        Bitmap iconMicro = microsIcon();
        try {

            if(idLineaSeleccionada == idLinea)
            {

                int id = microParadero.getInt("Id");

                if ( id != -1)
                {
                    swt = true;

                    double distancia = microParadero.getDouble("DistanciaEntre");
                    Log.e("DISTANCIAMICRO", distancia+"");
                    String[] metros = String.valueOf(distancia).split("\\.");
                    int metrosInt = Integer.parseInt(metros[0]);

                    String tiempoLlegada = calcularTiempo(distancia);

                    if(layoutCerrado == false)
                    {

                        rLayoutTiempo.setVisibility(View.VISIBLE);
                        txtTiempoMicro.setText(tiempoLlegada);

                        if(metrosInt < 150)
                        {
                            menos80mts = true;
                        }

                        if(menos80mts == true && layoutCerrado == false)
                        {
                            layoutCerrado = true;
                            rLayoutTiempo.setVisibility(View.GONE);
                        }

                    }

                    int idMicro = microParadero.getInt("MicroId");
                    //Toast.makeText(UserMapActivity.this, "Micro " + idMicro, Toast.LENGTH_SHORT).show();
                    for (Marker m : microsMarker)
                    {
                        m.setIcon(BitmapDescriptorFactory.fromBitmap(iconMicro));

                        String tag = m.getTag().toString();
                        if (tag.equals(idMicro + "")) {
                            m.setIcon(BitmapDescriptorFactory.fromBitmap(iconMicroParadero));
                        }
                    }
                }
                else
                {
                    resetMicros();
                    if(swt == true)
                    {
                        swt = false;
                        rLayoutTiempo.setVisibility(View.GONE);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void resetMicros()
    {
        Bitmap iconMicro = microsIcon();
        for(Marker m : microsMarker)
        {
            m.setIcon(BitmapDescriptorFactory.fromBitmap(iconMicro));
        }
    }
    private String calcularTiempo(double distancia) {
            String[] metros = String.valueOf(distancia).split("\\.");
            double k = Integer.parseInt(metros[0]);
            double kilometros = k / 1000;

            int kmh = 40;
            double tiempo = (kilometros / kmh) * 60; //40 km/h

            if (tiempo > 60) {
                double horasMin = tiempo / 60;
                if ((horasMin - (int) horasMin) != 0) {
                    String[] horasMinArray = String.valueOf(horasMin).split("\\.");
                    int horas = Integer.parseInt(horasMinArray[0]);
                    String m = (0 + "." + Integer.parseInt(horasMinArray[1]));
                    double minutosDecimales = Integer.parseInt(m) * 60;

                    String[] minutosSplit = String.valueOf(minutosDecimales).split("\\.");
                    int minutos = Integer.parseInt(minutosSplit[0]);

                    return horas + " hora " + minutos + " minutos.";
                } else {
                    int horas = (int) horasMin;
                    return horas + " hora/s.";
                }

            }
            else
            {
                if ((tiempo - (int) tiempo) != 0)
                {
                    int segundosInt;

                    //Decimales
                    String[] minSeg = String.valueOf(tiempo).split("\\.");
                    int minutos = Integer.parseInt(minSeg[0]);
                    String s = (0 + "." + Integer.parseInt(minSeg[1]));
                    double segundos = Double.parseDouble(s) * 60;
                    if ((segundos - (int) segundos) != 0) {
                        String[] segDecimals = String.valueOf(segundos).split("\\.");
                        segundosInt = Integer.parseInt(segDecimals[0]);
                    }
                    else
                    {
                        segundosInt = (int) segundos;
                    }
                     return minutos + " minutos " + segundosInt + " segundos.";

                }
                else
                {
                    //No decimales
                    int minutos = (int) tiempo;
                    return minutos + " minutos.";
                }
            }
    }

    public Polyline crearRuta(Rutas ruta, List<Marker> marcadoresParaderos, int _color) {


        ArrayList<Coordenada> coordenadas = ruta.listaCoordenadas;
        ArrayList<Paradero> paraderos = ruta.listaParaderos;

        Bitmap icon = markerIcon();
        PolylineOptions polyLineaNueva = new PolylineOptions();

        for (Coordenada c : coordenadas) {

            polyLineaNueva.color(_color);
            polyLineaNueva.add(new LatLng(c.latitud, c.longitud));

        }
        Polyline nuevaPolyline = mMap.addPolyline(polyLineaNueva);

        for (Paradero p : paraderos) {

            Marker paradero = mMap.addMarker(new MarkerOptions().position(new LatLng(p.latitud, p.longitud))
                    .icon(BitmapDescriptorFactory.fromBitmap(icon))
                    .title("Paradero")
                    .snippet("Seleccione aquí"));

            paradero.setTag(p.id);
            marcadoresParaderos.add(paradero);
            //marker.position(new LatLng(p.latitud,p.longitud));
            //marcadorVuelta = mMap.addMarker(marker);
        }
        return nuevaPolyline;

    }

    public void cargarSpinner() {
        Linea linea = new Linea();
        items = linea.obtenerNombres();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    //Obtengo desde "drawable" el diseño del marcador y lo envío al 'crearRuta'
    public Bitmap markerIcon() {

        Bitmap smallMarker;

        int largo = 72;
        int ancho = 46;
        BitmapDrawable bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.stop2);
        Bitmap b = bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, ancho, largo, false);

        return smallMarker;
    }

    public Bitmap microsIcon() {

        Bitmap smallMarker;

        int largo = 68;
        int ancho = 42;
        BitmapDrawable bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.micro_activa);
        Bitmap b = bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, ancho, largo, false);

        return smallMarker;
    }

    public Bitmap microsParaderoIcon() {

        Bitmap smallMarker;

        int largo = 68;
        int ancho = 42;
        BitmapDrawable bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.micro_paradero);
        Bitmap b = bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, ancho, largo, false);

        return smallMarker;
    }

    public Bitmap pasajeroIcon() {

        Bitmap smallMarker;

        int largo = 68;
        int ancho = 42;
        BitmapDrawable bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.pasajeros_marker);
        Bitmap b = bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, ancho, largo, false);

        return smallMarker;
    }

    //Remuevo la lista de Paraderos
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


    private void removerMicros() {

        for (Marker micro : microsMarker) {
            micro.remove();
        }
        microsMarker.clear();
    }

    private void reiniciarParaderos()
    {
        Bitmap stopIcon = markerIcon();
        for(Marker paraderoIda : paraderosRutaIda)
        {
            paraderoIda.setIcon(BitmapDescriptorFactory.fromBitmap(stopIcon));
            for(Marker paraderoVuelta : paraderosRutaVuelta)
            {
                paraderoVuelta.setIcon(BitmapDescriptorFactory.fromBitmap(stopIcon));
            }
        }
    }

    public void detenerServicio() {
        stopService(new Intent(getBaseContext(), ServicePosition.class));
        new AsyncTaskServerPosition.StopPosition().execute(idUser);
    }

    public void detenerServicioLineaFusion() {
        stopService(new Intent(getBaseContext(), LineaFusionService.class));
    }

    @Override
    protected void onStop() {
        super.onStop();

        SesionPasajero.idParaderoGuardado = idParaderoSeleccionado;
        SesionPasajero.idLineaPosicionGuardada = posicionLineaActual;

        SesionPasajero.rutaIda = rutaDeIdaActual;
        SesionPasajero.rutaVuelta = rutaDeVueltaActual;

        //spinner.setAdapter(null);
        //detenerServicio();
        //detenerServicioLineaFusion();
    }


    public void iniciarServicio()
    {
        Intent intent = new Intent(getBaseContext(), ServicePosition.class);
        intent.putExtra("usuarioId", idUser);
        startService(intent);
    }
    public void iniciarServicioLineaFusion()
    {
        Intent intent = new Intent(getBaseContext(), LineaFusionService.class);
        intent.putExtra("usuarioId", idUser);
        startService(intent);
    }

    public void removerFragment()
    {
        progressCalificacion.setVisibility(View.GONE);
        FragmentManager fm = getSupportFragmentManager();
        if(fm.getBackStackEntryCount()>0)
        {
            fm.popBackStack();
        }
    }

    public void calificarDialog(final double calificacionGlobal)
    {

        AlertDialog.Builder dialog = new AlertDialog.Builder(UserMapActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("¿Desea calificar esta micro?" );
        dialog.setNegativeButton("Más tarde", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                alert.cancel();
            }
        });
        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                FragmentManager FM = getSupportFragmentManager();
                FragmentTransaction FT = FM.beginTransaction();

                Bundle bundle = new Bundle();
                bundle.putInt("idMicro", idMicroGlobal);
                bundle.putDouble("cGlobal", calificacionGlobal);

                Fragment fragment = new CalificacionFragment();
                fragment.setArguments(bundle);
                FT.replace(R.id.frame_content_info, fragment);
                FT.addToBackStack(null);
                FT.commit();
            }
        });
        alert = dialog.create();
        alert.show();
    }

    public void verificarMiMicroAbordada(JSONObject miMicroObject, int idLinea)
    {
        try {

            int idMiMicro = miMicroObject.getInt("Id");

            if(idMiMicro != -1)
            {
                estabaEnMicro = true;
                estaAbierto = true;
                calificacionGlobal = miMicroObject.getDouble("Calificacion");

                if(idMicroGlobal == -1) //Entra una sola vez
                {
                    btnDeseleccionarParadero.setVisibility(View.GONE); //Desaparece el boton de eleccionar paradero.
                    idMicroGlobal = idMiMicro;
                    String patente = miMicroObject.getString("Patente");
                    Linea linea = Linea.BuscarLineaPorId(idLinea);
                    String nombreLinea = linea.nombreLinea;

                    relativeLayoutInfoMicro.setVisibility(View.VISIBLE);

                    patenteView.setText(patente);
                    nombreLineaView.setText(nombreLinea);

                    reiniciarParaderos();
                    calificarDialog(calificacionGlobal);
                }

                ratingBarView.setRating(calificacionGlobal.floatValue());

            }
            else
            {
                if( idMicroGlobal != -1)
                {
                    if(estabaEnMicro == true)
                    {
                        estabaEnMicro = false;
                        if(aCalificado == true)
                        {
                            btnCalificarMicro.setEnabled(true);
                        }

                        relativeLayoutInfoMicro.setVisibility(View.GONE);
                        spinnerLayout.setVisibility(View.VISIBLE);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void recibirPosicion(JSONObject posicion)
    {
        try {

            Bitmap iconPasajero = pasajeroIcon();

            double lat = posicion.getDouble("Latitud");
            double lng = posicion.getDouble("Longitud");

            if(marcador != null)
            {
                marcador.setPosition(new LatLng(lat,lng));
            }
            else
            {
                marcador = mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng))
                        .icon(BitmapDescriptorFactory.fromBitmap(iconPasajero))
                        .title("Mi posición"));
            }

            String TAG = "RecibirPosicion";

            Log.d(TAG, "lat: " + lat + " lng: "+lng);

            //new AsyncTaskServerPosition.SendPosition().execute(latLng.toString(),idSession);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Bitmap markerParaderoSig() {

        Bitmap smallMarker;

        int largo = 72;
        int ancho = 46;
        BitmapDrawable bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.stop_sig);
        Bitmap b = bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, ancho, largo, false);

        return smallMarker;
    }

    public void recibirParaderoSeleccionado()
    {
        paraderoSeleccionado = true;
        paraderoEncontrado = false;

        String tagId;
        Bitmap MiparaderoIcon = markerParaderoSig();
        resetParaderos();

        for (Marker pIda : paraderosRutaIda) {
            pIda.setTitle("Paradero");
            tagId = pIda.getTag().toString();

            if (tagId.equals(idParaderoSeleccionado+"")) {
                paraderoEncontrado = true;
                pIda.setTitle("Mi paradero Seleccionado");
                pIda.setIcon(BitmapDescriptorFactory.fromBitmap(MiparaderoIcon));
                //alertParadero.cancel();
                iniciarServicioLineaFusion();
            }

        }

        if (paraderoEncontrado == false) {
            for (Marker pVuelta : paraderosRutaVuelta) {
                pVuelta.setTitle("Paradero");
                tagId = pVuelta.getTag().toString();

                if (tagId.equals(idParaderoSeleccionado + "")) {
                    pVuelta.setTitle("Mi paradero Seleccionado");
                    pVuelta.setIcon(BitmapDescriptorFactory.fromBitmap(MiparaderoIcon));
                    //alertParadero.cancel();
                    iniciarServicioLineaFusion();
                }
            }
        }
    }


    public void paraderoDeseleccionado()
    {
        resetParaderos();
    }

    public void resetParaderos()
    {
        Bitmap paraderoIcon = markerIcon();
        for(Marker ida : paraderosRutaIda)
        {
            ida.setTitle("Paradero");
            ida.setSnippet("Seleccione aquí");
            ida.setIcon(BitmapDescriptorFactory.fromBitmap(paraderoIcon));
            for(Marker vuelta : paraderosRutaVuelta)
            {
                vuelta.setTitle("Paradero");
                vuelta.setSnippet("Seleccione aquí");
                vuelta.setIcon(BitmapDescriptorFactory.fromBitmap(paraderoIcon));
            }
        }
    }

    public void obtenerParametrosFusionados(JSONObject parametros)
    {
        try {
            Log.e("PARAMETROSUSERMAP", parametros.toString());

            int idLinea  = parametros.getInt("IdLineaChoferes");
            JSONObject microPraderoCercano = parametros.getJSONObject("MicroParaderoCercano"); //TODO: Si es un -1 debe desaparecer el mensaje del tiempo
            JSONObject microAbordada = parametros.getJSONObject("MicroAboradada");
            JSONArray choferes = parametros.getJSONArray("Choferes");

            if(idLineaSeleccionada != idLinea)
            {
                removerMicros();
            }
            else if(idLineaSeleccionada == idLinea)
            {

                //alertLineas.cancel();
                obtenerMicrosDelParadero(microPraderoCercano, idLinea);
                verificarMiMicroAbordada(microAbordada, idLinea);
                agregarMicros(choferes);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void cargandoParadero()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(UserMapActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Obteniendo datos, por favor espere.");
        alertParadero = dialog.create();
        alertParadero.show();
    }

    public void cargandoLinea()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(UserMapActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Obteniendo datos, por favor espere.");
        alertLineas = dialog.create();
        alertLineas.show();
    }

    //TIMERS
    private void actualizarPosicionMicros()
    {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {

                Log.e("LineaID", idLineaSeleccionada+"");
                new Micro.ObtenerMicrosPorLinea().execute(idLineaSeleccionada + "");
            }
        }, 0, 2000);
    }

    private void startTimer() //actualizarMicrosQueSeleccionaronParaderos LA MISMA
    {
        timerMicroParaderoMasCercano = new Timer();
        timerMicroParaderoMasCercano.scheduleAtFixedRate(new TimerTask() {
            public void run() {

                Log.e("ParaderoIDASDF", idParaderoSeleccionado+"");
                new Paradero.MicrosQueSeleccionaronParadero().execute(idParaderoSeleccionado+"");
            }
        }, 0, 1000);
    }

    private void obtenerPosicion() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {

                Log.e("UsuarioID", idUser);
                new Micro.CambiarPosicion().execute(idUser);
            }
        }, 0, 1000);
    }

    private void actualizarMiMicroAbordada() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {

                Log.e("UsuarioID", idUser);
                new Usuario.MiMicroAbordada().execute(idUser);
            }
        }, 0, 3000);
    }
}
