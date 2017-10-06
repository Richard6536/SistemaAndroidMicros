package com.android.micros.sistemaandroidmicros;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.android.micros.sistemaandroidmicros.Clases.ActivityController;
import com.android.micros.sistemaandroidmicros.Clases.Historial;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistorialParaderoActivity extends AppCompatActivity {

    public static JSONArray historialParaderosActual;
    static ArrayList<Integer> ordenParaderos = new ArrayList<>();
    static ArrayList<Integer> idParaderosLst = new ArrayList<>();
    private ArrayAdapter<Integer> adapter;
    ListView listView;
    int idParaderos, idIdaVuelta;
    String idParadero;
    int cont = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_paradero);
        Bundle bundle = getIntent().getExtras();
        idIdaVuelta = bundle.getInt("idIdaVuelta");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView)findViewById(R.id.listParaderos);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int item = Integer.parseInt(parent.getAdapter().getItem(position).toString());
                int indexOrden = ordenParaderos.indexOf(item);
                int idBuscado =idParaderosLst.get(indexOrden);
                idParadero = idBuscado+"";

                FragmentManager FM = getSupportFragmentManager();
                FragmentTransaction FT = FM.beginTransaction();

                Bundle bundle = new Bundle();
                bundle.putString("id", idParadero);

                Fragment fragment = new HistorialParaderoFragment();
                fragment.setArguments(bundle);
                FT.replace(R.id.fragment_container_paraderos, fragment);
                FT.commit();
            }
        });

        new Historial.ObtenerHistorialParaderos().execute(idIdaVuelta+"");
    }

    protected void onResume() {
        super.onResume();
        ActivityController.activiyAbiertaActual = this;

    }
    public void listarHistorial(JSONArray historialParaderos){

        historialParaderosActual = historialParaderos;

        if(historialParaderos.length() != 0)
        {
            for(int i = 0; i<historialParaderos.length(); i++) {
                try {

                    cont++;
                    JSONObject h = null;
                    h = historialParaderos.getJSONObject(i);
                    idParaderos = h.getInt("Id");
                    int orden = h.getInt("Orden");

                    ordenParaderos.add(orden);
                    idParaderosLst.add(idParaderos);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, ordenParaderos);
            listView.setAdapter(adapter);

            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listView.setItemChecked(0, true);

            listView.performItemClick(listView.getSelectedView(), 0, 0);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    @Override
    protected void onStop() {
        super.onStop();
        ordenParaderos.clear();
        idParaderosLst.clear();
        cont = 0;
    }
}
