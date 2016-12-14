package pl.aptewicz.nodemaps.ui.serviceman;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import pl.aptewicz.nodemaps.FtthJobDetailsActivity;
import pl.aptewicz.nodemaps.R;
import pl.aptewicz.nodemaps.listener.serviceman.OnFtthJobClickListener;
import pl.aptewicz.nodemaps.listener.serviceman.OnMarkerClickServicemanListener;
import pl.aptewicz.nodemaps.model.FtthCheckerUserRole;
import pl.aptewicz.nodemaps.model.FtthJob;
import pl.aptewicz.nodemaps.ui.AbstractMapActivity;
import pl.aptewicz.nodemaps.ui.adapter.FtthJobAdapter;
import pl.aptewicz.nodemaps.util.PolylineUtils;

public class ServicemanMapActivity extends AbstractMapActivity {

    public String appTitle;
    public DrawerLayout drawerLayout;
    public ListView drawerList;
    public FtthJob[] ftthJobs;

    private boolean showRoute;
    private String routePoints;
    private Location lastLocationFromJobDetails;
    private FtthJob ftthJob;

    @Override
    protected void setLayoutView() {
        setContentView(R.layout.serviceman_map_activity);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appTitle = getTitle().toString();
        createDrawerList();

        if(ftthJob != null) {
            updateFtthJob(ftthJob);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        createDrawerToogle();
    }

    @Override
    protected void getExtrasFromIntent(Intent intent) {
        super.getExtrasFromIntent(intent);

        showRoute = intent.getBooleanExtra(FtthJobDetailsActivity.SHOW_ROUTE, false);
        routePoints = intent.getStringExtra(FtthJobDetailsActivity.ROUTE_POINTS);
        lastLocationFromJobDetails = intent.getParcelableExtra(FtthJobDetailsActivity.LAST_LOCATION);
        ftthJob = (FtthJob) intent.getSerializableExtra(FtthJob.FTTH_JOB);
    }

    private void updateFtthJob(FtthJob ftthJob) {
        for(int i = 0; i < ftthJobs.length; i++) {
            if(ftthJobs[i].getId().equals(ftthJob.getId())) {
                ftthJobs[i] = ftthJob;
                drawerList.setItemChecked(i, true);
            }
        }
    }

    private void createDrawerList() {
        drawerList = (ListView) findViewById(R.id.left_drawer);
        if (ftthCheckerUser != null && ftthCheckerUser.getFtthJobs() != null) {
            ftthJobs = new FtthJob[ftthCheckerUser.getFtthJobs().size()];
            ftthJobs = ftthCheckerUser.getFtthJobs().toArray(ftthJobs);
            drawerList.setAdapter(new FtthJobAdapter(this, R.layout.ftth_job_list_item, ftthJobs));
            drawerList.setOnItemClickListener(new OnFtthJobClickListener(this));
        }
    }

    private void createDrawerToogle() {
        ServicemanActionBarDrawerToogle drawerToggle = new ServicemanActionBarDrawerToogle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_closed);
        drawerLayout.addDrawerListener(drawerToggle);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);

        googleMap.setOnMarkerClickListener(new OnMarkerClickServicemanListener(this));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(showRoute) {
            LatLng latLng = new LatLng(lastLocationFromJobDetails.getLatitude(),
                    lastLocationFromJobDetails.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            List<LatLng> route = PolylineUtils.decodePoly(routePoints);

            for (int i = 0; i < route.size() - 1; i++) {
                PolylineOptions step = new PolylineOptions().add(route.get(i))
                        .add(route.get(i + 1));
                googleMap.addPolyline(step);
            }

            MarkerOptions ftthJobMarker = new MarkerOptions().position(
                    new LatLng(ftthJob.getLatitude(),
                            ftthJob.getLongitude())).title(
                    ftthJob.getDescription());
            googleMap.addMarker(ftthJobMarker).showInfoWindow();
        } else {
            super.onConnected(bundle);
        }
    }
}
