package net.sf.jetro.object.annotations;

import java.util.Objects;

public class SampleBeanC {
	
	@JsonAlias(newKey = "stringB")
	private String stringA;

	public SampleBeanC() {}
	
	public SampleBeanC(String stringA) {
		super();
		this.stringA = stringA;
	}

	public String getStringA() {
		return stringA;
	}

	public void setStringA(String stringA) {
		this.stringA = stringA;
	}

	@Override
	public int hashCode() {
		return Objects.hash(stringA);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SampleBeanC other = (SampleBeanC) obj;
		return Objects.equals(stringA, other.stringA);
	}
}
