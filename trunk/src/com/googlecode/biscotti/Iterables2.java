package com.googlecode.biscotti;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Static methods which operate on or return {@link Iterable}s.
 * 
 * @author Zhenya Leonov
 * @see Iterables
 * 
 */
public final class Iterables2 {

	private Iterables2() {
	}

	/**
	 * Returns the number of elements in the provided {@code Iterable} which
	 * satisfy the specified {@code Predicate}.
	 * 
	 * @param <E>
	 *            the type of elements in the provided {@code Iterable}
	 * @param iterable
	 *            the provided {@code Iterable}
	 * @param predicate
	 *            the specified {@code Predicate}
	 * @return the number of elements in {@code iterable} which satisfy {@code
	 *         predicate}
	 */
	public static <E> int countIf(final Iterable<E> iterable,
			final Predicate<? super E> predicate) {
		Preconditions.checkNotNull(iterable);
		Preconditions.checkNotNull(predicate);
		int count = 0;
		for (E element : iterable)
			if (predicate.apply(element))
				count++;
		return count;
	}

	/**
	 * Returns the number of elements in the provided {@code Iterable} which do
	 * not satisfy the specified {@code Predicate}.
	 * 
	 * @param <E>
	 *            the type of elements in the provided {@code Iterable}
	 * @param iterable
	 *            the provided {@code Iterable}
	 * @param predicate
	 *            the the specified {@code Predicate}
	 * @return the number of elements in {@code iterable} which do not satisfy
	 *         {@code predicate}
	 */
	public static <E> int countyIfNot(final Iterable<E> iterable,
			final Predicate<? super E> predicate) {
		Preconditions.checkNotNull(iterable);
		Preconditions.checkNotNull(predicate);
		int count = 0;
		for (E element : iterable)
			if (!predicate.apply(element))
				count++;
		return count;
	}

}