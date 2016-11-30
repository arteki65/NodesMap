package pl.aptewicz.nodemaps;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.aptewicz.nodemaps.async.GetNodesTask;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapResult extends Activity implements Callback {

	public static final String EDGE_KEY = "EDGE";

	// Edges
	private List<PolylineOptions> edges = new ArrayList<PolylineOptions>();

	// Google Map
	private GoogleMap googleMap;

	private double zoom = 0.0;

	private MyOnCameraChangeListener myOnCameraChangeListener;

	private MyOnMapClickListener myOnMapClickListener;

	private boolean flag = false;

	private String serverIp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_result);

		Intent intent = getIntent();
		this.serverIp = intent.getStringExtra("serverIp");

		myOnCameraChangeListener = new MyOnCameraChangeListener();
		myOnMapClickListener = new MyOnMapClickListener();
		try {
			// loading map
			initializeMap();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void initializeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();

			// check if map is created successfully
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! Unable to create google map",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(),
						"The google maps was created successfully!!!",
						Toast.LENGTH_LONG).show();

				// TODO: change these values from constants to downloaded from
				// server
				double latitude = 52.231289;
				double longtitude = 21.006584;
				LatLng latLng = new LatLng(latitude, longtitude);

				CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(latLng).zoom(18).build();

				googleMap.getUiSettings().setZoomControlsEnabled(true);
				googleMap.getUiSettings().setZoomGesturesEnabled(false);
				googleMap.getUiSettings().setRotateGesturesEnabled(true);
				googleMap.getUiSettings().setCompassEnabled(true);

				googleMap.animateCamera(CameraUpdateFactory
						.newCameraPosition(cameraPosition));

				googleMap.setOnCameraChangeListener(myOnCameraChangeListener);
				googleMap.setOnMapClickListener(myOnMapClickListener);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// initializeMap();
		// TODO: draw markers from edges list
		if (!edges.isEmpty())
			for (PolylineOptions edge : edges)
				googleMap.addPolyline(edge);
	}

	@Override
	public boolean handleMessage(Message msg) {
		String jsonString = msg.getData().getString(EDGE_KEY);

		Toast.makeText(getApplicationContext(),
				"Node is downloaded from server!!!", Toast.LENGTH_SHORT).show();

		try {
			JSONArray jsonArray = new JSONArray(jsonString);
			// JSONObject jsonObjectFromRequest = new JSONObject(jsonString);
			// JSONArray jsonArray = jsonObjectFromRequest
			// .getJSONArray("nodesInArea");

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
						startNodeLatLng).title(
						startNodeLatitude + ", \n" + startNodeLongitude);
				googleMap.addMarker(startNodeMarker);

				MarkerOptions endNodeMarker = new MarkerOptions().position(
						endNodeLatLng).title(
						endNodeLatitude + ", \n" + endNodeLongitude);
				googleMap.addMarker(endNodeMarker);

				// PolylineOptions polyline = new
				// PolylineOptions().add(startNodeLatLng).add(endNodeLatLng);

				// Polyline polyline2 = new Pol

				PolylineOptions edge = new PolylineOptions().add(
						startNodeLatLng).add(endNodeLatLng);
				googleMap.addPolyline(edge);
				edges.add(edge);
			}
		} catch (JSONException e) {
			Toast.makeText(getApplicationContext(),
					"Cannot create jsonObject :(", Toast.LENGTH_SHORT).show();
		}

		return false;
	}
	private class MyOnCameraChangeListener implements OnCameraChangeListener {

		@Override
		public void onCameraChange(CameraPosition arg0) {
			// Log.d("MAIN", "camera change " + arg0.describeContents());

			// TODO: check if the latest zoom is different from present and
			// clear map
			if (arg0.zoom != MapResult.this.zoom) {
				MapResult.this.zoom = arg0.zoom;
				googleMap.clear();
				edges.clear();
			}

			// MarkerOptions marker = new MarkerOptions().position(new
			// LatLng(newLatitude, newLongitude)).title("Test Node");
			// googleMap.addMarker(marker);

			// TODO: only one time call this method for debuging
			if (flag == false) {
				LatLngBounds curScreen = googleMap.getProjection()
						.getVisibleRegion().latLngBounds;

				LatLngAndZoom latLngAndZoom = new LatLngAndZoom.Builder(
						curScreen, arg0.zoom).build();

				new GetNodesTask(new Handler(MapResult.this), serverIp)
						.execute(latLngAndZoom);

				// flag = true;
			}

			// googleMap.

			return;
		}
	}

	private class MyOnMapClickListener implements OnMapClickListener {

		@Override
		public void onMapClick(LatLng arg0) {
			System.out.println();

			// TODO: check if the latlng contains to any edge
			for (PolylineOptions edge : edges) {
				List<LatLng> listLatLng = edge.getPoints();

				LatLng startLatLng = listLatLng.get(0);
				LatLng endLatLng = listLatLng.get(1);

				double x1 = startLatLng.latitude;
				double y1 = startLatLng.longitude;

				double x2 = endLatLng.latitude;
				double y2 = endLatLng.longitude;

				double x3 = arg0.latitude;
				double y3 = arg0.longitude;

				double det = x1 * y2 + x2 * y3 + x3 * y1 - x3 * y2 - x1 * y3
						- x2 * y1;

				if (det < 2e-5 && det > -2e-5 && (Math.min(x1, x2) <= x3)
						&& (x3 <= Math.max(x1, x2)) && (Math.min(y1, y2) <= y3)
						&& (y3 <= Math.max(y1, y2))) {

					PolylineOptions newEdge = new PolylineOptions()
							.add(startLatLng).add(endLatLng).color(Color.BLUE);
					googleMap.addPolyline(newEdge);
				}
			}

			return;
		}

	}
}
