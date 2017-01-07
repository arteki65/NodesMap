package pl.aptewicz.nodemaps.model;

import java.io.Serializable;
import java.util.Collection;

public class FtthCheckerUser implements Serializable {

	public static final String FTTH_CHECKER_USER = "FtthCheckerUser";

	private Long id;

	private String username;

	private String password;

	private FtthCheckerUserRole ftthCheckerUserRole;

	private Collection<FtthIssue> ftthIssues;

	private LatLngDto lastPosition;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public FtthCheckerUserRole getFtthCheckerUserRole() {
		return ftthCheckerUserRole;
	}

	public void setFtthCheckerUserRole(FtthCheckerUserRole ftthCheckerUserRole) {
		this.ftthCheckerUserRole = ftthCheckerUserRole;
	}

	public Collection<FtthIssue> getFtthIssues() {
		return ftthIssues;
	}

	public void setFtthIssues(Collection<FtthIssue> ftthIssues) {
		this.ftthIssues = ftthIssues;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LatLngDto getLastPosition() {
		return lastPosition;
	}

	public void setLastPosition(LatLngDto lastPosition) {
		this.lastPosition = lastPosition;
	}
}
