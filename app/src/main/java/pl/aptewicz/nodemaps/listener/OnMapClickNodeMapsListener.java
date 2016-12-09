package pl.aptewicz.nodemaps.listener;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import pl.aptewicz.nodemaps.MapResult;


public class OnMapClickNodeMapsListener implements GoogleMap.OnMapClickListener {

    private final MapResult mapResult;

    public OnMapClickNodeMapsListener(MapResult mapResult) {
        this.mapResult = mapResult;
    }

    @Override
    public void onMapClick(LatLng arg0) {
        for (PolylineOptions edge : mapResult.edges) {
            List<LatLng> listLatLng = edge.getPoints();

            LatLng startLatLng = listLatLng.get(0);
            LatLng endLatLng = listLatLng.get(1);

            double x1 = startLatLng.latitude;
            double y1 = startLatLng.longitude;

            double x2 = endLatLng.latitude;
            double y2 = endLatLng.longitude;

            double x3 = arg0.latitude;
            double y3 = arg0.longitude;

            double det = x1 * y2 + x2 * y3 + x3 * y1 - x3 * y2 - x1 * y3
                    - x2 * y1;

            if (det < 2e-5 && det > -2e-5 && (Math.min(x1, x2) <= x3)
                    && (x3 <= Math.max(x1, x2)) && (Math.min(y1, y2) <= y3)
                    && (y3 <= Math.max(y1, y2))) {

                PolylineOptions newEdge = new PolylineOptions()
                        .add(startLatLng).add(endLatLng).color(Color.BLUE);
                mapResult.googleMap.addPolyline(newEdge);
            }
        }
    }
}
