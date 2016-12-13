package pl.aptewicz.nodemaps.service;
import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchLocationIntentService extends IntentService {

	public FetchLocationIntentService() {
		super("FetchLocation");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());

		String streetAddress = intent.getStringExtra(FetchLocationConstants.LOCATION_DATA_EXTRA);
		ResultReceiver resultReceiver = intent.getParcelableExtra(FetchLocationConstants.RECEIVER);

		List<Address> addresses = null;

		try {
			addresses = geocoder.getFromLocationName(streetAddress, 5);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (addresses != null) {
			Address address = addresses.get(0);
			ArrayList<String> addressFragments = new ArrayList<>();

			// Fetch the address lines using getAddressLine,
			// join them, and send them to the thread.
			for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
				addressFragments.add(address.getAddressLine(i));
			}
			addressFragments.add(String.valueOf(address.getLatitude()));
			addressFragments.add(String.valueOf(address.getLongitude()));

			Bundle bundle = new Bundle();
			bundle.putString(FetchLocationConstants.RESULT_DATA_KEY,
					TextUtils.join(System.getProperty("line.separator"), addressFragments));
			resultReceiver.send(FetchLocationConstants.SUCCESS_RESULT, bundle);
		}
	}
}
