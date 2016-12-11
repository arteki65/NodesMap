package pl.aptewicz.nodemaps.model;

import java.io.Serializable;

public class FtthJob implements Serializable {

	public static final String FTTH_JOB = "pl.aptewicz.nodemaps.FTTH_JOB";
	private final Long id;
	private final String description;
	private final double latitude;
	private final double longitude;
	private final String servicemanUsername;
	private final FtthJobStatus jobStatus;

	public FtthJob(Long id, String description, double latitude, double longitude,
			String servicemanUsername, FtthJobStatus ftthJobStatus) {
		this.id = id;
		this.description = description;
		this.latitude = latitude;
		this.longitude = longitude;
		this.servicemanUsername = servicemanUsername;
		this.jobStatus = ftthJobStatus;
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

	public FtthJobStatus getJobStatus() {
		return jobStatus;
	}
}
