package pl.aptewicz.nodemaps;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
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
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pl.aptewicz.nodemaps.listener.OnMapClickNodeMapsListener;
import pl.aptewicz.nodemaps.listener.admin.OnAdminDrawerItemClickListener;
import pl.aptewicz.nodemaps.listener.admin.OnCameraChangeNodeMapsListener;
import pl.aptewicz.nodemaps.listener.admin.OnMarkerClickAdminListener;
import pl.aptewicz.nodemaps.listener.serviceman.OnFtthJobClickListener;
import pl.aptewicz.nodemaps.listener.serviceman.OnMarkerClickServicemanListener;
import pl.aptewicz.nodemaps.model.FtthCheckerUser;
import pl.aptewicz.nodemaps.model.FtthCheckerUserRole;
import pl.aptewicz.nodemaps.model.FtthJob;
import pl.aptewicz.nodemaps.network.RequestQueueSingleton;
import pl.aptewicz.nodemaps.service.FetchLocationConstants;
import pl.aptewicz.nodemaps.ui.adapter.FtthJobAdapter;
import pl.aptewicz.nodemaps.ui.admin.AdminActionBarDrawerToogle;
import pl.aptewicz.nodemaps.ui.serviceman.ServicemanActionBarDrawerToogle;
import pl.aptewicz.nodemaps.util.PermissionUtils;
import pl.aptewicz.nodemaps.util.PolylineUtils;

