package pl.aptewicz.nodemaps;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pl.aptewicz.nodemaps.model.FtthCheckerUser;
import pl.aptewicz.nodemaps.network.RequestQueueSingleton;

public class FtthJobDetailsActivity extends AppCompatActivity {

	public static String FTTH_JOB_DESCRIPTION = "pl.aptewicz.nodemaps.FTTH_JOB_DESCRIPTION";
	public static String LAST_LOCATION = "pl.aptewicz.nodemaps.LAST_LOCATION";
	public static String ROUTE_POINTS = "pl.aptewicz.nodemaps.ROUTE_POINTS";
	public static String SHOW_ROUTE = "pl.aptewicz.nodemaps.SHOW_ROUTE";

	private static final String GOOGLE_API_KEY = "AIzaSyB-QKyOwRzSMznccH_jPwYwgxSjF2CrhZ8";

	private FtthCheckerUser ftthCheckerUser;
	private LatLng ftthJobLatLng;
	private RequestQueueSingleton requestQueueSingleton;
	private Location lastLocation;
	private String ftthJobDescription;

	@Override
	protected void onCreate(
			@Nullable
					Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ftth_job_details);

		Intent intent = getIntent();
		ftthCheckerUser = (FtthCheckerUser) intent
				.getSerializableExtra(FtthCheckerUser.FTTH_CHECKER_USER_KEY);
		ftthJobLatLng = intent.getParcelableExtra(AddFtthJobActivity.FTTH_JOB_LAT_LNG_KEY);
		ftthJobDescription = intent.getStringExtra(FTTH_JOB_DESCRIPTION);
		lastLocation = intent.getParcelableExtra(LAST_LOCATION);

		TextView latitudeTextView = (TextView) findViewById(R.id.latitude_text_view);
		latitudeTextView.setText(String.valueOf(ftthJobLatLng.latitude));

		TextView longitudeTextView = (TextView) findViewById(R.id.longitude_text_view);
		longitudeTextView.setText(String.valueOf(ftthJobLatLng.longitude));

		TextView ftthJobDescriptionTextView = (TextView) findViewById(
				R.id.ftth_job_description_text_view);
		ftthJobDescriptionTextView.setText(ftthJobDescription);

		requestQueueSingleton = RequestQueueSingleton.getInstance(this);
	}

	public void findWayToFtthJob(View view) {
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());

		List<Address> addresses = null;
		try {
			addresses = geocoder
					.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (addresses != null) {
			Address address = addresses.get(0);
			ArrayList<String> addressFragments = new ArrayList<>();

			for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
				addressFragments.add(address.getAddressLine(i));
			}

			String lastLocationAddress = TextUtils.join("+", addressFragments);
			lastLocationAddress = lastLocationAddress.replace(" ", "+");

			String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" +
					lastLocationAddress + "&destination=" + ftthJobLatLng.latitude + ","
					+ ftthJobLatLng.longitude + "&key=" + GOOGLE_API_KEY;

			JsonObjectRequest googleDirectionsApiRequest = new JsonObjectRequest(Request.Method.GET,
					url, null, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					try {
						JSONArray routes = response.getJSONArray("routes");
						JSONObject route = routes.getJSONObject(0);
						JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
						String routePoints = overviewPolyline.getString("points");

						Intent mapResultIntent = new Intent(FtthJobDetailsActivity.this,
								MapResult.class);
						mapResultIntent
								.putExtra(FtthCheckerUser.FTTH_CHECKER_USER_KEY, ftthCheckerUser);
						mapResultIntent.putExtra(ROUTE_POINTS, routePoints);
						mapResultIntent
								.putExtra(AddFtthJobActivity.FTTH_JOB_LAT_LNG_KEY, ftthJobLatLng);
						mapResultIntent.putExtra(LAST_LOCATION, lastLocation);
						mapResultIntent.putExtra(FTTH_JOB_DESCRIPTION, ftthJobDescription);
						mapResultIntent.putExtra(SHOW_ROUTE, true);
						mapResultIntent.setFlags(
								mapResultIntent.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP);
						FtthJobDetailsActivity.this.startActivity(mapResultIntent);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					Toast.makeText(getApplicationContext(),
							"Wystąpił błąd podczas wyszukiwania drogi.", Toast.LENGTH_LONG).show();
				}
			});

			requestQueueSingleton.addToRequestQueue(googleDirectionsApiRequest);
		}
	}
}
