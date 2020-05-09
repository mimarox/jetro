/*
 * #%L
 * Jetro Patch
 * %%
 * Copyright (C) 2013 - 2020 The original author or authors.
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

import net.sf.jetro.patch.pointer.JsonPointer;
import net.sf.jetro.path.JsonPath;
import net.sf.jetro.tree.JsonArray;
import net.sf.jetro.tree.JsonCollection;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonType;

public class AddPatchOperation extends ValueBasedPatchOperation {
	public AddPatchOperation(final JsonObject patchDefinition) {
		super(patchDefinition);
	}

	@Override
	public JsonType applyPatch(final JsonType source) throws JsonPatchException {
		if (path.isRootPath()) {
			return handleTarget(value);
		}
		
		processPreconditions(source);
		
		final JsonCollection target = (JsonCollection) source.deepCopy();
		target.recalculateTreePaths();
		
		addOrReplace(target);
	
		return handleTarget(target);
	}

	private void processPreconditions(final JsonType source) throws JsonPatchException {
		Objects.requireNonNull(source, "Argument 'source' must not be null");
				
		if (!(source instanceof JsonCollection)) {
			throw new JsonPatchException(new IllegalArgumentException("source must either be "
					+ "a JsonArray or a JsonObject"));
		}
	}
	
	private void addOrReplace(final JsonCollection target) throws JsonPatchException {
		final JsonPath targetPath = path.toJsonPath();
		final JsonPath parentPath = targetPath.removeLastElement();
		
		final Optional<JsonType> optional = target.getElementAt(parentPath);
		
		if (optional.isPresent()) {
			if (optional.get() instanceof JsonObject) {
				addOrReplaceOnJsonObject(target, targetPath);
			} else if (optional.get() instanceof JsonArray) {
				addToJsonCollection(target, targetPath);
			} else {
				throw new JsonPatchException("Couldn't add " + value + " to " + target +
						" at path \"" + JsonPointer.fromJsonPath(targetPath) + "\". The parent " +
						"value at \"" + JsonPointer.fromJsonPath(parentPath) + "\" is neither a "
								+ "JsonObject nor a JsonArray");
			}
		} else {
			throw new JsonPatchException("Couldn't add " + value + " to " + target +
					" at path \"" + JsonPointer.fromJsonPath(targetPath) + "\". The parent " +
					"value at \"" + JsonPointer.fromJsonPath(parentPath) + "\" does not exist.");
		}
	}

	private void addOrReplaceOnJsonObject(final JsonCollection target, JsonPath targetPath)
			throws JsonPatchException {
		if (target.hasElementAt(targetPath)) {
			target.replaceElementAt(targetPath, value);
		} else {
			addToJsonCollection(target, targetPath);
		}
	}

	private void addToJsonCollection(JsonCollection target, JsonPath targetPath)
			throws JsonPatchException {
		if (!target.addElementAt(targetPath, value)) {
			throw new JsonPatchException("Couldn't add " + value + " to " + target +
					" at path \"" + JsonPointer.fromJsonPath(targetPath) + "\"");
		}
	}
}
