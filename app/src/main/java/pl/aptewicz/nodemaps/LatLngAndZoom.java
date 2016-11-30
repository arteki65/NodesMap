package pl.aptewicz.nodemaps;

import com.google.android.gms.maps.model.LatLngBounds;

public class LatLngAndZoom {
	private final LatLngBounds latLngBounds;
	private final float zoom;
	
	private LatLngAndZoom(Builder builder)	{
		this.latLngBounds = builder.latLngBounds;
		this.zoom = builder.zoom;
	}
	
	public double getZoom() {
		return zoom;
	}

	public LatLngBounds getLatLngBounds() {
		return latLngBounds;
	}

	public static class Builder	{
		// Non optional parameters
		private final LatLngBounds latLngBounds;
		private final float zoom;
		
		public Builder(LatLngBounds latLngBounds, float zoom)	{
			this.latLngBounds = latLngBounds;
			this.zoom = zoom;
		}
		
		public LatLngAndZoom build()	{
			return new LatLngAndZoom(this);
		}
	}	
}
