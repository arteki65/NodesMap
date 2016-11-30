package pl.aptewicz.nodemaps.async;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import pl.aptewicz.nodemaps.LatLngAndZoom;
import pl.aptewicz.nodemaps.MapResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.android.gms.maps.model.LatLngBounds;

public class GetNodesTask extends AsyncTask<LatLngAndZoom, Void, String> {
	private String GET_EDGES_IN_AREA_BASE_URL = ":8080/PracaInzRest/edge/findEdgesInArea?";

	private Handler handler;

	private String serverIp;

	public GetNodesTask(Handler handler, String serverIp) {
		this.serverIp = serverIp;
		this.handler = handler;
	}

	@Override
	protected String doInBackground(LatLngAndZoom... params) {
		try {
			LatLngAndZoom latLngAndZoom = params[0];
			LatLngBounds latLngBounds = latLngAndZoom.getLatLngBounds();

			String url = "http://" + serverIp + GET_EDGES_IN_AREA_BASE_URL + "x1="
					+ latLngBounds.southwest.longitude + "&y1="
					+ latLngBounds.southwest.latitude + "&x2="
					+ latLngBounds.northeast.longitude + "&y2="
					+ latLngBounds.northeast.latitude + "&zoom="
					+ latLngAndZoom.getZoom();

			HttpGet request = new HttpGet(url);
			request.setHeader("Acccept", "application/json");

			HttpClient httpClient = new DefaultHttpClient();

			HttpResponse response = httpClient.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK)
				return "Error while downloading nodes";

			return EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	@Override
	protected void onPostExecute(String jsonString) {
		Message message = new Message();
		Bundle data = new Bundle();

		data.putString(MapResult.EDGE_KEY, jsonString);
		message.setData(data);

		handler.sendMessage(message);
		return;
	}

}
