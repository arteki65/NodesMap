package pl.aptewicz.nodemaps.listener.serviceman;
import android.content.Intent;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import pl.aptewicz.nodemaps.FtthJobDetailsActivity;
import pl.aptewicz.nodemaps.MapResult;
import pl.aptewicz.nodemaps.model.FtthCheckerUser;
import pl.aptewicz.nodemaps.model.FtthJob;
import pl.aptewicz.nodemaps.ui.serviceman.ServicemanMapActivity;

public class OnMarkerClickServicemanListener implements GoogleMap.OnMarkerClickListener {

	private final ServicemanMapActivity servicemanMapActivity;

	public OnMarkerClickServicemanListener(ServicemanMapActivity servicemanMapActivity) {
		this.servicemanMapActivity = servicemanMapActivity;
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		Intent ftthJobDetailsIntent = new Intent(servicemanMapActivity, FtthJobDetailsActivity.class);
		ftthJobDetailsIntent
				.putExtra(FtthCheckerUser.FTTH_CHECKER_USER, servicemanMapActivity.ftthCheckerUser);
		ftthJobDetailsIntent
				.setFlags(ftthJobDetailsIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
		ftthJobDetailsIntent.putExtra(FtthJobDetailsActivity.LAST_LOCATION, servicemanMapActivity.lastLocation);
		ftthJobDetailsIntent.putExtra(FtthJob.FTTH_JOB,
				servicemanMapActivity.ftthJobs[servicemanMapActivity.drawerList.getCheckedItemPosition()]);

		servicemanMapActivity.startActivity(ftthJobDetailsIntent);
		return false;
	}
}
