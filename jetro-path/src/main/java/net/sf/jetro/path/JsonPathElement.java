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

import java.io.Serializable;

abstract class JsonPathElement implements Serializable {
	private static final long serialVersionUID = -3286390362790937905L;
	private boolean wildcard;
	private boolean optional;

	JsonPathElement(final boolean wildcard, final boolean optional) {
		this.wildcard = wildcard;
		this.optional = optional;
	}

	boolean isWildcard() {
		return wildcard;
	}

	boolean isOptional() {
		return optional;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		JsonPathElement that = (JsonPathElement) o;
		if (optional != that.optional) return false;

		return equalsIgnoreOptional(that);
	}

	public boolean equalsIgnoreOptional(JsonPathElement other) {
		return other != null && wildcard == other.wildcard;
	}

	@Override
	public int hashCode() {
		int result = (wildcard ? 1 : 0);
		result = 31 * result + (optional ? 1 : 0);
		return result;
	}

	public abstract String toString();
}