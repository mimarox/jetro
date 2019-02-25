package net.sf.jetro.object.exception;

public class DeserializationException extends RuntimeException {
	private static final long serialVersionUID = 2781208198320064680L;

	public DeserializationException(String message) {
		super(message);
	}

	public DeserializationException(String message, Throwable cause) {
		super(message, cause);
	}
}
