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

/**
 * A {@link JsonVisitor} supporting chaining of visitors.
 * 
 * @param <R> the return type of {@link #getVisitingResult()}
 * @author Matthias Rothe
 */
public abstract class ChainedJsonVisitor<R> implements JsonVisitor<R> {
	private JsonVisitor<R> nextVisitor;

	/**
	 * This constructor is used if the resulting object is supposed to be the end point of a
	 * visitor chain. Without overwriting any hook methods this results in a no-op visitor.
	 */
	public ChainedJsonVisitor() {
	}

	public ChainedJsonVisitor(final JsonVisitor<R> nextVisitor) {
		this.nextVisitor = nextVisitor;
	}

	protected JsonVisitor<R> getNextVisitor() {
		return nextVisitor;
	}

	@Override
	@SuppressWarnings("unchecked")
	public final JsonObjectVisitor<R> visitObject() {
		boolean passOn = beforeVisitObject();
		return afterVisitObject(getNextVisitor() != null && passOn ?
				getNextVisitor().visitObject() : ChainedJsonObjectVisitor.NO_OP_VISITOR);
	}

	protected boolean beforeVisitObject() {
		return true;
	}

	protected JsonObjectVisitor<R> afterVisitObject(JsonObjectVisitor<R> jsonObjectVisitor) {
		return jsonObjectVisitor;
	}

	@Override
	@SuppressWarnings("unchecked")
	public final JsonArrayVisitor<R> visitArray() {
		boolean passOn = beforeVisitArray();
		return afterVisitArray(getNextVisitor() != null && passOn ?
				getNextVisitor().visitArray() : ChainedJsonArrayVisitor.NO_OP_VISITOR);
	}

	protected boolean beforeVisitArray() {
		return true;
	}

	protected JsonArrayVisitor<R> afterVisitArray(JsonArrayVisitor<R> jsonArrayVisitor) {
		return jsonArrayVisitor;
	}

	@Override
	public final void visitProperty(String name) {
		name = beforeVisitProperty(name);

		if (getNextVisitor() != null && name != null) {
			getNextVisitor().visitProperty(name);
		}

		afterVisitProperty(name);
	}

	protected String beforeVisitProperty(String name) {
		return name;
	}

	protected void afterVisitProperty(String name) {
	}

	@Override
	public final void visitValue(boolean value) {
		Boolean processedValue = beforeVisitValue(value);

		if (getNextVisitor() != null && processedValue != null) {
			getNextVisitor().visitValue(processedValue);
		}

		afterVisitValue(processedValue);
	}

	protected Boolean beforeVisitValue(boolean value) {
		return value;
	}

	protected void afterVisitValue(Boolean value) {
	}

	@Override
	public final void visitValue(Number value) {
		value = beforeVisitValue(value);

		if (getNextVisitor() != null && value != null) {
			getNextVisitor().visitValue(value);
		}

		afterVisitValue(value);
	}

	protected Number beforeVisitValue(Number value) {
		return value;
	}

	protected void afterVisitValue(Number value) {
	}

	@Override
	public final void visitValue(String value) {
		value = beforeVisitValue(value);

		if (getNextVisitor() != null && value != null) {
			getNextVisitor().visitValue(value);
		}

		afterVisitValue(value);
	}

	protected String beforeVisitValue(String value) {
		return value;
	}

	protected void afterVisitValue(String value) {
	}

	@Override
	public final void visitNullValue() {
		boolean passOn = beforeVisitNullValue();

		if (getNextVisitor() != null && passOn) {
			getNextVisitor().visitNullValue();
		}

		afterVisitNullValue();
	}

	protected boolean beforeVisitNullValue() {
		return true;
	}

	protected void afterVisitNullValue() {
	}

	@Override
	public final void visitEnd() {
		boolean passOn = beforeVisitEnd();

		if (getNextVisitor() != null && passOn) {
			getNextVisitor().visitEnd();
		}

		afterVisitEnd();
	}

	protected boolean beforeVisitEnd() {
		return true;
	}

	protected void afterVisitEnd() {
	}

	@Override
	public final R getVisitingResult() {
		beforeGetVisitingResult();
		return afterGetVisitingResult(getNextVisitor() == null ? null : getNextVisitor().getVisitingResult());
	}

	protected void beforeGetVisitingResult() {
	}

	protected R afterGetVisitingResult(R visitingResult) {
		return visitingResult;
	}

	public void attachVisitor(JsonVisitor<R> visitor) {
		attachVisitor(visitor, false);
	}

	public void attachVisitor(JsonVisitor<R> visitor, boolean replace) {
		if (nextVisitor == null) {
			nextVisitor = visitor;
		} else if (nextVisitor instanceof ChainedJsonVisitor) {
			((ChainedJsonVisitor<R>) nextVisitor).attachVisitor(visitor, replace);
		} else if (replace) {
			nextVisitor = visitor;
		} else {
			throw new IllegalStateException("Cannot attach visitor as a visitor is already "
					+ "attached and replacement was not requested");
		}
	}

	public void detachVisitor(JsonVisitor<R> visitor) {
		if (nextVisitor == null) {
			// do nothing
		} else if (nextVisitor.equals(visitor)) {
			nextVisitor = null;
		} else if (nextVisitor instanceof ChainedJsonVisitor) {
			((ChainedJsonVisitor<R>) nextVisitor).detachVisitor(visitor);
		}
	}
}