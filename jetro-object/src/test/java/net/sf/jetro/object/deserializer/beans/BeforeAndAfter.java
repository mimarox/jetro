package net.sf.jetro.object.deserializer.beans;

import java.util.Objects;

public class BeforeAndAfter {
	private String before;
	private String after;
	
	public String getBefore() {
		return before;
	}
	
	public void setBefore(String before) {
		this.before = before;
	}
	
	public String getAfter() {
		return after;
	}
	
	public void setAfter(String after) {
		this.after = after;
	}

	@Override
	public int hashCode() {
		return Objects.hash(after, before);
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
		BeforeAndAfter other = (BeforeAndAfter) obj;
		return Objects.equals(after, other.after) && Objects.equals(before, other.before);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BeforeAndAfter [before=").append(before)
		.append(", after=").append(after).append("]");
		return builder.toString();
	}
}
