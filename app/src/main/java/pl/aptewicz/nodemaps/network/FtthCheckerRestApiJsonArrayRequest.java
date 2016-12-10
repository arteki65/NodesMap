package pl.aptewicz.nodemaps.network;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

import pl.aptewicz.nodemaps.model.FtthCheckerUser;

public class FtthCheckerRestApiJsonArrayRequest extends JsonArrayRequest {

	private final FtthCheckerUser ftthCheckerUser;

	public FtthCheckerRestApiJsonArrayRequest(int method, String url, JSONArray jsonRequest,
			Response.Listener<JSONArray> listener, Response.ErrorListener errorListener,
			FtthCheckerUser ftthCheckerUser) {
		super(method, url, jsonRequest, listener, errorListener);
		this.ftthCheckerUser = ftthCheckerUser;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = new HashMap<>();
		headers.putAll(super.getHeaders());
		headers.put("Authorization", "Basic " + Base64.encodeToString(
				(ftthCheckerUser.getUsername() + ":" + ftthCheckerUser.getPassword()).getBytes(),
				Base64.DEFAULT));
		return headers;
	}
}
