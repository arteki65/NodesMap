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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import pl.aptewicz.nodemaps.R;
import pl.aptewicz.nodemaps.listener.OnMapClickNodeMapsListener;
import pl.aptewicz.nodemaps.model.FtthCheckerUser;
import pl.aptewicz.nodemaps.model.LatLngDto;
import pl.aptewicz.nodemaps.network.FtthCheckerRestApiRequest;
import pl.aptewicz.nodemaps.network.RequestQueueSingleton;
import pl.aptewicz.nodemaps.util.CameraPositionUtils;
import pl.aptewicz.nodemaps.util.PermissionUtils;
import pl.aptewicz.nodemaps.util.ServerAddressUtils;

public abstract class AbstractMapActivity extends AppCompatActivity implements OnMapReadyCallback,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener,
		LocationListener {

	public static final String CURRENT_CAMERA_POSITION = "pl.aptewicz.nodemaps.CURRENT_CAMERA_POSITION";

	public static final String START_AT_LAST_LOCATION = "pl.aptewicz.nodemaps.START_AT_LAST_LOCATION";

	public Toolbar toolbar;

	public FtthCheckerUser ftthCheckerUser;

	public GoogleMap googleMap;

	public Location lastLocation;

	public CameraPosition currentCameraPosition;

	public RequestQueueSingleton requestQueueSingleton;

	public Collection<PolylineOptions> edges = new ArrayList<>();

	protected GoogleApiClient googleApiClient;

	private boolean startAtLastLocation;

	private OnMapClickNodeMapsListener onMapClickNodeMapsListener;

	@Override
	protected void onCreate(
			@Nullable
					Bundle savedInstanceState) {
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
					currentCameraPosition = CameraPositionUtils
							.moveCamera(AbstractMapActivity.this, address.getLatitude(),
									address.getLongitude(), 18);
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
		uiSettings.setZoomControlsEnabled(false);
		uiSettings.setZoomGesturesEnabled(true);
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
		LocationRequest locationRequest = new LocationRequest();
		locationRequest.setInterval(10000);
		locationRequest.setFastestInterval(5000);
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		if (PermissionUtils.isEnoughPermissionsGranted(this)) {
			return;
		}
		//noinspection MissingPermission
		LocationServices.FusedLocationApi
				.requestLocationUpdates(googleApiClient, locationRequest, this);

		if (startAtLastLocation) {
			if (PermissionUtils.isEnoughPermissionsGranted(this)) {
				return;
			}

			//noinspection MissingPermission
			lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
			CameraPositionUtils
					.moveCamera(this, lastLocation.getLatitude(), lastLocation.getLongitude(), 18);

			updateLocation(lastLocation);
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

	@Override
	public void onLocationChanged(Location location) {
		Toast.makeText(this, "LOCATION UPDATE", Toast.LENGTH_SHORT).show();
		updateLocation(location);
	}

	private void updateLocation(Location newLocation) {
		LatLngDto lastPosition = new LatLngDto();
		lastPosition.setLatitude(newLocation.getLatitude());
		lastPosition.setLongitude(newLocation.getLongitude());
		ftthCheckerUser.setLastPosition(lastPosition);
		try {
			FtthCheckerRestApiRequest updateLastLocationRequest = new FtthCheckerRestApiRequest(
					Request.Method.PUT, ServerAddressUtils.getServerHttpAddressWithContext(this)
					+ "/user/updateLastLocation",
					new JSONObject(new Gson().toJson(ftthCheckerUser)),
					new Response.Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							Toast.makeText(AbstractMapActivity.this, "LOCATION UPDATED",
									Toast.LENGTH_SHORT).show();
						}
					}, new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					Toast.makeText(AbstractMapActivity.this, "LOCATION UPDATE ERROR",
							Toast.LENGTH_SHORT).show();
				}
			}, ftthCheckerUser);

			requestQueueSingleton.addToRequestQueue(updateLastLocationRequest);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
