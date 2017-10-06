package com.android.micros.sistemaandroidmicros;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.micros.sistemaandroidmicros.Clases.ActivityController;
import com.android.micros.sistemaandroidmicros.Clases.Historial;
import com.android.micros.sistemaandroidmicros.Clases.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistorialIdaVueltaActivity extends AppCompatActivity {

    ListView listView;
    int idHistorial, idIdaVuelta;
    public static JSONArray historialIdaVeultaActual;
    int cont = 0;

    static ArrayList<Integer> listId = new ArrayList<>();
    static ArrayList<Integer> listOrden = new ArrayList<>();

    private ArrayAdapter<Integer> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_ida_vuelta);

    }

    protected void onResume() {
        super.onResume();
        ActivityController.activiyAbiertaActual = this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView)findViewById(R.id.listIdaVuelta);
        Bundle bundle = getIntent().getExtras();
        idHistorial = bundle.getInt("idHistorial");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int item = Integer.parseInt(parent.getAdapter().getItem(position).toString());
                int indexOrden = listOrden.indexOf(item);
                int idBuscado =listId.get(indexOrden);
                idIdaVuelta = idBuscado;

                FragmentManager FM = getSupportFragmentManager();
                FragmentTransaction FT = FM.beginTransaction();

                Bundle bundle = new Bundle();
                bundle.putInt("id", idIdaVuelta);

                Fragment fragment = new HistorialIdaVueltaFragment();
                fragment.setArguments(bundle);
                FT.replace(R.id.fragment_container_idavuelta, fragment);
                FT.commit();
            }
        });

        new Historial.ObtenerHistorialIdaVuelta().execute(idHistorial+"");
    }
    public void listarHistorial(JSONArray historialIdaVuelta){

        historialIdaVeultaActual = historialIdaVuelta;

        if(historialIdaVuelta.length() != 0)
        {
            int orden2 = 0;
            for(int i = 0; i<historialIdaVuelta.length(); i++)
            {
                try {

                    cont++;
                    JSONObject h = null;
                    h = historialIdaVuelta.getJSONObject(i);
                    String hi = h.getString("HoraInicio");
                    String hf = h.getString("HoraTermino");

                    idIdaVuelta = h.getInt("Id");
                    int orden = h.getInt("Orden");

                    if(!hi.equals(hf))
                    {

                        orden2++;
                        listOrden.add(orden2);
                        listId.add(idIdaVuelta);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, listOrden);

            listView.setAdapter(adapter);
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listView.setItemChecked(0, true);
            listView.performItemClick(listView.getSelectedView(), 0, 0);
        }
    }

    public void redireccionarFragments(int idIdaVuelta)
    {
        Intent intent = new Intent(HistorialIdaVueltaActivity.this, HistorialParaderoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("idIdaVuelta", idIdaVuelta);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        listOrden.clear();
        listId.clear();
        cont = 0;
    }
}
