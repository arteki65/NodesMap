package pl.aptewicz.nodemaps.listener;

import android.os.Handler;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;

import pl.aptewicz.nodemaps.MapResult;
import pl.aptewicz.nodemaps.async.GetNodesTask;
import pl.aptewicz.nodemaps.model.LatLngAndZoom;

public class OnCameraChangeNodeMapsListener implements GoogleMap.OnCameraChangeListener {

    private final MapResult mapResult;

    public OnCameraChangeNodeMapsListener(MapResult mapResult) {
        this.mapResult = mapResult;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

        if (cameraPosition.zoom != mapResult.zoom) {
            mapResult.zoom = cameraPosition.zoom;
            mapResult.googleMap.clear();
            mapResult.edges.clear();
        }

        LatLngBounds curScreen = mapResult.googleMap.getProjection()
                .getVisibleRegion().latLngBounds;

        LatLngAndZoom latLngAndZoom = new LatLngAndZoom(
                curScreen, cameraPosition.zoom);

        new GetNodesTask(new Handler(mapResult), mapResult.serverIp)
                .execute(latLngAndZoom);
    }
}
