/*
 * #%L
 * Jetro Patch
 * %%
 * Copyright (C) 2013 - 2019 The original author or authors.
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
package net.sf.jetro.patch.pointer;

import java.io.Serializable;
import java.util.Objects;

abstract class JsonPointerElement<T extends Serializable> implements Serializable {
	private static final long serialVersionUID = -8641148961106248902L;
	private final T value;
	
	JsonPointerElement(final T value) {
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
	
	@Override
	public abstract String toString();

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("rawtypes")
		JsonPointerElement other = (JsonPointerElement) obj;
		return Objects.equals(value, other.value);
	}
}
