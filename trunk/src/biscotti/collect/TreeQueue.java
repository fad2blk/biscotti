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

package biscotti.collect;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.SortedSet;

import com.google.common.collect.Iterables;
import com.google.common.collect.MinMaxPriorityQueue;
import com.google.common.collect.Ordering;

/**
 * An unbounded priority {@link Queue} based on a modified <a
 * href="http://en.wikipedia.org/wiki/Red-black_tree">Red-Black Tree</a>. The
 * elements of this queue are sorted according to their <i>natural ordering</i>,
 * or by an explicit {@link Comparator} provided at creation. Attempting to
 * remove or insert {@code null} elements is prohibited. Querying for
 * {@code null} elements is allowed. Inserting non-comparable elements will
 * result in a {@code ClassCastException}. This queue uses the same general
 * ordering rules as a {@link PriorityQueue PriorityQueue}. The first element
 * (the head) of this queue is considered to be the <i>least</i> element with
 * respect to the specified ordering. A comparator is used (whether or not one
 * is explicitly provided) to perform all element comparisons. Two elements
 * which are deemed equal by the comparator's {@code compare(E, E)} method have
 * equal priority from the standpoint of this queue. Elements with equal
 * priority are sorted according to their insertion order.
 * <p>
 * Besides the regular {@link #peek() peek()}, {@link #poll() poll()},
 * {@link #remove() remove()} operations specified in the {@code Queue}
 * interface, this implementation provides additional {@link #peekLast()
 * peekLast()}, {@link #pollLast() pollLast()}, {@link #removeLast()
 * removeLast()} methods to examine the elements at the tail of the queue.
 * <p>
 * The {@link #iterator() iterator()} and {@link #descendingIterator()} methods
 * return <i>fail-fast</i> iterators which are guaranteed to traverse the
 * elements of the queue in ascending and descending priority order,
 * respectively. Attempts to modify the elements in this queue at any time after
 * an iterator is created, in any way except through the iterator's own remove
 * method, will result in a {@code ConcurrentModificationException}.
 * <p>
 * This queue is not <i>thread-safe</i>. If multiple threads modify this queue
 * concurrently it must be synchronized externally.
 * <p>
 * The underlying Red-Black Tree provides the following running time compared to
 * a {@link PriorityQueue PriorityQueue} (where <i>n</i> is the size of this
 * queue and <i>m</i> is the size of the specified collection which is iterable
 * in linear time):
 * <p>
 * <table border="1" cellpadding="3" cellspacing="1" style="width:400px;">
 *   <tr>
 *     <th style="text-align:center;" rowspan="2">Method</th>
 *     <th style="text-align:center;" colspan="2">Running Time</th>
 *   </tr>
 *   <tr>
 *     <td style="text-align:center;"><b>TreeQueue</b><br>(<i>worst-case</i>)</td>
 *     <td style="text-align:center;"><b>PriorityQueue</b><br>(<i>worst-case</i>)</td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #addAll(Collection) addAll(Collection)}<br>
 *       {@link #containsAll(Collection) containsAll(Collection)}</br>
 *       {@link #retainAll(Collection) retainAll(Collection)}</br>
 *       {@link #removeAll(Collection) removeAll(Collection)}
 *     </td>
 *     <td colspan="2" style="text-align:center;"><i>O(m log n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #add(Object) add(E)}</br>
 *       {@link #offer(Object) offer(E)}</br>
 *       {@link #remove(Object)}
 *     </td>
 *     <td colspan="2" style="text-align:center;"><i>O(log n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #contains(Object)}
 *     </td>
 *     <td bgcolor="FFCC99" style="text-align:center;"><i>O(log n)</i></td>
 *     <td bgcolor="FFCCCC" rowspan="2" style="text-align:center;"><i>O(n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #clear()}
 *     </td>
 *     <td bgcolor="FFCC99" rowspan="2" style="text-align:center;"><i>O(1)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #poll()}</br>
 *       {@link #remove() remove()}</br>
 *     </td>
 *     <td bgcolor="FFCCCC" style="text-align:center;"><i>O(log n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #element() element()}</br>
 *       {@link #isEmpty() isEmpty()}</br>
 *       {@link #peek()}</br>
 *       {@link #size()}
 *     </td>
 *     <td colspan="2" style="text-align:center;"><i>O(1)</i></td>
 *   </tr>
 * </table>
 * 
 * @author Zhenya Leonov
 * @param <E>
 *            the type of elements held in this queue
 */
public class TreeQueue<E> extends RedBlackTree<E> implements Queue<E> {

	private static final long serialVersionUID = 1L;

	/**
	 * A builder for the creation of {@code TreeQueue} instances. Instances of
	 * this builder are obtained calling
	 * {@link TreeQueue#orderedBy(Comparator)}.
	 * 
	 * @author Zhenya Leonov
	 * @param <B>
	 *            the upper bound of the type of queues this builder can produce
	 *            (for example a {@code Builder<Number>} can produce a
	 *            {@code TreeQueue<Float>} or a {@code TreeQueue<Integer>}
	 */
	public static final class Builder<B> {

		private final Comparator<B> comparator;

		private Builder(final Comparator<B> comparator) {
			this.comparator = comparator;
		}

		/**
		 * Builds an empty {@code TreeQueue} using the previously specified
		 * comparator.
		 * 
		 * @return an empty {@code TreeQueue} using the previously specified
		 *         comparator.
		 */
		public <T extends B> TreeQueue<T> create() {
			return new TreeQueue<T>(comparator);
		}

