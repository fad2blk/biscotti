package com.googlecode.biscotti.base;

import java.util.Comparator;

import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;

/**
 * Static utility methods used to verify correctness of arguments passed to your
 * own methods.
 * 
 * @author Zhenya Leonov
 * @see Preconditions
 */
final public class Preconditions2 {

	/**
	 * Ensures that an argument passed as a parameter to the calling method is
	 * not {@code null}.
	 * <p>
	 * Unlike {@link Preconditions#checkNotNull(Object)
	 * Preconditions.checkNotNull(T)} this method throws an
	 * {@code IllegalArgumentException} instead of a
	 * {@code NullPointerException} if the {@code arg} parameter is {@code null}.
	 * 
	 * @param arg
	 *            the argument passed to the calling method
	 * @return the non-null argument that was validated
	 * @throws IllegalArgumentException
	 *             if the argument is {@code null}
	 */
	public static <T> T checkArgumentNotNull(final T arg) {
		if (arg == null)
			throw new IllegalArgumentException();
		return arg;
	}

	/**
	 * Ensures that an argument passed as a parameter to the calling method is
	 * not {@code null}.
	 * <p>
	 * Unlike {@link Preconditions#checkNotNull(Object, Object)
	 * Preconditions.checkNotNull(T, Object)} this method throws an
	 * {@code IllegalArgumentException} instead of a
	 * {@code NullPointerException} if the {@code arg} parameter is {@code null}.
	 * 
	 * @param arg
	 *            the argument passed to the calling method
	 * @param message
	 *            the exception message to use if the check fails; will be
	 *            converted to a string using {@link String#valueOf(Object)}
	 * @return the non-null argument that was validated
	 * @throws IllegalArgumentException
	 *             if the argument is {@code null}
	 */
	public static <T> T checkArgumentNotNull(final T arg, final Object message) {
		if (arg == null)
			throw new IllegalArgumentException(String.valueOf(message));
		return arg;
	}

	/**
	 * Ensures that an argument passed as a parameter to the calling method is
	 * not {@code null}.
	 * <p>
	 * Unlike {@link Preconditions#checkNotNull(Object, String, Object...)
	 * Preconditions.checkNotNull(T, String, Object...)} this method throws an
	 * {@code IllegalArgumentException} instead of a
	 * {@code NullPointerException} if the {@code arg} parameter is {@code null}.
	 * 
	 * @param arg
	 *            the argument passed to the calling method
	 * @param template
	 *            a template for the exception message should the check fail.
	 *            The message is formed by replacing each {@code %s} placeholder
	 *            in the template with an argument. These are matched by
	 *            position - the first {@code %s} gets
	 *            {@code errorMessageArgs[0]}, etc. Unmatched arguments will be
	 *            appended to the formatted message in square braces. Unmatched
	 *            placeholders will be left as-is.
	 * @param messages
	 *            the arguments to be substituted into the message template.
	 *            Arguments are converted to strings using
	 *            {@link String#valueOf(Object)}.
	 * @return the non-null argument that was validated
	 * @throws IllegalArgumentException
	 *             if the argument is {@code null}
	 */
	public static <T> T checkArgumentNotNull(final T arg,
			final String template, final Object... messages) {
		if (arg == null)
			throw new IllegalArgumentException(format(template, messages));
		return arg;
	}

	/**
	 * Ensures that the specified element is located between {@code fromElement}
	 * inclusive, and {@code toElement} inclusive, according to their <i>natural
	 * ordering</i>.
	 * 
	 * @param element
	 *            a user-supplied element
	 * @param fromElement
	 *            low endpoint (inclusive) of the specified range
	 * @param toElement
	 *            high endpoint (inclusive) of the specified range
	 * @return the specified element
	 */
	public static <T> T checkElementPosition(final T element,
			final T fromElement, final T toElement) {
		return checkElementPosition(element, fromElement, toElement,
				(Comparator<? super T>) Ordering.natural());
	}

	/**
	 * Ensures that the specified element is located between {@code fromElement}
	 * inclusive, and {@code toElement} inclusive, according to the specified
	 * {@code Comparator}.
	 * 
	 * @param element
	 *            a user-supplied element
	 * @param fromElement
	 *            low endpoint (inclusive) of the specified range
	 * @param toElement
	 *            high endpoint (inclusive) of the specified range
	 * @param comparator
	 *            a user-supplied comparator
	 * @return the specified element
	 */
	public static <T> T checkElementPosition(final T element,
			final T fromElement, final T toElement,
			Comparator<? super T> comparator) {
		if (comparator.compare(element, fromElement) < 0)
			throw new IllegalArgumentException(format(
					"element (%s) must not be less than start element (%s)",
					element, fromElement));
		if (comparator.compare(element, toElement) > 0)
			throw new IllegalArgumentException(format(
					"element (%s) must not be greater than end element(%s)",
					element, toElement));
		return element;
	}

	/**
	 * Substitutes each {@code %s} in {@code template} with an argument. These
	 * are matched by position - the first {@code %s} gets {@code args[0]}, etc.
	 * If there are more arguments than placeholders, the unmatched arguments
	 * will be appended to the end of the formatted message in square braces.
	 * 
	 * @param template
	 *            a non-null string containing 0 or more {@code %s}
	 *            placeholders.
	 * @param args
	 *            the arguments to be substituted into the message template.
	 *            Arguments are converted to strings using
	 *            {@link String#valueOf(Object)}. Arguments can be {@code null}.
	 */
	private static String format(String template, Object... args) {
		StringBuilder sb = new StringBuilder(template);
		int i, c = 0;
		for (i = sb.indexOf("%s"); i > -1 && c < args.length; i = sb
				.indexOf("%s")) {
			sb.replace(i, i + 2, String.valueOf(args[c]));
			c++;
		}
		if (c < args.length) {
			sb.append(" [");
			for (i = c; i < args.length; i++)
				sb.append(String.valueOf(args[i])).append(", ");
			sb.replace(sb.length() - 2, sb.length(), "]");
		}
		return sb.toString();
	}

}