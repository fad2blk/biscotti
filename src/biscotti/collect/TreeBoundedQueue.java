package biscotti.collect;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.Serializable;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.SortedSet;

import com.google.common.collect.ForwardingQueue;
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
 * policy: if the element to be added has higher priority than the lowest
 * priority element currently in the queue, the new element is added and the
 * lowest priority element is removed; else the new element is rejected. The
 * backing {@code TreeQueue} maintains references to the highest and lowest
 * elements in the queue; rejecting an element is an <i>O(1)</i> operation.
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
	private int maximumSize;

	private TreeBoundedQueue(final int maximumSize,
			final Comparator<? super E> comparator) {
		delegate = TreeQueue.create(comparator);
		this.maximumSize = maximumSize;
	}

	private TreeBoundedQueue(final Comparator<? super E> comparator,
			final Iterable<? extends E> elements) {
		delegate = TreeQueue.create(comparator);
		for (E e : elements)
			offer(e);
		checkArgument(size() > 0);
		this.maximumSize = size();
	}

	/**
	 * Creates a new {@code TreeBoundedQeque} having the specified maximum size.
	 * 
	 * @param maximumSize
	 *            the maximum size (the bound) of this queue
	 * @return returns a new {@code TreeBoundedQeque} having the specified
	 *         maximum size
	 * @throws IllegalArgumentException
	 *             if {@code maxSize} is less than 1
	 */
	public static <E extends Comparable<? super E>> TreeBoundedQueue<E> create(
			final int maximumSize) {
		checkArgument(maximumSize > 0);
		return new TreeBoundedQueue<E>(maximumSize, Ordering.natural());
	}

	/**
	 * Creates a new empty {@code TreeBoundedQeque} having the specified maximum
	 * size and comparator.
	 * 
	 * @param maximumSize
	 *            the maximum size (the bound) of this queue
	 * @param comparator
	 *            the comparator that will be used to order this queue
	 * @return returns a new {@code TreeBoundedQeque} having the specified
	 *         maximum size
	 * @throws IllegalArgumentException
	 *             if {@code maxSize} is less than 1
	 */
	public static <E> TreeBoundedQueue<E> create(final int maximumSize,
			final Comparator<? super E> comparator) {
		checkArgument(maximumSize > 0);
		checkNotNull(comparator);
		return new TreeBoundedQueue<E>(maximumSize, comparator);
	}

	/**
	 * Creates a new {@code TreeBoundedQeque} containing the elements of and
	 * having the maximum size equal to the number of elements in the specified
	 * {@code Iterable}. If the specified iterable is an instance of
	 * {@link SortedSet}, {@link PriorityQueue}, or {@code SortedCollection}
	 * this queue will be ordered according to the same ordering. Otherwise,
	 * this queue will be ordered according to the <i>natural ordering</i> of
	 * its elements.
	 * 
	 * @param elements
	 *            the iterable whose elements are to be placed into the queue
	 * @return a new {@code TreeBoundedQeque} containing the elements of the
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
