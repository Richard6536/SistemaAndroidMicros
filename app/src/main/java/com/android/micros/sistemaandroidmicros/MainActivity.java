package com.android.micros.sistemaandroidmicros;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //...
    private Button btnDe, btnOcuBottomSheet;
    private LinearLayout bottomSheet, bottomSheetTime;
    private TextView txtMensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing a new String Array
        String[] plants = new String[]{
                "300",
                "200",
                "100",
                "45"
        };

        bottomSheet = (LinearLayout)findViewById(R.id.bottomSheet);
        final BottomSheetBehavior bsb = BottomSheetBehavior.from(bottomSheet);

        bottomSheetTime = (LinearLayout)findViewById(R.id.bottomSheetTime);
        final BottomSheetBehavior bsb2 = BottomSheetBehavior.from(bottomSheetTime);

        bsb.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                String nuevoEstado = "";

                switch(newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        nuevoEstado = "STATE_COLLAPSED";
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        nuevoEstado = "STATE_EXPANDED";
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        nuevoEstado = "STATE_HIDDEN";
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        nuevoEstado = "STATE_DRAGGING";
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        nuevoEstado = "STATE_SETTLING";
                        break;
                }

                Log.i("BottomSheets", "Nuevo estado: " + nuevoEstado);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("BottomSheets", "Offset: " + slideOffset);
            }
        });

        // Populate a List from Array elements
        final List<String> plantsList = new ArrayList<String>(Arrays.asList(plants));

        // Create a new ArrayAdapter
        final ArrayAdapter<String> gridViewArrayAdapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1, plantsList);

        // Data bind GridView with ArrayAdapter (String Array elements)
        //gv.setAdapter(gridViewArrayAdapter);
    }
}
