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

import net.sf.jetro.path.JsonPath;

public class JsonString extends JsonPrimitive<String> {
	private static final long serialVersionUID = 39487332732636472L;

	public JsonString() {
	}

	public JsonString(final JsonPath path) {
		this(path, null);
	}

	public JsonString(final String value) {
		this(null, value);
	}

	public JsonString(final JsonPath path, final String value) {
		super(path, value);
	}
	
	@Override
	public JsonString deepCopy() {
		return new JsonString(path, getValue());
	}
}