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
import java.util.Objects;

import net.sf.jetro.patch.pointer.JsonPointer;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonString;
import net.sf.jetro.tree.JsonType;

public abstract class JsonPatchOperation {
	protected final JsonPointer path;
	
	private JsonPatchOperation nextOperation;
	
	public JsonPatchOperation(final JsonObject patchDefinition) {
		Objects.requireNonNull(patchDefinition, "Argument 'patchDefinition' must not be null");
		
		if (!patchDefinition.containsAllKeys(Arrays.asList("path"))) {
			throw new IllegalArgumentException("Missing property 'path'");
		}
		
		this.path = JsonPointer.compile(((JsonString) patchDefinition.get("path")).getValue());
	}

	public abstract JsonType applyPatch(JsonType source) throws JsonPatchException;

	public void attachOperation(final JsonPatchOperation operation) {
		attachOperation(operation, false);
	}

	public void attachOperation(final JsonPatchOperation operation,	final boolean replace) {
		if (nextOperation == null || replace) {
			nextOperation = operation;
		} else {
			nextOperation.attachOperation(operation);
		}
	}

	protected JsonType handleTarget(JsonType target) throws JsonPatchException {
		if (nextOperation != null) {
			return nextOperation.applyPatch(target);
		} else {
			return target;
		}
	}
}
