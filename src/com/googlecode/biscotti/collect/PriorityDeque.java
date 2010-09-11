package com.googlecode.biscotti.collect;

import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

import com.google.common.base.Preconditions;

/**
 * An unbounded priority {@link Deque} based on a modified <a
 * href="http://en.wikipedia.org/wiki/Red-black_tree">red-black tree</a>. The
 * elements of this deque are ordered according to their <i>natural
 * ordering</i>, or by an explicit {@link Comparator} provided at creation.
 * Inserting {@code null} elements will fail cleanly and safely leaving this
 * deque unmodified. Querying for {@code null} elements is allowed. Attempting
 * to insert non-comparable elements will result in a {@code ClassCastException}
 * . The {@code addFirst(E)}, {@code addLast(E)}, {@code offerFirst(E)},
 * {@code offerLast(E)}, and {@code push(E)} operations are not supported.
 * <p>
 * This deque is ordered from <i>least</i> to <i>greatest</i> with respect to
 * the specified ordering. Elements with equal priority are ordered according to
 * their insertion order.
 * <p>
 * The {@link #iterator() iterator()} and {@link #descendingIterator()} methods
 * return <i>fail-fast</i> iterators which are guaranteed to traverse the
 * elements of the deque in priority and reverse priority order, respectively.
 * Attempts to modify the elements in this deque at any time after an iterator
 * is created, in any way except through the iterator's own remove method, will
 * result in a {@code ConcurrentModificationException}.
 * <p>
 * This deque is not <i>thread-safe</i>. If multiple threads modify this deque
 * concurrently it must be synchronized externally, consider "wrapping" the
 * deque using the {@code Collections3.synchronizedDeque(Deque)} method.
 * <p>
 * <b>Implementation Note:</b>This implementation uses a comparator (whether or
 * not one is explicitly provided) to maintain priority order, and
 * {@code equals} when testing for element equality. The ordering imposed by the
 * comparator is not guaranteed to be <i>consistent with equals</i>. For any two
 * elements {@code e1} and {@code e2} such that {@code e1.compareTo(e2) == 0} it
 * is not guaranteed {@code e1.equals(e2) == true}.
 * <p>
 * The underlying red-black tree provides the following worst case running time
 * (where <i>n</i> is the size of this queue, and <i>m</i> is the size of the
 * specified collection):
 * <p>
 * <table border cellpadding="3" cellspacing="1">
 *   <tr>
 *     <th align="center">Method</th>
 *     <th align="center">Running Time</th>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #addAll(Collection) addAll(Collection)}<br>
 *       {@link #containsAll(Collection) containsAll(Collection)}</br>
 *       {@link #retainAll(Collection) retainAll(Collection)}</br>
 *       {@link #removeAll(Collection) removeAll(Collection)}
 *     </td>
 *     <td align="center"><i>O(m log n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #clear() clear()}<br>
 *     </td>
 *     <td align="center"><i>O(n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #add(Object) add(E)}</br>
 *       {@link #contains(Object) contains(Object)}</br>
 *       {@link #offer(Object) offer(E)}</br>
 *       {@link #remove(Object) remove(Object)}</br>
 *     </td>
 *     <td align="center"><i>O(log n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #element() element()}</br>
 *       {@link #isEmpty() isEmpty()}</br>
 *       {@link #peek() peek()}</br>
 *       {@link #poll() poll()}</br>
 *       {@link #remove() remove()}</br>
 *       {@link #size() size()}<br>
 *       {@link #getFirst() getFirst()}</br>
 *       {@link #getLast() getLast()}</br>
 *       {@link #peekFirst() peekFirst()}</br>
 *       {@link #peekLast() peekLast()}</br>
 *       {@link #pollFirst() pollFirst()}</br>
 *       {@link #pollLast() pollLast()}</br>
 *       {@link #pop() pop()}</br>
 *       {@link #removeFirst() removeFirst()}</br>
 *       {@link #removeLast() removeLast()}</br>
 *     </td>
 *     <td align="center"><i>O(1)</i></td>
 *   </tr>
 * </table>
 * <p>
 * Note: This deque uses the same ordering rules as
 * {@link java.util.PriorityQueue java.util.PriorityQueue}. In comparison it
 * provides identical functionality, faster overall running time and ordered
 * traversals via its iterators.
 * 
 * @author Zhenya Leonov
 * @param <E>
 *            the type of elements held in this deque
 */
