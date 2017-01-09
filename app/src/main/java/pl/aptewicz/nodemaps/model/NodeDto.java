package pl.aptewicz.nodemaps.model;

import java.io.Serializable;

public class NodeDto implements Serializable {

	private Long name;

	private Double x;

	private Double y;

	public Long getName() {
		return name;
	}

	public void setName(Long name) {
		this.name = name;
	}

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}
}
