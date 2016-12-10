package pl.aptewicz.nodemaps.listener.admin;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import pl.aptewicz.nodemaps.MapResult;
import pl.aptewicz.nodemaps.model.LatLngAndZoom;
import pl.aptewicz.nodemaps.network.FtthCheckerRestApiJsonArrayRequest;

public class OnCameraChangeNodeMapsListener implements GoogleMap.OnCameraChangeListener {

	private final MapResult mapResult;

	public OnCameraChangeNodeMapsListener(MapResult mapResult) {
		this.mapResult = mapResult;
	}

	@Override
	public void onCameraChange(CameraPosition cameraPosition) {

		if (cameraPosition.zoom != mapResult.zoom) {
			mapResult.zoom = cameraPosition.zoom;
			mapResult.googleMap.clear();
			mapResult.edges.clear();
		}

		LatLngBounds curScreen = mapResult.googleMap.getProjection()
				.getVisibleRegion().latLngBounds;

		LatLngAndZoom latLngAndZoom = new LatLngAndZoom(curScreen, cameraPosition.zoom);

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(mapResult);
		String serverAddress = sharedPreferences.getString("server_address", "default");

		LatLngBounds latLngBounds = latLngAndZoom.getLatLngBounds();

		String GET_EDGES_IN_AREA_BASE_URL = "/PracaInzRest/edge/findEdgesInArea?";
		String url = "http://" + serverAddress + GET_EDGES_IN_AREA_BASE_URL + "x1="
				+ latLngBounds.southwest.longitude + "&y1=" + latLngBounds.southwest.latitude
				+ "&x2=" + latLngBounds.northeast.longitude + "&y2="
				+ latLngBounds.northeast.latitude + "&zoom=" + latLngAndZoom.getZoom();

		FtthCheckerRestApiJsonArrayRequest ftthCheckerRestApiJsonArrayRequest = new
				FtthCheckerRestApiJsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
			@Override
			public void onResponse(JSONArray response) {
				handleResponse(response.toString());
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				handleResponse(error.toString());
			}
		}, mapResult.ftthCheckerUser);

		mapResult.requestQueueSingleton.addToRequestQueue(ftthCheckerRestApiJsonArrayRequest);
	}

	private boolean handleResponse(String jsonString) {

		if (jsonString != null) {
			Toast.makeText(mapResult, "Node is downloaded from server!!!",
					Toast.LENGTH_SHORT).show();

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

					LatLng startNodeLatLng = new LatLng(startNodeLatitude, startNodeLongitude);
					LatLng endNodeLatLng = new LatLng(endNodeLatitude, endNodeLongitude);

					MarkerOptions startNodeMarker = new MarkerOptions().position(startNodeLatLng)
							.title(String
									.format(Locale.getDefault(), "%.2f \n %.2f", startNodeLatitude,
											startNodeLongitude));
					mapResult.googleMap.addMarker(startNodeMarker);

					MarkerOptions endNodeMarker = new MarkerOptions().position(endNodeLatLng)
							.title(String
									.format(Locale.getDefault(), "%.2f \n %.2f", endNodeLatitude,
											endNodeLongitude));
					mapResult.googleMap.addMarker(endNodeMarker);

					PolylineOptions edge = new PolylineOptions().add(startNodeLatLng)
							.add(endNodeLatLng);
					mapResult.googleMap.addPolyline(edge);
					mapResult.edges.add(edge);
				}
			} catch (JSONException e) {
				Toast.makeText(mapResult, "Cannot create jsonObject :(",
						Toast.LENGTH_SHORT).show();
			}
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
}
