package pl.aptewicz.nodemaps.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestQueueSingleton {

	private static RequestQueueSingleton INSTANCE;

	private RequestQueue requestQueue;

	private RequestQueueSingleton(Context context) {
		requestQueue = Volley.newRequestQueue(context.getApplicationContext());
	}

	public static synchronized RequestQueueSingleton getInstance(Context context) {
		if (INSTANCE == null) {
			INSTANCE = new RequestQueueSingleton(context);
		}
		return INSTANCE;
	}

	public <T> void addToRequestQueue(Request<T> request) {
		requestQueue.add(request);
	}

	public void cancelAllWithTag(String tag) {
		requestQueue.cancelAll(tag);
	}
}

