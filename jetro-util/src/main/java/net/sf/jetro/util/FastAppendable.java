/*
 * #%L
 * Jetro Utilities
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
package net.sf.jetro.util;

import java.io.IOException;

/**
 * @author matthias.rothe
 * @since 27.02.14.
 */
@Deprecated
public class FastAppendable implements Appendable {

	private char[] buffer;
	private int bufferSize;
	private int capacity;

	public FastAppendable(int capacity) {
		if (capacity < 1) {
			throw new IllegalArgumentException("capacity < 1");
		}

		this.capacity = capacity;
		reset();
	}

	@Override
	public FastAppendable append(CharSequence csq) throws IOException {
		if (csq != null) {
			append(csq.toString().toCharArray());
		}
		return this;
	}

	@Override
	public Appendable append(CharSequence csq, int start, int end) throws IOException {
		if (csq != null) {
			append(csq.subSequence(start, end));
		}
		return this;
	}

	@Override
	public Appendable append(char c) throws IOException {
		ensureCapacity(1);
		buffer[bufferSize++] = c;
		return this;
	}

	private void append(char[] chars) {
		ensureCapacity(chars.length);
		System.arraycopy(chars, 0, buffer, bufferSize, chars.length);
		bufferSize += chars.length;
	}

	private void ensureCapacity(int newLength) {
		if (bufferSize + newLength >= buffer.length) {
			char[] newBuffer = new char[buffer.length * 2];
			System.arraycopy(buffer, 0, newBuffer, 0, bufferSize);
			buffer = newBuffer;
		}
	}

	public void reset() {
		buffer = new char[capacity];
		bufferSize = 0;
	}

	@Override
	public String toString() {
		char[] result = new char[bufferSize];
		System.arraycopy(buffer, 0, result, 0, bufferSize);
		//CHECKSTYLE:OFF
		return new String(result);
		//CHECKSTYLE:ON
	}
}
