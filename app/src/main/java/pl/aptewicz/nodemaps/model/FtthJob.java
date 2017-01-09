package pl.aptewicz.nodemaps.model;

import java.io.Serializable;
import java.util.Collection;

public class FtthJob implements Serializable{

	public static final String FTTH_JOB = "pl.aptewicz.nodemaps.FTTH_JOB";

	private Long id;

	private String description;

	private FtthJobStatus jobStatus;

	private String servicemanUsername;

	private Collection<AccessPointDto> affectedAccessPoints;

	public FtthJob(Long id, String description, FtthJobStatus jobStatus, String servicemanUsername,
			Collection<AccessPointDto> affectedAccessPoints) {
		this.id = id;
		this.description = description;
		this.jobStatus = jobStatus;
		this.servicemanUsername = servicemanUsername;
		this.affectedAccessPoints = affectedAccessPoints;
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

	public FtthJobStatus getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(FtthJobStatus jobStatus) {
		this.jobStatus = jobStatus;
	}

	public String getServicemanUsername() {
		return servicemanUsername;
	}

	public void setServicemanUsername(String servicemanUsername) {
		this.servicemanUsername = servicemanUsername;
	}

	public Collection<AccessPointDto> getAffectedAccessPoints() {
		return affectedAccessPoints;
	}

	public void setAffectedAccessPoints(Collection<AccessPointDto> affectedAccessPoints) {
		this.affectedAccessPoints = affectedAccessPoints;
	}
}
