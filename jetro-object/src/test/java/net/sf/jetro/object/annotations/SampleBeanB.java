package net.sf.jetro.object.annotations;

import java.util.Objects;

public class SampleBeanB {
	private String stringA;
	
	@JsonIgnore
	private String stringB;
	
	private long longA;
	
	@JsonIgnore
	private long longB;
	
	public SampleBeanB() {}
	
	public SampleBeanB(String stringA, String stringB, long longA, long longB) {
		super();
		this.stringA = stringA;
		this.stringB = stringB;
		this.longA = longA;
		this.longB = longB;
	}

	public String getStringA() {
		return stringA;
	}
	
	public void setStringA(String stringA) {
		this.stringA = stringA;
	}
	
	public String getStringB() {
		return stringB;
	}
	
	public void setStringB(String stringB) {
		this.stringB = stringB;
	}
	
	public long getLongA() {
		return longA;
	}
	
	public void setLongA(long longA) {
		this.longA = longA;
	}
	
	public long getLongB() {
		return longB;
	}
	
	public void setLongB(long longB) {
		this.longB = longB;
	}

	@Override
	public int hashCode() {
		return Objects.hash(Long.valueOf(longA), Long.valueOf(longB), stringA, stringB);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SampleBeanB other = (SampleBeanB) obj;
		return longA == other.longA && longB == other.longB && Objects.equals(stringA, other.stringA)
				&& Objects.equals(stringB, other.stringB);
	}

}
