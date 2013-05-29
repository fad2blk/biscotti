package com.palamida.common.base;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An immutable {@code Pair} of non-{@code null} objects. Neither the first nor
 * second object can be changed after creation.
 * 
 * @author Zhenya Leonov
 * 
 * @param <T>
 *            The type of the first object
 * @param <U>
 *            The type of the second object
 */
final public class ImmutablePair<T, U> extends Pair<T, U> {

	private ImmutablePair(final T first, final U second) {
		super(first, second);
	}

	/**
	 * Returns an immutable {@code Pair} containing the specified objects.
	 * 
	 * @param first
	 *            the first object
	 * @param second
	 *            the second object
	 * @return an immutable {@code Pair} containing the specified objects
	 */
	public static <T, U> ImmutablePair<T, U> of(final T first, final U second) {
		return new ImmutablePair<T, U>(checkNotNull(first),
				checkNotNull(second));
	}

	/**
	 * Always throws {@code UnsupportedOperationException}.
	 * 
	 * @throws UnsupportedOperationException
	 *             always
	 */
	@Override
	public void setFirst(final T first) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Always throws {@code UnsupportedOperationException}.
	 * 
	 * @throws UnsupportedOperationException
	 *             always
	 */
	@Override
	public void setSecond(final U second) {
		throw new UnsupportedOperationException();
	}

}