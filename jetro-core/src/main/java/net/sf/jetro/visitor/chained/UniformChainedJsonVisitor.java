package net.sf.jetro.visitor.chained;

import net.sf.jetro.visitor.JsonArrayVisitor;
import net.sf.jetro.visitor.JsonObjectVisitor;
import net.sf.jetro.visitor.JsonVisitor;

public abstract class UniformChainedJsonVisitor<R> extends ChainedJsonVisitor<R> {
	private class UniformChainedJsonObjectVisitor extends ChainedJsonObjectVisitor<R> {
		public UniformChainedJsonObjectVisitor(JsonVisitor<R> nextVisitor) {
			super(nextVisitor);
		}

		protected void beforeVisitObject() {
			UniformChainedJsonVisitor.this.beforeVisitObject();
		}

		protected JsonObjectVisitor<R> afterVisitObject(JsonObjectVisitor<R> jsonObjectVisitor) {
			return UniformChainedJsonVisitor.this.afterVisitObject(jsonObjectVisitor);
		}

		protected void beforeVisitArray() {
			UniformChainedJsonVisitor.this.beforeVisitArray();
		}

		protected JsonArrayVisitor<R> afterVisitArray(JsonArrayVisitor<R> jsonArrayVisitor) {
			return UniformChainedJsonVisitor.this.afterVisitArray(jsonArrayVisitor);
		}

		protected String beforeVisitProperty(String name) {
			return UniformChainedJsonVisitor.this.beforeVisitProperty(name);
		}

		protected void afterVisitProperty(String name) {
			UniformChainedJsonVisitor.this.afterVisitProperty(name);
		}

		protected boolean beforeVisitValue(boolean value) {
			return UniformChainedJsonVisitor.this.beforeVisitValue(value);
		}

		protected void afterVisitValue(boolean value) {
			UniformChainedJsonVisitor.this.afterVisitValue(value);
		}

		protected Number beforeVisitValue(Number value) {
			return UniformChainedJsonVisitor.this.beforeVisitValue(value);
		}

		protected void afterVisitValue(Number value) {
			UniformChainedJsonVisitor.this.afterVisitValue(value);
		}

		protected String beforeVisitValue(String value) {
			return UniformChainedJsonVisitor.this.beforeVisitValue(value);
		}

		protected void afterVisitValue(String value) {
			UniformChainedJsonVisitor.this.afterVisitValue(value);
		}

		protected void beforeVisitNullValue() {
			UniformChainedJsonVisitor.this.beforeVisitNullValue();
		}

		protected void afterVisitNullValue() {
			UniformChainedJsonVisitor.this.afterVisitNullValue();
		}

		protected void beforeVisitEnd() {
			UniformChainedJsonVisitor.this.beforeVisitObjectEnd();
		}

		protected void afterVisitEnd() {
			UniformChainedJsonVisitor.this.afterVisitObjectEnd();
		}

		protected void beforeGetVisitingResult() {
			UniformChainedJsonVisitor.this.beforeGetVisitingResult();
		}

		protected R afterGetVisitingResult(R visitingResult) {
			return UniformChainedJsonVisitor.this.afterGetVisitingResult(visitingResult);
		}
	}

	private class UniformChainedJsonArrayVisitor extends ChainedJsonArrayVisitor<R> {
		public UniformChainedJsonArrayVisitor(JsonVisitor<R> nextVisitor) {
			super(nextVisitor);
		}

		protected void beforeVisitObject() {
			UniformChainedJsonVisitor.this.beforeVisitObject();
		}

		protected JsonObjectVisitor<R> afterVisitObject(JsonObjectVisitor<R> jsonObjectVisitor) {
			return UniformChainedJsonVisitor.this.afterVisitObject(jsonObjectVisitor);
		}

		protected void beforeVisitArray() {
			UniformChainedJsonVisitor.this.beforeVisitArray();
		}

		protected JsonArrayVisitor<R> afterVisitArray(JsonArrayVisitor<R> jsonArrayVisitor) {
			return UniformChainedJsonVisitor.this.afterVisitArray(jsonArrayVisitor);
		}

		protected String beforeVisitProperty(String name) {
			return UniformChainedJsonVisitor.this.beforeVisitProperty(name);
		}

		protected void afterVisitProperty(String name) {
			UniformChainedJsonVisitor.this.afterVisitProperty(name);
		}

		protected boolean beforeVisitValue(boolean value) {
			return UniformChainedJsonVisitor.this.beforeVisitValue(value);
		}

		protected void afterVisitValue(boolean value) {
			UniformChainedJsonVisitor.this.afterVisitValue(value);
		}

		protected Number beforeVisitValue(Number value) {
			return UniformChainedJsonVisitor.this.beforeVisitValue(value);
		}

		protected void afterVisitValue(Number value) {
			UniformChainedJsonVisitor.this.afterVisitValue(value);
		}

		protected String beforeVisitValue(String value) {
			return UniformChainedJsonVisitor.this.beforeVisitValue(value);
		}

		protected void afterVisitValue(String value) {
			UniformChainedJsonVisitor.this.afterVisitValue(value);
		}

		protected void beforeVisitNullValue() {
			UniformChainedJsonVisitor.this.beforeVisitNullValue();
		}

		protected void afterVisitNullValue() {
			UniformChainedJsonVisitor.this.afterVisitNullValue();
		}

		protected void beforeVisitEnd() {
			UniformChainedJsonVisitor.this.beforeVisitArrayEnd();
		}

		protected void afterVisitEnd() {
			UniformChainedJsonVisitor.this.afterVisitArrayEnd();
		}

		protected void beforeGetVisitingResult() {
			UniformChainedJsonVisitor.this.beforeGetVisitingResult();
		}

		protected R afterGetVisitingResult(R visitingResult) {
			return UniformChainedJsonVisitor.this.afterGetVisitingResult(visitingResult);
		}
	}

	public UniformChainedJsonVisitor() {
	}

	public UniformChainedJsonVisitor(JsonVisitor<R> nextVisitor) {
		super(nextVisitor);
	}

	@Override
	protected JsonObjectVisitor<R> afterVisitObject(JsonObjectVisitor<R> visitor) {
		return new UniformChainedJsonObjectVisitor(visitor);
	}

	@Override
	protected JsonArrayVisitor<R> afterVisitArray(JsonArrayVisitor<R> visitor) {
		return new UniformChainedJsonArrayVisitor(visitor);
	}

	protected void beforeVisitObjectEnd() {
	}

	protected void afterVisitObjectEnd() {
	}

	protected void beforeVisitArrayEnd() {
	}

	protected void afterVisitArrayEnd() {
	}
}