final public class PriorityDeque<E> extends PriorityQueue<E> implements
		Deque<E> {

	private static final long serialVersionUID = 1L;

	private PriorityDeque(final Comparator<? super E> comparator) {
		super(comparator);
	}

	private PriorityDeque(final Iterable<? extends E> elements) {
		super(elements);
	}

	/**
	 * Creates a new {@code PriorityDeque} that orders its elements according to
	 * their <i>natural ordering</i>.
	 * 
	 * @return a new {@code PriorityDeque} that orders its elements according to
	 *         their <i>natural ordering</i>
	 */
	public static <E> PriorityDeque<E> create() {
		return new PriorityDeque<E>((Comparator<? super E>) null);
	}

	/**
	 * Creates a new {@code PriorityDeque} that orders its elements according to
	 * the specified comparator.
	 * 
	 * @param comparator
	 *            the comparator that will be used to order this priority deque
	 * @return a new {@code PriorityDeque} that orders its elements according to
	 *         {@code comparator}
	 */
	public static <E> PriorityDeque<E> create(
			final Comparator<? super E> comparator) {
		Preconditions.checkNotNull(comparator);
		return new PriorityDeque<E>(comparator);
	}

	/**
	 * Creates a new {@code PriorityDeque} containing the elements of the
	 * specified {@code Iterable}. If the specified iterable is an instance of
	 * of {@link SortedSet}, {@link java.util.PriorityQueue
	 * java.util.PriorityQueue}, or {@code SortedCollection} this deque will be
	 * ordered according to the same ordering. Otherwise, this priority deque
	 * will be ordered according to the <i>natural ordering</i> of its elements.
	 * 
	 * @param elements
	 *            the iterable whose elements are to be placed into the deque
	 * @return a new {@code PriorityDeque} containing the elements of the
	 *         specified iterable
	 * @throws ClassCastException
	 *             if elements of the specified iterable cannot be compared to
	 *             one another according to the priority deque's ordering
	 * @throws NullPointerException
	 *             if any of the elements of the specified iterable or the
	 *             iterable itself is {@code null}
	 */
	public static <E> PriorityDeque<E> create(
			final Iterable<? extends E> elements) {
		Preconditions.checkNotNull(elements);
		return new PriorityDeque<E>(elements);
	}

	/**
	 * Guaranteed to throw an {@code UnsupportedOperationException} exception
	 * and leave the underlying data unmodified.
	 * 
	 * @throws UnsupportedOperationException
	 *             always
	 */
	@Override
	public void addFirst(E e) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Guaranteed to throw an {@code UnsupportedOperationException} exception
	 * and leave the underlying data unmodified.
	 * 
	 * @throws UnsupportedOperationException
	 *             always
	 */
	@Override
	public void addLast(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<E> descendingIterator() {
		return new Iterator<E>() {
			private Node next = max;
			private Node last = null;
			private int expectedModCount = count;

			@Override
			public boolean hasNext() {
				return next != null;
			}

			@Override
			public void remove() {
				if (last == null)
					throw new IllegalStateException();
				if (count != expectedModCount)
					throw new ConcurrentModificationException();
				if (last.left != null && last.right != null)
					next = last;
				delete(last);
				expectedModCount = count;
				last = null;
			}

			@Override
			public E next() {
				Node node = next;
				if (node == null)
					throw new NoSuchElementException();
				if (count != expectedModCount)
					throw new ConcurrentModificationException();
				next = predecessor(node);
				last = node;
				return node.element;
			}
		};
	}

	@Override
	public E getFirst() {
		return element();
	}

	@Override
	public E getLast() {
		E e = peekLast();
		if (e != null)
			return e;
		else
			throw new NoSuchElementException();
	}

	/**
	 * Guaranteed to throw an {@code UnsupportedOperationException} exception
	 * and leave the underlying data unmodified.
	 * 
	 * @throws UnsupportedOperationException
	 *             always
	 */
	@Override
	public boolean offerFirst(E e) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Guaranteed to throw an {@code UnsupportedOperationException} exception
	 * and leave the underlying data unmodified.
	 * 
	 * @throws UnsupportedOperationException
	 *             always
	 */
	@Override
	public boolean offerLast(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E peekFirst() {
		return peek();
	}

	@Override
	public E peekLast() {
		if (isEmpty())
			return null;
		return max.element;
	}

	@Override
	public E pollFirst() {
		return poll();
	}

	@Override
	public E pollLast() {
		if (isEmpty())
			return null;
		E e = max.element;
		delete(max);
		return e;
	}

	@Override
	public E pop() {
		return remove();
	}

	/**
	 * Guaranteed to throw an {@code UnsupportedOperationException} exception
	 * and leave the underlying data unmodified.
	 * 
	 * @throws UnsupportedOperationException
	 *             always
	 */
	@Override
	public void push(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E removeFirst() {
		return remove();
	}

	/**
	 * Guaranteed to throw an {@code UnsupportedOperationException} exception
	 * and leave the underlying data unmodified.
	 * 
	 * @throws UnsupportedOperationException
	 *             always
	 */
	public boolean removeFirstOccurrence(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E removeLast() {
		E e = pollLast();
		if (e != null)
			return e;
		else
			throw new NoSuchElementException();
	}

	/**
	 * Guaranteed to throw an {@code UnsupportedOperationException} exception
	 * and leave the underlying data unmodified.
	 * 
	 * @throws UnsupportedOperationException
	 *             always
	 */
	public boolean removeLastOccurrence(Object o) {
		throw new UnsupportedOperationException();
	}

}