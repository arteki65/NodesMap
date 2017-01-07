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

import pl.aptewicz.nodemaps.model.FtthIssue;
import pl.aptewicz.nodemaps.ui.serviceman.ServicemanMapActivity;

public class OnFtthJobClickListener implements OnItemClickListener {

	private final ServicemanMapActivity servicemanMapActivity;

	public OnFtthJobClickListener(ServicemanMapActivity servicemanMapActivity) {
		this.servicemanMapActivity = servicemanMapActivity;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		servicemanMapActivity.drawerLayout.closeDrawer(servicemanMapActivity.drawerList);
		GoogleMap googleMap = servicemanMapActivity.googleMap;
		FtthIssue ftthIssue = servicemanMapActivity.ftthIssues[position];

		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng(ftthIssue.getLatitude(), ftthIssue.getLongitude())).zoom(14).build();
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

		MarkerOptions ftthJobMarker = new MarkerOptions()
				.position(new LatLng(ftthIssue.getLatitude(), ftthIssue.getLongitude()))
				.title(ftthIssue.getDescription());

		Marker marker = googleMap.addMarker(ftthJobMarker);
		marker.showInfoWindow();
	}
}
