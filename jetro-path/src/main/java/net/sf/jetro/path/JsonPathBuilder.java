/*
 * #%L
 * Jetro JsonPath
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

		JsonPath path = new JsonPath(pathElements.toArray(
				new JsonPathElement[pathElements.size()]), containsOptionals);

		if (notOnlyOptional) {
			return path;
		} else {
			throw new JsonPathCompilerException("A JsonPath cannot only contain "
					+ "optional path elements, but [" + path + "] does.");
		}
	}

	void reset() {
		pathElements.clear();
	}

	int getDepth() {
		return pathElements.size();
	}
}