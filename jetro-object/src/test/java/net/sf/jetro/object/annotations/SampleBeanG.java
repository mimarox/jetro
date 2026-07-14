package net.sf.jetro.object.annotations;

import java.util.Objects;

@JsonIgnoreProperties(properties = {"stringD", "longD"})
public class SampleBeanG extends SampleBeanB {
	private String stringC;
	private String stringD;
	private long longC;
	private long longD;
	
	public SampleBeanG() {}
	
	public SampleBeanG(String stringA, String stringB, long longA, long longB,
			String stringC, String stringD, long longC, long longD) {
		super(stringA, stringB, longA, longB);
		this.stringC = stringC;
		this.stringD = stringD;
		this.longC = longC;
		this.longD = longD;
	}

	public String getStringC() {
		return stringC;
	}

	public void setStringC(String stringC) {
		this.stringC = stringC;
	}

	public String getStringD() {
		return stringD;
	}

	public void setStringD(String stringD) {
		this.stringD = stringD;
	}

	public long getLongC() {
		return longC;
	}

	public void setLongC(long longC) {
		this.longC = longC;
	}

	public long getLongD() {
		return longD;
	}

	public void setLongD(long longD) {
		this.longD = longD;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(longC, longD, stringC, stringD);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SampleBeanG other = (SampleBeanG) obj;
		return longC == other.longC && longD == other.longD && Objects.equals(stringC, other.stringC)
				&& Objects.equals(stringD, other.stringD);
	}
}
