package com.palamida.util.collect;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.SortedSet;

import com.google.common.collect.ForwardingQueue;
import com.google.common.collect.Iterables;
import com.google.common.collect.MinMaxPriorityQueue;
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
 * When the queue is full the {@code offer(E)} method behaves according to the
 * following policy: if the element to be added has higher priority than the
 * lowest priority element currently in the queue, the new element is added and
 * the lowest priority element is removed; else the new element is rejected. The
 * backing {@code TreeQueue} maintains references to the highest and lowest
 * elements in the queue; rejecting an element is an <i>O(1)</i> operation.
 * <p>
 * The {@code add(E)} and {@code addAll(Collection)} operations will throw an
 * {@code IllegalStateException} when the queue is full and a new element is
 * rejected; as required by the contract of {@link Queue#add Queue.add(E)}.
 * <p>
 * Bounded priority queues are useful when implementing <i>n-best</i> algorithms
 * (e.g. finding the <i>best</i> <i>n</i> elements in an arbitrary collection).
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
final class BoundedPriorityQueue<E> extends ForwardingQueue<E> implements BoundedQueue<E>, SortedCollection<E>,
		Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	private TreeQueue<E> delegate;
	private final int maxSize;

	private BoundedPriorityQueue(final int maxSize, final Comparator<? super E> comparator) {
		delegate = TreeQueue.orderedBy(comparator).create();
		this.maxSize = maxSize;
	}

	private BoundedPriorityQueue(final Comparator<? super E> comparator, final Iterable<? extends E> elements) {
		delegate = TreeQueue.orderedBy(comparator).create();
		for (E e : elements)
			offer(e);
		checkArgument(size() > 0);
		this.maxSize = size();
	}

	// /**
	// * Creates a new {@code TreeBoundedQeque} which orders its elements
	// * according to their <i>natural ordering</i>, and having the specified
	// * maximum size.
	// *
	// * @param maximumSize
	// * the maximum size (the bound) of this queue
	// * @return returns a new {@code TreeBoundedQeque} which orders its
	// elements
	// * according to their <i>natural ordering</i>, having the specified
	// * maximum size
	// * @throws IllegalArgumentException
	// * if {@code maximumSize} is less than 1
	// */
	// public static <E extends Comparable<? super E>> BoundedPriorityQueue<E>
	// create(
	// final int maximumSize) {
	// checkArgument(maximumSize > 0);
	// return new BoundedPriorityQueue<E>(maximumSize, Ordering.natural());
	// }

	// /**
	// * Creates a new empty {@code TreeBoundedQeque} having the specified
	// maximum
	// * size and comparator.
	// *
	// * @param maximumSize
	// * the maximum size (the bound) of this queue
	// * @param comparator
	// * the comparator that will be used to order this queue
	// * @return returns a new {@code TreeBoundedQeque} having the specified
	// * maximum size
	// * @throws IllegalArgumentException
	// * if {@code maximumSize} is less than 1
	// */
	// public static <E> BoundedPriorityQueue<E> create(final int maximumSize,
	// final Comparator<? super E> comparator) {
	// checkArgument(maximumSize > 0);
	// checkNotNull(comparator);
	// return new BoundedPriorityQueue<E>(maximumSize, comparator);
	// }

	/**
	 * Creates a new {@code BoundedPriorityQueue} containing the elements of,
	 * and having its capacity equal to the number of elements in the specified
	 * collection. If the collection is an instance of {@link SortedSet},
	 * {@link PriorityQueue}, {@link MinMaxPriorityQueue}, or
	 * {@code SortedCollection} this queue will be ordered according to the same
	 * ordering. Otherwise, this queue will be ordered according to the
	 * <i>natural ordering</i> of its elements.
	 * 
	 * @param elements
	 *            the collection whose elements are to be placed into the queue
	 * @return a new {@code BoundedPriorityQueue} containing the elements of the
	 *         specified collection
	 * @throws ClassCastException
	 *             if elements of the specified collection cannot be compared to
	 *             one another according to the priority queue's ordering
	 * @throws NullPointerException
	 *             if any of the elements of the specified collection or the
	 *             collection itself is {@code null}
	 * @throws IllegalArgumentException
	 *             if the specified collection is empty
	 */
	@SuppressWarnings({ "unchecked" })
	public static <E extends Comparable<? super E>> BoundedPriorityQueue<E> from(final Collection<? extends E> elements) {
		checkNotNull(elements);
		final Comparator<? super E> comparator;
		if (elements instanceof SortedSet<?>)
			comparator = ((SortedSet<? super E>) elements).comparator();
		else if (elements instanceof PriorityQueue<?>)
			comparator = ((PriorityQueue<? super E>) elements).comparator();
		else if (elements instanceof SortedCollection<?>)
			comparator = ((SortedCollection<? super E>) elements).comparator();
		else if (elements instanceof MinMaxPriorityQueue<?>)
			comparator = ((MinMaxPriorityQueue<? super E>) elements).comparator();
		else
			comparator = (Comparator<? super E>) Ordering.natural();
		return new BoundedPriorityQueue<E>(comparator, elements);
	}

	/**
	 * Returns a new builder configured to build {@code BoundedPriorityQueue}
	 * instances that are limited to the specified maximum number of elements.
	 * 
	 * @param maxElements
	 *            the maximum number of elements which can be placed in this
	 *            queue
	 * @return a new builder configured to build {@code BoundedPriorityQueue}
	 *         instances that are limited to the specified maximum number of
	 *         elements
	 */
	public static <E extends Comparable<? super E>> Builder<E> maxElements(final int maxElements) {
		checkState(maxElements > 0, "maxElements < 1");
		final Comparator<E> c = Ordering.natural();
		return new Builder<E>(c).maxElements(maxElements);
	}

	/**
	 * Returns a new builder configured to build {@code BoundedPriorityQueue}
	 * instances that use the specified comparator for ordering.
	 * 
	 * @param comparator
	 *            the specified comparator
	 * @return a new builder configured to build {@code BoundedPriorityQueue}
	 *         instances that use the specified comparator for ordering
	 */
	public static <B> Builder<B> orderedBy(final Comparator<B> comparator) {
		checkNotNull(comparator);
		return new Builder<B>(comparator);
	}

	/**
	 * A builder for the creation of {@code BoundedPriorityQueue} instances.
	 * Instances of this builder are obtained calling
	 * {@link BoundedPriorityQueue#orderedBy(Comparator)} and
	 * {@link BoundedPriorityQueue#maxElements(int)}.
	 * 
	 * @author Zhenya Leonov
	 * @param <B>
	 *            the upper bound of the type of queues this builder can produce
	 *            (for example a {@code Builder<Number>} can produce a
	 *            {@code BoundedPriorityQueue<Float>} or a
	 *            {@code BoundedPriorityQueue<Integer>}
	 */
	public static final class Builder<B> {

		private final Comparator<B> comparator;
		private int maxElements = 0;

		private Builder(final Comparator<B> comparator) {
			this.comparator = comparator;
		}

		/**
		 * Configures this builder to build {@code BoundedPriorityQueue}
		 * instances that are limited to the specified maximum number of
		 * elements.
		 * 
		 * @param maxElements
		 *            the total number of elements which can be placed in this
		 *            queue
		 * @return this builder
		 */
		public Builder<B> maxElements(final int maxElements) {
			checkState(maxElements > 0, "maxElements < 1");
			this.maxElements = maxElements;
			return this;
		}

		/**
		 * Builds an empty {@code BoundedPriorityQueue} that is limited to the
		 * specified maximum number of elements, using the previously specified
		 * comparator.
		 * 
		 * @return an empty {@code BoundedPriorityQueue} using the previously
		 *         specified comparator and maximum number of elements
		 * @throws IllegalArgumentException
		 *             if the maximum number of elements has not been specified
		 */
		public <T extends B> BoundedPriorityQueue<T> create() {
			checkState(maxElements > 0, "maxElements unspecified");
			return new BoundedPriorityQueue<T>(maxElements, comparator);
		}

		/**
		 * Builds a new {@code BoundedPriorityQueue} using the previously
		 * specified comparator and maxElements, having the given initial
		 * elements.
		 * 
		 * @param elements
		 *            the initial elements to be placed in this queue
		 * @return a new {@code BoundedPriorityQueue} using the previously
		 *         specified comparator and capacity, having the given initial
		 *         elements
		 * @throws IllegalStateException
		 *             if the capacity has not been specified
		 */
		public <T extends B> BoundedPriorityQueue<T> create(final Iterable<? extends T> elements) {
			checkState(maxElements > 0, "Capacity unspecified");
			checkNotNull(elements);
			final BoundedPriorityQueue<T> list = new BoundedPriorityQueue<T>(maxElements, comparator);
			Iterables.addAll(list, elements);
			return list;
		}
	}

	// @Override
	// public boolean add(E e) {
	// checkState(offer(e), "Queue full");
	// return true;
	// }

	@Override
	public boolean offer(E e) {
		if (size() == maxSize)
			if (comparator().compare(e, delegate().peekLast()) < 0)
				delegate().pollLast();
			else
				return false;
		return delegate().offer(e);
	}

	@Override
	public int maxSize() {
		return maxSize;
	}

	@Override
	public int remainingCapacity() {
		return maxSize - size();
	}

	@Override
	public boolean isFull() {
		return maxSize == size();
	}

	@Override
	public Comparator<? super E> comparator() {
		return delegate().comparator();
	}

	@Override
	protected TreeQueue<E> delegate() {
		return delegate;
	}

	/**
	 * Returns a shallow copy of this {@code BoundedPriorityQueue}. The elements
	 * themselves are not cloned.
	 * 
	 * @return a shallow copy of this queue
	 */
	@SuppressWarnings("unchecked")
	@Override
	public BoundedPriorityQueue<E> clone() throws CloneNotSupportedException {
		BoundedPriorityQueue<E> clone = (BoundedPriorityQueue<E>) super.clone();
		clone.delegate = (TreeQueue<E>) delegate.clone();
		return clone;
	}

}
