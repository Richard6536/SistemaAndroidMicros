package com.android.micros.sistemaandroidmicros;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Richard on 19/04/2017.
 */

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
