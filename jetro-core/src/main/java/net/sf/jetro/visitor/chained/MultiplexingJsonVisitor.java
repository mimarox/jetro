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

import net.sf.jetro.util.Stack;
import net.sf.jetro.visitor.JsonArrayVisitor;
import net.sf.jetro.visitor.JsonObjectVisitor;
import net.sf.jetro.visitor.JsonVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * This class multiplexes all calls to all subsequent visitors.
 * 
 * @param <R> the return type of {@link #getVisitingResult()}
 * @author matthias.rothe
 * @since 13.03.14.
 */
public final class MultiplexingJsonVisitor<R> extends UniformChainedJsonVisitor<R> {
	private List<Stack<JsonVisitor<?>>> visitorStacks;

	public MultiplexingJsonVisitor(JsonVisitor<R> masterVisitor, JsonVisitor<?>... slaveVisitors) {
		super(masterVisitor);

		if (slaveVisitors != null && slaveVisitors.length > 0) {
			visitorStacks = new ArrayList<Stack<JsonVisitor<?>>>();

			for (JsonVisitor<?> visitor : slaveVisitors) {
				Stack<JsonVisitor<?>> stack = new Stack<JsonVisitor<?>>();
				stack.push(visitor);
				visitorStacks.add(stack);
			}
		}
	}

	@Override
	protected JsonObjectVisitor<R> afterVisitObject(JsonObjectVisitor<R> visitor) {
		for (Stack<JsonVisitor<?>> stack : visitorStacks) {
			stack.push(stack.peek().visitObject());
		}

		return super.afterVisitObject(visitor);
	}

	@Override
	protected void afterVisitObjectEnd() {
		for (Stack<JsonVisitor<?>> stack : visitorStacks) {
			JsonVisitor<?> topVisitor = stack.peek();

			if (topVisitor instanceof JsonObjectVisitor) {
				topVisitor.visitEnd();
				stack.pop();
			}
		}
	}

	@Override
	protected JsonArrayVisitor<R> afterVisitArray(JsonArrayVisitor<R> visitor) {
		for (Stack<JsonVisitor<?>> stack : visitorStacks) {
			stack.push(stack.peek().visitArray());
		}

		return super.afterVisitArray(visitor);
	}

	@Override
	protected void afterVisitArrayEnd() {
		for (Stack<JsonVisitor<?>> stack : visitorStacks) {
			JsonVisitor<?> topVisitor = stack.peek();

			if (topVisitor instanceof JsonArrayVisitor) {
				topVisitor.visitEnd();
				stack.pop();
			}
		}
	}

	@Override
	protected void afterVisitProperty(String name) {
		for (Stack<JsonVisitor<?>> stack : visitorStacks) {
			stack.peek().visitProperty(name);
		}
	}

	@Override
	protected void afterVisitValue(Boolean value) {
		for (Stack<JsonVisitor<?>> stack : visitorStacks) {
			stack.peek().visitValue(value);
		}
	}

	@Override
	protected void afterVisitValue(Number value) {
		for (Stack<JsonVisitor<?>> stack : visitorStacks) {
			stack.peek().visitValue(value);
		}
	}

	@Override
	protected void afterVisitValue(String value) {
		for (Stack<JsonVisitor<?>> stack : visitorStacks) {
			stack.peek().visitValue(value);
		}
	}

	@Override
	protected void afterVisitNullValue() {
		for (Stack<JsonVisitor<?>> stack : visitorStacks) {
			stack.peek().visitNullValue();
		}
	}

	@Override
	protected void afterVisitEnd() {
		for (Stack<JsonVisitor<?>> stack : visitorStacks) {
			stack.pop().visitEnd();
		}
	}
}
