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

import static com.google.common.base.Preconditions.*;

import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.SortedSet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

/**
 * <pre>
 * An unbounded double-ended priority queue (also known as a {@link Deque})
 * based on a modified <a
 * href="http://en.wikipedia.org/wiki/Red-black_tree">Red-Black Tree</a>. The
 * elements of this deque are sorted according to their <i>natural ordering</i>,
 * or by an explicit {@link Comparator} provided at creation. Inserting
 * {@code null} elements will fail cleanly and safely leaving this deque
 * unmodified. Querying for {@code null} elements is allowed. Attempting to
 * insert non-comparable elements will result in a {@code ClassCastException} .
 * The {@code addFirst(E)}, {@code addLast(E)}, {@code offerFirst(E)},
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
 * concurrently it must be synchronized externally.
 * <p>
 * <b>Implementation Note:</b> This implementation uses a comparator (whether or
 * not one is explicitly provided) to maintain priority order, and
 * {@code equals} when testing for element equality. The ordering imposed by the
 * comparator is not required to be <i>consistent with equals</i>. Given a
 * comparator {@code c}, for any two elements {@code e1} and {@code e2} such
 * that {@code c.compare(e1, e2) == 0} it is not necessarily true that
 * {@code e1.equals(e2) == true}.
 * <p>
 * The underlying Red-Black Tree provides the following worst case running time
 * (where <i>n</i> is the size of this list and <i>m</i> is the size of the
 * specified collection):
 * <p>
 * <table border="1" cellpadding="3" cellspacing="1" style="width:400px;">
 *   <tr>
 *     <th style="text-align:center;">Method</th>
 *     <th style="text-align:center;">Running Time</th>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #addAll(Collection) addAll(Collection)}<br>
 *       {@link #containsAll(Collection) containsAll(Collection)}</br>
 *       {@link #retainAll(Collection) retainAll(Collection)}</br>
 *       {@link #removeAll(Collection) removeAll(Collection)}
 *     </td>
 *     <td style="text-align:center;">
 *       <i>O(m log n)</i>
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #add(Object) add(E)}</br>
 *       {@link #contains(Object) contains(Object)}</br>
 *       {@link #offer(Object) offer(E)}</br>
 *       {@link #remove(Object) remove(Object)}
 *     </td>
 *     <td style="text-align:center;">
 *       <i>O(log n)</i></td>
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
 *       {@link #clear()}
 *     </td>
 *     <td style="text-align:center;"><i>O(1)</i></td>
 *   </tr>
 * </table>
 * <p>
 * Refer to {@link OLDTreeQueue} for a comparison with {@link PriorityQueue
 * java.util.PriorityQueue}.
 * <p>
 * Note: This deque uses the same ordering rules as
 * {@code java.util.PriorityQueue}. In comparison it offers element operations
 * at both ends, ordered traversals via its iterators, and faster overall
 * running time.
 * 
 * @author Zhenya Leonov
 * @param <E>
 *            the type of elements held in this deque
 */
