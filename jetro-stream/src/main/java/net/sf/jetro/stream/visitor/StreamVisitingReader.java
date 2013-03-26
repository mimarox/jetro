package net.sf.jetro.stream.visitor;

import java.io.IOException;

import net.sf.jetro.stream.JsonReader;
import net.sf.jetro.stream.JsonToken;
import net.sf.jetro.util.Stack;
import net.sf.jetro.visitor.JsonVisitor;
import net.sf.jetro.visitor.VisitingReader;

public class StreamVisitingReader implements VisitingReader {
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

		try {
			acceptInternal(visitor);
		} catch (IOException e) {
			throw new JsonIOException(e);
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
}