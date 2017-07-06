package com.android.micros.sistemaandroidmicros;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.micros.sistemaandroidmicros.Clases.Calificacion;
import com.android.micros.sistemaandroidmicros.Clases.Usuario;
import com.google.android.gms.plus.PlusOneButton;

import org.json.JSONException;
import org.json.JSONObject;

public class CalificacionFragment extends Fragment {

    RatingBar ratingBar, ratingBarGlobal;
    TextView txtMiCalificacion;
    Button btnCalificar;
    float miCalififcacion;
    int idMicro = 1;
    float calificacionGlobal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calificacion, container, false);
        ratingBar = (RatingBar)view.findViewById(R.id.ratingBar);
        ratingBarGlobal = (RatingBar)view.findViewById(R.id.ratingBarGlobal);
        txtMiCalificacion = (TextView)view.findViewById(R.id.txtMiCalificacion);
        btnCalificar = (Button)view.findViewById(R.id.btnCalificar);

        Double calificacion = this.getArguments().getDouble("cGlobal");
        calificacionGlobal = calificacion.floatValue();
        ratingBarGlobal.setRating(calificacionGlobal);

        btnCalificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try
                {
                    JSONObject params = new JSONObject();
                    params.put("Calificacion", miCalififcacion);

                    new Calificacion.AgregarCalificacion().execute(idMicro+"", params.toString());

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                miCalififcacion = rating;
                txtMiCalificacion.setText(rating+"");
            }
        });

        return view;
    }



}
