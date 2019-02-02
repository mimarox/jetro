package net.sf.jetro.object.deserializer.beans;

import java.util.Objects;

public class SubSubBean extends SubBean {
	private String subSubString;

	public String getSubSubString() {
		return subSubString;
	}

	public void setSubSubString(String subSubString) {
		this.subSubString = subSubString;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(subSubString);
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
		SubSubBean other = (SubSubBean) obj;
		return Objects.equals(baseString, other.baseString) && 
				Objects.equals(subString, other.subString) &&
				Objects.equals(subSubString, other.subSubString);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SubSubBean [subSubString=").append(subSubString)
				.append(", subString=").append(subString)
				.append(", baseString=").append(baseString).append("]");
		return builder.toString();
	}
}
