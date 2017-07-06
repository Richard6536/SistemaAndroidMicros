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
import android.widget.Switch;
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

    private Button btnStart, btnStop, btnIniciarRecorrido;

    private List<Marker> paraderosRutaIda = new ArrayList<>();
    private List<Marker> paraderosRutaVuelta = new ArrayList<>();

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

        btnIniciarRecorrido = (Button) findViewById(R.id.btnIniciarRecorrido);
        btnIniciarRecorrido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            new Micro.IniciarRecorrido().execute(microActual.id+"");

            }
        });


        btnStart = (Button) findViewById(R.id.btnComenzar);
        btnStop = (Button) findViewById(R.id.btnParar);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(getBaseContext(), ServicePosition.class));
            }
        });

        lblMensaje = (TextView) findViewById(R.id.lblMensaje);
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

    public void continuarInicioRecorrido()
    {
        buscarMiParadero();
        iniciarServicio();
    }

    public void validarLinea(Micro micro) {

        if (micro.id == -1) {
            lblMensaje.setText("No está asociado a una micro");
            //No está asociado a una micro
        } else if (micro.lineaId == null) {
            lblMensaje.setText("La micro no está asociada a una linea.");
            //La micro no está asociada a una linea
        } else {

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

        switchTest = (Switch) findViewById(R.id.switchTest);
        switchTest.setChecked(false);

        switchTest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //Si es true, apaga el gps y comienza a obtenerPosicion
                    Toast.makeText(ChoferMapActivity.this, "Encendido", Toast.LENGTH_SHORT).show();

                    final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        buildAlertMessageNoGps();
                        switchTest.setChecked(false);

                    } else {
                        detenerServicio();
                        //obtenerPosicion();
                        if (ActivityCompat.checkSelfPermission(ChoferMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ChoferMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        cMap.setMyLocationEnabled(false);
                    }

                }
                else
                {

                    Toast.makeText(ChoferMapActivity.this, "Apagado", Toast.LENGTH_SHORT).show();
                }
            }
        });

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

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share)
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

        /* TODO: Era un método antiguo de asociar el paradero y chofer

        cMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker paraderoSeleccionado) {
                Toast.makeText(ChoferMapActivity.this, "click en paradero Id : " + paraderoSeleccionado.getTag(), Toast.LENGTH_SHORT).show();

                //String id = microActual.id+"";
                //new Paradero.AsociarParaderoChofer().execute(id, paraderoSeleccionado.getTag().toString());
            }
        });*/
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

    public void obtenerPosicion2()
    {

        final Handler handler = new Handler();

        Timer timer = new Timer();
        TimerTask task = new TimerTask()
        {
            @Override
            public void run() {

                final Runnable runnable = new Runnable() {
                    public void run() {


                    }
                };
            }

        };
        timer.schedule(task, 0, 500);
    }

    public void obtenerPosicion()
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

                            if(stopRunner == false)
                            {
                                new Micro.CambiarPosicion().execute(idSession);
                            }
                            else
                            {
                                handler.removeCallbacks(this);
                            }
                        }
                        catch (Exception e)
                        {
                            String a = "error: "+e;
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(task, 0, 500);
    }

    public void buscarMiParadero()
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
                            boolean stop = false;
                            if(stop == false)
                            {
                                stop = true;
                                new Paradero.ObtenerMiParadero().execute(microActual.id+"");
                            }
                            else
                            {
                                handler.postDelayed(this,1);
                            }
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

    public void buscarMisPasajeros(final int idMiParadero)
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
                            new Paradero.UsuariosQueSeleccionaronParadero().execute(idMiParadero+"");
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

    private void buildAlertMessageNoGps()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Debe desactivar el GPS para habilitar esta opción, desea ir a conf.?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void recibirMiParadero(JSONObject miParadero)
    {
        if(miParadero != null)
        {
            try {

                paraderoEncontrado = false;

                Bitmap MiparaderoIcon = markerParaderoSig();
                Bitmap paraderoIcon = markerIcon();

                //Marker paraderoEncontrado = null;
                String tagId;
                int id = miParadero.getInt("Id");

                if(id != -1)
                {
                    for(Marker pIda : paraderosRutaIda)
                    {
                        pIda.setIcon(BitmapDescriptorFactory.fromBitmap(paraderoIcon));
                        pIda.setTitle("Paradero");
                        tagId = pIda.getTag().toString();
                        if(tagId.equals(id+""))
                        {
                            paraderoEncontrado = true;
                            pIda.setTitle("Mi paradero");
                            pIda.setIcon(BitmapDescriptorFactory.fromBitmap(MiparaderoIcon));
                            //paraderoEncontrado = pIda;
                        }

                    }

                    if(paraderoEncontrado == false)
                    {
                        for(Marker pVuelta : paraderosRutaVuelta)
                        {
                            pVuelta.setIcon(BitmapDescriptorFactory.fromBitmap(paraderoIcon));
                            pVuelta.setTitle("Paradero");
                            tagId = pVuelta.getTag().toString();
                            if(tagId.equals(id+""))
                            {
                                pVuelta.setTitle("Mi paradero");
                                pVuelta.setIcon(BitmapDescriptorFactory.fromBitmap(MiparaderoIcon));
                                //paraderoEncontrado = pVuelta;
                            }
                        }
                    }
                    //Envío el id de mi paradero para buscar los pasajeros que seleccionaron ese paradero.
                    buscarMisPasajeros(id);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void recibirUsuariosParadero(JSONArray usuarios)
    {
        if(usuarios.length() != 0)
        {

            try {

                borrarUsuariosMarcadores(usuarios);

                for(int i=0; i<usuarios.length(); i++)
                {
                    JSONObject usuario = null;

                    usuario = usuarios.getJSONObject(i);

                    int id = usuario.getInt("UsuarioId");
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
                        Marker m = cMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("Soy una pasajero"));
                        m.setTag(id);
                        usuariosMarker.add(m);
                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        actualizarMiMicro();
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
            btnIniciarRecorrido.setEnabled(false);
        }
        else
        {
            btnIniciarRecorrido.setEnabled(true);
        }
    }

    public void mensajeLatLng(String latLng)
    {
        Toast.makeText(ChoferMapActivity.this, latLng, Toast.LENGTH_SHORT).show();
    }
}