public class MapResult extends AppCompatActivity implements OnMapReadyCallback,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	public double zoom = 0.0;
	public Collection<PolylineOptions> edges = new ArrayList<>();
	public GoogleMap googleMap;
	public FtthJob[] ftthJobs;
	public RequestQueueSingleton requestQueueSingleton;
	public FtthCheckerUser ftthCheckerUser;
	public DrawerLayout drawerLayout;
	public ListView drawerList;
	public String appTitle;
	public String fetchedLatLong;
	public Location lastLocation;

	private OnCameraChangeNodeMapsListener onCameraChangeNodeMapsListener;
	private OnMapClickNodeMapsListener onMapClickNodeMapsListener;
	private ActionBarDrawerToggle drawerToggle;
	private GoogleApiClient googleApiClient;
	private boolean showRoute;
	private Location lastLocationFromFtthJobDetails;
	private String routeOvervierw;
	private LatLng ftthJobLatLng;
	private String ftthJobDescription;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_result);

		getExtrasFromIntent();

		appTitle = getTitle().toString();

		createDrawerList();

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		createDrawerToogle();

		MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		onMapClickNodeMapsListener = new OnMapClickNodeMapsListener(this);

		if (googleApiClient == null) {
			googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
		}

		requestQueueSingleton = RequestQueueSingleton.getInstance(this);
	}

	private void createDrawerToogle() {
		if (FtthCheckerUserRole.ADMIN.equals(ftthCheckerUser.getFtthCheckerUserRole())) {
			drawerToggle = new AdminActionBarDrawerToogle(this, drawerLayout, R.string.drawer_open,
					R.string.drawer_closed);
			drawerLayout.addDrawerListener(drawerToggle);
		} else if (FtthCheckerUserRole.SERVICEMAN
				.equals(ftthCheckerUser.getFtthCheckerUserRole())) {
			drawerToggle = new ServicemanActionBarDrawerToogle(this, drawerLayout,
					R.string.drawer_open, R.string.drawer_closed);
			drawerLayout.addDrawerListener(drawerToggle);
		}
	}

	private void getExtrasFromIntent() {
		Intent intent = getIntent();
		ftthCheckerUser = (FtthCheckerUser) intent
				.getSerializableExtra(FtthCheckerUser.FTTH_CHECKER_USER_KEY);
		fetchedLatLong = intent.getStringExtra(FetchLocationConstants.LAT_LNG);
		showRoute = intent.getBooleanExtra(FtthJobDetailsActivity.SHOW_ROUTE, false);
		lastLocationFromFtthJobDetails = intent
				.getParcelableExtra(FtthJobDetailsActivity.LAST_LOCATION);
		routeOvervierw = intent.getStringExtra(FtthJobDetailsActivity.ROUTE_POINTS);
		ftthJobLatLng = intent.getParcelableExtra(AddFtthJobActivity.FTTH_JOB_LAT_LNG_KEY);
		ftthJobDescription = intent.getStringExtra(FtthJobDetailsActivity.FTTH_JOB_DESCRIPTION);
	}

	private void createDrawerList() {
		drawerList = (ListView) findViewById(R.id.left_drawer);
		if (ftthCheckerUser != null && FtthCheckerUserRole.SERVICEMAN
				.equals(ftthCheckerUser.getFtthCheckerUserRole())) {
			ftthJobs = new FtthJob[ftthCheckerUser.getFtthJobs().size()];
			ftthJobs = ftthCheckerUser.getFtthJobs().toArray(ftthJobs);
			drawerList.setAdapter(new FtthJobAdapter(this, R.layout.ftth_job_list_item, ftthJobs));
			drawerList.setOnItemClickListener(new OnFtthJobClickListener(this));
		} else if (ftthCheckerUser != null && FtthCheckerUserRole.ADMIN
				.equals(ftthCheckerUser.getFtthCheckerUserRole())) {
			drawerList.setAdapter(
					new ArrayAdapter<>(this, R.layout.map_result_admin_drawer_list_item,
							getResources().getStringArray(R.array.mapResutAdminDrawerListItem)));
			drawerList.setOnItemClickListener(
					new OnAdminDrawerItemClickListener(this, ftthCheckerUser));
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
		Toast.makeText(getApplicationContext(), "The google maps was created successfully!!!",
				Toast.LENGTH_LONG).show();

		CameraPosition cameraPosition;
		if (fetchedLatLong != null) {
			LatLng latLng = new LatLng(Double.valueOf(fetchedLatLong.split(" ")[0]),
					Double.valueOf(fetchedLatLong.split(" ")[1]));
			cameraPosition = new CameraPosition.Builder().target(latLng).zoom(18).build();
			googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			onCameraChangeNodeMapsListener = new OnCameraChangeNodeMapsListener(this);
		}

		if (showRoute) {
			LatLng latLng = new LatLng(lastLocationFromFtthJobDetails.getLatitude(),
					lastLocationFromFtthJobDetails.getLongitude());
			cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
			googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

			List<LatLng> route = PolylineUtils.decodePoly(routeOvervierw);

			for (int i = 0; i < route.size() - 1; i++) {
				PolylineOptions step = new PolylineOptions().add(
						route.get(i)).add(route.get(i + 1));
				googleMap.addPolyline(step);
			}

			MarkerOptions ftthJobMarker = new MarkerOptions().position(ftthJobLatLng)
					.title(ftthJobDescription);
			googleMap.addMarker(ftthJobMarker).showInfoWindow();
		}

		UiSettings uiSettings = googleMap.getUiSettings();
		uiSettings.setZoomControlsEnabled(true);
		uiSettings.setZoomGesturesEnabled(false);
		uiSettings.setRotateGesturesEnabled(true);
		uiSettings.setCompassEnabled(true);
		uiSettings.setMapToolbarEnabled(true);
		uiSettings.setMyLocationButtonEnabled(true);

		googleMap.setOnCameraChangeListener(onCameraChangeNodeMapsListener);
		googleMap.setOnMapClickListener(onMapClickNodeMapsListener);

		if (FtthCheckerUserRole.ADMIN.equals(ftthCheckerUser.getFtthCheckerUserRole())) {
			googleMap.setOnMarkerClickListener(new OnMarkerClickAdminListener(this));
		} else if (FtthCheckerUserRole.SERVICEMAN
				.equals(ftthCheckerUser.getFtthCheckerUserRole())) {
			googleMap.setOnMarkerClickListener(new OnMarkerClickServicemanListener(this));
		}

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
	public void onConnected(
			@Nullable
					Bundle bundle) {
		if (PermissionUtils.isEnoughPermissionsGranted(this)) {
			return;
		}

		if (fetchedLatLong == null && !showRoute) {
			//noinspection MissingPermission
			lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
					.zoom(18).build();
			googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			onCameraChangeNodeMapsListener = new OnCameraChangeNodeMapsListener(this);
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
