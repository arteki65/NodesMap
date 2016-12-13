package pl.aptewicz.nodemaps.listener.serviceman;
import android.content.Intent;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import pl.aptewicz.nodemaps.FtthJobDetailsActivity;
import pl.aptewicz.nodemaps.MapResult;
import pl.aptewicz.nodemaps.model.FtthCheckerUser;
import pl.aptewicz.nodemaps.model.FtthJob;

public class OnMarkerClickServicemanListener implements GoogleMap.OnMarkerClickListener {

	private final MapResult mapResult;

	public OnMarkerClickServicemanListener(MapResult mapResult) {
		this.mapResult = mapResult;
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		Intent ftthJobDetailsIntent = new Intent(mapResult, FtthJobDetailsActivity.class);
		ftthJobDetailsIntent
				.putExtra(FtthCheckerUser.FTTH_CHECKER_USER, mapResult.ftthCheckerUser);
		ftthJobDetailsIntent
				.setFlags(ftthJobDetailsIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
		ftthJobDetailsIntent.putExtra(FtthJobDetailsActivity.LAST_LOCATION, mapResult.lastLocation);
		ftthJobDetailsIntent.putExtra(FtthJob.FTTH_JOB,
				mapResult.ftthJobs[mapResult.drawerList.getCheckedItemPosition()]);

		mapResult.startActivity(ftthJobDetailsIntent);
		return false;
	}
}
