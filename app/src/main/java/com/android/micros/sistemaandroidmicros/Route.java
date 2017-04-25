package com.android.micros.sistemaandroidmicros;
import com.google.android.gms.maps.model.LatLng;
import java.util.List;
/**
 * Created by Richard on 19/04/2017.
 */

public class Route {

    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;
    public List<LatLng> points;
}
