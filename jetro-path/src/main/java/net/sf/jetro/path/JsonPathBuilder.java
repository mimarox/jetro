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
		return new JsonPath(pathElements.toArray(new JsonPathElement[pathElements.size()]));
	}

	void reset() {
		pathElements.clear();
	}
}