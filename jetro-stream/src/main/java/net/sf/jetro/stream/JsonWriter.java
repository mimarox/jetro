/*
 * #%L
 * Jetro Stream
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
//CHECKSTYLE:OFF
package net.sf.jetro.stream;

import java.io.IOException;
import java.io.Writer;

public class JsonWriter extends JsonGenerator {
	/** The output data, containing at most one top-level array or object. */
	private final Writer out;

	/**
	 * Creates a new instance that writes a JSON-encoded stream to {@code out}.
	 * For best performance, ensure {@link Writer} is buffered; wrapping in
	 * {@link java.io.BufferedWriter BufferedWriter} if necessary.
	 */
	public JsonWriter(Writer out) {
		super(out);
		this.out = out;
	}

	/**
	 * Ensures all buffered data is written to the underlying {@link Writer}
	 * and flushes that writer.
	 */
	@Override
	public void flush() throws IOException {
		super.flush();
		out.flush();
	}

	/**
	 * Flushes and closes this writer and the underlying {@link Writer}.
	 *
	 * @throws IOException if the JSON document is incomplete.
	 */
	@Override
	public void close() throws IOException {
		out.close();
		super.close();
	}
}
//CHECKSTYLE:ON
