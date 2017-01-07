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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pl.aptewicz.nodemaps.model.FtthCheckerUser;
import pl.aptewicz.nodemaps.model.FtthIssue;
import pl.aptewicz.nodemaps.network.FtthCheckerRestApiRequest;
import pl.aptewicz.nodemaps.network.RequestQueueSingleton;
import pl.aptewicz.nodemaps.ui.serviceman.ServicemanMapActivity;
import pl.aptewicz.nodemaps.util.ServerAddressUtils;

public class FtthIssueDetailsActivity extends AppCompatActivity {

	public static final String SHOW_SIGNAL_PATH = "pl.aptewicz.nodemaps.SHOW_SIGNAL_PATH";

	public static String LAST_LOCATION = "pl.aptewicz.nodemaps.LAST_LOCATION";

	public static String ROUTE_POINTS = "pl.aptewicz.nodemaps.ROUTE_POINTS";

	public static String SHOW_ROUTE = "pl.aptewicz.nodemaps.SHOW_ROUTE";

	private FtthCheckerUser ftthCheckerUser;

	private RequestQueueSingleton requestQueueSingleton;

	private Location lastLocation;

	private FtthIssue ftthIssue;

	@Override
	protected void onCreate(
			@Nullable
					Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ftth_job_details);

		Intent intent = getIntent();
		ftthCheckerUser = (FtthCheckerUser) intent
				.getSerializableExtra(FtthCheckerUser.FTTH_CHECKER_USER);
		lastLocation = intent.getParcelableExtra(LAST_LOCATION);
		ftthIssue = (FtthIssue) intent.getSerializableExtra(FtthIssue.FTTH_ISSUE);

		TextView latitudeTextView = (TextView) findViewById(R.id.latitude_text_view);
		latitudeTextView.setText(String.valueOf(ftthIssue.getLatitude()));

		TextView longitudeTextView = (TextView) findViewById(R.id.longitude_text_view);
		longitudeTextView.setText(String.valueOf(ftthIssue.getLongitude()));

		TextView ftthJobDescriptionTextView = (TextView) findViewById(
				R.id.ftth_job_description_text_view);
		ftthJobDescriptionTextView.setText(ftthIssue.getDescription());

		requestQueueSingleton = RequestQueueSingleton.getInstance(this);
	}

	public void findWayToFtthJob(View view) throws UnsupportedEncodingException {
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
				addressFragments.add(URLEncoder.encode(address.getAddressLine(i), "UTF-8"));
			}

			String lastLocationAddress = TextUtils.join("+", addressFragments);

			String url = ServerAddressUtils.getServerHttpAddressWithContext(this) +
					"/route?origin=" + lastLocationAddress + "&destination=" + ftthIssue
					.getLatitude() + "," + ftthIssue.getLongitude();

			FtthCheckerRestApiRequest googleDirectionsApiRequest = new FtthCheckerRestApiRequest(
					Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

				@Override
				public void onResponse(JSONObject response) {
					try {
						JSONArray routes = response.getJSONArray("routes");
						JSONObject route = routes.getJSONObject(0);
						JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
						String routePoints = overviewPolyline.getString("points");

						Intent servicemanMapActivity = new Intent(FtthIssueDetailsActivity.this,
								ServicemanMapActivity.class);
						servicemanMapActivity
								.putExtra(FtthCheckerUser.FTTH_CHECKER_USER, ftthCheckerUser);
						servicemanMapActivity.putExtra(ROUTE_POINTS, routePoints);
						servicemanMapActivity.putExtra(LAST_LOCATION, lastLocation);
						servicemanMapActivity.putExtra(FtthIssue.FTTH_ISSUE, ftthIssue);
						servicemanMapActivity.putExtra(SHOW_ROUTE, true);
						servicemanMapActivity.setFlags(
								servicemanMapActivity.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP);
						FtthIssueDetailsActivity.this.startActivity(servicemanMapActivity);
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
			}, ftthCheckerUser);

			requestQueueSingleton.addToRequestQueue(googleDirectionsApiRequest);
		}
	}

	public void updateFtthJob(View view) {
		Intent updateFtthJobIntent = new Intent(this, UpdateFtthIssueActivity.class);
		updateFtthJobIntent.putExtra(FtthCheckerUser.FTTH_CHECKER_USER, ftthCheckerUser);
		updateFtthJobIntent.putExtra(FtthIssue.FTTH_ISSUE, ftthIssue);

		updateFtthJobIntent
				.setFlags(updateFtthJobIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(updateFtthJobIntent);
	}

	public void showSignalPath(View view) {
		//TODO: show signal path the same as in javascript
		Intent servicemanMapActivity = new Intent(this, ServicemanMapActivity.class);
		servicemanMapActivity.putExtra(FtthCheckerUser.FTTH_CHECKER_USER, ftthCheckerUser);
		servicemanMapActivity.putExtra(FtthIssue.FTTH_ISSUE, ftthIssue);
		servicemanMapActivity.putExtra(SHOW_SIGNAL_PATH, true);
		servicemanMapActivity
				.setFlags(servicemanMapActivity.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(servicemanMapActivity);
	}
}
