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
    static ArrayList<Integer> arrayListIdParaderos = new ArrayList<>();
    private ArrayAdapter<Integer> adapter;
    ListView listView;
    int idParaderos, idIdaVuelta;
    String idParadero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_paradero);
        Bundle bundle = getIntent().getExtras();
        idIdaVuelta = bundle.getInt("idIdaVuelta");

        listView = (ListView)findViewById(R.id.listParaderos);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                idParadero = parent.getAdapter().getItem(position).toString();

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

                    JSONObject h = null;
                    h = historialParaderos.getJSONObject(i);
                    idParaderos = h.getInt("Id");

                    arrayListIdParaderos.add(idParaderos);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, arrayListIdParaderos);
            listView.setAdapter(adapter);

            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listView.setItemChecked(0, true);

            listView.performItemClick(listView.getSelectedView(), 0, 0);
        }
    }

}
