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

package biscotti.common.collect;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import com.google.common.collect.ForwardingQueue;
import com.google.common.collect.Lists;

/**
 * A linked list implementation of {@link BoundedQueue}. This queue orders
 * elements in <i>first-in-first-out</i> (FIFO) manner; it considers the
 * <i>eldest</i> element to be the least recently inserted element. To prevent
 * this queue from exceeding its capacity restrictions the eldest element is
 * removed as needed when calling {@code add(E)}, {@code offer(E)}, and
 * {@code addAll(Collection)} methods. Elements may be {@code null}.
 * <p>
 * This queue is not <i>thread-safe</i>. If multiple threads modify this queue
 * concurrently it must be synchronized externally.
 * 
 * @author Zhenya Leonov
 * @param <E>
 *            the type of elements held in this queue
 * @see LinkedList
 */
public final class LinkedBoundedQueue<E> extends ForwardingQueue<E> implements
		BoundedQueue<E>, Cloneable, Serializable {

	private static final long serialVersionUID = 1L;
	private LinkedList<E> queue;
	private final int maxSize;

	private LinkedBoundedQueue(final int maxSize) {
		checkArgument(maxSize > 0);
		queue = Lists.newLinkedList();
		this.maxSize = maxSize;
	}

	private LinkedBoundedQueue(final LinkedList<E> queue, final int maxSize) {
		this.queue = queue;
		this.maxSize = maxSize;
	}

	private LinkedBoundedQueue(final Iterable<? extends E> iterable) {
		checkNotNull(iterable);
		this.queue = Lists.newLinkedList(iterable);
		checkArgument(!isEmpty());
		this.maxSize = this.size();
	}

	/**
	 * Creates a new {@code LinkedBoundedQueue} having the specified maximum
	 * size.
	 * 
	 * @param maxSize
	 *            the maximum size (the bound) of this queue
	 * @return a new {@code LinkedBoundedQueue} having the specified maximum
	 *         size
	 * @throws IllegalArgumentException
	 *             if {@code maxSize} is less than 1
	 */
	public static <E> LinkedBoundedQueue<E> create(final int maxSize) {
		return new LinkedBoundedQueue<E>(maxSize);
	}

	/**
	 * Creates a new {@code LinkedBoundedQueue} with the same elements,
	 * iteration order, and having the maximum size equal to the number of
	 * elements in the specified {@code iterable}.
	 * 
	 * @param elements
	 *            the iterable instance whose elements are to be placed in this
	 *            queue
	 * @return a new {@code LinkedBoundedQueue} with the same elements,
	 *         iteration order, and having the maximum size equal to the number
	 *         of elements in the specified iterable
	 * @throws IllegalArgumentException
	 *             if the specified iterable is empty
	 */
	public static <E> LinkedBoundedQueue<E> create(
			final Iterable<? extends E> elements) {
		return new LinkedBoundedQueue<E>(elements);
	}

	/**
	 * Inserts the specified element into this queue, removing the
	 * <i>least-recently-inserted</i> element as necessary to prevent the queue
	 * from overflowing.
	 */
	public boolean add(E e) {
		return offer(e); // will always return true
	}

	/**
	 * Inserts the specified element into this queue, removing the
	 * <i>least-recently-inserted</i> element as necessary to prevent the queue
	 * from overflowing.
	 */
	@Override
	public boolean offer(E e) {
		if (size() == maxSize)
			remove();
		return super.offer(e);
	}

	/**
	 * Adds all of the elements in the specified collection to this queue,
	 * removing stale elements (according to their insertion order) as necessary
	 * to prevent the queue from overflowing.
	 * 
	 * @see Collection#add(Object) add(E)
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		checkNotNull(c);
		checkState(c != this);
		boolean returnValue = false;
		for (E element : c)
			if (add(element))
				returnValue = true;
		return returnValue;
	}

	@Override
	public int maxSize() {
		return maxSize;
	}

	@Override
	protected Queue<E> delegate() {
		return queue;
	}

	@Override
	public int remainingCapacity() {
		return maxSize - size();
	}

	/**
	 * Returns a shallow copy of this {@code LinkedBoundedQueue}. The elements
	 * themselves are not cloned.
	 * 
	 * @return a shallow copy of this queue
	 */
	@SuppressWarnings("unchecked")
	@Override
	public LinkedBoundedQueue<E> clone() throws CloneNotSupportedException {
		LinkedBoundedQueue<E> clone = (LinkedBoundedQueue<E>) super.clone();
		clone.queue = (LinkedList<E>) queue.clone();
		return clone;
	}

}