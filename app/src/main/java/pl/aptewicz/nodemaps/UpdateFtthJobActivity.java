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
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import pl.aptewicz.nodemaps.model.FtthCheckerUser;
import pl.aptewicz.nodemaps.model.FtthJob;
import pl.aptewicz.nodemaps.model.FtthJobStatus;
import pl.aptewicz.nodemaps.network.FtthCheckerRestApiRequest;
import pl.aptewicz.nodemaps.network.RequestQueueSingleton;
import pl.aptewicz.nodemaps.ui.serviceman.ServicemanMapActivity;
import pl.aptewicz.nodemaps.util.ServerAddressUtils;

public class UpdateFtthJobActivity extends AppCompatActivity {

	private FtthJob ftthJob;
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
		ftthJob = (FtthJob) intent.getSerializableExtra(FtthJob.FTTH_JOB);

		TextView latitudeTextView = (TextView) findViewById(R.id.latitude_text_view);
		latitudeTextView.setText(String.valueOf(ftthJob.getLatitude()));

		TextView longitudeTextView = (TextView) findViewById(R.id.longitude_text_view);
		longitudeTextView.setText(String.valueOf(ftthJob.getLongitude()));

		TextView ftthJobDescriptionTextView = (TextView) findViewById(
				R.id.ftth_job_description_text_view);
		ftthJobDescriptionTextView.setText(ftthJob.getDescription());

		ftthJobStatusesList = (ListView) findViewById(R.id.ftt_job_statuses_list);

		int listLayout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
				android.R.layout.simple_list_item_activated_1 :
				android.R.layout.simple_list_item_1;
		ftthJobStatusesList
				.setAdapter(new ArrayAdapter<>(this, listLayout, FtthJobStatus.values()));
		int checkedStatus = 0;
		for (int i = 0; i < FtthJobStatus.values().length; i++) {
			if (FtthJobStatus.values()[i].equals(ftthJob.getJobStatus())) {
				checkedStatus = i;
			}
		}
		ftthJobStatusesList.setItemChecked(checkedStatus, true);

		requestQueueSingleton = RequestQueueSingleton.getInstance(this);
	}

	public void updateFtthJob(View view) throws JSONException {
		FtthJobStatus updatedFtthJobStatus = FtthJobStatus.values()[ftthJobStatusesList
				.getCheckedItemPosition()];
		final FtthJob updatedFtthJob = new FtthJob(ftthJob.getId(), ftthJob.getDescription(),
				ftthJob.getLatitude(), ftthJob.getLongitude(), ftthCheckerUser.getUsername(),
				updatedFtthJobStatus);

		FtthCheckerRestApiRequest ftthCheckerRestApiRequest = new FtthCheckerRestApiRequest(
				Request.Method.PUT,
				ServerAddressUtils.getServerHttpAddressWithContext(this) + "/ftthJob",
				new JSONObject(new Gson().toJson(updatedFtthJob)),
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Toast.makeText(getApplicationContext(), "Zlecenie zaktualizowane!",
								Toast.LENGTH_LONG).show();
						Intent servicemanMapActivity = new Intent(UpdateFtthJobActivity.this,
								ServicemanMapActivity.class);
						servicemanMapActivity
								.putExtra(FtthCheckerUser.FTTH_CHECKER_USER, ftthCheckerUser);
						servicemanMapActivity.putExtra(FtthJob.FTTH_JOB, updatedFtthJob);
						servicemanMapActivity.setFlags(
								servicemanMapActivity.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP);
						UpdateFtthJobActivity.this.startActivity(servicemanMapActivity);
					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getApplicationContext(),
						"Wystąpił błąd podczas aktualizowania zlecenia.", Toast.LENGTH_LONG).show();
			}
		}, ftthCheckerUser);

		requestQueueSingleton.addToRequestQueue(ftthCheckerRestApiRequest);
	}
}
