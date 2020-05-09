/*
 * #%L
 * Jetro JsonPath
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
package net.sf.jetro.path;

import java.util.Objects;

/**
 * A path element specifying an array index. An array index may actually have an index,
 * or a wildcard or the end-of-array specifier. Furthermore it might be optional. 
 * 
 * @author Matthias Rothe
 */
public class ArrayIndexPathElement extends JsonPathElement {
	private static final long serialVersionUID = -4814966817683544359L;
	private int index;
	private final boolean endOfArray;
	
	/**
	 * Create an array index path element as an end-of-array element.
	 */
	public ArrayIndexPathElement() {
		super(false, false);
		this.endOfArray = true;
	}
	
	/**
	 * Create a non-optional array index path element with an index.
	 * The index must be non-negative.
	 * 
	 * @param index the index to set
	 */
	public ArrayIndexPathElement(final int index) {
		this(index, false);
	}

	ArrayIndexPathElement(final int index, final boolean optional) {
		super(false, optional);

		if (index < 0) {
			throw new ArrayIndexOutOfBoundsException(index);
		}

		this.index = index;
		this.endOfArray = false;
	}

	ArrayIndexPathElement(final boolean wildcard, final boolean optional) {
		super(wildcard, optional);
		this.endOfArray = false;
	}

	/**
	 * Returns the index.
	 * 
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Returns whether this array index path element is an end-of-array element.
	 * 
	 * @return <code>true</code> if and only if this array index path element is
	 * an end-of-array element, <code>false</code> otherwise.
	 */
	public boolean isEndOfArray() {
		return endOfArray;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.jetro.path.JsonPathElement#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("[");

		if (isWildcard()) {
			builder.append(JsonPath.WILDCARD);
		} else if (isEndOfArray()) {
			builder.append(JsonPath.END_OF_ARRAY);
		} else {
			builder.append(index);
		}

		builder.append("]");

		if (isOptional()) {
			builder.append(JsonPath.OPTIONAL);
		}

		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.jetro.path.JsonPathElement#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(index, endOfArray);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.jetro.path.JsonPathElement#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		
		return equalsIgnoreOptional((JsonPathElement) obj);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.jetro.path.JsonPathElement#equalsIgnoreOptional(net.sf.jetro.path.JsonPathElement)
	 */
	@Override
	public boolean equalsIgnoreOptional(JsonPathElement other) {
		if (this == other) {
			return true;
		}
		
		if (getClass() != other.getClass()) {
			return false;
		}
		
		ArrayIndexPathElement that = (ArrayIndexPathElement) other;
		
		if (index != that.index || endOfArray != that.endOfArray) {
			return false;
		}
		
		return super.equalsIgnoreOptional(other);
	}
}