package net.sf.jetro.visitor.chained;

public class StringReturningJsonVisitor extends UniformChainedJsonVisitor<String> {
	private final StringBuilder result = new StringBuilder();

	@Override
	protected boolean beforeVisitObject() {
		result.append("{");
		return true;
	}

	@Override
	protected boolean beforeVisitArray() {
		result.append("[");
		return true;
	}

	@Override
	protected String beforeVisitProperty(String name) {
		result.append(name).append(": ");
		return name;
	}

	@Override
	protected Boolean beforeVisitValue(boolean value) {
		visitAnyValue(value);
		return value;
	}

	@Override
	protected Number beforeVisitValue(Number value) {
		visitAnyValue(value);
		return value;
	}

	@Override
	protected String beforeVisitValue(String value) {
		visitAnyValue(value);
		return value;
	}

	protected void afterVisitNullValue() {
		visitAnyValue(null);
	}
	
	private void visitAnyValue(Object value) {
		result.append(value).append(", ");
	}

	@Override
	protected void afterVisitObjectEnd() {
		result.append("}, ");
	}

	protected void afterVisitArrayEnd() {
		result.append("], ");
	}
	
	@Override
	protected String afterGetVisitingResult(String visitingResult) {
		return result.toString();
	}
}
