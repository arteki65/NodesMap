package pl.aptewicz.nodemaps.model;

import com.google.android.gms.maps.model.LatLngBounds;

public class LatLngAndZoom {
	private final LatLngBounds latLngBounds;
	private final float zoom;
	
	public LatLngAndZoom(LatLngBounds latLngBounds, float zoom)	{
		this.latLngBounds = latLngBounds;
		this.zoom = zoom;
	}
	
	public double getZoom() {
		return zoom;
	}

	public LatLngBounds getLatLngBounds() {
		return latLngBounds;
	}
}
