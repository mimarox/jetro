package net.sf.jetro.object.deserializer.beans;

import java.util.Objects;

public class BaseBean {
	protected String baseString;

	public BaseBean() {}
	
	public BaseBean(String baseString) {
		this.baseString = baseString;
	}

	public String getBaseString() {
		return baseString;
	}

	public void setBaseString(String baseString) {
		this.baseString = baseString;
	}

	@Override
	public int hashCode() {
		return Objects.hash(baseString);
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
		BaseBean other = (BaseBean) obj;
		return Objects.equals(baseString, other.baseString);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BaseBean [baseString=").append(baseString).append("]");
		return builder.toString();
	}
}
