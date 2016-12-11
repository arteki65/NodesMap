package pl.aptewicz.nodemaps.listener.admin;

import android.content.Intent;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import pl.aptewicz.nodemaps.AddFtthJobActivity;
import pl.aptewicz.nodemaps.MapResult;
import pl.aptewicz.nodemaps.model.FtthCheckerUser;
import pl.aptewicz.nodemaps.service.FetchLocationConstants;

public class OnMarkerClickAdminListener implements GoogleMap.OnMarkerClickListener {

	private final MapResult mapResult;

	public OnMarkerClickAdminListener(MapResult mapResult) {
		this.mapResult = mapResult;
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		Intent addFtthJobIntent = new Intent(mapResult, AddFtthJobActivity.class);
		addFtthJobIntent.putExtra(FtthCheckerUser.FTTH_CHECKER_USER_KEY, mapResult.ftthCheckerUser);
		addFtthJobIntent.setFlags(addFtthJobIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
		addFtthJobIntent.putExtra(AddFtthJobActivity.FTTH_JOB_LAT_LNG_KEY, marker.getPosition());
		addFtthJobIntent.putExtra(FetchLocationConstants.LAT_LNG, mapResult.fetchedLatLong);

		mapResult.startActivity(addFtthJobIntent);
		return false;
	}
}
