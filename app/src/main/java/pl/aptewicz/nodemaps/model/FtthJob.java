package pl.aptewicz.nodemaps.model;

import java.io.Serializable;

public class FtthJob implements Serializable {

	private final Long id;
	private final String description;
	private final double latitude;
	private final double longitude;
	private final String servicemanUsername;

	public FtthJob(Long id, String description, double latitude, double longitude,
			String servicemanUsername) {
		this.id = id;
		this.description = description;
		this.latitude = latitude;
		this.longitude = longitude;
		this.servicemanUsername = servicemanUsername;
	}

	public Long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getServicemanUsername() {
		return servicemanUsername;
	}
}
