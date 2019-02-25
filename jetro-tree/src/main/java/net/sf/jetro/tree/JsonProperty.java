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
package net.sf.jetro.tree;

import net.sf.jetro.context.RenderContext;
import net.sf.jetro.tree.renderer.DefaultJsonRenderer;
import net.sf.jetro.tree.renderer.JsonRenderer;
import net.sf.jetro.tree.visitor.JsonElementVisitingReader;
import net.sf.jetro.visitor.JsonVisitor;

import java.util.Map.Entry;
import java.util.Objects;

public final class JsonProperty implements JsonElement, Entry<String, JsonType> {
	private static final long serialVersionUID = 1421897236649764494L;

	private String key;
	private JsonType value;

	public JsonProperty(String key) {
		this(key, (JsonType) null);
	}

	public JsonProperty(String key, JsonType value) {
		if (key == null) {
			throw new IllegalArgumentException("key must not be null");
		}

		this.key = key;
		this.value = value;
	}

	public JsonProperty(String key, String value) {
		this(key, new JsonString(value));
	}

	public JsonProperty(String key, boolean value) {
		this(key, new JsonBoolean(value));
	}

	public JsonProperty(String key, Number value) {
		this(key, new JsonNumber(value));
	}

	@Override
	public JsonProperty deepCopy() {
		return new JsonProperty(key, value.deepCopy());
	}

	public JsonProperty shallowCopy() {
		return new JsonProperty(key, value);
	}
	
	@Override
	public String getKey() {
		return key;
	}

	@Override
	public JsonType getValue() {
		return value;
	}

	@Override
	public JsonType setValue(JsonType value) {
		JsonType oldValue = this.value;
		this.value = (JsonType) value;
		return oldValue;
	}

	@Override
	public String toJson() {
		return new DefaultJsonRenderer(new RenderContext().setLenient(true)).render(this);
	}

	@Override
	public String toJson(JsonRenderer renderer) {
		return renderer.render(this);
	}

	@Override
	public void mergeInto(JsonVisitor<?> visitor) {
		JsonElementVisitingReader reader = new JsonElementVisitingReader(this);
		reader.accept(visitor);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JsonProperty [key=").append(key).append(", value=").append(value).append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, value);
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
		JsonProperty other = (JsonProperty) obj;
		return Objects.equals(key, other.key) && Objects.equals(value, other.value);
	}

	public boolean keyEquals(JsonProperty other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		return Objects.equals(key, other.key);
	}
}