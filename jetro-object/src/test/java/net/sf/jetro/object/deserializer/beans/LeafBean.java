package net.sf.jetro.object.deserializer.beans;

import java.util.Objects;

public class LeafBean {
	private Number number;

	public LeafBean() {}
	
	public LeafBean(Number number) {
		this.number = number;
	}
	
	public Number getNumber() {
		return number;
	}

	public void setNumber(Number number) {
		this.number = number;
	}

	@Override
	public int hashCode() {
		return Objects.hash(number.intValue());
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
		LeafBean other = (LeafBean) obj;
		return Objects.equals(number.intValue(), other.number.intValue());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LeafBean [number=").append(number).append("]");
		return builder.toString();
	}
}
