package pl.aptewicz.nodemaps.ui.serviceman;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pl.aptewicz.nodemaps.FtthIssueDetailsActivity;
import pl.aptewicz.nodemaps.R;
import pl.aptewicz.nodemaps.listener.serviceman.OnFtthJobClickListener;
import pl.aptewicz.nodemaps.listener.serviceman.OnMarkerClickServicemanListener;
import pl.aptewicz.nodemaps.model.FtthIssue;
import pl.aptewicz.nodemaps.network.FtthCheckerRestApiJsonArrayRequest;
import pl.aptewicz.nodemaps.ui.AbstractMapActivity;
import pl.aptewicz.nodemaps.ui.adapter.FtthIssueAdapter;
import pl.aptewicz.nodemaps.util.PermissionUtils;
import pl.aptewicz.nodemaps.util.PolylineUtils;
import pl.aptewicz.nodemaps.util.ServerAddressUtils;

public class ServicemanMapActivity extends AbstractMapActivity {

	public String appTitle;

	public DrawerLayout drawerLayout;

	public ListView drawerList;

	public FtthIssue[] ftthIssues;

	private boolean showRoute;

	private String routePoints;

	private Location lastLocationFromJobDetails;

	private FtthIssue ftthIssue;

	@Override
	protected void setLayoutView() {
		setContentView(R.layout.serviceman_map_activity);
	}

	@Override
	protected void onCreate(
			@Nullable
					Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		appTitle = getTitle().toString();
		createDrawerList();

		if (ftthIssue != null) {
			updateFtthJob(ftthIssue);
		}

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		createDrawerToogle();
	}

	@Override
	protected void getExtrasFromIntent(Intent intent) {
		super.getExtrasFromIntent(intent);

		showRoute = intent.getBooleanExtra(FtthIssueDetailsActivity.SHOW_ROUTE, false);
		routePoints = intent.getStringExtra(FtthIssueDetailsActivity.ROUTE_POINTS);
		lastLocationFromJobDetails = intent
				.getParcelableExtra(FtthIssueDetailsActivity.LAST_LOCATION);
		ftthIssue = (FtthIssue) intent.getSerializableExtra(FtthIssue.FTTH_ISSUE);
	}

	private void updateFtthJob(FtthIssue ftthJob) {
		for (int i = 0; i < ftthIssues.length; i++) {
			if (ftthIssues[i].getId().equals(ftthJob.getId())) {
				ftthIssues[i] = ftthJob;
				drawerList.setItemChecked(i, true);
			}
		}
	}

	private void createDrawerList() {
		drawerList = (ListView) findViewById(R.id.left_drawer);

		if (ftthCheckerUser.getFtthIssues() == null) {
			downloadFtthIssues();
		}

		if (ftthCheckerUser != null && ftthCheckerUser.getFtthIssues() != null) {
			fillIssuesDrawerList();
		}
	}

	private void fillIssuesDrawerList() {
		ftthIssues = new FtthIssue[ftthCheckerUser.getFtthIssues().size()];
		ftthIssues = ftthCheckerUser.getFtthIssues().toArray(ftthIssues);
		drawerList.setAdapter(new FtthIssueAdapter(this, R.layout.ftth_job_list_item, ftthIssues));
		drawerList.setOnItemClickListener(new OnFtthJobClickListener(this));
	}

	private void downloadFtthIssues() {
		FtthCheckerRestApiJsonArrayRequest issuesRequest = new FtthCheckerRestApiJsonArrayRequest(
				Request.Method.GET, ServerAddressUtils.getServerHttpAddressWithContext(this) +
				"/ftthIssue/" + ftthCheckerUser.getUsername(), null,
				new Response.Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						Collection<FtthIssue> ftthIssues = new ArrayList<>();

						for (int i = 0; i < response.length(); i++) {
							try {
								FtthIssue ftthIssue = new Gson()
										.fromJson(response.getJSONObject(i).toString(),
												FtthIssue.class);
								ftthIssues.add(ftthIssue);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

						ftthCheckerUser.setFtthIssues(ftthIssues);
						fillIssuesDrawerList();

						Toast.makeText(ServicemanMapActivity.this, "Pobrano listę zgłoszeń.",
								Toast.LENGTH_SHORT).show();
					}
				}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(ServicemanMapActivity.this,
						"Błąd podczas pobierania listy " + "zgłoszeń", Toast.LENGTH_SHORT).show();
			}
		}, ftthCheckerUser);

		requestQueueSingleton.addToRequestQueue(issuesRequest);
	}

	private void createDrawerToogle() {
		ServicemanActionBarDrawerToogle drawerToggle = new ServicemanActionBarDrawerToogle(this,
				drawerLayout, R.string.drawer_open, R.string.drawer_closed);
		drawerLayout.addDrawerListener(drawerToggle);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		super.onMapReady(googleMap);

		googleMap.setOnMarkerClickListener(new OnMarkerClickServicemanListener(this));
	}

	@Override
	public void onConnected(
			@Nullable
					Bundle bundle) {
		if (showRoute) {
			if (PermissionUtils.isEnoughPermissionsGranted(this)) {
				return;
			}

			//noinspection MissingPermission
			lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

			LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

			LatLng latLng = new LatLng(lastLocationFromJobDetails.getLatitude(),
					lastLocationFromJobDetails.getLongitude());
			boundsBuilder.include(latLng);

			List<LatLng> route = PolylineUtils.decodePoly(routePoints);

			for (int i = 0; i < route.size() - 1; i++) {
				PolylineOptions step = new PolylineOptions().add(route.get(i))
						.add(route.get(i + 1));
				boundsBuilder.include(route.get(i));
				boundsBuilder.include(route.get(i + 1));
				googleMap.addPolyline(step);
			}

			MarkerOptions ftthJobMarker = new MarkerOptions()
					.position(new LatLng(ftthIssue.getLatitude(), ftthIssue.getLongitude()))
					.title(ftthIssue.getDescription());
			googleMap.addMarker(ftthJobMarker).showInfoWindow();

			boundsBuilder.include(ftthJobMarker.getPosition());

			googleMap
					.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100));
		} else {
			super.onConnected(bundle);
		}
	}
}
