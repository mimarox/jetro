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

import java.util.Objects;
import java.util.Optional;

import net.sf.jetro.path.JsonPath;
import net.sf.jetro.tree.JsonCollection;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonProperty;
import net.sf.jetro.tree.JsonType;

public class MovePatchOperation extends FromBasedPatchOperation {
	public MovePatchOperation(final JsonObject patchDefinition) {
		super(patchDefinition);
		
		if (getFrom().isParentPointerOf(getPath())) {
			throw new IllegalArgumentException("The from JSON Pointer must not be a parent "
					+ "of the path JSON Pointer");
		}
	}

	@Override
	public JsonType applyPatch(final JsonType source) throws JsonPatchException {
		Objects.requireNonNull(source, "Argument 'source' must not be null");
		
		JsonType target = prepareFrom(source);
		Optional<JsonType> optional = target.getElementAt(getFrom().toJsonPath());
		
		if (optional.isPresent()) {
			return handleTarget(createMoveOperations(optional.get()).applyPatch(target));
		} else {
			throw new JsonPatchException("Couldn't move non-existing value from " + getFrom() +
					" in " + target);
		}
	}

	private JsonType prepareFrom(final JsonType source) {
		final JsonType target = source.deepCopy();
		
		if (target instanceof JsonCollection) {
			((JsonCollection) target).recalculateTreePaths();
		} else {
			target.resetPaths();
			target.addPath(new JsonPath());
		}
		
		return target;
	}
	
	private JsonPatchOperation createMoveOperations(final JsonType value) {
		final RemovePatchOperation removeOperation = createRemoveOperation();
		final AddPatchOperation addOperation = createAddOperation(value);
		
		removeOperation.attachOperation(addOperation);
		
		return removeOperation;
	}

	private RemovePatchOperation createRemoveOperation() {
		final JsonObject patchDefinition = new JsonObject();
		patchDefinition.add(new JsonProperty("path", getFrom().toString()));
		
		return new RemovePatchOperation(patchDefinition);
	}

	private AddPatchOperation createAddOperation(final JsonType value) {
		final JsonObject patchDefinition = new JsonObject();
		patchDefinition.add(new JsonProperty("path", getPath().toString()));
		patchDefinition.add(new JsonProperty("value", value));
		
		return new AddPatchOperation(patchDefinition);
	}
}
