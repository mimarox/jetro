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

public class PropertyNamePathElement extends JsonPathElement {
	private static final long serialVersionUID = 588262327967230142L;
	private final String name;

	public PropertyNamePathElement(final String name) {
		this(name, false);
	}

	PropertyNamePathElement(final String name, final boolean optional) {
		super(false, optional);

		if (name == null || "".equals(name)) {
			throw new IllegalArgumentException("name must not be null or empty");
		}

		this.name = name;
	}

	PropertyNamePathElement(final boolean wildcard, final boolean optional) {
		super(wildcard, optional);
		this.name = null;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(".");

		if (isWildcard()) {
			builder.append(JsonPath.WILDCARD);
		} else {
			builder.append(name);
		}

		if (isOptional()) {
			builder.append(JsonPath.OPTIONAL);
		}

		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		return equalsIgnoreOptional((JsonPathElement) obj);
	}

	@Override
	public boolean equalsIgnoreOptional(JsonPathElement other) {
		if (this == other)
			return true;
		if (getClass() != other.getClass())
			return false;
		PropertyNamePathElement that = (PropertyNamePathElement) other;
		if (name == null) {
			if (that.name != null)
				return false;
		} else if (!name.equals(that.name))
			return false;
		return super.equalsIgnoreOptional(other);
	}
}