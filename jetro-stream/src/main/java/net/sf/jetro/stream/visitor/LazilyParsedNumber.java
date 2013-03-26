package net.sf.jetro.stream.visitor;

public class LazilyParsedNumber extends Number {
	private static final long serialVersionUID = 8387800346023499875L;
	private String numericValue;

	public LazilyParsedNumber(final String numericValue) {
		if (numericValue == null) {
			throw new IllegalArgumentException("numericValue must not be null");
		}

		this.numericValue = numericValue;
	}

	@Override
	public double doubleValue() {
		return Double.parseDouble(numericValue);
	}

	@Override
	public float floatValue() {
		return Float.parseFloat(numericValue);
	}

	@Override
	public int intValue() {
		return Integer.parseInt(numericValue);
	}

	@Override
	public long longValue() {
		return Long.parseLong(numericValue);
	}

	@Override
	public String toString() {
		return numericValue;
	}
}