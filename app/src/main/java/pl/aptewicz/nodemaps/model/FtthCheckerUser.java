package pl.aptewicz.nodemaps.model;

import java.io.Serializable;
import java.util.Collection;

public class FtthCheckerUser implements Serializable {

	public static final String FTTH_CHECKER_USER = "FtthCheckerUser";

	private Long id;

	private String username;

	private String password;

	private FtthCheckerUserRole ftthCheckerUserRole;

	private Collection<FtthJob> ftthJobs;

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
	public Collection<FtthJob> getFtthJobs() {
		return ftthJobs;
	}
	public void setFtthJobs(Collection<FtthJob> ftthJobs) {
		this.ftthJobs = ftthJobs;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
