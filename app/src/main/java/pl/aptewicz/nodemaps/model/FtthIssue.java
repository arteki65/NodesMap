package pl.aptewicz.nodemaps.model;

import java.io.Serializable;

public class FtthIssue implements Serializable {

	public static final String FTTH_ISSUE = "pl.aptewicz.nodemaps.FTTH_ISSUE";

	private Long id;

	private String description;

	private double longitude;

	private double latitude;

	private FtthJob ftthJob;

	public FtthIssue(Long id, String description, double longitude, double latitude,
			FtthJob ftthJob) {
		this.id = id;
		this.description = description;
		this.longitude = longitude;
		this.latitude = latitude;
		this.ftthJob = ftthJob;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public FtthJob getFtthJob() {
		return ftthJob;
	}

	public void setFtthJob(FtthJob ftthJob) {
		this.ftthJob = ftthJob;
	}
}
