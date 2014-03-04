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