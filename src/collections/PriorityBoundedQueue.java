package collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.Queue;
import java.util.SortedSet;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;

/**
 * An implementation of {@link BoundedQueue} backed by a {@link PriorityQueue}.
 * The elements of this queue are ordered according to their <i>natural
 * ordering</i>, or by an explicit {@link Comparator} provided at creation.
 * Inserting {@code null} elements is prohibited. Attempting to insert
 * non-comparable elements will result in a {@code ClassCastException}. An
 * additional {@link offerAll(Collection)} method is provided. The difference
 * between {@code offerAll(Collection)} and {@code addAll(Collection)} is
 * analogous to the difference between {@code offer(E)} and {@code add(E)}.
 * <p>
 * The first element (the head) of this queue is considered to be the
 * <i>least</i> element with respect to the specified ordering. Elements with
 * equal priority are ordered according to their insertion order.
 * <p>
 * When the queue is full the {@code add(E)}, {@code offer(E)}, and {@code
 * addAll(Collection)} operations behave according to the following policy: if
 * the element to be added is greater than the element with the <i>lowest</i>
 * priority, the <i>lowest</i> element is removed and the new element is added;
 * else the new element is rejected.
 * <p>
 * This queue is not <i>thread-safe</i>. If multiple threads modify this queue
 * concurrently it must be synchronized externally, consider "wrapping" the
 * queue using the {@code Collections3.synchronizedBoundedQueue(BoundedQueue)}
 * method.
 * <p>
 * Refer to {@link PriorityQueue} for details of the underlying implementation.
 * 
 * @author Zhenya Leonov
 * @param <E>
 *            the type of elements held in this queue
 */
public final class PriorityBoundedQueue<E> extends PriorityQueue<E> implements
		BoundedQueue<E> {

	private static final long serialVersionUID = -4139767385422869092L;

	private int maxSize;

	private PriorityBoundedQueue(final int maxSize,
			final Comparator<? super E> c) {
		super(c);
		this.maxSize = maxSize;
	}

	private PriorityBoundedQueue(final Iterable<? extends E> elements) {
		super(elements);
		this.maxSize = size();
	}

	/**
	 * Creates a new {@code PriorityBoundedQueue} having the specified maximum
	 * size.
	 * 
	 * @param maxSize
	 *            the maximum size (the bound) of this queue
	 * @return returns a new {@code PriorityBoundedQueue} having the specified
	 *         maximum size
	 * @throws IllegalArgumentException
	 *             if {@code maxSize} is less than 1
	 */
	public static <E> PriorityBoundedQueue<E> create(final int maxSize) {
		Preconditions.checkArgument(maxSize > 0);
		return new PriorityBoundedQueue<E>(maxSize, null);
	}

	/**
	 * Creates a new empty {@code PriorityBoundedQueue} having the specified
	 * maximum size and comparator.
	 * 
	 * @param maxSize
	 *            the maximum size (the bound) of this queue
	 * @param comparator
	 *            the comparator that will be used to order this priority queue,
	 *            or {@code null} for {@link Comparable natural ordering}
	 * @return returns a new {@code PriorityBoundedQueue} having the specified
	 *         maximum size
	 * @throws IllegalArgumentException
	 *             if {@code maxSize} is less than 1
	 */
	public static <E> PriorityBoundedQueue<E> create(final int maxSize,
			final Comparator<? super E> comparator) {
		Preconditions.checkArgument(maxSize > 0);
		Preconditions.checkNotNull(comparator);
		return new PriorityBoundedQueue<E>(maxSize, comparator);
	}

	/**
	 * Creates a new {@code PriorityBoundedQueue} containing the elements of and
	 * having the maximum size equal to the number of elements in the specified
	 * {@code Iterable}. If the specified iterable is a collection and is an
	 * instance of a {@link SortedSet} or is another {@code PriorityQueue}
	 * instance, this queue will be ordered according to the same ordering.
	 * Otherwise, this priority queue will be ordered according to the
	 * {@link Comparable natural ordering} of its elements.
	 * 
	 * @param elements
	 *            the iterable whose elements are to be placed into the queue
	 * @return a new {@code PriorityBoundedQueue} containing the elements of the
	 *         specified iterable
	 * @throws ClassCastException
	 *             if elements of the specified iterable cannot be compared to
	 *             one another according to the priority queue's ordering
	 * @throws NullPointerException
	 *             if any of the elements of the specified iterable or the
	 *             iterable itself is {@code null}
	 */
	public static <E> PriorityBoundedQueue<E> create(
			final Iterable<? extends E> elements) {
		Preconditions.checkNotNull(elements);
		return new PriorityBoundedQueue<E>(elements);
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
		Preconditions.checkState(offer(e), "Queue full");
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

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return super.addAll(c);
	}

	/**
	 * Adds all of the elements in the specified collection to this queue, if
	 * it's possible to do so without violating capacity restrictions. (optional
	 * operation). If no more space in the queue is available elements are
	 * rejected quietly <i>without</i> throwing an {@code IllegalStateException}
	 * . The difference between this operation and
	 * {@link Collection#addAll(Collection) addAll(Collection)} is analogous to
	 * the difference between {@link Queue#offer(Object) offer(E)} and
	 * {@link Queue#add(Object) add(E)}. This method is generally preferable to
	 * {@code addAll(Collection)}, which can fail only by throwing an exception.
	 * <p>
	 * Attempts to {@code offerAll} of a collection which contains {@code null}
	 * elements will fail cleanly and safely leaving this queue unmodified. If
	 * you are not sure whether or not your collection contains {@code null}
	 * elements considering filtering it by calling {@link Collections2#filter
	 * Collections2.filter(Collection, Predicate)} with a predicate obtained
	 * from {@link Predicates#notNull()}. Other runtime exception encountered
	 * while trying to add an element may result in only some of the elements
	 * having been successfully added when the associated exception is thrown.
	 * <p>
	 * 
	 * @param c
	 *            collection containing elements to be added to this collection
	 * @return {@code true} if at least one element was added to this queue,
	 *         else returns {@code false}
	 * @throws UnsupportedOperationException
	 *             if the {@code offerAll(Collection)} operation is not
	 *             supported
	 * @throws ClassCastException
	 *             if the class of an element of the specified collection
	 *             prevents it from being added to this queue
	 * @throws NullPointerException
	 *             if the specified collection contains a {@code null} element
	 *             and this queue does not permit {@code null} elements, or if
	 *             the specified collection is {@code null}
	 * @throws IllegalArgumentException
	 *             if some property of an element of the specified collection
	 *             prevents it from being added to this queue
	 * @see Queue#offer(Object) offer(E)
	 * @see Queue#add(Object) add(E)
	 */
	public boolean offerAll(Collection<? extends E> c) {
		Preconditions.checkNotNull(c);
		for (E e : c)
			Preconditions.checkNotNull(e);
		boolean returnValue = false;
		for (E element : c)
			if (offer(element))
				returnValue = true;
		return returnValue;
	}

	@Override
	public int maxSize() {
		return maxSize;
	}

	@Override
	public int remainingCapacity() {
		return maxSize - size();
	}
}