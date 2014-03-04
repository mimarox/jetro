package net.sf.jetro.util;

import java.io.IOException;

/**
 * @author matthias.rothe
 * @since 27.02.14.
 */
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
		return new String(result);
	}
}
