package net.sf.jetro.stream.visitor;

public class JsonIOException extends RuntimeException {
	private static final long serialVersionUID = -1261669025142641792L;

	public JsonIOException(Exception cause) {
		super(cause);
	}

	public JsonIOException(String message, Exception cause) {
		super(message, cause);
	}
}