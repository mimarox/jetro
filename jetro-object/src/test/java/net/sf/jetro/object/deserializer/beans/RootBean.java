package net.sf.jetro.object.deserializer.beans;

import java.util.Objects;

public class RootBean {
	private String string;
	private ChildBean child;
	private LeafBean leaf;
	
	public String getString() {
		return string;
	}
	
	public void setString(String string) {
		this.string = string;
	}
	
	public ChildBean getChild() {
		return child;
	}
	
	public void setChild(ChildBean child) {
		this.child = child;
	}
	
	public LeafBean getLeaf() {
		return leaf;
	}
	
	public void setLeaf(LeafBean leaf) {
		this.leaf = leaf;
	}

	@Override
	public int hashCode() {
		return Objects.hash(child, leaf, string);
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
		RootBean other = (RootBean) obj;
		return Objects.equals(child, other.child) && Objects.equals(leaf, other.leaf)
				&& Objects.equals(string, other.string);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RootBean [string=").append(string).append(", child=")
				.append(child).append(", leaf=").append(leaf).append("]");
		return builder.toString();
	}
}
