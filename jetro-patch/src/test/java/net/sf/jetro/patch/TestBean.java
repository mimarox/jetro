package net.sf.jetro.patch;

import java.util.List;
import java.util.Objects;

public class TestBean {
	private List<Integer> a;

	public List<Integer> getA() {
		return a;
	}

	public void setA(final List<Integer> a) {
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
		TestBean other = (TestBean) obj;
		return Objects.equals(a, other.a);
	}
}
