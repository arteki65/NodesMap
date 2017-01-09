package pl.aptewicz.nodemaps.model;

public class Hierarchy {

	private Long id;

	private String accessSiteDescription;

	private NodeDto accessSiteNode;

	private String distributionSiteDescription;

	private NodeDto distributionSiteNode;

	private String centralSiteDescription;

	private NodeDto centralSiteNode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAccessSiteDescription() {
		return accessSiteDescription;
	}

	public void setAccessSiteDescription(String accessSiteDescription) {
		this.accessSiteDescription = accessSiteDescription;
	}

	public NodeDto getAccessSiteNode() {
		return accessSiteNode;
	}

	public void setAccessSiteNode(NodeDto accessSiteNode) {
		this.accessSiteNode = accessSiteNode;
	}

	public String getDistributionSiteDescription() {
		return distributionSiteDescription;
	}

	public void setDistributionSiteDescription(String distributionSiteDescription) {
		this.distributionSiteDescription = distributionSiteDescription;
	}

	public NodeDto getDistributionSiteNode() {
		return distributionSiteNode;
	}

	public void setDistributionSiteNode(NodeDto distributionSiteNode) {
		this.distributionSiteNode = distributionSiteNode;
	}

	public String getCentralSiteDescription() {
		return centralSiteDescription;
	}

	public void setCentralSiteDescription(String centralSiteDescription) {
		this.centralSiteDescription = centralSiteDescription;
	}

	public NodeDto getCentralSiteNode() {
		return centralSiteNode;
	}

	public void setCentralSiteNode(NodeDto centralSiteNode) {
		this.centralSiteNode = centralSiteNode;
	}
}