		/**
		 * Builds a new {@code TreeQueue} using the previously specified
		 * comparator, and having the given initial elements.
		 * 
		 * @param elements
		 *            the initial elements to be placed in this queue
		 * @return a new {@code TreeQueue} using the previously specified
		 *         comparator, and having the given initial elements
		 */
		public <T extends B> TreeQueue<T> create(
				final Iterable<? extends T> elements) {
			checkNotNull(elements);
			final TreeQueue<T> list = new TreeQueue<T>(comparator);
			Iterables.addAll(list, elements);
			return list;
		}
	}

	private class DescendingIterator extends IteratorImpl {

		private DescendingIterator() {
			super();
			next = min;
		}

		@Override
		public E next() {
			checkForConcurrentModification();
			if (next == nil)
				throw new NoSuchElementException();
			last = next;
			next = predecessor(next);
			return last.element;
		}
	}

	/**
	 * Creates a new {@code TreeQueue} that orders its elements according to
	 * their <i>natural ordering</i>.
	 * 
	 * @return a new {@code TreeQueue} that orders its elements according to
	 *         their <i>natural ordering</i>
	 */
	public static <E extends Comparable<? super E>> TreeQueue<E> create() {
		return orderedBy(Ordering.natural()).create();
	}

	/**
	 * Creates a new {@code TreeQueue} containing the specified initial
	 * elements. If {@code elements} is an instance of {@link SortedSet} ,
	 * {@link PriorityQueue}, {@link MinMaxPriorityQueue}, or
	 * {@code SortedCollection} this queue will be ordered according to the same
	 * ordering. Otherwise, this queue will be ordered according to the
	 * <i>natural ordering</i> of its elements.
	 * 
	 * @param elements
	 *            the collection whose elements are to be placed into the list
	 * @return a new {@code TreeQueue} containing the elements of the specified
	 *         collection
	 * @throws ClassCastException
	 *             if elements of the specified collection cannot be compared to
	 *             one another according to this list's ordering
	 * @throws NullPointerException
	 *             if any of the elements of the specified collection or the
	 *             collection itself is {@code null}
	 */
	@SuppressWarnings({ "unchecked" })
	public static <E> TreeQueue<E> create(
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
		return orderedBy(comparator).create(elements);
	}

	/**
	 * Creates and returns a new builder, configured to build {@code TreeQueue}
	 * instances that use the specified comparator ordering.
	 * 
	 * @param comparator
	 *            the specified comparator
	 * @return a new building which builds {@code TreeQueue} instances that use
	 *         the specified comparator for ordering
	 */
	public static <B> Builder<B> orderedBy(final Comparator<B> comparator) {
		checkNotNull(comparator);
		return new Builder<B>(comparator);
	}

	private TreeQueue(final Comparator<? super E> comparator) {
		super(comparator);
	}

	@Override
	public boolean add(E e) {
		if (offer(e))
			return true;
		else
			throw new IllegalStateException("Queue full");
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		checkNotNull(c);
		checkState(c != this);
		boolean modified = false;
		for (E e : c)
			if (add(e))
				modified = true;
		return modified;
	}

	/**
	 * Returns a shallow copy of this {@code TreeQueue}. The elements themselves
	 * are not cloned.
	 * 
	 * @return a shallow copy of this queue
	 */
	@SuppressWarnings("unchecked")
	@Override
	public TreeQueue<E> clone() {
		TreeQueue<E> clone;
		try {
			clone = (TreeQueue<E>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
		clone.nil = new Node();
		clone.modCount = 0;
		clone.root = clone.nil;
		clone.min = clone.nil;
		clone.max = clone.nil;
		clone.size = 0;
		clone.addAll(this);
		return clone;
	}

	/**
	 * Returns an iterator over the elements of this queue in reverse order from
	 * last (tail) to first (head).
	 * 
	 * @return an iterator over the elements of this queue in reverse order
	 */
	public Iterator<E> descendingIterator() {
		return new DescendingIterator();
	}

	@Override
	public E element() {
		E x = peek();
		if (x != null)
			return x;
		else
			throw new NoSuchElementException();
	}

	@Override
	public boolean offer(E e) {
		checkNotNull(e);
		final Node newNode = new Node(e);
		insert(newNode);
		return true;
	}

	@Override
	public E peek() {
		if (isEmpty())
			return null;
		return min.element;
	}

	/**
	 * Retrieves, but does not remove, the last element of this queue, or
	 * returns {@code null} if this queue is empty.
	 * 
	 * @return the last element of this queue, or {@code null} if this queue is
	 *         empty
	 */
	public E peekLast() {
		if (isEmpty())
			return null;
		return max.element;
	}

	@Override
	public E poll() {
		if (isEmpty())
			return null;
		final E e = min.element;
		delete(min);
		return e;
	}

	/**
	 * Retrieves and removes the last element of this queue, or returns
	 * {@code null} if this queue is empty.
	 * 
	 * @return the last element of this queue, or {@code null} if this queue is
	 *         empty
	 */
	public E pollLast() {
		if (isEmpty())
			return null;
		final E e = max.element;
		delete(max);
		return e;
	}

	@Override
	public E remove() {
		E x = poll();
		if (x != null)
			return x;
		else
			throw new NoSuchElementException();
	}

	/**
	 * Retrieves and removes the last element of this queue. This method differs
	 * from {@link #pollLast pollLast()} only in that it throws an exception if
	 * this queue is empty.
	 * 
	 * @return the last element of this queue
	 * @throws NoSuchElementException
	 *             if this queue is empty
	 */
	public E removeLast() {
		final E e = pollLast();
		if (e != null)
			return e;
		else
			throw new NoSuchElementException();
	}

}