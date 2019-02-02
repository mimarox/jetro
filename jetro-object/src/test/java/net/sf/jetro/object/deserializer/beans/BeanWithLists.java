package net.sf.jetro.object.deserializer.beans;

import java.util.List;
import java.util.Objects;

public class BeanWithLists {
	private List<Double> doubles;
	private List<Boolean> booleans;
	private List<LeafBean> leafs;
	
	public List<Double> getDoubles() {
		return doubles;
	}
	
	public void setDoubles(List<Double> doubles) {
		this.doubles = doubles;
	}
	
	public List<Boolean> getBooleans() {
		return booleans;
	}
	
	public void setBooleans(List<Boolean> booleans) {
		this.booleans = booleans;
	}

	public List<LeafBean> getLeafs() {
		return leafs;
	}

	public void setLeafs(List<LeafBean> leafs) {
		this.leafs = leafs;
	}

	@Override
	public int hashCode() {
		return Objects.hash(booleans, doubles, leafs);
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
		BeanWithLists other = (BeanWithLists) obj;
		return Objects.equals(booleans, other.booleans) && Objects.equals(doubles, other.doubles)
				&& Objects.equals(leafs, other.leafs);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BeanWithLists [doubles=").append(doubles)
				.append(", booleans=").append(booleans)
				.append(", leafs=").append(leafs).append("]");
		return builder.toString();
	}
}
