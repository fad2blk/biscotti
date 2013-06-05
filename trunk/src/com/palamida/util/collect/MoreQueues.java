package com.palamida.util.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;

import com.google.common.collect.Iterators;
import com.google.common.collect.Queues;

/**
 * Static utility methods which operate on or return {@link Queue}s.
 * 
 * @author Zhenya Leonov
 * @see Queues
 */
public class MoreQueues {

	private MoreQueues() {
	}

	/**
	 * Creates an empty {@code ArrayDeque} with an initial capacity sufficient
	 * to hold the specified number of elements.
	 * 
	 * @param numElements
	 *            lower bound on initial capacity of the deque
	 * @return an empty {@code ArrayDeque} with an initial capacity sufficient
	 *         to hold the specified number of elements
	 */
	public static <E> ArrayDeque<E> newArrayDequeWithCapacity(
			final int numElements) {
		return new ArrayDeque<E>(numElements);
	}

	/**
	 * Creates an {@code ArrayDeque} containing the specified initial elements.
	 * 
	 * @param first
	 *            the first element
	 * @param rest
	 *            an array of additional elements, possibly empty
	 * @return an {@code ArrayDeque} containing the specified initial elements
	 */
	public static <E> ArrayDeque<E> newArrayDeque(final E first,
			final E... rest) {
		checkNotNull(first);
		checkNotNull(rest);
		final ArrayDeque<E> arrayDeque = new ArrayDeque<E>(rest.length + 1);
		arrayDeque.add(first);
		Collections.addAll(arrayDeque, rest);
		return arrayDeque;
	}

	/**
	 * Creates an {@code ArrayDeque} containing the elements returned by the
	 * specified iterator.
	 * 
	 * @param elements
	 *            the iterator whose elements are to be placed into the deque
	 * @return an {@code ArrayDeque} containing the elements returned by the
	 *         specified iterator
	 */
	public static <E> ArrayDeque<E> newArrayDeque(
			final Iterator<? extends E> elements) {
		checkNotNull(elements);
		final ArrayDeque<E> arrayDeque = new ArrayDeque<E>();
		Iterators.addAll(arrayDeque, elements);
		return arrayDeque;
	}

}
