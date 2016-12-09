package pl.aptewicz.nodemaps;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import pl.aptewicz.nodemaps.listener.OnCameraChangeNodeMapsListener;
import pl.aptewicz.nodemaps.listener.OnFtthJobClickListener;
import pl.aptewicz.nodemaps.listener.OnMapClickNodeMapsListener;
import pl.aptewicz.nodemaps.model.FtthJob;
import pl.aptewicz.nodemaps.ui.adapter.FtthJobAdapter;
import pl.aptewicz.nodemaps.util.PermissionUtils;

public class MapResult extends AppCompatActivity implements Callback, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String EDGE_KEY = "EDGE";

    public double zoom = 0.0;

    public Collection<PolylineOptions> edges = new ArrayList<>();

    public GoogleMap googleMap;

    public String serverIp;
    public FtthJob[] ftthJobs;

    private OnCameraChangeNodeMapsListener onCameraChangeNodeMapsListener;

    private OnMapClickNodeMapsListener onMapClickNodeMapsListener;

    private ActionBarDrawerToggle drawerToggle;

    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private String appTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_result);

        appTitle = getTitle().toString();

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ftthJobs = new FtthJob[5];
        for(int i = 0; i < 5; i++)  {
            double latitude = 52.190623 + 0.0001 * i;
            double longitude = 20.981345 + 0.0001 * i;
            ftthJobs[i] = new FtthJob((long) (i + 1), "Zlecenie " + (i + 1), latitude, longitude);
        }

        ListView drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new FtthJobAdapter(this, R.layout.ftth_job_list_item, ftthJobs));
        drawerList.setOnItemClickListener(new OnFtthJobClickListener(this));

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_closed){

            @Override
            public void onDrawerOpened(View drawerView) {
                if(getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Lista zleceń");
                }
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                if(getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(appTitle);
                }
                super.onDrawerClosed(drawerView);
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        serverIp = intent.getStringExtra("serverIp");

        onCameraChangeNodeMapsListener = new OnCameraChangeNodeMapsListener(this);
        onMapClickNodeMapsListener = new OnMapClickNodeMapsListener(this);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(getApplicationContext(),
                "The google maps was created successfully!!!",
                Toast.LENGTH_LONG).show();

        //TODO: get last known location
        double latitude = 52.231289;
        double longitude = 21.006584;
        LatLng latLng = new LatLng(latitude, longitude);

        CameraPosition cameraPosition;
        if (lastLocation != null) {
            cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                    .zoom(12).build();
        } else {
            cameraPosition = new CameraPosition.Builder()
                    .target(latLng).zoom(12).build();
        }

        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setZoomGesturesEnabled(false);
        uiSettings.setRotateGesturesEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);

        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        googleMap.setOnCameraChangeListener(onCameraChangeNodeMapsListener);
        //googleMap.setOnMapClickListener(onMapClickNodeMapsListener);
        googleMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                polyline.setColor(Color.BLUE);
            }
        });
        if (PermissionUtils.isEnoughPermissionsGranted(this)) {
            return;
        }
        //noinspection MissingPermission
        googleMap.setMyLocationEnabled(true);

        this.googleMap = googleMap;
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
    public boolean handleMessage(Message msg) {
        String jsonString = msg.getData().getString(EDGE_KEY);

        Toast.makeText(getApplicationContext(),
                "Node is downloaded from server!!!", Toast.LENGTH_SHORT).show();

        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                JSONObject nodeAJsonObject = jsonObject.getJSONObject("nodeA");

                double startNodeLatitude = nodeAJsonObject.getDouble("y");
                double startNodeLongitude = nodeAJsonObject.getDouble("x");

                JSONObject nodeBJsonObject = jsonObject.getJSONObject("nodeB");

                double endNodeLatitude = nodeBJsonObject.getDouble("y");
                double endNodeLongitude = nodeBJsonObject.getDouble("x");

                LatLng startNodeLatLng = new LatLng(startNodeLatitude,
                        startNodeLongitude);
                LatLng endNodeLatLng = new LatLng(endNodeLatitude,
                        endNodeLongitude);

                MarkerOptions startNodeMarker = new MarkerOptions().position(
                        startNodeLatLng).title(String.format(Locale.getDefault(),
                        "%.2f \n %.2f", startNodeLatitude, startNodeLongitude));
                googleMap.addMarker(startNodeMarker);

                MarkerOptions endNodeMarker = new MarkerOptions().position(
                        endNodeLatLng).title(String.format(Locale.getDefault(),
                        "%.2f \n %.2f", endNodeLatitude, endNodeLongitude));
                googleMap.addMarker(endNodeMarker);

                PolylineOptions edge = new PolylineOptions().add(
                        startNodeLatLng).add(endNodeLatLng);
                googleMap.addPolyline(edge);
                edges.add(edge);
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),
                    "Cannot create jsonObject :(", Toast.LENGTH_SHORT).show();
        }

        /*String encodedPolyline =
                "knp}Hu|`_CmCq@kIoByA]mHiBaUqFcOsDwCs@mFkAeLsCWEGlAKxESfK_@|PSvKKbEZV`DpCfA~@aEdQcAhEaBbHgIx]eApEu@rDCAG@EDIVDZDFFBHADGFWE[";

        List<LatLng> latLngs = PolylineUtils.decodePoly(encodedPolyline);

        for (int i = 0; i < latLngs.size() - 1; i++) {
            PolylineOptions step = new PolylineOptions().add(
                    latLngs.get(i)).add(latLngs.get(i + 1));
            googleMap.addPolyline(step);
        }*/

        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (PermissionUtils.isEnoughPermissionsGranted(this)) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //noinspection MissingPermission
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                .zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
