package net.sf.jetro.transform.beans;

import java.util.List;
import java.util.Objects;

public class WrappingAndAddingTarget {
	private List<Persons> persons;

	public List<Persons> getPersons() {
		return persons;
	}

	public void setPersons(List<Persons> persons) {
		this.persons = persons;
	}

	@Override
	public int hashCode() {
		return Objects.hash(persons);
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
		WrappingAndAddingTarget other = (WrappingAndAddingTarget) obj;
		return Objects.equals(persons, other.persons);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WrappingAndAddingTarget [persons=").append(persons).append("]");
		return builder.toString();
	}
}
