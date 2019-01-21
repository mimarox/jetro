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

public class JsonNumber extends JsonPrimitive<Number> {
	private static final long serialVersionUID = 130455634564941198L;

	public JsonNumber() {
		super();
	}

	public JsonNumber(JsonPath path, Number value) {
		super(path, value);
	}

	public JsonNumber(JsonPath path) {
		super(path);
	}

	public JsonNumber(Number value) {
		super(value);
	}

	@Override
	public JsonNumber deepCopy() {
		return new JsonNumber(path, getValue());
	}
}