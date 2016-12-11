package pl.aptewicz.nodemaps.listener.serviceman;
import android.content.Intent;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import pl.aptewicz.nodemaps.AddFtthJobActivity;
import pl.aptewicz.nodemaps.FtthJobDetailsActivity;
import pl.aptewicz.nodemaps.MapResult;
import pl.aptewicz.nodemaps.model.FtthCheckerUser;

public class OnMarkerClickServicemanListener implements GoogleMap.OnMarkerClickListener {

	private final MapResult mapResult;

	public OnMarkerClickServicemanListener(MapResult mapResult) {
		this.mapResult = mapResult;
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		Intent ftthJobDetailsIntent = new Intent(mapResult, FtthJobDetailsActivity.class);
		ftthJobDetailsIntent
				.putExtra(FtthCheckerUser.FTTH_CHECKER_USER_KEY, mapResult.ftthCheckerUser);
		ftthJobDetailsIntent
				.setFlags(ftthJobDetailsIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
		ftthJobDetailsIntent
				.putExtra(AddFtthJobActivity.FTTH_JOB_LAT_LNG_KEY, marker.getPosition());
		ftthJobDetailsIntent.putExtra(FtthJobDetailsActivity.LAST_LOCATION, mapResult.lastLocation);
		ftthJobDetailsIntent
				.putExtra(FtthJobDetailsActivity.FTTH_JOB_DESCRIPTION, marker.getTitle());

		mapResult.startActivity(ftthJobDetailsIntent);
		return false;
	}
}
