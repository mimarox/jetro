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
package net.sf.jetro.patch;

import java.util.Arrays;

import net.sf.jetro.path.JsonPath;
import net.sf.jetro.tree.JsonCollection;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonType;

public abstract class ValueBasedPatchOperation extends JsonPatchOperation {
	private final JsonType value;
	
	public ValueBasedPatchOperation(final JsonObject patchDefinition) {
		super(patchDefinition);
		
		if (!patchDefinition.containsAllKeys(Arrays.asList("value"))) {
			throw new IllegalArgumentException("Missing property 'value'");
		}
		
		value = patchDefinition.get("value").deepCopy();
		
		if (getValue() instanceof JsonCollection) {
			((JsonCollection) getValue()).recalculateTreePaths();
		} else {
			getValue().resetPaths();
			getValue().addPath(new JsonPath());
		}
	}

	protected JsonType getValue() {
		return value;
	}
}
