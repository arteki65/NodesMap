package pl.aptewicz.nodemaps.ui.admin;

import com.google.android.gms.maps.GoogleMap;

import pl.aptewicz.nodemaps.R;
import pl.aptewicz.nodemaps.listener.admin.OnMarkerClickAdminListener;
import pl.aptewicz.nodemaps.ui.AbstractMapActivity;


public class AdminMapActivity extends AbstractMapActivity {

    @Override
    protected void setLayoutView() {
        setContentView(R.layout.admin_map_activity);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);

        googleMap.setOnMarkerClickListener(new OnMarkerClickAdminListener(this));
    }
}
