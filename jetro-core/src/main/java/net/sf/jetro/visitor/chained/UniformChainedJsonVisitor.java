package net.sf.jetro.visitor.chained;

import net.sf.jetro.visitor.JsonArrayVisitor;
import net.sf.jetro.visitor.JsonObjectVisitor;
import net.sf.jetro.visitor.JsonVisitor;

public abstract class UniformChainedJsonVisitor<R> extends ChainedJsonVisitor<R> {
	private class UniformChainedJsonObjectVisitor extends ChainedJsonObjectVisitor<R> {
		public UniformChainedJsonObjectVisitor(JsonVisitor<R> nextVisitor) {
			super(nextVisitor);
		}

		@Override
		protected void beforeVisitObject() {
			UniformChainedJsonVisitor.this.beforeVisitObject();
		}

		@Override
		protected JsonObjectVisitor<R> afterVisitObject(JsonObjectVisitor<R> jsonObjectVisitor) {
			return UniformChainedJsonVisitor.this.afterVisitObject(jsonObjectVisitor);
		}

		@Override
		protected void beforeVisitArray() {
			UniformChainedJsonVisitor.this.beforeVisitArray();
		}

		@Override
		protected JsonArrayVisitor<R> afterVisitArray(JsonArrayVisitor<R> jsonArrayVisitor) {
			return UniformChainedJsonVisitor.this.afterVisitArray(jsonArrayVisitor);
		}

		@Override
		protected String beforeVisitProperty(String name) {
			return UniformChainedJsonVisitor.this.beforeVisitProperty(name);
		}

		@Override
		protected void afterVisitProperty(String name) {
			UniformChainedJsonVisitor.this.afterVisitProperty(name);
		}

		@Override
		protected Boolean beforeVisitValue(boolean value) {
			return UniformChainedJsonVisitor.this.beforeVisitValue(value);
		}

		@Override
		protected void afterVisitValue(Boolean value) {
			UniformChainedJsonVisitor.this.afterVisitValue(value);
		}

		@Override
		protected Number beforeVisitValue(Number value) {
			return UniformChainedJsonVisitor.this.beforeVisitValue(value);
		}

		@Override
		protected void afterVisitValue(Number value) {
			UniformChainedJsonVisitor.this.afterVisitValue(value);
		}

		@Override
		protected String beforeVisitValue(String value) {
			return UniformChainedJsonVisitor.this.beforeVisitValue(value);
		}

		@Override
		protected void afterVisitValue(String value) {
			UniformChainedJsonVisitor.this.afterVisitValue(value);
		}

		@Override
		protected boolean beforeVisitNullValue() {
			return UniformChainedJsonVisitor.this.beforeVisitNullValue();
		}

		@Override
		protected void afterVisitNullValue() {
			UniformChainedJsonVisitor.this.afterVisitNullValue();
		}

		@Override
		protected boolean beforeVisitEnd() {
			return UniformChainedJsonVisitor.this.beforeVisitObjectEnd();
		}

		@Override
		protected void afterVisitEnd() {
			UniformChainedJsonVisitor.this.afterVisitObjectEnd();
		}

		@Override
		protected void beforeGetVisitingResult() {
			UniformChainedJsonVisitor.this.beforeGetVisitingResult();
		}

		@Override
		protected R afterGetVisitingResult(R visitingResult) {
			return UniformChainedJsonVisitor.this.afterGetVisitingResult(visitingResult);
		}
	}

	private class UniformChainedJsonArrayVisitor extends ChainedJsonArrayVisitor<R> {
		public UniformChainedJsonArrayVisitor(JsonVisitor<R> nextVisitor) {
			super(nextVisitor);
		}

		@Override
		protected void beforeVisitObject() {
			UniformChainedJsonVisitor.this.beforeVisitObject();
		}

		@Override
		protected JsonObjectVisitor<R> afterVisitObject(JsonObjectVisitor<R> jsonObjectVisitor) {
			return UniformChainedJsonVisitor.this.afterVisitObject(jsonObjectVisitor);
		}

		@Override
		protected void beforeVisitArray() {
			UniformChainedJsonVisitor.this.beforeVisitArray();
		}

		@Override
		protected JsonArrayVisitor<R> afterVisitArray(JsonArrayVisitor<R> jsonArrayVisitor) {
			return UniformChainedJsonVisitor.this.afterVisitArray(jsonArrayVisitor);
		}

		@Override
		protected String beforeVisitProperty(String name) {
			return UniformChainedJsonVisitor.this.beforeVisitProperty(name);
		}

		@Override
		protected void afterVisitProperty(String name) {
			UniformChainedJsonVisitor.this.afterVisitProperty(name);
		}

		@Override
		protected Boolean beforeVisitValue(boolean value) {
			return UniformChainedJsonVisitor.this.beforeVisitValue(value);
		}

		@Override
		protected void afterVisitValue(Boolean value) {
			UniformChainedJsonVisitor.this.afterVisitValue(value);
		}

		@Override
		protected Number beforeVisitValue(Number value) {
			return UniformChainedJsonVisitor.this.beforeVisitValue(value);
		}

		@Override
		protected void afterVisitValue(Number value) {
			UniformChainedJsonVisitor.this.afterVisitValue(value);
		}

		@Override
		protected String beforeVisitValue(String value) {
			return UniformChainedJsonVisitor.this.beforeVisitValue(value);
		}

		@Override
		protected void afterVisitValue(String value) {
			UniformChainedJsonVisitor.this.afterVisitValue(value);
		}

		@Override
		protected boolean beforeVisitNullValue() {
			return UniformChainedJsonVisitor.this.beforeVisitNullValue();
		}

		@Override
		protected void afterVisitNullValue() {
			UniformChainedJsonVisitor.this.afterVisitNullValue();
		}

		@Override
		protected boolean beforeVisitEnd() {
			return UniformChainedJsonVisitor.this.beforeVisitArrayEnd();
		}

		@Override
		protected void afterVisitEnd() {
			UniformChainedJsonVisitor.this.afterVisitArrayEnd();
		}

		@Override
		protected void beforeGetVisitingResult() {
			UniformChainedJsonVisitor.this.beforeGetVisitingResult();
		}

		@Override
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

	protected boolean beforeVisitObjectEnd() {
		return true;
	}

	protected void afterVisitObjectEnd() {
	}

	protected boolean beforeVisitArrayEnd() {
		return true;
	}

	protected void afterVisitArrayEnd() {
	}
}