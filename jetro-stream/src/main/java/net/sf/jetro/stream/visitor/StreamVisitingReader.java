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

import net.sf.jetro.stream.JsonReader;
import net.sf.jetro.stream.JsonToken;
import net.sf.jetro.util.Stack;
import net.sf.jetro.visitor.JsonVisitor;
import net.sf.jetro.visitor.VisitingReader;

import java.io.Closeable;
import java.io.IOException;

public class StreamVisitingReader implements VisitingReader, Closeable {
	private JsonReader reader;

	public StreamVisitingReader(final JsonReader reader) {
		if (reader == null) {
			throw new IllegalArgumentException("reader must not be null");
		}

		this.reader = reader;
	}

	@Override
	public void accept(final JsonVisitor<?> visitor) {
		if (visitor == null) {
			throw new IllegalArgumentException("visitor must not be null");
		}

		if (reader == null) {
			throw new IllegalStateException("reader is already closed");
		}

		try {
			acceptInternal(visitor);
		} catch (Exception e) {
			String message = "An exception occurred while processing a JSON stream on line " + reader.getLineNumber()
					+ ", column " + reader.getColumnNumber();
			throw new JsonIOException(message, e);
		}
	}

	private void acceptInternal(final JsonVisitor<?> visitor) throws IOException {
		Stack<JsonVisitor<?>> stack = new Stack<JsonVisitor<?>>();
		stack.push(visitor);

		JsonToken token;

		while ((token = reader.peek()) != JsonToken.END_DOCUMENT) {
			switch (token) {
			case BEGIN_ARRAY:
				reader.beginArray();
				stack.push(stack.peek().visitArray());
				break;
			case END_ARRAY:
				reader.endArray();
				stack.pop().visitEnd();
				break;
			case BEGIN_OBJECT:
				reader.beginObject();
				stack.push(stack.peek().visitObject());
				break;
			case END_OBJECT:
				reader.endObject();
				stack.pop().visitEnd();
				break;
			case NAME:
				stack.peek().visitProperty(reader.nextName());
				break;
			case BOOLEAN:
				stack.peek().visitValue(reader.nextBoolean());
				break;
			case NUMBER:
				stack.peek().visitValue(new LazilyParsedNumber(reader.nextString()));
				break;
			case STRING:
				stack.peek().visitValue(reader.nextString());
				break;
			case NULL:
				reader.nextNull();
				stack.peek().visitNullValue();
				break;
			default:
				throw new IllegalStateException("Unsupported token type: " + token);
			}
		}

		stack.pop().visitEnd();
	}

	/**
	 * Closes this stream and releases any system resources associated
	 * with it. If the stream is already closed then invoking this
	 * method has no effect.
	 *
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	public void close() throws IOException {
		reader.close();
		reader = null;
	}
}