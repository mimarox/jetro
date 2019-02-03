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

import java.util.Set;

import net.sf.jetro.path.JsonPath;

public final class JsonBoolean extends JsonPrimitive<Boolean> {
	private static final long serialVersionUID = -8707418235663464907L;

	public JsonBoolean() {
		super();
	}

	public JsonBoolean(Boolean value) {
		super(value);
	}

	public JsonBoolean(JsonPath path, Boolean value) {
		super(path, value);
	}

	public JsonBoolean(JsonPath path) {
		super(path);
	}
	
	private JsonBoolean(Set<JsonPath> paths, Boolean value) {
		super(paths, value);
	}

	@Override
	public JsonBoolean deepCopy() {
		return new JsonBoolean(paths, getValue());
	}
}