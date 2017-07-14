package com.android.micros.sistemaandroidmicros;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.LogRecord;

public class ChoferMapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    //INFORMACIÓN PERSONAL
    String nombre;
    String email;
    String patente;
    String lineaNombre;

    int idParaderoSeleccionado = -1;

    boolean modoTest = true;
    boolean stopRunner;
    boolean paraderoEncontrado;

    private GoogleMap cMap;
    private int idChofer;
    private int idMicro;
    private int idLineaActual;

    private TextView lblMensaje;
    private Switch switchTest;

    private List<Marker> usuariosMarker = new ArrayList<>();

    UserSessionManager session;
    public String idSession;
    Polyline polylineIda;
    Polyline polylineVuelta;
    Micro microActual;
    Marker miMicroMarker;

    Button btnIniciarRecorrido;

    private List<Marker> paraderosRutaIda = new ArrayList<>();
    private List<Marker> paraderosRutaVuelta = new ArrayList<>();

    LinearLayout recorridoLayout;

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

        btnIniciarRecorrido = (Button) findViewById(R.id.btnComenzarRecorrido);
        btnIniciarRecorrido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            btnIniciarRecorrido.setText("Recorrido Iniciado");
            btnIniciarRecorrido.setEnabled(false);
            btnIniciarRecorrido.setBackgroundColor(Color.GRAY);


            new Micro.IniciarRecorrido().execute(microActual.id+"");

            }
        });

        lblMensaje = (TextView) findViewById(R.id.lblMensaje);
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


        new Micro.ObtenerMicroDeChofer().execute(idSession);

        //Métodos que se ejecutan constantemente --------------

        //actualizarMiMicro();             //Usuarios(5)/ObtenerMicro
        actualizarRecorrido();
        iniciarServicio();               //Usuarios(5)/ActualizarPosicion
    }

    public void validarLinea(Micro micro) {

        if (micro.id == -1) {
            lblMensaje.setText("No está asociado a una micro");
            //No está asociado a una micro
        }
        else
        {

            microActual = micro;
            patente = micro.patente;

            Rutas rutaIda = new Rutas();
            Rutas rutaVuelta = new Rutas();

            Linea linea = new Linea();
            linea = Linea.BuscarLineaPorId(micro.lineaId);
            idLineaActual = linea.idLinea;
            lineaNombre = linea.nombreLinea;

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

            Intent intent = new Intent(ChoferMapActivity.this, ChoferMapActivity.class);
            startActivity(intent);
            finish();


        } else if (id == R.id.nav_gallery) {

            Intent intent = new Intent(ChoferMapActivity.this, HistorialActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("idMicro", microActual.id+"");
            intent.putExtras(bundle);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_slideshow)
        {
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

        }
        else if (id == R.id.nav_share)
        {


            if(modoTest == true)
            {

                modoTest = false;
                stopRunner = false;
                item.setTitle("Desactivar Modo Tester");
                obtenerPosicion();
            }
            else
            {
                modoTest = true;
                stopRunner = true;
                item.setTitle("Activar Modo Tester");
                obtenerPosicion();
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
        super.onStop();
        Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
        new Usuario.DetenerPosicion().execute(idSession);
        detenerServicio();
    }

    @Override
    protected void onRestart()
    {
        Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();
        super.onRestart();
        iniciarServicio();
    }

    public void recibirPosicion(JSONObject posicion)
    {
        try {
            Bitmap icon = microsIcon();

            double lat = posicion.getDouble("Latitud");
            double lng = posicion.getDouble("Longitud");
            Toast.makeText(ChoferMapActivity.this, lat+"-"+lng, Toast.LENGTH_SHORT).show();

            if(miMicroMarker != null)
            {
                miMicroMarker.setPosition(new LatLng(lat,lng));
            }
            else
            {
                miMicroMarker = cMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng))
                        .icon(BitmapDescriptorFactory.fromBitmap(icon))
                        .title("Mi posición"));
            }

            String TAG = "RecibirPosicion";

            Log.d(TAG, "lat: " + lat + " lng: "+lng);

            //new AsyncTaskServerPosition.SendPosition().execute(latLng.toString(),idSession);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void obtenerPosicion()
    {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {

                Log.e("IDSESSION", idSession+"");
                new Micro.CambiarPosicion().execute(idSession);
            }
        }, 0, 1000);
    }

    private void buscarMiParadero()
    {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {

                Log.e("MICROID", microActual+"");
                new Paradero.ObtenerMiParadero().execute(microActual.id+"");
            }
        }, 0, 1000);
    }

    private void buscarMisPasajeros()
    {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {

                Log.e("buscarMisPasajeros", idParaderoSeleccionado+"");
                new Paradero.UsuariosQueSeleccionaronParadero().execute(idParaderoSeleccionado+"");
            }
        }, 0, 1000);
    }

    private void actualizarRecorrido()
    {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                new Usuario.ObtenerDatosRecorridoFusion().execute(idSession);
            }
        }, 0, 1000);
    }

    public void actualizarMiMicro()
    {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask task = new TimerTask()
        {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try
                        {
                            new Micro.ObtenerMiMicroConstanteMente().execute(idSession.toString());
                        }
                        catch (Exception e)
                        {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(task, 0, 500);
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


    public void recibirMiParadero(int miParadero)
    {

                paraderoEncontrado = false;

                Bitmap MiparaderoIcon = markerParaderoSig();
                Bitmap paraderoIcon = markerIcon();

                String tagId;
                if (miParadero != -1)
                {
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
                }
                else
                {
                    for(Marker pIda : paraderosRutaIda)
                    {
                        pIda.setIcon(BitmapDescriptorFactory.fromBitmap(paraderoIcon));
                    }

                    for(Marker pVuelta : paraderosRutaVuelta)
                    {
                        pVuelta.setIcon(BitmapDescriptorFactory.fromBitmap(paraderoIcon));
                    }
                }
            //buscarMisPasajeros();     //Paraderos(5)/UsuariosQueSeleccionaron
    }

    public void recibirUsuariosParadero(JSONArray usuarios)
    {

        if(usuarios.length() != 0)
        {

            Bitmap iconPasajero = pasajeroIcon();

            try {

                borrarUsuariosMarcadores(usuarios);

                for(int i=0; i<usuarios.length(); i++)
                {
                    JSONObject usuario = null;
                    usuario = usuarios.getJSONObject(i);
                    int id = usuario.getInt("UsuarioId");

                    if(id == -1)
                    {
                        double lat = usuario.getDouble("Latitud");
                        double lng = usuario.getDouble("Longitud");
                        double distancia = usuario.getDouble("Distancia");

                        Marker usuarioMarker = obtenerMarcadorUsuario(id);

                        if(usuarioMarker != null)
                        {
                            usuarioMarker.setPosition(new LatLng(lat,lng));
                        }
                        else
                        {
                            Marker m = cMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("Soy una pasajero")
                            .icon(BitmapDescriptorFactory.fromBitmap(iconPasajero)));
                            m.setTag(id);
                            usuariosMarker.add(m);
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private Marker obtenerMarcadorUsuario(int idUsuario)
    {
        Marker usuarioMarker = null;
        String idUserStr = idUsuario+"";
        for(Marker u : usuariosMarker)
        {
            if(u.getTag().toString().equals(idUserStr))
            {
                usuarioMarker = u;
            }
        }
        return usuarioMarker;
    }

    private void borrarUsuariosMarcadores(JSONArray usuarios) throws JSONException {

        Marker usuarioEncontrado = null;
        for(Marker us : usuariosMarker)
        {
            String tagId = us.getTag().toString();
            for(int i = 0; i<usuarios.length(); i++)
            {
                JSONObject usuario = null;
                usuario = usuarios.getJSONObject(i);
                int id = usuario.getInt("UsuarioId");

                if(tagId.equals(id+""))
                {
                    usuarioEncontrado = us;
                }
            }

            if(usuarioEncontrado == null)
            {
                usuarioEncontrado.remove();
                usuariosMarker.remove(usuarioEncontrado);
            }
        }
    }

    public void validarSigCoord(boolean esNull)
    {
        if(esNull == true)
        {
            //btnIniciarRecorrido.setEnabled(false);
        }
        else
        {
            //btnIniciarRecorrido.setEnabled(true);
        }
    }

    public void obtenerParametrosFusionadosChofer(JSONObject parametros)
    {
        try {

            int idSiguienteParadero = parametros.getInt("IdSiguienteParadero");

            if(idSiguienteParadero != -1)
            {
                JSONObject miMicro = parametros.getJSONObject("MiMicro");
                JSONArray usuarioParaderos = parametros.getJSONArray("UsuarioParaderos");

                Micro micro = new Micro();
                micro.id = miMicro.getInt("Id");
                micro.patente = miMicro.getString("Patente");
                String calificacion = miMicro.getString("Calificacion");
                micro.calificacion = Float.valueOf(calificacion);

                recibirUsuariosParadero(usuarioParaderos);
                recibirMiParadero(idSiguienteParadero);
                validarLinea(micro);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

