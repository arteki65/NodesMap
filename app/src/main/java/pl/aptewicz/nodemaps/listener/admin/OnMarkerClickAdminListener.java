package pl.aptewicz.nodemaps.listener.admin;

import android.content.Intent;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;

import pl.aptewicz.nodemaps.AddFtthJobActivity;
import pl.aptewicz.nodemaps.MapResult;
import pl.aptewicz.nodemaps.model.FtthCheckerUser;
import pl.aptewicz.nodemaps.service.FetchLocationConstants;
import pl.aptewicz.nodemaps.ui.AbstractMapActivity;
import pl.aptewicz.nodemaps.ui.admin.AdminMapActivity;

public class OnMarkerClickAdminListener implements GoogleMap.OnMarkerClickListener {

	private final AdminMapActivity adminMapActivity;

	public OnMarkerClickAdminListener(AdminMapActivity adminMapActivity) {
		this.adminMapActivity = adminMapActivity;
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		adminMapActivity.currentCameraPosition = adminMapActivity.googleMap.getCameraPosition();
		Intent addFtthJobIntent = new Intent(adminMapActivity, AddFtthJobActivity.class);
		addFtthJobIntent.putExtra(FtthCheckerUser.FTTH_CHECKER_USER, adminMapActivity.ftthCheckerUser);
		addFtthJobIntent.setFlags(addFtthJobIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
		addFtthJobIntent.putExtra(AddFtthJobActivity.FTTH_JOB_LAT_LNG_KEY, marker.getPosition());
		addFtthJobIntent.putExtra(AbstractMapActivity.CURRENT_CAMERA_POSITION, adminMapActivity.currentCameraPosition);

		adminMapActivity.startActivity(addFtthJobIntent);
		return false;
	}
}
