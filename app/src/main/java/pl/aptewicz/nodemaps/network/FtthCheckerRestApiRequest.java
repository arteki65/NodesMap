package pl.aptewicz.nodemaps.network;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pl.aptewicz.nodemaps.model.FtthCheckerUser;

public class FtthCheckerRestApiRequest extends JsonObjectRequest {

	private final FtthCheckerUser ftthCheckerUser;

	public FtthCheckerRestApiRequest(int method, String url, JSONObject jsonRequest,
			Response.Listener<JSONObject> listener, Response.ErrorListener errorListener,
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
