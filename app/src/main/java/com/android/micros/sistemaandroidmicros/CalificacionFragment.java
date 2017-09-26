package com.android.micros.sistemaandroidmicros;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.micros.sistemaandroidmicros.Clases.Calificacion;


import org.json.JSONException;
import org.json.JSONObject;

import static com.android.micros.sistemaandroidmicros.UserMapActivity.aCalificado;
import static com.android.micros.sistemaandroidmicros.UserMapActivity.btnCalificarMicro;

public class CalificacionFragment extends Fragment {

    RatingBar ratingBar, ratingBarGlobal;
    TextView txtMiCalificacion;
    Button btnCalificar;
    float miCalififcacion;
    int idMicro;
    float calificacionGlobal;
    public static ProgressBar progressCalificacion;

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

        progressCalificacion = (ProgressBar)view.findViewById(R.id.progressCalificacion);
        progressCalificacion.setVisibility(View.GONE);

        idMicro = this.getArguments().getInt("idMicro");
        Double calificacion = this.getArguments().getDouble("cGlobal");
        calificacionGlobal = calificacion.floatValue();
        ratingBarGlobal.setRating(calificacionGlobal);

        btnCalificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try
                {
                    progressCalificacion.setVisibility(View.VISIBLE);
                    btnCalificar.setEnabled(false);
                    btnCalificarMicro.setEnabled(false);
                    aCalificado = true;
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