public class TreeDeque<E> extends AbstractTree<E> implements Deque<E>,
		SortedCollection<E> {

	private TreeDeque(final Comparator<? super E> comparator) {
		super(comparator);
	}

	private TreeDeque(final Comparator<? super E> comparator,
			final Iterable<? extends E> elements) {
		super(comparator);
		Iterables.addAll(this, elements);
	}

	/**
	 * Creates a new {@code TreeQueue} that orders its elements according to
	 * their <i>natural ordering</i>.
	 * 
	 * @return a new {@code TreeQueue} that orders its elements according to
	 *         their <i>natural ordering</i>
	 */
	public static <E extends Comparable<? super E>> TreeDeque<E> create() {
		return new TreeDeque<E>(Ordering.natural());
	}

	/**
	 * Creates a new {@code TreeQueue} that orders its elements according to the
	 * specified comparator.
	 * 
	 * @param comparator
	 *            the comparator that will be used to order this queue
	 * @return a new {@code TreeQueue} that orders its elements according to
	 *         {@code comparator}
	 */
	public static <E> TreeDeque<E> create(
			final Comparator<? super E> comparator) {
		checkNotNull(comparator);
		return new TreeDeque<E>(comparator);
	}

	/**
	 * Creates a new {@code TreeQueue} containing the elements of the specified
	 * {@code Iterable}. If the specified iterable is an instance of
	 * {@link SortedSet}, {@link PriorityQueue}, or {@code SortedCollection}
	 * this queue will be ordered according to the same ordering. Otherwise,
	 * this queue will be ordered according to the <i>natural ordering</i> of
	 * its elements.
	 * 
	 * @param elements
	 *            the iterable whose elements are to be placed into the queue
	 * @return a new {@code TreeQueue} containing the elements of the specified
	 *         iterable
	 * @throws ClassCastException
	 *             if elements of the specified iterable cannot be compared to
	 *             one another according to this queue's ordering
	 * @throws NullPointerException
	 *             if any of the elements of the specified iterable or the
	 *             iterable itself is {@code null}
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <E> TreeDeque<E> create(
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
		return new TreeDeque<E>(comparator, elements);
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public Object[] toArray() {
		// is this correct?
		return ImmutableList.copyOf(this).toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// is this correct?
		checkNotNull(a);
		return ImmutableList.copyOf(this).toArray(a);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		checkNotNull(c);
		if (c != this)
			for (Object e : c)
				if (!contains(e))
					return false;
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		checkNotNull(c);
		checkArgument(c != this);
		boolean mod = false;
		for (E e : c)
			if (add(e))
				mod = true;
		return mod;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		checkNotNull(c);
		checkArgument(c != this);
		// rewrite
		boolean flag = false;
		Iterator<E> it = iterator();
		while (it.hasNext()) {
			if (c.contains(it.next())) {
				it.remove();
				flag = true;
			}
		}
		return flag;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		checkNotNull(c);
		// rewrite
		boolean flag = false;
		if (c != this) {
			Iterator<E> it = iterator();
			while (it.hasNext())
				if (!c.contains(it.next())) {
					it.remove();
					flag = true;
				}
		}
		return flag;
	}

	@Override
	public void clear() {
		modCount++;
		root = nil;
		min = nil;
		max = nil;
		size = 0;
	}

	/**
	 * Returns the comparator used to order the elements in this deque. If one
	 * was not explicitly provided a <i>natural order</i> comparator is
	 * returned.
	 * 
	 * @return the comparator used to order this queue
	 */
	@Override
	public Comparator<? super E> comparator() {
		return comparator;
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
	public boolean remove(Object o) {
		checkNotNull(o);
		@SuppressWarnings("unchecked")
		final Node<E> node = search((E) o);
		if (node == null)
			return false;
		delete(node);
		return true;
	}

	@Override
	public E removeFirst() {
		return remove();
	}

	@Override
	public E removeLast() {
		final E e = pollLast();
		if (e != null)
			return e;
		else
			throw new NoSuchElementException();
	}

	@Override
	public E pollFirst() {
		return poll();
	}

	@Override
	public E pollLast() {
		if (isEmpty())
			return null;
		final E e = max.element;
		delete(max);
		return e;
	}

	@Override
	public E getFirst() {
		return element();
	}

	@Override
	public E getLast() {
		final E e = peekLast();
		if (e != null)
			return e;
		else
			throw new NoSuchElementException();
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

	@Override
	public boolean add(E e) {
		if (offer(e))
			return true;
		else
			throw new IllegalStateException();
	}

	@Override
	public boolean offer(E e) {
		checkNotNull(e);
		final Node<E> newNode = new Node<E>(e);
		insert(newNode);
		return true;
	}

	@Override
	public E poll() {
		if (isEmpty())
			return null;
		final E e = min.element;
		delete(min);
		return e;
	}

	@Override
	public E remove() {
		if (isEmpty())
			throw new NoSuchElementException();
		return poll();
	}

	@Override
	public E element() {
		final E e = peek();
		if (e == null)
			throw new NoSuchElementException();
		return e;
	}

	@Override
	public E peek() {
		if (isEmpty())
			return null;
		return min.element;
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
	public E pop() {
		return remove();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(Object o) {
		return o != null && search((E) o) != null;
	}

	@Override
	public int size() {
		return size;
	}

	/**
	 * Returns an iterator over the elements of this deque in priority order
	 * from first (head) to last (tail).
	 * 
	 * @return an iterator over the elements of this deque in priority order
	 */
	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private Node<E> next = min;
			private Node<E> last = nil;
			private int expectedModCount = modCount;

			@Override
			public boolean hasNext() {
				return next != nil;
			}

			@Override
			public void remove() {
				checkForConcurrentModification();
				if (last == nil)
					throw new IllegalStateException();
				if (last.left != nil && last.right != nil)
					next = last;
				delete(last);
				expectedModCount = modCount;
				last = nil;
			}

			@Override
			public E next() {
				checkForConcurrentModification();
				if (next == nil)
					throw new NoSuchElementException();
				last = next;
				next = successor(next);
				return last.element;
			}

			private void checkForConcurrentModification() {
				if (modCount != expectedModCount)
					throw new ConcurrentModificationException();
			}
		};
	}

	/**
	 * Returns an iterator over the elements of this deque in reverse order from
	 * last (tail) to first (head).
	 * 
	 * @return an iterator over the elements of this deque in reverse order
	 */
	@Override
	public Iterator<E> descendingIterator() {
		return new Iterator<E>() {
			private Node<E> next = max;
			private Node<E> last = nil;
			private int expectedModCount = modCount;

			@Override
			public boolean hasNext() {
				return next != null;
			}

			@Override
			public void remove() {
				checkForConcurrentModification();
				if (last == null)
					throw new IllegalStateException();
				if (last.left != nil && last.right != nil)
					next = last;
				delete(last);
				expectedModCount = modCount;
				last = nil;
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

			private void checkForConcurrentModification() {
				if (modCount != expectedModCount)
					throw new ConcurrentModificationException();
			}
		};
	}

	@Override
	Node<E> search(final E e) {
		Node<E> n = root;
		while (n != nil) {
			int cmp = comparator.compare(e, n.element);
			if (e.equals(n.element))
				return n;
			if (cmp < 0)
				n = n.left;
			else
				n = n.right;
		}
		return null;
	}

}