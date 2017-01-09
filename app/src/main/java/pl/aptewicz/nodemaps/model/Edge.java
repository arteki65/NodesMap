package pl.aptewicz.nodemaps.model;

public class Edge {

	private Long name;

	private NodeDto nodeA;

	private NodeDto nodeB;

	private Double length;

	public Long getName() {
		return name;
	}

	public void setName(Long name) {
		this.name = name;
	}

	public NodeDto getNodeA() {
		return nodeA;
	}

	public void setNodeA(NodeDto nodeA) {
		this.nodeA = nodeA;
	}

	public NodeDto getNodeB() {
		return nodeB;
	}

	public void setNodeB(NodeDto nodeB) {
		this.nodeB = nodeB;
	}

	public Double getLength() {
		return length;
	}

	public void setLength(Double length) {
		this.length = length;
	}
}
