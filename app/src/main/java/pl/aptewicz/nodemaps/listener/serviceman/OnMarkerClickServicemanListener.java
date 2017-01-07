package pl.aptewicz.nodemaps.listener.serviceman;

import android.content.Intent;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import pl.aptewicz.nodemaps.FtthIssueDetailsActivity;
import pl.aptewicz.nodemaps.model.FtthCheckerUser;
import pl.aptewicz.nodemaps.model.FtthIssue;
import pl.aptewicz.nodemaps.ui.serviceman.ServicemanMapActivity;

public class OnMarkerClickServicemanListener implements GoogleMap.OnMarkerClickListener {

	private final ServicemanMapActivity servicemanMapActivity;

	public OnMarkerClickServicemanListener(ServicemanMapActivity servicemanMapActivity) {
		this.servicemanMapActivity = servicemanMapActivity;
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		Intent ftthIssueDetailsIntent = new Intent(servicemanMapActivity,
				FtthIssueDetailsActivity.class);
		ftthIssueDetailsIntent
				.putExtra(FtthCheckerUser.FTTH_CHECKER_USER, servicemanMapActivity.ftthCheckerUser);
		ftthIssueDetailsIntent
				.setFlags(ftthIssueDetailsIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
		ftthIssueDetailsIntent
				.putExtra(FtthIssueDetailsActivity.LAST_LOCATION, servicemanMapActivity.lastLocation);
		ftthIssueDetailsIntent.putExtra(FtthIssue.FTTH_ISSUE,
				servicemanMapActivity.ftthIssues[servicemanMapActivity.drawerList
						.getCheckedItemPosition()]);

		servicemanMapActivity.startActivity(ftthIssueDetailsIntent);
		return false;
	}
}
