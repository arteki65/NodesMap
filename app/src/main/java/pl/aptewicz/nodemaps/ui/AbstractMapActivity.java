package pl.aptewicz.nodemaps.ui;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import pl.aptewicz.nodemaps.R;
import pl.aptewicz.nodemaps.listener.OnMapClickNodeMapsListener;
import pl.aptewicz.nodemaps.model.FtthCheckerUser;
import pl.aptewicz.nodemaps.network.RequestQueueSingleton;
import pl.aptewicz.nodemaps.util.CameraPositionUtils;
import pl.aptewicz.nodemaps.util.PermissionUtils;


public abstract class AbstractMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String CURRENT_CAMERA_POSITION = "pl.aptewicz.nodemaps.CURRENT_CAMERA_POSITION";
    public static final String START_AT_LAST_LOCATION = "pl.aptewicz.nodemaps.START_AT_LAST_LOCATION";

    public Toolbar toolbar;
    public FtthCheckerUser ftthCheckerUser;
    public GoogleMap googleMap;
    public Location lastLocation;
    public CameraPosition currentCameraPosition;
    public RequestQueueSingleton requestQueueSingleton;
    public Collection<PolylineOptions> edges = new ArrayList<>();

    private GoogleApiClient googleApiClient;
    private boolean startAtLastLocation;
    private OnMapClickNodeMapsListener onMapClickNodeMapsListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutView();

        initToolbar();

        getExtrasFromIntent(getIntent());

        initMapFragment();

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        }

        onMapClickNodeMapsListener = new OnMapClickNodeMapsListener(this);

        requestQueueSingleton = RequestQueueSingleton.getInstance(this);
    }

    protected abstract void setLayoutView();

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        startAtLastLocation = false;
        currentCameraPosition = googleMap.getCameraPosition();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    protected void initMapFragment() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    protected void getExtrasFromIntent(Intent intent) {
        ftthCheckerUser = (FtthCheckerUser) intent
                .getSerializableExtra(FtthCheckerUser.FTTH_CHECKER_USER);
        currentCameraPosition = intent.getParcelableExtra(CURRENT_CAMERA_POSITION);
        startAtLastLocation = intent.getBooleanExtra(START_AT_LAST_LOCATION, true);
    }

    protected void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_result_options, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setQueryHint(getString(R.string.search_address_hint));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Geocoder geocoder = new Geocoder(AbstractMapActivity.this, Locale.getDefault());

                List<Address> addresses = null;

                try {
                    addresses = geocoder.getFromLocationName(query, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (addresses != null) {
                    Address address = addresses.get(0);
                    currentCameraPosition = CameraPositionUtils.moveCamera(AbstractMapActivity.this, address.getLatitude(), address.getLongitude(), 18);
                    MenuItemCompat.collapseActionView(searchItem);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setZoomGesturesEnabled(false);
        uiSettings.setRotateGesturesEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);

        if (PermissionUtils.isEnoughPermissionsGranted(this)) {
            return;
        }
        //noinspection MissingPermission
        googleMap.setMyLocationEnabled(true);

        googleMap.setOnMapClickListener(onMapClickNodeMapsListener);

        this.googleMap = googleMap;
    }

    @Override
    public void onConnected(
            @Nullable
                    Bundle bundle) {
        if(startAtLastLocation) {
            if (PermissionUtils.isEnoughPermissionsGranted(this)) {
                return;
            }

            //noinspection MissingPermission
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            CameraPositionUtils.moveCamera(this, lastLocation.getLatitude(), lastLocation.getLongitude(), 18);
        } else {
            CameraPositionUtils.moveCamera(this, currentCameraPosition);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(
            @NonNull
                    ConnectionResult connectionResult) {
    }
}
