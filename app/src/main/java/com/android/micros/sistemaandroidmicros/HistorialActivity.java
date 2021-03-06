package com.android.micros.sistemaandroidmicros;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.micros.sistemaandroidmicros.Clases.ActivityController;
import com.android.micros.sistemaandroidmicros.Clases.Historial;
import com.android.micros.sistemaandroidmicros.Clases.HistorialIdaVuelta;
import com.android.micros.sistemaandroidmicros.Clases.ItemsAdapter;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class HistorialActivity extends AppCompatActivity{

    static ArrayList<String> arrayListFecha = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    public static JSONArray historialActual;
    private ArrayList<Integer> itemsId = new ArrayList<>();
    ListView listView;
    SearchView searchViewItems;

    int idHistorial;
    TextView txtNoExisteHistorial;
    private String idMicro;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchViewItems = (SearchView)findViewById(R.id.searchViewItems);
        searchViewItems.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {

                adapter.getFilter().filter(text);

                return false;
            }
        });
    }

    protected void onResume() {
        super.onResume();
        ActivityController.activiyAbiertaActual = this;

        txtNoExisteHistorial = (TextView)findViewById(R.id.txtNoExisteHistorial);
        listView = (ListView)findViewById(R.id.listFecha);

        Bundle bundle = getIntent().getExtras();
        idMicro = bundle.getString("idMicro").toString();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String item = parent.getAdapter().getItem(position).toString();
                String[] split = item.split("\\.");
                String a = split[1];

                idHistorial = Integer.parseInt(a);

                FragmentManager FM = getSupportFragmentManager();
                FragmentTransaction FT = FM.beginTransaction();

                Bundle bundle = new Bundle();
                bundle.putInt("id", idHistorial);

                Fragment fragment = new HistorialBaseFragment();
                fragment.setArguments(bundle);
                FT.replace(R.id.fragment_container, fragment);
                FT.commit();
            }
        });

        new Historial.ObtenerHistorialDiario().execute(idMicro);
    }

    public void listarHistorial(JSONArray historial){

        historialActual = historial;

        itemsId = new ArrayList<>();

        if(historial.length() != 0)
        {
            for(int i = historial.length(); i>=0; i--) {
                try {

                    JSONObject h = null;
                    h = historial.getJSONObject(i);

                    String fecha = h.getString("Fecha");
                    String horaInicio = h.getString("HoraInicio");
                    String horaFinal = h.getString("HoraFinal");


                    if(!horaInicio.equals(horaFinal))
                    {

                        int id = h.getInt("Id");

                        String[] fechaSplit = fecha.split("T");

                        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = null;

                        try {

                            date = fmt.parse(fechaSplit[0]);
                            SimpleDateFormat fmtOut = new SimpleDateFormat("dd/MM/yyyy");


                            String fechaId = fmtOut.format(date)+"                           ."+id;
                            arrayListFecha.add(fechaId);
                            //itemsId.add(id);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, arrayListFecha);
            listView.setAdapter(adapter);

            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listView.setItemChecked(0, true);

            listView.performItemClick(listView.getSelectedView(), 0, 0);
        }
        else
        {
            txtNoExisteHistorial.setText("No tiene historiales");
        }
    }

    public void redireccionarFragments(final int idHistorial)
    {
        Intent intent = new Intent(HistorialActivity.this, HistorialIdaVueltaActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("idHistorial", idHistorial);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent intent = new Intent(this, ChoferMapActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        arrayListFecha.clear();
    }
}
