package net.sf.jetro.path;

import java.util.ArrayList;
import java.util.List;

class JsonPathBuilder {
	private List<JsonPathElement> pathElements = new ArrayList<JsonPathElement>();

	void append(final JsonPathElement pathElement) {
		if (pathElement == null) {
			throw new IllegalArgumentException("pathElement must not be null");
		}

		pathElements.add(pathElement);
	}

	JsonPath build() {
		boolean notOnlyOptional = false;
		boolean containsOptionals = false;

		for (JsonPathElement element : pathElements) {
			if (element.isOptional()) {
				containsOptionals = true;
			} else {
				notOnlyOptional = true;
			}
		}

		JsonPath path = new JsonPath(pathElements.toArray(new JsonPathElement[pathElements.size()]), containsOptionals);

		if (notOnlyOptional) {
			return path;
		} else {
			throw new JsonPathCompilerException("A JsonPath cannot only contain optional path elements, but [" + path + "] does.");
		}
	}

	void reset() {
		pathElements.clear();
	}

	int getDepth() {
		return pathElements.size();
	}
}