package pl.aptewicz.nodemaps.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ServerAddressUtils {

	public static String getServerHttpAddressWithContext(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String serverAddress = sharedPreferences.getString("server_address", "default");
		return "http://" + serverAddress + "/PracaInzRest";
	}
}
