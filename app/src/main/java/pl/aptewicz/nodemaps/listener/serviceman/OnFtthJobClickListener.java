package pl.aptewicz.nodemaps.listener.serviceman;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import pl.aptewicz.nodemaps.MapResult;
import pl.aptewicz.nodemaps.model.FtthJob;

public class OnFtthJobClickListener implements OnItemClickListener {

	private final MapResult mapResult;

	public OnFtthJobClickListener(MapResult mapResult) {
		this.mapResult = mapResult;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		mapResult.drawerLayout.closeDrawer(mapResult.drawerList);
		GoogleMap googleMap = mapResult.googleMap;
		FtthJob ftthJob = mapResult.ftthJobs[position];

		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng(ftthJob.getLatitude(), ftthJob.getLongitude())).zoom(14).build();
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

		MarkerOptions ftthJobMarker = new MarkerOptions()
				.position(new LatLng(ftthJob.getLatitude(), ftthJob.getLongitude())).title
						(ftthJob.getDescription());

		Marker marker = googleMap.addMarker(ftthJobMarker);
		marker.showInfoWindow();
	}
}
