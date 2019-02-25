package net.sf.jetro.object.deserializer.beans;

import java.util.Objects;

public class ChildBean {
	private boolean happy;
	private LeafBean leaf;
	
	public boolean isHappy() {
		return happy;
	}
	
	public void setHappy(boolean happy) {
		this.happy = happy;
	}
	
	public LeafBean getLeaf() {
		return leaf;
	}
	
	public void setLeaf(LeafBean leaf) {
		this.leaf = leaf;
	}

	@Override
	public int hashCode() {
		return Objects.hash(happy, leaf);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ChildBean other = (ChildBean) obj;
		return happy == other.happy && Objects.equals(leaf, other.leaf);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ChildBean [happy=").append(happy).append(", leaf=").append(leaf).append("]");
		return builder.toString();
	}
}
