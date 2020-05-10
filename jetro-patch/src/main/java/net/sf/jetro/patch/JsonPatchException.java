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

/**
 * If thrown this exception indicates that an error occurred while a JSON patch
 * was applied and requires that the original source JSON structure be returned
 * unchanged.
 * 
 * @author Matthias Rothe
 */
public class JsonPatchException extends Exception {
	private static final long serialVersionUID = -912686328514490899L;

	public JsonPatchException(String message) {
		super(message);
	}

	public JsonPatchException(Throwable cause) {
		super(cause);
	}

	public JsonPatchException(String message, Throwable cause) {
		super(message, cause);
	}
}
