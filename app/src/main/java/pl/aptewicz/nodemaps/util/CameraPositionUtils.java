package pl.aptewicz.nodemaps.util;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import pl.aptewicz.nodemaps.ui.AbstractMapActivity;

public class CameraPositionUtils {

    public static CameraPosition moveCamera(AbstractMapActivity abstractMapActivity, double latitude, double longitude, float zoom) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(zoom).build();
        moveCamera(abstractMapActivity, cameraPosition);
        return cameraPosition;
    }

    public static void moveCamera(AbstractMapActivity abstractMapActivity, CameraPosition cameraPosition) {
        abstractMapActivity.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        abstractMapActivity.currentCameraPosition = cameraPosition;
    }
}
