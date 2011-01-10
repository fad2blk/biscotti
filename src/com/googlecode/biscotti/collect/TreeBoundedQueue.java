/*
 * Copyright (C) 2010 Zhenya Leonov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googlecode.biscotti.collect;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.SortedSet;

import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

/**
 * An implementation of {@link BoundedQueue} backed by a {@link TreeQueue}. The
 * elements of this queue are sorted according to their <i>natural ordering</i>,
 * or by an explicit {@link Comparator} provided at creation. Inserting
 * {@code null} elements is prohibited. Attempting to insert non-comparable
 * elements will result in a {@code ClassCastException}.
 * <p>
 * The first element (the head) of this queue is considered to be the
 * <i>least</i> element with respect to the specified ordering. Elements with
 * equal priority are ordered according to their insertion order.
 * <p>
 * When the queue is full the {@code add(E)}, {@code offer(E)}, and
 * {@code addAll(Collection)} operations behave according to the following
 * policy: if the element to be added is greater than the element with the
 * <i>lowest</i> priority, the <i>lowest</i> element is removed and the new
 * element is added; else the new element is rejected.
 * <p>
 * This queue is not <i>thread-safe</i>. If multiple threads modify this queue
 * concurrently it must be synchronized externally.
 * <p>
 * Refer to {@link TreeQueue} for details of the underlying implementation.
 * 
 * @author Zhenya Leonov
 * @param <E>
 *            the type of elements held in this queue
 */
public final class TreeBoundedQueue<E> extends TreeQueue<E> implements
		BoundedQueue<E> {

	private static final long serialVersionUID = 1L;
	private final int maxSize;

	private TreeBoundedQueue(final int maxSize,
			final Comparator<? super E> comparator) {
		super(comparator);
		this.maxSize = maxSize;
	}

	private TreeBoundedQueue(final Comparator<? super E> comparator,
			final Iterable<? extends E> elements) {
		super(comparator);
		checkArgument(Iterables.addAll(this, elements));
		this.maxSize = size;
	}

	/**
	 * Creates a new {@code TreeBoundedQueue} having the specified maximum size.
	 * 
	 * @param maxSize
	 *            the maximum size (the bound) of this queue
	 * @return returns a new {@code TreeBoundedQueue} having the specified
	 *         maximum size
	 * @throws IllegalArgumentException
	 *             if {@code maxSize} is less than 1
	 */
	public static <E extends Comparable<? super E>> TreeBoundedQueue<E> create(
			final int maxSize) {
		checkArgument(maxSize > 0);
		return new TreeBoundedQueue<E>(maxSize, Ordering.natural());
	}

	/**
	 * Creates a new empty {@code TreeBoundedQueue} having the specified maximum
	 * size and comparator.
	 * 
	 * @param maxSize
	 *            the maximum size (the bound) of this queue
	 * @param comparator
	 *            the comparator that will be used to order this queue
	 * @return returns a new {@code TreeBoundedQueue} having the specified
	 *         maximum size
	 * @throws IllegalArgumentException
	 *             if {@code maxSize} is less than 1
	 */
	public static <E> TreeBoundedQueue<E> create(final int maxSize,
			final Comparator<? super E> comparator) {
		checkArgument(maxSize > 0);
		checkNotNull(comparator);
		return new TreeBoundedQueue<E>(maxSize, comparator);
	}

	/**
	 * Creates a new {@code TreeBoundedQueue} containing the elements of and
	 * having the maximum size equal to the number of elements in the specified
	 * {@code Iterable}. If the specified iterable is an instance of
	 * {@link SortedSet}, {@link PriorityQueue}, or {@code SortedCollection}
	 * this queue will be ordered according to the same ordering. Otherwise,
	 * this queue will be ordered according to the <i>natural ordering</i> of
	 * its elements.
	 * 
	 * @param elements
	 *            the iterable whose elements are to be placed into the queue
	 * @return a new {@code TreeBoundedQueue} containing the elements of the
	 *         specified iterable
	 * @throws ClassCastException
	 *             if elements of the specified iterable cannot be compared to
	 *             one another according to the priority queue's ordering
	 * @throws NullPointerException
	 *             if any of the elements of the specified iterable or the
	 *             iterable itself is {@code null}
	 * @throws IllegalArgumentException
	 *             if the specified iterable is empty
	 */
	public static <E> TreeBoundedQueue<E> create(
			final Iterable<? extends E> elements) {
		checkNotNull(elements);
		final Comparator<? super E> comparator;
		if (elements instanceof SortedSet<?>)
			comparator = ((SortedSet) elements).comparator();
		else if (elements instanceof PriorityQueue<?>)
			comparator = ((PriorityQueue) elements).comparator();
		else if (elements instanceof SortedCollection<?>)
			comparator = ((SortedCollection) elements).comparator();
		else
			comparator = (Comparator<? super E>) Ordering.natural();
		TreeBoundedQueue<E> q = new TreeBoundedQueue<E>(comparator, elements);
		return q;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalStateException
	 *             if this queue is full and the element to be added has equal
	 *             or lower priority than the element at the tail of this queue
	 */
	@Override
	public boolean add(E e) {
		checkState(offer(e), "Queue full");
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return {@inheritDoc} if this queue is full and the element to be added
	 *         has equal or lower priority than the element at the head of this
	 *         queue
	 */
	@Override
	public boolean offer(E e) {
		if (size() == maxSize)
			if (comparator().compare(e, min.element) > 0)
				remove(min);
			else
				return false;
		return super.offer(e);
	}

	// @Override
	// public boolean addAll(Collection<? extends E> c) {
	// return super.addAll(c);
	// }

	@Override
	public int maxSize() {
		return maxSize;
	}

	@Override
	public int remainingCapacity() {
		return maxSize - size();
	}

}