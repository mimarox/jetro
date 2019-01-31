/*
 * #%L
 * Jetro Stream
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
package net.sf.jetro.stream.visitor;

public class LazilyParsedNumber extends Number {
	private static final long serialVersionUID = 8387800346023499875L;
	private String numericValue;

	public LazilyParsedNumber(final String numericValue) {
		if (numericValue == null) {
			throw new IllegalArgumentException("numericValue must not be null");
		}

		this.numericValue = numericValue;
	}

	@Override
	public byte byteValue() {
		return Byte.parseByte(numericValue);
	}
	
	@Override
	public double doubleValue() {
		return Double.parseDouble(numericValue);
	}

	@Override
	public float floatValue() {
		return Float.parseFloat(numericValue);
	}

	@Override
	public int intValue() {
		return Integer.parseInt(numericValue);
	}

	@Override
	public long longValue() {
		return Long.parseLong(numericValue);
	}

	@Override
	public short shortValue() {
		return Short.parseShort(numericValue);
	}
	
	@Override
	public String toString() {
		return numericValue;
	}
}