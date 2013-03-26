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
	protected void doBeforeVisitObject() {
		elements.push(new JsonObject(currentPath()));
	}

	@Override
	protected void doBeforeVisitArray() {
		elements.push(new JsonArray(currentPath()));
	}

	@Override
	protected void afterVisitProperty(String name) {
		elements.push(new JsonProperty(name));
	}

	@Override
	protected void afterVisitValue(boolean value) {
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
	protected void doAfterVisitObjectEnd() {
		JsonElement top = elements.pop();

		if (!(top instanceof JsonObject)) {
			throw new MalformedJsonException("Object end out of scope. Expected JsonObject, but was "
					+ top.getClass().getSimpleName());
		}

		JsonObject object = (JsonObject) top;
		afterVisitValue(object);
	}

	@Override
	protected void doAfterVisitArrayEnd() {
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
	protected void doAfterVisitEnd() {
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
				for (JsonElement childElement : (VirtualJsonRoot) result) {
					((VirtualJsonRoot) element).add(childElement);
				}
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