package net.sf.jetro.transform.highlevel.editors;

/**
 * This class provides a {@link RuntimeException} subtype to be thrown from
 * editor functions.
 * 
 * @author Matthias Rothe
 */
public class EditorException extends RuntimeException {
	private static final long serialVersionUID = -6460490886814853275L;

	/**
	 * Public constructor taking a message.
	 * 
	 * @param message the message
	 */
	public EditorException(final String message) {
		super(message);
	}
	
	/**
	 * Public constructor taking a message and a cause.
	 * 
	 * @param message the message
	 * @param cause the {@link Throwable} causing this exception to be thrown
	 */
	public EditorException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
