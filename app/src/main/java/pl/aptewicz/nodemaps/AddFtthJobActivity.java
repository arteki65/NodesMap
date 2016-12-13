package pl.aptewicz.nodemaps;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.aptewicz.nodemaps.model.FtthCheckerUser;
import pl.aptewicz.nodemaps.model.FtthJob;
import pl.aptewicz.nodemaps.model.FtthJobStatus;
import pl.aptewicz.nodemaps.network.FtthCheckerRestApiJsonArrayRequest;
import pl.aptewicz.nodemaps.network.FtthCheckerRestApiRequest;
import pl.aptewicz.nodemaps.network.RequestQueueSingleton;
import pl.aptewicz.nodemaps.ui.AbstractMapActivity;
import pl.aptewicz.nodemaps.ui.admin.AdminMapActivity;
import pl.aptewicz.nodemaps.util.ServerAddressUtils;

public class AddFtthJobActivity extends AppCompatActivity {

    public static final String FTTH_JOB_LAT_LNG_KEY = "pl.aptewicz.nodemaps.FTTH_JOB_LAT_LNG";

    private RequestQueueSingleton requestQueueSingleton;
    private FtthCheckerUser ftthCheckerUser;
    private ListView servicemenList;
    private LatLng latLng;
    private String[] servicemenUsernames;
    private CameraPosition currentCameraPosition;

    @Override
    protected void onCreate(
            @Nullable
                    Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_ffth_job);

        Intent intent = getIntent();
        ftthCheckerUser = (FtthCheckerUser) intent
                .getSerializableExtra(FtthCheckerUser.FTTH_CHECKER_USER);
        latLng = intent.getParcelableExtra(FTTH_JOB_LAT_LNG_KEY);
        currentCameraPosition = intent.getParcelableExtra(AbstractMapActivity.CURRENT_CAMERA_POSITION);

        TextView latitudeTextView = (TextView) findViewById(R.id.latitude_text_view);
        latitudeTextView.setText(String.valueOf(latLng.latitude));

        TextView longitudeTextView = (TextView) findViewById(R.id.longitude_text_view);
        longitudeTextView.setText(String.valueOf(latLng.longitude));

        requestQueueSingleton = RequestQueueSingleton.getInstance(this);

        servicemenList = (ListView) findViewById(R.id.servicemen_list);

        FtthCheckerRestApiJsonArrayRequest ftthCheckerRestApiJsonArrayRequest = new FtthCheckerRestApiJsonArrayRequest(
                Request.Method.GET,
                ServerAddressUtils.getServerHttpAddressWithContext(this) + "/user/servicemen", null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            servicemenUsernames = new String[response.length()];
                            for (int i = 0; i < response.length(); i++) {
                                servicemenUsernames[i] = response.getString(i);
                            }
                            int listLayout =
                                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                                            android.R.layout.simple_list_item_activated_1 :
                                            android.R.layout.simple_list_item_1;
                            servicemenList.setAdapter(new ArrayAdapter<>(AddFtthJobActivity
                                    .this, listLayout, servicemenUsernames));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, ftthCheckerUser);

        requestQueueSingleton.addToRequestQueue(ftthCheckerRestApiJsonArrayRequest);
    }

    public void addFtthJob(View view) {
        EditText ftthJobDescriptionEditText = (EditText) findViewById(
                R.id.ftthJobDescriptionEditText);

        FtthJob ftthJob = new FtthJob(null, ftthJobDescriptionEditText.getText().toString(),
                latLng.latitude, latLng.longitude,
                servicemenUsernames[servicemenList.getCheckedItemPosition()], FtthJobStatus.NEW);

        try {
            FtthCheckerRestApiRequest ftthCheckerRestApiRequest = new FtthCheckerRestApiRequest(
                    Request.Method.PUT,
                    ServerAddressUtils.getServerHttpAddressWithContext(this) + "/ftthJob",
                    new JSONObject(new Gson().toJson(ftthJob)),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(getApplicationContext(), "Zlecenie utworzone!",
                                    Toast.LENGTH_LONG).show();
                            Intent mapResultIntent = new Intent(AddFtthJobActivity.this,
                                    AdminMapActivity.class);
                            mapResultIntent.putExtra(AbstractMapActivity.CURRENT_CAMERA_POSITION, currentCameraPosition);
                            mapResultIntent.putExtra(AdminMapActivity.START_AT_LAST_LOCATION, false);
                            mapResultIntent.putExtra(FtthCheckerUser.FTTH_CHECKER_USER,
                                    ftthCheckerUser);
                            mapResultIntent.setFlags(
                                    mapResultIntent.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            AddFtthJobActivity.this.startActivity(mapResultIntent);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),
                            "Wystąpił błąd podczas worzenia " + "zlecenia.", Toast.LENGTH_LONG)
                            .show();
                }
            }, ftthCheckerUser);

            requestQueueSingleton.addToRequestQueue(ftthCheckerRestApiRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
