package com.googlecode.biscotti.collect;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingQueue;
import com.google.common.collect.Lists;

/**
 * A linked list implementation of {@link BoundedQueue}. This queue orders
 * elements in <i>insertion/first-in-first-out</i> (FIFO) manner; it considers
 * the <i>eldest</i> element to be the least recently inserted element. To
 * prevent this queue from exceeding its capacity restrictions the eldest
 * element is removed as needed when calling {@code add(E)}, {@code offer(E)},
 * {@code addAll(Collection)} and {@code offerAll(Collection)} methods. Elements
 * may be {@code null}.
 * <p>
 * This queue is not <i>thread-safe</i>. If multiple threads modify this queue
 * concurrently it must be synchronized externally, consider "wrapping" the
 * queue using the {@code Collections3.synchronizedBoundedQueue(BoundedQueue)}
 * method.
 * 
 * @author Zhenya Leonov
 * @param <E>
 *            the type of elements held in this queue
 * @see LinkedList
 */
public final class LinkedBoundedQueue<E> extends ForwardingQueue<E> implements
		BoundedQueue<E> {

	private final Queue<E> queue;
	private final int maxSize;

	private LinkedBoundedQueue(final int maxSize) {
		Preconditions.checkArgument(maxSize > 0);
		queue = Lists.newLinkedList();
		this.maxSize = maxSize;
	}
	
	private LinkedBoundedQueue(final Queue<E> queue, final int maxSize){
		this.queue = queue;
		this.maxSize = maxSize;
	}

	private LinkedBoundedQueue(final Iterable<? extends E> iterable) {
		Preconditions.checkNotNull(iterable);
		this.queue = Lists.newLinkedList(iterable);
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
	 * @param iterable
	 *            the iterable instance whose elements are to be placed in this
	 *            queue
	 * @return a new {@code LinkedBoundedQueue} with the same elements,
	 *         iteration order, and having the maximum size equal to the number
	 *         of elements in the specified iterable
	 */
	public static <E> LinkedBoundedQueue<E> create(
			final Iterable<? extends E> iterable) {
		return new LinkedBoundedQueue<E>(iterable);
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
	 * Inserts the specified element into this queue, removing the least
	 * recently inserted element as necessary to prevent the queue from
	 * overflowing.
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
		Preconditions.checkNotNull(c);
		Preconditions.checkState(c != this);
		boolean returnValue = false;
		for (E element : c)
			if (add(element))
				returnValue = true;
		return returnValue;
	}

//	@Override
//	public boolean offerAll(Collection<? extends E> c) {
//		Preconditions.checkNotNull(c);
//		Preconditions.checkState(c != this);
//		boolean returnValue = false;
//		for (E element : c)
//			if (offer(element))
//				returnValue = true;
//		return returnValue;
//	}

	@Override
	public int maxSize() {
		return maxSize;
	}

	@Override
	protected Queue<E> delegate() {
		return queue;
	}

//	/**
//	 * Creates and returns a copy of this queue.
//	 */
//	@Override
//	public LinkedBoundedQueue<E> clone() {
//		try {
//			return (LinkedBoundedQueue<E>) super.clone();
//		} catch (CloneNotSupportedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}

	@Override
	public int remainingCapacity() {
		return maxSize - size();
	}

}