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

/**
 * This class serves as <code>null</code> value.
 * 
 * @author Matthias Rothe
 */
public final class Null {
	/**
	 * The only instance of this class that will ever get generated.
	 */
	public static final Null INSTANCE = new Null();

	private Null() {
	}

	@Override
	public String toString() {
		return "Null";
	}
}