package pl.aptewicz.nodemaps;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import pl.aptewicz.nodemaps.model.FtthCheckerUser;
import pl.aptewicz.nodemaps.model.FtthIssue;
import pl.aptewicz.nodemaps.model.FtthJob;
import pl.aptewicz.nodemaps.model.FtthJobStatus;
import pl.aptewicz.nodemaps.model.FtthRestApiError;
import pl.aptewicz.nodemaps.network.FtthCheckerRestApiRequest;
import pl.aptewicz.nodemaps.network.RequestQueueSingleton;
import pl.aptewicz.nodemaps.ui.serviceman.ServicemanMapActivity;
import pl.aptewicz.nodemaps.util.ServerAddressUtils;

public class UpdateFtthIssueActivity extends AppCompatActivity {

	private FtthIssue ftthIssue;

	private FtthCheckerUser ftthCheckerUser;

	private RequestQueueSingleton requestQueueSingleton;

	private ListView ftthJobStatusesList;

	@Override
	protected void onCreate(
			@Nullable
					Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_ftth_job);

		Intent intent = getIntent();
		ftthCheckerUser = (FtthCheckerUser) intent
				.getSerializableExtra(FtthCheckerUser.FTTH_CHECKER_USER);
		ftthIssue = (FtthIssue) intent.getSerializableExtra(FtthIssue.FTTH_ISSUE);

		TextView latitudeTextView = (TextView) findViewById(R.id.latitude_text_view);
		latitudeTextView.setText(String.valueOf(ftthIssue.getLatitude()));

		TextView longitudeTextView = (TextView) findViewById(R.id.longitude_text_view);
		longitudeTextView.setText(String.valueOf(ftthIssue.getLongitude()));

		TextView ftthJobDescriptionTextView = (TextView) findViewById(
				R.id.ftth_job_description_text_view);
		ftthJobDescriptionTextView.setText(ftthIssue.getDescription());

		ftthJobStatusesList = (ListView) findViewById(R.id.ftt_job_statuses_list);

		int listLayout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
				android.R.layout.simple_list_item_activated_1 :
				android.R.layout.simple_list_item_1;
		ftthJobStatusesList
				.setAdapter(new ArrayAdapter<>(this, listLayout, FtthJobStatus.values()));
		int checkedStatus = 0;
		for (int i = 0; i < FtthJobStatus.values().length; i++) {
			if (FtthJobStatus.values()[i].equals(ftthIssue.getFtthJob().getJobStatus())) {
				checkedStatus = i;
			}
		}
		ftthJobStatusesList.setItemChecked(checkedStatus, true);

		requestQueueSingleton = RequestQueueSingleton.getInstance(this);
	}

	public void updateFtthJob(View view) throws JSONException {
		FtthJobStatus updatedFtthJobStatus = FtthJobStatus.values()[ftthJobStatusesList
				.getCheckedItemPosition()];
		final FtthJob ftthJobToUpdate = new FtthJob(ftthIssue.getFtthJob().getId(),
				ftthIssue.getDescription(), updatedFtthJobStatus, ftthCheckerUser.getUsername(),
				ftthIssue.getFtthJob().getAffectedAccessPoints());

		FtthCheckerRestApiRequest ftthCheckerRestApiRequest = new FtthCheckerRestApiRequest(
				Request.Method.PUT,
				ServerAddressUtils.getServerHttpAddressWithContext(this) + "/ftthJob/updateStatus",
				new JSONObject(new Gson().toJson(ftthJobToUpdate)),
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						FtthJob updatedFtthJob = new Gson()
								.fromJson(response.toString(), FtthJob.class);
						Toast.makeText(getApplicationContext(), "Zlecenie zaktualizowane!",
								Toast.LENGTH_LONG).show();
						Intent servicemanMapActivity = new Intent(UpdateFtthIssueActivity.this,
								ServicemanMapActivity.class);
						servicemanMapActivity
								.putExtra(FtthCheckerUser.FTTH_CHECKER_USER, ftthCheckerUser);
						ftthIssue.setFtthJob(updatedFtthJob);
						servicemanMapActivity.putExtra(FtthIssue.FTTH_ISSUE, ftthIssue);
						servicemanMapActivity.setFlags(
								servicemanMapActivity.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP);
						UpdateFtthIssueActivity.this.startActivity(servicemanMapActivity);
					}
				}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				if (error instanceof ServerError && error.networkResponse.statusCode == 500) {
					FtthRestApiError ftthRestApiError = new Gson()
							.fromJson(new String(error.networkResponse.data),
									FtthRestApiError.class);
					Toast.makeText(UpdateFtthIssueActivity.this, ftthRestApiError.translate(),
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(),
							"Wystąpił błąd podczas aktualizowania zlecenia.", Toast.LENGTH_LONG)
							.show();
				}
			}
		}, ftthCheckerUser);

		requestQueueSingleton.addToRequestQueue(ftthCheckerRestApiRequest);
	}
}
