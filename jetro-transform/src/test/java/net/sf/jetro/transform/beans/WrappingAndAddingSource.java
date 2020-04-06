package net.sf.jetro.transform.beans;

import java.util.Objects;

public class WrappingAndAddingSource {
	private Persons person;

	public Persons getPerson() {
		return person;
	}

	public void setPerson(Persons person) {
		this.person = person;
	}

	@Override
	public int hashCode() {
		return Objects.hash(person);
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
		WrappingAndAddingSource other = (WrappingAndAddingSource) obj;
		return Objects.equals(person, other.person);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WrappingAndAddingSource [person=").append(person).append("]");
		return builder.toString();
	}
}
