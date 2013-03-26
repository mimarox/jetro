package net.sf.jetro.stream.visitor;

import java.io.IOException;

public class JsonIOException extends RuntimeException {
	private static final long serialVersionUID = -1261669025142641792L;

	public JsonIOException(IOException cause) {
		super(cause);
	}
}