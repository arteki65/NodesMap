package pl.aptewicz.nodemaps.ui.admin;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collection;

import pl.aptewicz.nodemaps.R;
import pl.aptewicz.nodemaps.listener.admin.OnCameraChangeNodeMapsListener;
import pl.aptewicz.nodemaps.listener.admin.OnMarkerClickAdminListener;
import pl.aptewicz.nodemaps.ui.AbstractMapActivity;


public class AdminMapActivity extends AbstractMapActivity {

    @Override
    protected void setLayoutView() {
        setContentView(R.layout.admin_map_activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!edges.isEmpty()) {
            for (PolylineOptions edge : edges) {
                googleMap.addPolyline(edge);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);

        googleMap.setOnMarkerClickListener(new OnMarkerClickAdminListener(this));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        super.onConnected(bundle);

        googleMap.setOnCameraChangeListener(new OnCameraChangeNodeMapsListener(this));
    }
}
