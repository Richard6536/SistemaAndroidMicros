package com.android.micros.sistemaandroidmicros;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.micros.sistemaandroidmicros.Clases.ActivityController;
import com.android.micros.sistemaandroidmicros.Clases.Coordenada;
import com.android.micros.sistemaandroidmicros.Clases.Linea;
import com.android.micros.sistemaandroidmicros.Clases.Micro;
import com.android.micros.sistemaandroidmicros.Clases.Paradero;
import com.android.micros.sistemaandroidmicros.Clases.Rutas;
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

public class UserMapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private List<Marker> markers = new ArrayList<Marker>();
    private final Handler mHandler = new Handler();
    private Marker selectedMarker;
    //private Animator animator = new Animator();

    private Button btnBuscar, btnStart, btnStop, btnReset;
    private EditText etOrigin, etDestination;

    //NavHeader
    private TextView nombreNavHeader, correoNavHeader;

    Polyline polylineIda;
    Polyline polylineVuelta;

    private List<Marker> paraderosRutaIda = new ArrayList<>();
    private List<Marker> paraderosRutaVuelta = new ArrayList<>();

    private List<Marker> microsMarker = new ArrayList<>();

    private List<Marker> Paraderos = new ArrayList<>();

    Marker marcador;
    private ProgressDialog progressDialog;
    private Spinner spinner;
    ArrayAdapter<String> adapter;

    //Datos de Facebook
    private String nombreFacebook, correoFacebook;

    private TextView nameHeader, emailHeader, mensaje;

    UserSessionManager session;
    private String name;
    private String email;
    private String idUser;

    int idLineaSeleccionada;

    String GPS_FILTER = "MyGPSLocation";

    private String idParaderoSeleccionado;
    private TextView datosUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);
        session = new UserSessionManager(getApplicationContext());

        HashMap<String, String> userid = session.obtenerRolyId();
        idUser = userid.get(UserSessionManager.KEY_ID);

        spinner = (Spinner) findViewById(R.id.spLineas);
        cargarSpinner();
        datosUsuario = (TextView) findViewById(R.id.datosUsuario);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO: -------------INICIO-----------------------------------------------
        /*
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button)findViewById(R.id.btnStop);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(), ServicePosition.class);
                intent.putExtra("usuarioId", idUser);
                startService(intent);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detenerServicio();
            }
        });
        */
        //TODO: ----------------FIN------------------------------------------------


        //Toast.makeText(getApplicationContext(),"User Login Status: " + session.isUserLoggedIn(),Toast.LENGTH_LONG).show();

        if (session.checkLogin())
            finish();

        HashMap<String, String> user = session.obtenerDetallesUsuario();
        name = user.get(UserSessionManager.KEY_NAME);
        email = user.get(UserSessionManager.KEY_EMAIL);

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

        //TODO: -------------INICIO-----------------------------------------------

        //TODO: -------------FIN-----------------------------------------------

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {


                if (polylineIda != null && polylineVuelta != null) {
                    polylineIda.remove();
                    polylineVuelta.remove();
                    removerParaderos();
                    removerMicros();
                }

                Object item = parent.getItemAtPosition(pos);
                String itemStr = item.toString();
                Linea linea = new Linea();
                Rutas rutaIda;
                Rutas rutaVuelta;

                //Retorna el Id de la linea que se selecciona en el spinner
                idLineaSeleccionada = linea.buscarLineaSpinner(itemStr);


                //Envío el Id de la linea y recibo la linea completa
                linea = Linea.BuscarLineaPorId(idLineaSeleccionada);

                rutaIda = Rutas.BuscarRutaPorId(linea.idRutaIda);
                rutaVuelta = Rutas.BuscarRutaPorId(linea.idRutaVuelta);

                polylineIda = crearRuta(rutaIda, paraderosRutaIda, Color.RED);
                polylineVuelta = crearRuta(rutaVuelta, paraderosRutaVuelta, Color.BLUE);
                actualizarPosicionMicros();

            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            Log.d(TAG,"Permission " + permissions[0] +" granted");
        }
    }

    protected void onResume() {
        super.onResume();
        ActivityController.activiyAbiertaActual = this;

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

        if (id == R.id.nav_camera)
        {
            Intent in = new Intent(UserMapActivity.this, UserMapActivity.class);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            startActivity(in);

        } else if (id == R.id.nav_gallery)
        {

            Intent in = new Intent(UserMapActivity.this, RecomendarRutaActivity.class);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            startActivity(in);

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

        LatLng hcmus = new LatLng(-40.5769389, -73.1260218);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmus, 13));


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
                Toast.makeText(UserMapActivity.this, "click en paradero Id : " + paraderoSeleccionado.getTag(), Toast.LENGTH_SHORT).show();

                idParaderoSeleccionado = paraderoSeleccionado.getTag().toString();

                JSONObject idParadero = new JSONObject();
                try {

                    idParadero.put("IdParadero",idParaderoSeleccionado);
                    //Se envía el id del usuario y paradero para hacer la relación
                    new Usuario.SeleccionarParadero().execute(idUser, idParadero.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        /*
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                addMarkerToMap(latLng);
                animator.startAnimation(false);
            }
        });*/
    }

    public void ContinuacionSeleccionParadero(String mensaje)
    {
        Toast.makeText(UserMapActivity.this, "mensaje : " + mensaje, Toast.LENGTH_SHORT).show();
        iniciarServicio();
        actualizarMicrosQueSeleccionaronParaderos(idParaderoSeleccionado);
    }

    public void agregarMicros(JSONArray choferes)
    {
        if(choferes.length() != 0)
        {
            Bitmap iconMicroActivo = microsIcon();
            for (int i = 0; i < choferes.length(); i++) {
                JSONObject jsonobject = null;
                try {
                    jsonobject = choferes.getJSONObject(i);
                    double lat = jsonobject.getDouble("Latitud");
                    double lng = jsonobject.getDouble("Longitud");
                    int idMicro = jsonobject.getInt("Id");
                    boolean estaActivo = jsonobject.getBoolean("TransmitiendoPosicion");

                    if(estaActivo)
                    {

                        //revisar si en la lista microsmarker existe un marcador con tag == id chofer
                        Marker marcadorChofer = obtenerChoferMarcador(idMicro);
                        if(marcadorChofer != null)
                        {
                            marcadorChofer.setPosition(new LatLng(lat, lng));
                            //a ese marker se le cambia la posicion que se recibio
                        }
                        else
                        {
                            //si no existe se crea un nuevo marcador
                            //se le asigna como tag el id del chofer
                            //ese marcador se agrega a la lista


                            Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("Soy una micro")
                                    .icon(BitmapDescriptorFactory.fromBitmap(iconMicroActivo)));
                            m.setTag(idMicro);
                            microsMarker.add(m);
                        }

                    }
                    else
                    {
                        //chofer esta inactivo
                        //revisar si chofer esta en la lista de marcadores usando el tag
                        Marker marcadorMicro = obtenerChoferMarcador(idMicro);
                        if(marcadorMicro != null)
                        {
                            Marker choferSeleccionado = null;
                            for (Marker chofer : microsMarker)
                            {
                                if(chofer.getTag() == marcadorMicro.getTag())
                                {

                                    choferSeleccionado = chofer;
                                    chofer.remove();
                                }
                            }
                            if(choferSeleccionado != null)
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

    private Marker obtenerChoferMarcador(int id)
    {
        Marker mChofer = null;
        String idString = id+"";
        for(Marker m : microsMarker )
        {
            if(m.getTag().toString().equals(idString))
            {
                mChofer = m;
            }
        }
        return mChofer;
    }

    public void obtenerMicrosDelParadero(JSONObject microParadero)
    {
        Bitmap iconMicroParadero = microsParaderoIcon();
        Bitmap iconMicro = microsIcon();
                try {

                    int id = microParadero.getInt("Id");

                    if(id != -1)
                    {
                        double distancia = microParadero.getDouble("DistanciaEntre");
                        String tiempoLlegada = calcularTiempo(distancia);

                        int idMicro = microParadero.getInt("MicroId");
                        Toast.makeText(UserMapActivity.this,"Micro "+idMicro, Toast.LENGTH_SHORT).show();
                        for(Marker m : microsMarker)
                        {
                            m.setTitle("Soy una micro");
                            m.setIcon(BitmapDescriptorFactory.fromBitmap(iconMicro));

                            String tag = m.getTag().toString();
                            if(tag.equals(idMicro+""))
                            {
                                m.setTitle(tiempoLlegada);
                                m.setIcon(BitmapDescriptorFactory.fromBitmap(iconMicroParadero));
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

    }

    private String calcularTiempo(double distancia)
    {
        String[] metros = String.valueOf(distancia).split("\\.");
        double k= Integer.parseInt(metros[0]);
        double kilometros = k/1000;

        int kmh = 40;
        double tiempo = (kilometros/kmh) * 60; //40 km/h

        if(tiempo > 60)
        {
            double horasMin = tiempo/60;
            if((horasMin-(int)horasMin)!=0)
            {
                String[] horasMinArray = String.valueOf(horasMin).split("\\.");
                int horas = Integer.parseInt(horasMinArray[0]);
                String m = (0+"."+Integer.parseInt(horasMinArray[1]));
                double minutosDecimales = Integer.parseInt(m) * 60;

                String[] minutosSplit = String.valueOf(minutosDecimales).split("\\.");
                int minutos = Integer.parseInt(minutosSplit[0]);

                return "Tiempo de llegada: "+horas+" hora "+ minutos+" minutos.";
            }
            else
            {
                int horas = (int)horasMin;
                return "Tiempo de llegada: "+horas+" hora/s.";
            }

        }
        else
        {
            if((tiempo-(int)tiempo)!=0)
            {
                int segundosInt;

                //Decimales
                String[] minSeg = String.valueOf(tiempo).split("\\.");
                int minutos = Integer.parseInt(minSeg[0]);
                String s = (0 + "." + Integer.parseInt(minSeg[1]));
                double segundos = Double.parseDouble(s) * 60;
                if((segundos-(int)segundos)!=0)
                {
                    String[] segDecimals = String.valueOf(segundos).split("\\.");
                    segundosInt = Integer.parseInt(segDecimals[0]);
                }
                else
                {
                    segundosInt = (int)segundos;
                }

                return "Tiempo de LLegada: "+minutos+ " minutos "+segundosInt+" segundos.";
            }
            else
            {
                //No decimales
                int minutos = (int)tiempo;
                return "Tiempo de LLegada: "+minutos+ " minutos.";
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
                    .title("Paradero"));

            paradero.setTag(p.id);
            marcadoresParaderos.add(paradero);
            //marker.position(new LatLng(p.latitud,p.longitud));
            //marcadorVuelta = mMap.addMarker(marker);
        }
        return nuevaPolyline;

    }

    public void cargarSpinner() {
        Linea linea = new Linea();
        ArrayList<String> items = linea.obtenerNombres();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    //Obtengo desde "drawable" el diseño del marcador y lo envío al 'crearRuta'
    public Bitmap markerIcon() {

        Bitmap smallMarker;

        int largo = 68;
        int ancho = 42;
        BitmapDrawable bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.map_marker);
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


    private void removerMicros()
    {
        for(Marker micro : microsMarker)
        {
            micro.remove();
        }
        microsMarker.clear();
    }

    public void detenerServicio()
    {
        stopService(new Intent(getBaseContext(), ServicePosition.class));
        new AsyncTaskServerPosition.StopPosition().execute(idUser);
    }

    @Override
    protected void onStop()
    {
        Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
        super.onStop();
        detenerServicio();
    }

    public void actualizarPosicionMicros()
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
                            new Micro.ObtenerMicrosPorLinea().execute(idLineaSeleccionada+"");
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

    public void actualizarMicrosQueSeleccionaronParaderos(final String idParadero)
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
                            new Paradero.MicrosQueSeleccionaronParadero().execute(idParadero);
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

    public void iniciarServicio()
    {
        Intent intent = new Intent(getBaseContext(), ServicePosition.class);
        intent.putExtra("usuarioId", idUser);
        startService(intent);
    }


}
