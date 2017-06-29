package com.android.micros.sistemaandroidmicros;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.micros.sistemaandroidmicros.Clases.ActivityController;
import com.android.micros.sistemaandroidmicros.Clases.Historial;
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

    int id1 = 7;
    int id2 = 8;
    int id3 = 9;
    int id4 = 10;

    String fecha1 = "1. 22-06-17";
    String fecha2 = "2. 23-06-17";
    String fecha3 = "3. 24-06-17";
    String fecha4 = "4. 25-06-17";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);
        listView = (ListView)findViewById(R.id.listFecha);
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

        arrayListFecha.add(fecha1);
        arrayListFecha.add(fecha2);
        arrayListFecha.add(fecha3);
        arrayListFecha.add(fecha4);

        Bundle bundle = getIntent().getExtras();
        idMicro = bundle.getString("idMicro").toString();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int posicion = position;
                int posicionActual = 0;

                for(Integer idr: itemsId)
                {
                    if(posicion == posicionActual)
                    {
                        idHistorial = idr;
                        break;
                    }
                    posicionActual++;
                }

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
            /*
            for(int i = 0; i<historial.length(); i++)
            {
                try {

                    JSONObject h = null;
                    h = historial.getJSONObject(i);

                    String fecha = h.getString("Fecha");
                    int id = h.getInt("Id");

                    arrayListFecha.add(fecha);
                    itemsId.add(id);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }*/

            itemsId.add(id1);
            itemsId.add(id2);
            itemsId.add(id3);
            itemsId.add(id4);

            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, arrayListFecha);
            listView.setAdapter(adapter);

            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listView.setItemChecked(0, true);

            listView.performItemClick(listView.getSelectedView(), 0, 0);
        }
        else
        {

            itemsId.add(id1);
            itemsId.add(id2);
            itemsId.add(id3);
            itemsId.add(id4);

            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, arrayListFecha);
            listView.setAdapter(adapter);

            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listView.setItemChecked(0, true);

            listView.performItemClick(listView.getSelectedView(), 0, 0);



        }


    }

    public void redireccionarFragments()
    {
        FragmentManager FM = getSupportFragmentManager();
        FragmentTransaction FT = FM.beginTransaction();

        Fragment fragment = new HistorialIdaVueltaFragment();
        FT.replace(R.id.fragment_container, fragment);
        FT.addToBackStack(null);
        FT.commit();
    }
}
