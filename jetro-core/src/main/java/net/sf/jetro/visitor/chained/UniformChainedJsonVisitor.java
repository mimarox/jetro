/*
 * #%L
 * Jetro Core
 * %%
 * Copyright (C) 2013 - 2016 The original author or authors.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
		protected JsonVisitor<R> getNextVisitor() {
			return UniformChainedJsonVisitor.this.getNextVisitor(super.getNextVisitor());
		}

		@Override
		protected boolean beforeVisitObject() {
			return UniformChainedJsonVisitor.this.beforeVisitObject();
		}

		@Override
		protected JsonObjectVisitor<R> afterVisitObject(JsonObjectVisitor<R> jsonObjectVisitor) {
			return UniformChainedJsonVisitor.this.afterVisitObject(jsonObjectVisitor);
		}

		@Override
		protected boolean beforeVisitArray() {
			return UniformChainedJsonVisitor.this.beforeVisitArray();
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
		protected JsonVisitor<R> getNextVisitor() {
			return UniformChainedJsonVisitor.this.getNextVisitor(super.getNextVisitor());
		}

		@Override
		protected boolean beforeVisitObject() {
			return UniformChainedJsonVisitor.this.beforeVisitObject();
		}

		@Override
		protected JsonObjectVisitor<R> afterVisitObject(JsonObjectVisitor<R> jsonObjectVisitor) {
			return UniformChainedJsonVisitor.this.afterVisitObject(jsonObjectVisitor);
		}

		@Override
		protected boolean beforeVisitArray() {
			return UniformChainedJsonVisitor.this.beforeVisitArray();
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

	protected JsonVisitor<R> getNextVisitor(JsonVisitor<R> nextVisitor) {
		return nextVisitor;
	}
}