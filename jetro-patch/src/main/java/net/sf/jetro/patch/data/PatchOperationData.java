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
package net.sf.jetro.patch.data;

import java.util.Objects;

import net.sf.jetro.patch.pointer.JsonPointer;

public abstract class PatchOperationData {
	private final PatchOperation op;
	private final JsonPointer path;
	
	PatchOperationData(final PatchOperation op, final JsonPointer path) {
		Objects.requireNonNull(op, "Argument 'op' must not be null");
		Objects.requireNonNull(path, "Argument 'path' must not be null");
		
		this.op = op;
		this.path = path;
	}

	public PatchOperation getOp() {
		return op;
	}

	public JsonPointer getPath() {
		return path;
	}
}
