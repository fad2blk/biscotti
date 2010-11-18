package com.googlecode.biscotti.base;

/**
 * A runtime exception thrown by the {@code clone()} method to indicate that an
 * object could not or should not be cloned.
 * <p>
 * Unlike the checked {@link java.lang.CloneNotSupportedException}, this
 * exception can be thrown by sub classes whose parent classes do not throw a
 * {@link java.lang.CloneNotSupportedException}.
 */
public class CloneNotSupportedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a {@code CloneNotSupportedException} with no detail message.
	 */
	public CloneNotSupportedException() {
		super();
	}

	/**
	 * Constructs a {@code CloneNotSupportedException} with the specified detail
	 * message.
	 * 
	 * @param s
	 *            the detail message
	 */
	public CloneNotSupportedException(String s) {
		super(s);
	}
}
