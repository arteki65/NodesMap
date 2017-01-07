package pl.aptewicz.nodemaps.model;

import java.io.Serializable;

public class AccessPointDto implements Serializable {

	private Long id;

	private NodeDto node;

	private String type;

	private String description;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public NodeDto getNode() {
		return node;
	}

	public void setNode(NodeDto node) {
		this.node = node;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
