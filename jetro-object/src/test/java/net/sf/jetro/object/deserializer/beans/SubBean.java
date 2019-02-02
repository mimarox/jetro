package net.sf.jetro.object.deserializer.beans;

import java.util.Objects;

public class SubBean extends BaseBean {
	protected String subString;

	public String getSubString() {
		return subString;
	}

	public void setSubString(String subString) {
		this.subString = subString;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(subString);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SubBean other = (SubBean) obj;
		return Objects.equals(baseString, other.baseString) && 
				Objects.equals(subString, other.subString);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SubBean [subString=").append(subString)
			.append(", baseString=").append(baseString).append("]");
		return builder.toString();
	}
}
