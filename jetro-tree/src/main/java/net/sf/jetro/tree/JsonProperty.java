package net.sf.jetro.tree;

import java.util.Map.Entry;

import net.sf.jetro.tree.renderer.DefaultJsonRenderer;

public class JsonProperty implements JsonElement, Entry<String, JsonType> {
	private static final long serialVersionUID = 1421897236649764494L;

	private String key;
	private JsonType value;

	public JsonProperty(String key) {
		if (key == null) {
			throw new IllegalArgumentException("key must not be null");
		}

		this.key = key;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
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
		if (key == null) {
			if (other.key != null) {
				return false;
			}
		} else if (!key.equals(other.key)) {
			return false;
		}
		return true;
	}

	@Override
	public String toJson() {
		return new DefaultJsonRenderer().render(this);
	}

	@Override
	public String toJson(final JsonRenderer renderer) {
		return renderer.render(this);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JsonProperty [key=").append(key).append(", value=").append(value).append("]");
		return builder.toString();
	}
}