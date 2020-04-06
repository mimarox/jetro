package net.sf.jetro.transform.beans;

import java.util.List;
import java.util.Objects;

public class ExpectedObject {
	private List<String> a;

	public List<String> getA() {
		return a;
	}

	public void setA(List<String> a) {
		this.a = a;
	}

	@Override
	public int hashCode() {
		return Objects.hash(a);
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
		ExpectedObject other = (ExpectedObject) obj;
		return Objects.equals(a, other.a);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExpectedObject [a=").append(a).append("]");
		return builder.toString();
	}
}
