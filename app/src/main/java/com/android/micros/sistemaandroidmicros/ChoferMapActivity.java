package com.android.micros.sistemaandroidmicros;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.android.micros.sistemaandroidmicros.Clases.ActivityController;
import com.android.micros.sistemaandroidmicros.Clases.Coordenada;
import com.android.micros.sistemaandroidmicros.Clases.Linea;
import com.android.micros.sistemaandroidmicros.Clases.Micro;
import com.android.micros.sistemaandroidmicros.Clases.Paradero;
import com.android.micros.sistemaandroidmicros.Clases.Rutas;
import com.android.micros.sistemaandroidmicros.Clases.Usuario;
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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.android.micros.sistemaandroidmicros.networkController.networkCreate;
import static com.android.micros.sistemaandroidmicros.networkController.networkStatus;

public class ChoferMapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    //INFORMACIÓN PERSONAL
    String nombre;
    String email;
    String patente;
    String lineaNombre;
    String kilometrosDia;

    boolean estaEnRecorrido = false;
    int idParaderoSeleccionado = -1;

    boolean paraderoValido = false;
    boolean modoTest = true;
    boolean stopRunner = false;
    boolean paraderoEncontrado;

    private GoogleMap cMap;
    private int idChofer;
    private int idMicro;
    private int idLineaActual;

    private TextView txtNombreNavHeaderChofer;
    private TextView txtCorreoNavHeaderChofer;
    private TextView txtLineaChof, txtPatenteChof, txtKilometrosDia;
    private TextView lblMensaje;
    private Switch switchTest;

    static TextView check_connection;
    private BroadcastReceiver mNetworkReceiver;

    private ArrayList<Marker> usuariosMarker = new ArrayList<>();

    UserSessionManager session;
    public String idSession;
    Polyline polylineIda;
    Polyline polylineVuelta;
    Micro microActual;
    Marker miMicroMarker;

    public static Location ultimaLocalizacion = null;

    Button btnIniciarRecorrido;

    private List<Marker> paraderosRutaIda = new ArrayList<>();
    private List<Marker> paraderosRutaVuelta = new ArrayList<>();

    LinearLayout recorridoLayout;

    private double coorLat;
    private double coorLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chofer_map);
        ActivityController.activiyAbiertaActual = this;

        session = new UserSessionManager(getApplicationContext());

        if (session.checkLogin())
            finish();

        HashMap<String, String> user = session.obtenerDetallesUsuario();
        HashMap<String, String> chofer = session.obtenerRolyId();

        idSession = chofer.get(UserSessionManager.KEY_ID);
        nombre = user.get(UserSessionManager.KEY_NAME);
        email = user.get(UserSessionManager.KEY_EMAIL);
        check_connection = (TextView) findViewById(R.id.tv2_check_connection);

        btnIniciarRecorrido = (Button) findViewById(R.id.btnComenzarRecorrido);
        btnIniciarRecorrido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                detenerServicioRecorridoFusion();
                btnIniciarRecorrido.setText("Recorrido Iniciado");
                btnIniciarRecorrido.setEnabled(false);
                btnIniciarRecorrido.setBackgroundColor(Color.GRAY);


                new Micro.IniciarRecorrido().execute(microActual.id + "");

            }
        });

        lblMensaje = (TextView) findViewById(R.id.lblMensaje);
        txtLineaChof = (TextView) findViewById(R.id.txtLineaChof);
        txtPatenteChof = (TextView) findViewById(R.id.txtPatenteChof);
        txtKilometrosDia = (TextView) findViewById(R.id.txtKilometrosDia);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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


        new Micro.ObtenerMicroDeChofer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, idSession);

            mNetworkReceiver = new NetworkChangeReceiverChofer();

        registerNetworkBroadcastForNougat();
        iniciarServicio();               //Usuarios(5)/ActualizarPosicion
        enviarPosicionChofer();

    }

    public void iniciarServicioRecorridoFusion() {
        iniciarServicioRecorridoFusionDX();
    }

    public void validarLinea(Micro micro) {

        lblMensaje.setText("");
        if (micro.id == -1) {
            //lblMensaje.setText("No está asociado a una micro");
            //No está asociado a una micro
        } else {
            lblMensaje.setText("");
            microActual = micro;
            patente = micro.patente;

            Rutas rutaIda = new Rutas();
            Rutas rutaVuelta = new Rutas();

            Linea linea = new Linea();
            linea = Linea.BuscarLineaPorId(micro.lineaId);
            idLineaActual = linea.idLinea;
            lineaNombre = linea.nombreLinea;
            //String kilometros = micro.kilometrosDia+"";

            txtLineaChof.setText(lineaNombre);
            txtPatenteChof.setText(patente);

            rutaIda = Rutas.BuscarRutaPorId(linea.idRutaIda);
            rutaVuelta = Rutas.BuscarRutaPorId(linea.idRutaVuelta);

            polylineIda = crearRuta(rutaIda, paraderosRutaIda, Color.RED);
            polylineVuelta = crearRuta(rutaVuelta, paraderosRutaVuelta, Color.BLUE);

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
        Polyline nuevaPolyline = cMap.addPolyline(polyLineaNueva);


        for (Paradero p : paraderos) {
            Marker paradero = cMap.addMarker(new MarkerOptions().position(new LatLng(p.latitud, p.longitud))
                    .icon(BitmapDescriptorFactory.fromBitmap(icon))
                    .title("Paradero"));

            paradero.setTag(p.id);
            marcadoresParaderos.add(paradero);
            //marker.position(new LatLng(p.latitud,p.longitud));
            //marcadorVuelta = mMap.addMarker(marker);
        }

        return nuevaPolyline;

    }

    public Bitmap markerIcon() {

        Bitmap smallMarker;

        int largo = 72;
        int ancho = 46;
        BitmapDrawable bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.stop2);
        Bitmap b = bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, ancho, largo, false);

        return smallMarker;
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

        txtCorreoNavHeaderChofer = (TextView) findViewById(R.id.txtCorreoNavHeaderChofer);
        txtNombreNavHeaderChofer = (TextView) findViewById(R.id.txtNombreNavHeaderChofer);

        txtNombreNavHeaderChofer.setText(nombre);
        txtCorreoNavHeaderChofer.setText(email);

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
            new Usuario.DetenerPosicion().execute(idSession);
            detenerServicio();
            session.logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

            Intent intent = new Intent(ChoferMapActivity.this, ChoferMapActivity.class);
            startActivity(intent);
            finish();


        } else if (id == R.id.nav_gallery) {

            Intent intent = new Intent(ChoferMapActivity.this, HistorialActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("idMicro", microActual.id + "");
            intent.putExtras(bundle);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_slideshow) {
            FragmentManager FM = getSupportFragmentManager();
            FragmentTransaction FT = FM.beginTransaction();

            Bundle bundle = new Bundle();
            bundle.putString("nombre", nombre);
            bundle.putString("email", email);
            bundle.putString("patente", patente);
            bundle.putString("linea", lineaNombre);

            Fragment fragment = new PerfilChoferFragment();
            fragment.setArguments(bundle);

            FT.replace(R.id.frame_content_chofer_info, fragment);
            FT.addToBackStack(null);
            FT.commit();

        } else if (id == R.id.nav_share) {


            if (modoTest == true) {

                modoTest = false;
                stopRunner = true;
                item.setTitle("Desactivar Modo Tester"); //tester activado
                detenerServicio();
                obtenerPosicionChofer();
            } else {
                modoTest = true;
                stopRunner = false;
                item.setTitle("Activar Modo Tester"); //tester desactivado
                iniciarServicio();
                enviarPosicionChofer();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        cMap = googleMap;

        LatLng position = new LatLng(-40.5769389, -73.1260218);
        cMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 14));

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

    }

    public void detenerServicio() {
        stopService(new Intent(getBaseContext(), ServicePositionChofer.class));
        new AsyncTaskServerPosition.StopPosition().execute(idSession);
    }

    public void iniciarServicio() {
        Intent intent = new Intent(getBaseContext(), ServicePositionChofer.class);
        intent.putExtra("usuarioId", idSession);
        startService(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
        new Usuario.DetenerPosicion().execute(idSession);
        //detenerServicioRecorridoFusion();
        //detenerServicio();
    }

    @Override
    protected void onRestart() {
        //Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();
        super.onRestart();
        iniciarServicio();
    }

    public void recibirPosicion(JSONObject posicion) {
        try {
            Bitmap icon = microsIcon();

            double lat = posicion.getDouble("Latitud");
            double lng = posicion.getDouble("Longitud");
            //Toast.makeText(ChoferMapActivity.this, lat+"-"+lng, Toast.LENGTH_SHORT).show();

            if (miMicroMarker != null) {
                miMicroMarker.setPosition(new LatLng(lat, lng));
            } else {
                miMicroMarker = cMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                        .icon(BitmapDescriptorFactory.fromBitmap(icon))
                        .title("Mi posición"));
            }

            String TAG = "RecibirPosicion";

            Log.d(TAG, "lat: " + lat + " lng: " + lng);

            //new AsyncTaskServerPosition.SendPosition().execute(latLng.toString(),idSession);

        } catch (JSONException e) {
            String m = e.getMessage();
            Log.d("ERRORMICROC",m);
        }
    }

    private void obtenerPosicionChofer() {
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (stopRunner == true) {
                    Log.e("IDSESSION", idSession + "");
                    new Usuario.ObtenerPosicionChofer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, idSession);

                } else {
                    timer.cancel();
                    timer.purge();
                }

            }
        }, 0, 1000);
    }

    private void enviarPosicionChofer() {
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (stopRunner == false) {
                    Log.e("IDSESSION", idSession + "");

                    obtenerCoor();

                } else {
                    timer.cancel();
                    timer.purge();
                }

            }
        }, 0, 1000);
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

    public Bitmap pasajeroIcon() {

        Bitmap smallMarker;

        int largo = 68;
        int ancho = 42;
        BitmapDrawable bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.pasajeros_marker);
        Bitmap b = bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, ancho, largo, false);

        return smallMarker;
    }


    public void recibirMiParadero(int miParadero) {

        paraderoEncontrado = false;
        Log.e("IDMIPARADERO0", miParadero + "" + paraderoValido);
        Bitmap MiparaderoIcon = markerParaderoSig();
        Bitmap paraderoIcon = markerIcon();

        String tagId;
        if (miParadero != -1) {
            paraderoValido = true;
            Log.e("IDMIPARADERO", miParadero + "" + paraderoValido);

            for (Marker pIda : paraderosRutaIda) {
                pIda.setIcon(BitmapDescriptorFactory.fromBitmap(paraderoIcon));
                pIda.setTitle("Paradero");
                tagId = pIda.getTag().toString();

                if (tagId.equals(miParadero + "")) {
                    paraderoEncontrado = true;
                    idParaderoSeleccionado = miParadero;
                    pIda.setTitle("Mi paradero");
                    pIda.setIcon(BitmapDescriptorFactory.fromBitmap(MiparaderoIcon));
                    //paraderoEncontrado = pIda;
                }

            }

            if (paraderoEncontrado == false) {
                for (Marker pVuelta : paraderosRutaVuelta) {
                    pVuelta.setIcon(BitmapDescriptorFactory.fromBitmap(paraderoIcon));
                    pVuelta.setTitle("Paradero");
                    tagId = pVuelta.getTag().toString();
                    if (tagId.equals(miParadero + "")) {
                        idParaderoSeleccionado = miParadero;
                        pVuelta.setTitle("Mi paradero");
                        pVuelta.setIcon(BitmapDescriptorFactory.fromBitmap(MiparaderoIcon));
                        //paraderoEncontrado = pVuelta;
                    }
                }
            }
        } else {

            if (paraderoValido == true) {
                Log.e("IDMIPARADERO45", miParadero + "" + paraderoValido);
                paraderoValido = false;
                for (Marker pIda : paraderosRutaIda) {
                    Log.e("PARADERORUTAIDA", "asdf");
                    pIda.setIcon(BitmapDescriptorFactory.fromBitmap(paraderoIcon));

                    for (Marker pVuelta : paraderosRutaVuelta) {
                        Log.e("PARADERORUTAVUELTA", "asdf");
                        pVuelta.setIcon(BitmapDescriptorFactory.fromBitmap(paraderoIcon));
                    }
                }
            }
        }
        //buscarMisPasajeros();     //Paraderos(5)/UsuariosQueSeleccionaron
    }

    public void recibirUsuariosParadero(JSONArray usuarios) {

        if (usuarios.length() != 0) {

            Bitmap iconPasajero = pasajeroIcon();

            try {
                borrarUsuariosMarcadores(usuarios);
                Log.e("USUARIOSJSONXD2", usuarios.toString());

                for (int i = 0; i < usuarios.length(); i++) {

                    JSONObject usuario = null;
                    usuario = usuarios.getJSONObject(i);
                    int id = usuario.getInt("UsuarioId");

                    if (id != -1) {
                        double lat = usuario.getDouble("Latitud");
                        double lng = usuario.getDouble("Longitud");
                        double distancia = usuario.getDouble("Distancia");

                        Marker usuarioMarker = obtenerMarcadorUsuario(id);

                            String tiempo = calcularTiempo(distancia);
                            String[] metrosString = String.valueOf(distancia).split("\\.");
                            int metros = Integer.parseInt(metrosString[0]);

                        if (usuarioMarker != null) {
                            usuarioMarker.setPosition(new LatLng(lat, lng));
                            usuarioMarker.setTitle(tiempo);
                            usuarioMarker.setSnippet(metros+" metros");
                            usuarioMarker.showInfoWindow();
                        } else {

                                Log.e("USUARIOCHOFER", lat + lng + "");
                                Marker m = cMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(tiempo)
                                        .icon(BitmapDescriptorFactory.fromBitmap(iconPasajero))
                                        .snippet(metros+" metros"));
                                m.showInfoWindow();
                                m.setTag(id);
                                usuariosMarker.add(m);

                        }

                    }
                }

            } catch (JSONException e) {
                Log.e("JSONEXCEPTIONUSUARIOS", e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                Log.e("EXCEPTIONASDF", e.getMessage());
            }
        } else {
            borrarUsuariosMarcadores(usuarios);
        }
    }

    private Marker obtenerMarcadorUsuario(int idUsuario) {
        Marker usuarioMarker = null;
        String idUserStr = idUsuario + "";
        for (Marker u : usuariosMarker) {
            if (u.getTag().toString().equals(idUserStr)) {
                usuarioMarker = u;
            }
        }
        return usuarioMarker;
    }

    private boolean borrarPorPasajero(Marker pasajero)
    {
        Marker pasajeroAeliminar = null;
        for(Marker p : usuariosMarker)
        {
            if(p.getTag().toString().equals(pasajero.getTag().toString()))
            {
                pasajeroAeliminar = p;
            }
        }

        if(pasajeroAeliminar != null)
        {
            usuariosMarker.remove(pasajeroAeliminar);
            return true;
        }
        else{
            return false;
        }
    }

    private void borrarUsuariosMarcadores(JSONArray usuarios) {

        try {

            ArrayList<Marker> usuariosParaBorrar = new ArrayList<>();
            int rishi = 0;
            rishi = usuariosMarker.size();
            Log.e("USUARIOSJSONXD", usuarios.toString());
            Log.e("USUARIOSMARKERSIZE", rishi + "");

            if (usuarios.length() == 0) {
                Log.e("USUARIOSMARKERSIZE546", "entró");
                ArrayList<Marker> borrarTodo = usuariosMarker;
                for (Marker bt : borrarTodo) {

                    usuariosMarker.remove(bt);
                    bt.remove();
                }
            } else if (rishi != 0) {
                for (Marker us : usuariosMarker) {
                    Marker usuarioEncontrado = null;
                    String tagId = us.getTag().toString();
                    Log.e("USUARIOTAG2", tagId);
                    for (int i = 0; i < usuarios.length(); i++) {

                        JSONObject usuario = null;
                        usuario = usuarios.getJSONObject(i);
                        int id = usuario.getInt("UsuarioId");
                        Log.e("IDUSUARIOJSONXDXD", id + "");

                        if (tagId.equals(id + "")) {
                            Log.e("USUARIOTAG", us.getTag().toString());
                            usuarioEncontrado = us;
                        }
                    }
                    if (usuarioEncontrado == null) {
                        Log.e("USUARIOSJSONXD3", usuarios.toString());
                        usuariosParaBorrar.add(us);

                    }
                }

                for (Marker usd : usuariosParaBorrar) {
                    Log.e("USUARIOSJSONXD4", usuarios.toString());
                    usuariosMarker.remove(usd);
                    usd.remove();
                }
            }
        } catch (Exception e) {
            Log.e("ExceptionUSUARIOXD", e.getMessage());
        }

    }

    public void validarSigCoord(boolean esNull) {
        if (esNull == true) {
            //btnIniciarRecorrido.setEnabled(false);
        } else {
            //btnIniciarRecorrido.setEnabled(true);
        }
    }

    public void verificarSiguienteVertice(int idSgtVertice) {
        Log.e("IDSIGUIENTEVERTICE1", idSgtVertice + "");
        if (estaEnRecorrido == false && idSgtVertice != -1) {
            Log.e("IDSIGUIENTEVERTICE2", estaEnRecorrido + "");
            estaEnRecorrido = true;
        } else if (estaEnRecorrido == true && idSgtVertice == -1) {
            int orange = Color.parseColor("#f46e00");

            estaEnRecorrido = false;
            Log.e("IDSIGUIENTEVERTICE3", estaEnRecorrido + "");
            btnIniciarRecorrido.setText("Iniciar Recorrido");
            btnIniciarRecorrido.setBackgroundColor(orange);
            btnIniciarRecorrido.setEnabled(true);
        }
    }

    public void obtenerParametrosFusionadosChofer(JSONObject parametros) {
        try {
            Log.e("JSONPARAMETROS", parametros.toString());
            int idSiguienteParadero = parametros.getInt("IdSiguienteParadero");
            recibirMiParadero(idSiguienteParadero);
            int idSgtVertice = parametros.getInt("IdSiguienteVertice");
            verificarSiguienteVertice(idSgtVertice);

            if (idSiguienteParadero != -1) {
                Log.e("PARAMETROSCHOFER", parametros.toString());
                JSONObject miMicro = parametros.getJSONObject("MiMicro");
                JSONArray usuarioParaderos = parametros.getJSONArray("UsuarioParaderos");

                Micro micro = new Micro();
                micro.id = miMicro.getInt("Id");
                micro.patente = miMicro.getString("Patente");
                String calificacion = miMicro.getString("Calificacion");
                micro.calificacion = Float.valueOf(calificacion);
                double kmt = miMicro.getDouble("KilometrosDia");

                DecimalFormat df = new DecimalFormat("#.###");
                df.setRoundingMode(RoundingMode.CEILING);
                String km = df.format(kmt).toString();

                recibirUsuariosParadero(usuarioParaderos);
                txtKilometrosDia.setText(km);
                Log.e("KILOMETROSXDIA", km);

                //validarLinea(micro);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void iniciarServicioRecorridoFusionDX() {
        Intent intent = new Intent(getBaseContext(), RecorridoFusionService.class);
        intent.putExtra("usuarioId", idSession);
        startService(intent);
    }

    public void detenerServicioRecorridoFusion() {
        stopService(new Intent(getBaseContext(), RecorridoFusionService.class));
    }

    private String calcularTiempo(double distancia) {
        String[] metros = String.valueOf(distancia).split("\\.");
        double k = Integer.parseInt(metros[0]);
        double kilometros = k / 1000;

        int kmh = 4;
        double tiempo = (kilometros / kmh) * 60; //40 km/h

        if (tiempo > 60) {
            double horasMin = tiempo / 60;
            if ((horasMin - (int) horasMin) != 0) {
                String[] horasMinArray = String.valueOf(horasMin).split("\\.");
                int horas = Integer.parseInt(horasMinArray[0]);
                String m = (0 + "." + horasMinArray[1]);
                double minDecimales = Double.parseDouble(m);
                double minutosDecimales = minDecimales * 60;

                String[] minutosSplit = String.valueOf(minutosDecimales).split("\\.");
                int minutos = Integer.parseInt(minutosSplit[0]);

                return horas + " hora " + minutos + " minutos.";
            } else {
                int horas = (int) horasMin;
                return horas + " hora/s.";
            }

        } else {
            if ((tiempo - (int) tiempo) != 0) {
                int segundosInt;

                //Decimales
                String[] minSeg = String.valueOf(tiempo).split("\\.");
                int minutos = Integer.parseInt(minSeg[0]);
                String s = (0 + "." + minSeg[1]);
                double segundos = Double.parseDouble(s) * 60;
                if ((segundos - (int) segundos) != 0) {
                    String[] segDecimals = String.valueOf(segundos).split("\\.");
                    segundosInt = Integer.parseInt(segDecimals[0]);
                } else {
                    segundosInt = (int) segundos;
                }
                return minutos + " minutos " + segundosInt + " segundos.";

            } else {
                //No decimales
                int minutos = (int) tiempo;
                return minutos + " minutos.";
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }

    public static void dialog(boolean value) {

        if (value) {

            Log.e("0network: ", networkCreate+"");
            if(networkCreate == networkStatus)
            {
                Log.e("network: ", networkCreate+"");
            }
            else
            {
                networkCreate = true;
            }
                Handler handler = new Handler();
                Runnable delayrunnable = new Runnable() {
                    @Override
                    public void run() {
                        check_connection.setVisibility(View.GONE);
                    }
                };
                handler.postDelayed(delayrunnable, 3000);

        } else {
            Log.e("00network: ", networkCreate+"");
            if(networkCreate == true)
            {
                Log.e("network2: ", networkCreate+"");
                //networkCreate = true;
                check_connection.setVisibility(View.VISIBLE);
                check_connection.setText("No hay acceso a internet, compruebe su conexión.");
                check_connection.setBackgroundColor(Color.RED);
                check_connection.setTextColor(Color.WHITE);
            }
        }
    }


    private void registerNetworkBroadcastForNougat() {
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void obtenerCoor() {

        if(ultimaLocalizacion != null)
        {
            double latitude = ultimaLocalizacion.getLatitude();
            double longitude = ultimaLocalizacion.getLongitude();

            JSONObject actualPosition = new JSONObject();

            try {

                actualPosition.put("Latitud", latitude);
                actualPosition.put("Longitud", longitude);
                Log.e("coor", latitude+"-"+longitude);

                new Usuario.ActualizarPosicionChofer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, idSession, actualPosition.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

