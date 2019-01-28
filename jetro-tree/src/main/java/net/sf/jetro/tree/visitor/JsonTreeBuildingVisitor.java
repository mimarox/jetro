/*
 * #%L
 * Jetro Tree
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
package net.sf.jetro.tree.visitor;

import net.sf.jetro.exception.MalformedJsonException;
import net.sf.jetro.tree.JsonArray;
import net.sf.jetro.tree.JsonBoolean;
import net.sf.jetro.tree.JsonElement;
import net.sf.jetro.tree.JsonNull;
import net.sf.jetro.tree.JsonNumber;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonProperty;
import net.sf.jetro.tree.JsonString;
import net.sf.jetro.tree.JsonType;
import net.sf.jetro.tree.VirtualJsonRoot;
import net.sf.jetro.util.Stack;
import net.sf.jetro.visitor.JsonVisitor;
import net.sf.jetro.visitor.pathaware.PathAwareJsonVisitor;

public class JsonTreeBuildingVisitor extends PathAwareJsonVisitor<JsonElement> {
	private Stack<JsonElement> elements = new Stack<JsonElement>();
	private boolean reset;

	public JsonTreeBuildingVisitor() {
		this(null);
	}

	public JsonTreeBuildingVisitor(JsonVisitor<JsonElement> nextVisitor) {
		super(nextVisitor);
		elements.push(new VirtualJsonRoot());
	}

	@Override
	protected boolean doBeforeVisitObject() {
		elements.push(new JsonObject(currentPath()));
		return true;
	}

	@Override
	protected boolean doBeforeVisitArray() {
		elements.push(new JsonArray(currentPath()));
		return true;
	}

	@Override
	protected void afterVisitProperty(String name) {
		elements.push(new JsonProperty(name));
	}

	@Override
	protected void afterVisitValue(Boolean value) {
		afterVisitValue(new JsonBoolean(currentPath(), value));
	}

	@Override
	protected void afterVisitValue(Number value) {
		afterVisitValue(new JsonNumber(currentPath(), value));
	}

	@Override
	protected void afterVisitValue(String value) {
		afterVisitValue(new JsonString(currentPath(), value));
	}

	@Override
	protected void afterVisitNullValue() {
		afterVisitValue(new JsonNull(currentPath()));
	}

	@Override
	protected void afterVisitObjectEnd() {
		JsonElement top = elements.pop();

		if (!(top instanceof JsonObject)) {
			throw new MalformedJsonException("Object end out of scope. Expected JsonObject, but was "
					+ top.getClass().getSimpleName());
		}

		JsonObject object = (JsonObject) top;
		afterVisitValue(object);
	}

	@Override
	protected void afterVisitArrayEnd() {
		JsonElement top = elements.pop();

		if (!(top instanceof JsonArray)) {
			throw new MalformedJsonException("Array end out of scope. Expected JsonArray, but was "
					+ top.getClass().getSimpleName());
		}

		JsonArray object = (JsonArray) top;
		afterVisitValue(object);
	}

	private void afterVisitValue(JsonType value) {
		JsonElement top = elements.peek();

		if (top instanceof VirtualJsonRoot) {
			((VirtualJsonRoot) top).add(value);
		} else if (top instanceof JsonArray) {
			((JsonArray) top).add(value);
		} else if (top instanceof JsonProperty) {
			JsonProperty property = (JsonProperty) elements.pop();
			property.setValue(value);

			top = elements.peek();

			if (top instanceof VirtualJsonRoot) {
				((VirtualJsonRoot) top).add(property);
			} else if (top instanceof JsonObject) {
				((JsonObject) top).add(property);
			} else {
				throw new MalformedJsonException(
					"JSON property occurred outside of scope. Expected either VirtualJsonRoot or JsonObject, but was "
							+ top.getClass().getSimpleName());
			}
		} else {
			throw new MalformedJsonException(
				"JSON primitive occurred outside of scope. Expected either VirtualJsonRoot, JsonArray or JsonProperty, but was "
						+ top.getClass().getSimpleName());
		}
	}

	@Override
	protected void afterVisitEnd() {
		JsonElement top = elements.peek();

		if (!(top instanceof VirtualJsonRoot)) {
			throw new MalformedJsonException("End out of scope. Expected VirtualJsonRoot, but was "
					+ top.getClass().getSimpleName());
		}

		reset = true;
	}

	@Override
	protected JsonElement afterGetVisitingResult(JsonElement element) {
		JsonElement result;
		JsonElement top = elements.peek();

		if (top instanceof VirtualJsonRoot) {
			VirtualJsonRoot root = (VirtualJsonRoot) top;
			result = root.size() == 1 ? root.get(0) : root;
		} else {
			result = top;
		}

		if (element != null) {
			if (result instanceof VirtualJsonRoot && element instanceof VirtualJsonRoot) {
				((VirtualJsonRoot) result).addAll(0, (VirtualJsonRoot) element);
			} else if (result instanceof VirtualJsonRoot) {
				((VirtualJsonRoot) result).add(0, element);
			} else if (element instanceof VirtualJsonRoot) {
				((VirtualJsonRoot) element).add(result);
				result = element;
			} else {
				VirtualJsonRoot root = new VirtualJsonRoot();
				root.add(element);
				root.add(result);
				result = root;
			}
		}

		if (reset) {
			elements.pop();
			elements.push(new VirtualJsonRoot());
			reset = false;
		}

		return result;
	}
}