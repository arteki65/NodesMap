package pl.aptewicz.nodemaps.ui.serviceman;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pl.aptewicz.nodemaps.FtthIssueDetailsActivity;
import pl.aptewicz.nodemaps.R;
import pl.aptewicz.nodemaps.listener.serviceman.OnFtthJobClickListener;
import pl.aptewicz.nodemaps.listener.serviceman.OnMarkerClickServicemanListener;
import pl.aptewicz.nodemaps.model.AccessPointDto;
import pl.aptewicz.nodemaps.model.Edge;
import pl.aptewicz.nodemaps.model.FtthCheckerUser;
import pl.aptewicz.nodemaps.model.FtthIssue;
import pl.aptewicz.nodemaps.model.Hierarchy;
import pl.aptewicz.nodemaps.model.NodeDto;
import pl.aptewicz.nodemaps.network.FtthCheckerRestApiJsonArrayRequest;
import pl.aptewicz.nodemaps.network.FtthCheckerRestApiRequest;
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

	private boolean showSignalPath;

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
		showSignalPath = intent.getBooleanExtra(FtthIssueDetailsActivity.SHOW_SIGNAL_PATH, false);
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
		} else if (showSignalPath) {
			if (PermissionUtils.isEnoughPermissionsGranted(this)) {
				return;
			}

			//noinspection MissingPermission
			lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

			final LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
			boundsBuilder
					.include(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));

			LatLng issueLatLng = new LatLng(ftthIssue.getLatitude(), ftthIssue.getLongitude());
			MarkerOptions issueLocationMarker = new MarkerOptions().position(issueLatLng)
					.title("Miejsce zgłoszenia").icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
			googleMap.addMarker(issueLocationMarker).showInfoWindow();

			boundsBuilder.include(issueLatLng);

			AccessPointDto accessPoint = ftthIssue.getFtthJob().getAffectedAccessPoints().iterator()
					.next();
			LatLng accessPointLatLng = new LatLng(accessPoint.getNode().getY(),
					accessPoint.getNode().getX());
			MarkerOptions accessPointMarker = new MarkerOptions().position(accessPointLatLng)
					.title("Punkt dostępowy: " + accessPoint.getType())
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
			googleMap.addMarker(accessPointMarker).showInfoWindow();

			boundsBuilder.include(accessPointLatLng);

			FtthCheckerRestApiRequest getHierarchyRequest = new FtthCheckerRestApiRequest(
					Request.Method.GET, ServerAddressUtils.getServerHttpAddressWithContext(this)
					+ "/hierarchy/findByAccessSiteLike/" + accessPoint.getNode().getName() + "_"
					+ ftthIssue.getFtthJob().getAffectedAccessPoints().iterator().next()
					.getDescription().substring(0, 3), null, new Response.Listener<JSONObject>() {

				@Override
				public void onResponse(JSONObject response) {
					Hierarchy hierarchy = new Gson().fromJson(response.toString(), Hierarchy.class);

					NodeDto distributionSiteNode = hierarchy.getDistributionSiteNode();
					LatLng distributionSiteLatLng = new LatLng(distributionSiteNode.getY(),
							distributionSiteNode.getX());
					boundsBuilder.include(distributionSiteLatLng);

					MarkerOptions distributionPointMarker = new MarkerOptions()
							.position(distributionSiteLatLng).title("Punkt dystrybucji:" + " " +
									hierarchy.getDistributionSiteDescription())
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
					googleMap.addMarker(distributionPointMarker).showInfoWindow();

					NodeDto centralSiteNode = hierarchy.getCentralSiteNode();
					LatLng centralSiteLatLng = new LatLng(centralSiteNode.getY(),
							centralSiteNode.getX());
					boundsBuilder.include(centralSiteLatLng);

					MarkerOptions centralSiteMarker = new MarkerOptions()
							.position(centralSiteLatLng)
							.title("OLT: " + hierarchy.getCentralSiteDescription())
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
					googleMap.addMarker(centralSiteMarker).showInfoWindow();

					googleMap.animateCamera(
							CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100));

					googleMap.setOnMarkerClickListener(null);
				}
			}, new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {

				}
			}, ftthCheckerUser);

			requestQueueSingleton.addToRequestQueue(getHierarchyRequest);

			FtthCheckerRestApiJsonArrayRequest findPathRequest = new FtthCheckerRestApiJsonArrayRequest(
					Request.Method.GET, ServerAddressUtils.getServerHttpAddressWithContext(this)
					+ "/path/findPathForIssue/" + ftthIssue.getId(), null,
					new Response.Listener<JSONArray>() {

						@Override
						public void onResponse(JSONArray response) {
							Collection<Edge> path = new ArrayList<>();

							for (int i = 0; i < response.length(); i++) {
								try {
									Edge edge = new Gson()
											.fromJson(response.getJSONObject(i).toString(),
													Edge.class);
									path.add(edge);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}

							for (Edge edge : path) {
								PolylineOptions step = new PolylineOptions()
										.add(new LatLng(edge.getNodeA().getY(),
												edge.getNodeA().getX()))
										.add(new LatLng(edge.getNodeB().getY(),
												edge.getNodeB().getX())).color(Color.BLUE);
								googleMap.addPolyline(step);
							}
						}
					}, new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {

				}
			}, ftthCheckerUser);

			requestQueueSingleton.addToRequestQueue(findPathRequest);
		} else {
			super.onConnected(bundle);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_update_issue).setVisible(showSignalPath);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_update_issue:
				Intent ftthIssueDetailsIntent = new Intent(this, FtthIssueDetailsActivity.class);
				ftthIssueDetailsIntent.putExtra(FtthCheckerUser.FTTH_CHECKER_USER, ftthCheckerUser);
				ftthIssueDetailsIntent.setFlags(
						ftthIssueDetailsIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
				ftthIssueDetailsIntent
						.putExtra(FtthIssueDetailsActivity.LAST_LOCATION, lastLocation);
				ftthIssueDetailsIntent.putExtra(FtthIssue.FTTH_ISSUE,
						ftthIssues[drawerList.getCheckedItemPosition()]);

				startActivity(ftthIssueDetailsIntent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
