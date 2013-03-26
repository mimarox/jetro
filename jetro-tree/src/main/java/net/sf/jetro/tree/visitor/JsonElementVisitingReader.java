package net.sf.jetro.tree.visitor;

import net.sf.jetro.tree.JsonArray;
import net.sf.jetro.tree.JsonBoolean;
import net.sf.jetro.tree.JsonElement;
import net.sf.jetro.tree.JsonNull;
import net.sf.jetro.tree.JsonNumber;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonProperty;
import net.sf.jetro.tree.JsonString;
import net.sf.jetro.tree.JsonType;
import net.sf.jetro.util.Stack;
import net.sf.jetro.visitor.JsonVisitor;
import net.sf.jetro.visitor.VisitingReader;

public class JsonElementVisitingReader implements VisitingReader {
	private Stack<JsonVisitor<?>> visitorStack = new Stack<JsonVisitor<?>>();
	private JsonElement jsonElement;

	public JsonElementVisitingReader(JsonElement jsonElement) {
		if (jsonElement == null) {
			throw new IllegalArgumentException("jsonElement must not be null");
		}

		this.jsonElement = jsonElement;
	}

	@Override
	public void accept(JsonVisitor<?> visitor) {
		visitorStack.push(visitor);
		readJsonElement(jsonElement);
	}

	private void readJsonElement(JsonElement jsonElement) {
		if (jsonElement instanceof JsonProperty) {
			readJsonProperty((JsonProperty) jsonElement);
		} else if (jsonElement instanceof JsonObject) {
			readJsonObject((JsonObject) jsonElement);
		} else if (jsonElement instanceof JsonArray) {
			readJsonArray((JsonArray) jsonElement);
		} else if (jsonElement instanceof JsonString) {
			readJsonString((JsonString) jsonElement);
		} else if (jsonElement instanceof JsonNumber) {
			readJsonNumber((JsonNumber) jsonElement);
		} else if (jsonElement instanceof JsonBoolean) {
			readJsonBoolean((JsonBoolean) jsonElement);
		} else if (jsonElement instanceof JsonNull) {
			readJsonNull();
		} else {
			throw new IllegalArgumentException("Cannot read unsupported element type "
					+ jsonElement.getClass().getSimpleName());
		}
	}

	private void readJsonObject(JsonObject jsonElement) {
		visitorStack.push(visitorStack.peek().visitObject());

		for (JsonProperty property : jsonElement) {
			readJsonProperty(property);
		}

		visitorStack.pop().visitEnd();
	}

	private void readJsonArray(JsonArray jsonElement) {
		visitorStack.push(visitorStack.peek().visitArray());

		for (JsonType value : jsonElement) {
			readJsonElement(value);
		}

		visitorStack.pop().visitEnd();
	}

	private void readJsonProperty(JsonProperty jsonElement) {
		visitorStack.peek().visitProperty(jsonElement.getKey());
		readJsonElement(jsonElement.getValue());
	}

	private void readJsonString(JsonString jsonElement) {
		String value = jsonElement.getValue();

		if (value == null) {
			readJsonNull();
		} else {
			visitorStack.peek().visitValue(value);
		}
	}

	private void readJsonNumber(JsonNumber jsonElement) {
		Number value = jsonElement.getValue();

		if (value == null) {
			readJsonNull();
		} else {
			visitorStack.peek().visitValue(value);
		}
	}

	private void readJsonBoolean(JsonBoolean jsonElement) {
		Boolean value = jsonElement.getValue();

		if (value == null) {
			readJsonNull();
		} else {
			visitorStack.peek().visitValue(value);
		}
	}

	private void readJsonNull() {
		visitorStack.peek().visitNullValue();
	}
}