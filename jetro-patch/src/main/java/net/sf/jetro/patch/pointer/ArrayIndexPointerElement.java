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
package net.sf.jetro.patch.pointer;

public class ArrayIndexPointerElement extends JsonPointerElement<Integer> {
	private static final long serialVersionUID = 7455005293018033992L;
	
	private final boolean nextToLast;
	
	public ArrayIndexPointerElement() {
		super(null);
		this.nextToLast = true;
	}
	
	public ArrayIndexPointerElement(final Integer value) {
		super(value);
		nextToLast = false;
	}
	
	public boolean isNextToLast() {
		return nextToLast;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("/");
		
		if (nextToLast) {
			builder.append("-");
		} else {
			builder.append(getValue());
		}
		
		return builder.toString();
	}
}
