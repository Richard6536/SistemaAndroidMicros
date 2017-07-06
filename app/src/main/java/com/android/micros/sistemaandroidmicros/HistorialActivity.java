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

import com.android.micros.sistemaandroidmicros.Clases.ActivityController;
import com.android.micros.sistemaandroidmicros.Clases.Historial;
import com.android.micros.sistemaandroidmicros.Clases.HistorialIdaVuelta;
import com.android.micros.sistemaandroidmicros.Clases.ItemsAdapter;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class HistorialActivity extends AppCompatActivity{

    static ArrayList<String> arrayListFecha = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    public static JSONArray historialActual;
    private ArrayList<Integer> itemsId = new ArrayList<>();
    ListView listView;
    SearchView searchViewItems;

    int idHistorial;

    private String idMicro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);
        listView = (ListView)findViewById(R.id.listFecha);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        Bundle bundle = getIntent().getExtras();
        idMicro = bundle.getString("idMicro").toString();

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


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String item = parent.getAdapter().getItem(position).toString();
                String[] split = item.split("\\.");
                String a = split[0];

                idHistorial = Integer.parseInt(a);

                FragmentManager FM = getSupportFragmentManager();
                FragmentTransaction FT = FM.beginTransaction();

                Bundle bundle = new Bundle();
                bundle.putInt("id", idHistorial);

                Fragment fragment = new HistorialBaseFragment();
                fragment.setArguments(bundle);
                FT.replace(R.id.fragment_container, fragment);
                FT.addToBackStack(null);
                FT.commit();
            }
        });

        new Historial.ObtenerHistorialDiario().execute(idMicro);
    }

    protected void onResume() {
        super.onResume();
        ActivityController.activiyAbiertaActual = this;

    }

    public void listarHistorial(JSONArray historial){

        historialActual = historial;

        itemsId = new ArrayList<>();

        if(historial.length() != 0)
        {
            for(int i = 0; i<historial.length(); i++) {
                try {

                    JSONObject h = null;
                    h = historial.getJSONObject(i);

                    String fecha = h.getString("Fecha");
                    String horaInicio = h.getString("HoraInicio"); //DATETIME
                    String horaFinal = h.getString("HoraFinal");   //DATETIME

                    if(!horaInicio.equals(horaFinal))
                    {

                        int id = h.getInt("Id");

                        String[] fechaSplit = fecha.split("T");
                        String fechaId = id + "."+"     " + fechaSplit[0];
                        arrayListFecha.add(fechaId);
                        //itemsId.add(id);
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
    }

    public void redireccionarFragments(final int idHistorial)
    {
        Intent intent = new Intent(HistorialActivity.this, HistorialIdaVueltaActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("idHistorial", idHistorial);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
