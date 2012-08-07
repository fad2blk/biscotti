package biscotti.collect;

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
final public class TreeBoundedQueue<E> extends ForwardingQueue<E> implements
		BoundedQueue<E>, SortedCollection<E>, Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	private TreeQueue<E> delegate;
	private final int maximumSize;

	private TreeBoundedQueue(final int maximumSize,
			final Comparator<? super E> comparator) {
		delegate = TreeQueue.orderedBy(comparator).create();
		this.maximumSize = maximumSize;
	}

	private TreeBoundedQueue(final Comparator<? super E> comparator,
			final Iterable<? extends E> elements) {
		delegate = TreeQueue.orderedBy(comparator).create();
		for (E e : elements)
			offer(e);
		checkArgument(size() > 0);
		this.maximumSize = size();
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
	// * if {@code maxSize} is less than 1
	// */
	// public static <E extends Comparable<? super E>> TreeBoundedQueue<E>
	// create(
	// final int maximumSize) {
	// checkArgument(maximumSize > 0);
	// return new TreeBoundedQueue<E>(maximumSize, Ordering.natural());
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
	// * if {@code maxSize} is less than 1
	// */
	// public static <E> TreeBoundedQueue<E> create(final int maximumSize,
	// final Comparator<? super E> comparator) {
	// checkArgument(maximumSize > 0);
	// checkNotNull(comparator);
	// return new TreeBoundedQueue<E>(maximumSize, comparator);
	// }

	/**
	 * Creates a new {@code TreeBoundedQueue} containing the elements of, and
	 * having the maximum size equal to the number of elements in the specified
	 * collection. If the collection is an instance of {@link SortedSet},
	 * {@link PriorityQueue}, {@link MinMaxPriorityQueue}, or
	 * {@code SortedCollection} this queue will be ordered according to the same
	 * ordering. Otherwise, this queue will be ordered according to the
	 * <i>natural ordering</i> of its elements.
	 * 
	 * @param elements
	 *            the collection whose elements are to be placed into the queue
	 * @return a new {@code TreeBoundedQueue} containing the elements of the
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
	public static <E> TreeBoundedQueue<E> create(
			final Collection<? extends E> elements) {
		checkNotNull(elements);
		final Comparator<? super E> comparator;
		if (elements instanceof SortedSet<?>)
			comparator = ((SortedSet<? super E>) elements).comparator();
		else if (elements instanceof PriorityQueue<?>)
			comparator = ((PriorityQueue<? super E>) elements).comparator();
		else if (elements instanceof SortedCollection<?>)
			comparator = ((SortedCollection<? super E>) elements).comparator();
		else if (elements instanceof MinMaxPriorityQueue<?>)
			comparator = ((MinMaxPriorityQueue<? super E>) elements)
					.comparator();
		else
			comparator = (Comparator<? super E>) Ordering.natural();
		return new TreeBoundedQueue<E>(comparator, elements);
	}

	/**
	 * Creates and returns a new builder, configured to build
	 * {@code TreeBoundedQueue} instances that use the specified comparator
	 * ordering.
	 * 
	 * @param comparator
	 *            the specified comparator
	 * @return a new building which builds {@code TreeBoundedQueue} instances
	 *         that use the specified comparator for ordering
	 */
	public static <B> Builder<B> orderedBy(final Comparator<B> comparator) {
		checkNotNull(comparator);
		return new Builder<B>(comparator);
	}

	/**
	 * A builder for the creation of {@code TreeBoundedQueue} instances.
	 * Instances of this builder are obtained calling
	 * {@link TreeBoundedQueue#orderedBy(Comparator)}.
	 * 
	 * @author Zhenya Leonov
	 * @param <B>
	 *            the upper bound of the type of queues this builder can produce
	 *            (for example a {@code Builder<Number>} can produce a
	 *            {@code TreeBoundedQueue<Float>} or a
	 *            {@code TreeBoundedQueue<Integer>}
	 */
	public static final class Builder<B> {

		private final Comparator<B> comparator;

		private Builder(final Comparator<B> comparator) {
			this.comparator = comparator;
		}

		/**
		 * Builds an empty {@code TreeBoundedQueue} using the previously
		 * specified comparator and the given maximumSize.
		 * 
		 * @param maximumSize the maximum size of this queue
		 * @return an empty {@code TreeBoundedQueue} using the previously
		 *         specified comparator and the given maximum size
		 * @throws IllegalArgumentException
		 *             if the maximum size has not been specified
		 */
		public <T extends B> TreeBoundedQueue<T> create(final int maximumSize) {
			checkArgument(maximumSize > 0, "maximum size < 1");
			return new TreeBoundedQueue<T>(maximumSize, comparator);
		}

		/**
		 * Builds a new {@code TreeBoundedQueue} using the previously specified
		 * comparator and maximum size, and having the given initial elements.
		 * 
		 * @param maximumSize the maximum size of this queue
		 * @param elements
		 *            the initial elements to be placed in this queue
		 * @return a new {@code TreeBoundedQueue} using the previously specified
		 *        comparator and maximum size, having the given initial elements
		 * @throws IllegalStateException
		 *             if the maximum size has not been specified
		 */
		public <T extends B> TreeBoundedQueue<T> create(final int maximumSize,
				final Iterable<? extends T> elements) {
			checkArgument(maximumSize > 0, "maximum size < 1");
			checkNotNull(elements);
			final TreeBoundedQueue<T> list = new TreeBoundedQueue<T>(
					maximumSize, comparator);
			Iterables.addAll(list, elements);
			return list;
		}
	}

	@Override
	public boolean add(E e) {
		checkState(offer(e), "Queue full");
		return true;
	}

	@Override
	public boolean offer(E e) {
		if (size() == maximumSize)
			if (comparator().compare(e, delegate().peekLast()) < 0)
				delegate().pollLast();
			else
				return false;
		return delegate().offer(e);
	}

	@Override
	public int maximumSize() {
		return maximumSize;
	}

	@Override
	public int remainingCapacity() {
		return maximumSize - size();
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
	 * Returns a shallow copy of this {@code TreeBoundedQueue}. The elements
	 * themselves are not cloned.
	 * 
	 * @return a shallow copy of this queue
	 */
	@SuppressWarnings("unchecked")
	@Override
	public TreeBoundedQueue<E> clone() throws CloneNotSupportedException {
		TreeBoundedQueue<E> clone = (TreeBoundedQueue<E>) super.clone();
		clone.delegate = (TreeQueue<E>) delegate.clone();
		return clone;
	}

}
