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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;
import static com.google.common.base.Preconditions.checkPositionIndexes;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.SortedSet;

import com.google.common.collect.Iterables;
import com.google.common.collect.MinMaxPriorityQueue;
import com.google.common.collect.Ordering;

/**
 * A {@link Sortedlist} implementation based on a modified <a
 * href="http://en.wikipedia.org/wiki/Skip_list">skip list</a>. Elements are
 * sorted from <i>least</i> to <i>greatest</i> according to their <i>natural
 * ordering</i>, or by an explicit {@link Comparator} provided at creation.
 * Attempting to remove or insert {@code null} elements is prohibited. Querying
 * for {@code null} elements is allowed. Inserting non-comparable elements will
 * result in a {@code ClassCastException}.
 * <p>
 * The iterators obtained from the {@link #iterator()} and
 * {@link #listIterator()} methods are <i>fail-fast</i>. Attempts to modify the
 * elements in this list at any time after an iterator is created, in any way
 * except through the iterator's own remove method, will result in a
 * {@code ConcurrentModificationException}. Further, the list iterator does not
 * support the {@code add(E)} and {@code set(E)} operations.
 * <p>
 * This sorted-list is not <i>thread-safe</i>. If multiple threads modify this
 * sorted-list concurrently it must be synchronized externally.
 * <p>
 * This implementation uses a comparator (whether or not one is explicitly
 * provided) to perform all element comparisons. Two elements which are deemed
 * equal by the comparator's {@code compare(E, E)} method are, from the
 * standpoint of this list, equal. Further, no guarantee is made as to the final
 * order of <i>equal</i> elements. Ties may be broken arbitrarily.
 * <p>
 * The underlying array-based <a
 * href="http://en.wikipedia.org/wiki/Skip_list">skip list</a> is modified to
 * provide logarithmic running time for insertion, removal, and <a
 * href="http://en.wikipedia.org/wiki/Random_access">random access</a> lookup
 * operations (e.g. get the element at the i<i>th</i> index).
 * <p>
 * Invented by <a href="http://www.cs.umd.edu/~pugh/">Bill Pugh</a> in 1990, A
 * skip list is a probabilistic data structure for maintaining items in sorted
 * order. Strictly speaking it is impossible to make any hard guarantees
 * regarding the worst-case performance of this class. Practical performance is
 * <i>expected</i> to be logarithmic with an extremely high degree of
 * probability as the list grows.
 * <p>
 * The following table summarizes the performance of this class compared to a
 * {@link Treelist} (where n is the size of this sorted-list and <i>m</i> is the
 * size of the specified collection which is iterable in linear time):
 * <p>
 * <table border="1" cellpadding="3" cellspacing="1" style="width:400px;">
 *   <tr>
 *     <th style="text-align:center;" rowspan="2">Method</th>
 *     <th style="text-align:center;" colspan="2">Running Time</th>
 *   </tr>
 *   <tr>
 *     <td style="text-align:center;"><b>Skiplist</b><br>(<i>expected</i>)</td>
 *     <td style="text-align:center;"><b>Treelist</b><br>(<i>worst-case</i>)</td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #addAll(Collection) addAll(Collection)}<br/>
 *       {@link #containsAll(Collection) containsAll(Collection)}<br/>
 *       {@link #retainAll(Collection) retainAll(Collection)}<br/>
 *       {@link #removeAll(Collection) removeAll(Collection)}
 *     </td>
 *     <td style="text-align:center;" colspan="2"><i>O(m log n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #indexOf(Object)}<br/>
 *       {@link #lastIndexOf(Object)}<br/>
 *       {@link #get(int)}<br/>
 *       {@link #remove(int)}<br/>
 *       {@link #listIterator(int)}
 *     </td>
 *     <td style="text-align:center;" bgcolor="FFCC99"><i>O(log n)</i></td>
 *     <td style="text-align:center;" bgcolor="FFCCCC"><i>O(n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #add(Object) add(E)}<br/>
 *       {@link #contains(Object)}<br/>
 *       {@link #remove(Object)}
 *     </td>
 *     <td style="text-align:center;" colspan="2"><i>O(log n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #clear() clear()}<br/>
 *       {@link #isEmpty() isEmpty()}<br/>
 *       {@link #size()}<br/>
 *     </td>
 *     <td style="text-align:center;" colspan="2"><i>O(1)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link Iterator#remove()}<br/>
 *       {@link ListIterator#remove()}
 *     </td>
 *     <td style="text-align:center;" bgcolor="FFCC99"><i>O(log n)</i></td>
 *     <td style="text-align:center;" bgcolor="FFCCCC"><i>O(1)</i></td>
 *   </tr>
 * </table>
 * <p>
 * The sub-list views exhibit identical time complexity, with the
 * exception of the {@code clear()} operation which runs in linear time
 * proportional to the size of the view.
 * 
 * @author Zhenya Leonov
 * @param <E>
 *            the type of elements maintained by this list
 * @see Treelist
 */
public class Skiplist<E> extends AbstractCollection<E> implements
		Sortedlist<E>, Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	private static final double P = .5;
	private static final int MAX_LEVEL = 32;
	transient int size = 0;
	private transient int level = 1;
	private transient Random random = new Random();
	private transient Node head = new Node(null, MAX_LEVEL);
	private final Comparator<? super E> comparator;
	@SuppressWarnings("unchecked")
	private transient Node[] update = (Node[]) new Object[MAX_LEVEL];
	private transient int[] index = new int[MAX_LEVEL];
	transient int modCount = 0;

	private Skiplist(final Comparator<? super E> comparator) {
		this.comparator = comparator;
		for (int i = 0; i < MAX_LEVEL; i++) {
			head.next[i] = head;
			head.dist[i] = 1;
		}
		head.prev = head;
	}

	/**
	 * Creates a new {@code Skiplist} that orders its elements according to
	 * their <i>natural ordering</i>.
	 * 
	 * @return a new {@code Skiplist} that orders its elements according to
	 *         their <i>natural ordering</i>
	 */
	public static <E extends Comparable<? super E>> Skiplist<E> create() {
		return new Skiplist<E>(Ordering.natural());
	}

	/**
	 * Creates a new {@code Skiplist} containing the specified initial elements.
	 * If {@code elements} is an instance of {@link SortedSet},
	 * {@link PriorityQueue}, {@link MinMaxPriorityQueue}, or
	 * {@code SortedCollection}, this list will be ordered according to the same
	 * ordering. Otherwise, this list will be ordered according to the
	 * <i>natural ordering</i> of its elements.
	 * 
	 * @param elements
	 *            the collection whose elements are to be placed into the list
	 * @return a new {@code Skiplist} containing the elements of the specified
	 *         collection
	 * @throws ClassCastException
	 *             if elements of the specified collection cannot be compared to
	 *             one another according to this list's ordering
	 * @throws NullPointerException
	 *             if any of the elements of the specified collection or the
	 *             collection itself is {@code null}
	 */
	@SuppressWarnings({ "unchecked" })
	public static <E> Skiplist<E> create(final Collection<? extends E> elements) {
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
	 * Creates and returns a new builder, configured to build {@code Skiplist}
	 * instances that use the specified comparator ordering.
	 * 
	 * @param comparator
	 *            the specified comparator
	 * @return a new building which builds {@code Skiplist} instances that use
	 *         the specified comparator for ordering
	 */
	public static <B> Builder<B> orderedBy(final Comparator<B> comparator) {
		checkNotNull(comparator);
		return new Builder<B>(comparator);
	}

	/**
	 * A builder for the creation of {@code Skiplist} instances. Instances of
	 * this builder are obtained calling {@link Skiplist#orderedBy(Comparator)}.
	 * 
	 * @author Zhenya Leonov
	 * @param <B>
	 *            the upper bound of the type of queues this builder can produce
	 *            (for example a {@code Builder<Number>} can produce a
	 *            {@code Skiplist<Float>} or a {@code Skiplist<Integer>}
	 */
	public static final class Builder<B> {

		private final Comparator<B> comparator;

		private Builder(final Comparator<B> comparator) {
			this.comparator = comparator;
		}

		/**
		 * Builds an empty {@code Skiplist} using the previously specified
		 * comparator.
		 * 
		 * @return an empty {@code Skiplist} using the previously specified
		 *         comparator.
		 */
		public <T extends B> Skiplist<T> create() {
			return new Skiplist<T>(comparator);
		}

		/**
		 * Builds a new {@code Skiplist} using the previously specified
		 * comparator, and having the given initial elements.
		 * 
		 * @param elements
		 *            the initial elements to be placed in this queue
		 * @return a new {@code Skiplist} using the previously specified
		 *         comparator, and having the given initial elements
		 */
		public <T extends B> Skiplist<T> create(
				final Iterable<? extends T> elements) {
			checkNotNull(elements);
			final Skiplist<T> list = new Skiplist<T>(comparator);
			Iterables.addAll(list, elements);
			return list;
		}
	}

	/**
	 * Returns the comparator used to order the elements in this list. If one
	 * was not explicitly provided a <i>natural order</i> comparator is
	 * returned.
	 * 
	 * @return the comparator used to order this list
	 */
	@Override
	public Comparator<? super E> comparator() {
		return comparator;
	}

	/**
	 * Inserts the specified element into this list in sorted order.
	 */
	@Override
	public boolean add(E e) {
		checkNotNull(e);
		final int newLevel = randomLevel();
		Node x = head;
		Node y = head;
		int i;
		int idx = 0;
		for (i = level - 1; i >= 0; i--) {
			while (x.next[i] != y
					&& comparator.compare(x.next[i].element, e) < 0) {
				idx += x.dist[i];
				x = x.next[i];
			}
			y = x.next[i];
			update[i] = x;
			index[i] = idx;
		}
		if (newLevel > level) {
			for (i = level; i < newLevel; i++) {
				head.dist[i] = size + 1;
				update[i] = head;
			}
			level = newLevel;
		}
		x = new Node(e, newLevel);
		for (i = 0; i < level; i++) {
			if (i > newLevel - 1)
				update[i].dist[i]++;
			else {
				x.next[i] = update[i].next[i];
				update[i].next[i] = x;
				x.dist[i] = index[i] + update[i].dist[i] - idx;
				update[i].dist[i] = idx + 1 - index[i];

			}
		}
		x.prev = update[0];
		x.next().prev = x;
		modCount++;
		size++;
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean contains(Object o) {
		return o != null && search((E) o) != null;
	}

	@Override
	public E get(int index) {
		checkElementIndex(index, size);
		return search(index).element;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int indexOf(Object o) {
		if (o != null) {
			Node curr = head;
			int idx = 0;
			final E element = (E) o;
			for (int i = level - 1; i >= 0; i--)
				while (curr.next[i] != head
						&& comparator.compare(curr.next[i].element, element) < 0) {
					idx += curr.dist[i];
					curr = curr.next[i];
				}
			curr = curr.next();
			if (curr != head && comparator.compare(curr.element, element) == 0)
				return idx;
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int lastIndexOf(Object o) {
		if (o != null) {
			Node curr = head;
			int idx = -1;
			final E element = (E) o;
			for (int i = level - 1; i >= 0; i--)
				while (curr.next[i] != head
						&& comparator.compare(curr.next[i].element, element) <= 0) {
					idx += curr.dist[i];
					curr = curr.next[i];
				}
			if (curr != head && comparator.compare(curr.element, element) == 0)
				return idx;
		}
		return -1;
	}

	@Override
	public Iterator<E> iterator() {
		return listIterator();
	}

	@Override
	public ListIterator<E> listIterator() {
		return listIterator(0);
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		checkPositionIndex(index, size);
		return new ListIteratorImpl(index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object o) {
		checkNotNull(o);
		final E element = (E) o;
		Node curr = head;
		for (int i = level - 1; i >= 0; i--) {
			while (curr.next[i] != head
					&& comparator.compare(curr.next[i].element, element) < 0)
				curr = curr.next[i];
			update[i] = curr;
		}
		curr = curr.next();
		if (curr == head || comparator.compare(curr.element, element) != 0)
			return false;
		delete(curr, update);
		return true;
	}

	@Override
	public E remove(int index) {
		checkElementIndex(index, size);
		Node curr = head;
		int idx = 0;
		for (int i = level - 1; i >= 0; i--) {
			while (idx + curr.dist[i] <= index) {
				idx += curr.dist[i];
				curr = curr.next[i];
			}
			update[i] = curr;
		}
		curr = curr.next();
		delete(curr, update);
		return curr.element;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void clear() {
		for (int i = 0; i < MAX_LEVEL; i++) {
			head.next[i] = head;
			head.dist[i] = 1;
		}
		head.prev = head;
		modCount++;
		size = 0;
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		for (E e : this)
			hashCode = 31 * hashCode + e.hashCode();
		return hashCode;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Sortedlist))
			return false;
		try {
			@SuppressWarnings("unchecked")
			final Iterator<E> i = ((Collection<E>) o).iterator();
			for (E e : this)
				if (comparator.compare(e, i.next()) != 0)
					return false;
			return !i.hasNext();
		} catch (ClassCastException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
	}

	@Override
	public Skiplist<E> sublist(int fromIndex, int toIndex) {
		checkPositionIndexes(fromIndex, toIndex, size);
		return new Sublist(this, fromIndex, toIndex);
	}

	/**
	 * Returns a shallow copy of this {@code Skiplist}. The elements themselves
	 * are not cloned.
	 * 
	 * @return a shallow copy of this skip list
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Skiplist<E> clone() throws CloneNotSupportedException {
		Skiplist<E> clone;
		try {
			clone = (Skiplist<E>) super.clone();
		} catch (java.lang.CloneNotSupportedException e) {
			throw new InternalError();
		}
		for (int i = 0; i < MAX_LEVEL; i++) {
			clone.head.next[i] = clone.head;
			clone.head.dist[i] = 1;
		}
		clone.head.prev = clone.head;
		clone.random = new Random();
		clone.level = 1;
		clone.modCount = 0;
		clone.size = 0;
		clone.addAll(this);
		return clone;
	}

	private void writeObject(java.io.ObjectOutputStream oos)
			throws java.io.IOException {
		oos.defaultWriteObject();
		oos.writeInt(size);
		for (E e : this)
			oos.writeObject(e);
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream ois)
			throws java.io.IOException, ClassNotFoundException {
		ois.defaultReadObject();
		head = new Node(null, MAX_LEVEL);
		for (int i = 0; i < MAX_LEVEL; i++) {
			head.next[i] = head;
			head.dist[i] = 1;
		}
		update = (Node[]) new Object[MAX_LEVEL];
		index = new int[MAX_LEVEL];
		head.prev = head;
		random = new Random();
		level = 1;
		int size = ois.readInt();
		for (int i = 0; i < size; i++)
			add((E) ois.readObject());
	}

	private class ListIteratorImpl implements ListIterator<E> {
		private Node node;
		private Node last = null;
		private final int offset;
		private int index = 0;
		private int expectedModCount = modCount;

		private ListIteratorImpl(final int index) {
			node = index == 0 ? head.next() : search(index);
			offset = index;
		}

		@Override
		public void add(E element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasNext() {
			return index + offset < size();
		}

		@Override
		public boolean hasPrevious() {
			return index + offset > 0;
		}

		@Override
		public E next() {
			checkForConcurrentModification();
			if (!hasNext())
				throw new NoSuchElementException();
			index++;
			last = node;
			node = node.next();
			return last.element;
		}

		@Override
		public int nextIndex() {
			return index + offset;
		}

		@Override
		public E previous() {
			checkForConcurrentModification();
			if (index == 0)
				throw new NoSuchElementException();
			index--;
			last = node = node.prev;
			return node.element;
		}

		@Override
		public int previousIndex() {
			return index + offset - 1;
		}

		@Override
		public void remove() {
			checkForConcurrentModification();
			checkState(last != null);
			Skiplist.this.remove(--index + offset);
			expectedModCount = modCount;
			last = null;
		}

		@Override
		public void set(E element) {
			throw new UnsupportedOperationException();
		}

		void checkForConcurrentModification() {
			if (expectedModCount != modCount)
				throw new ConcurrentModificationException();
		}
	}

	// skip list

	private class Node {
		private E element;
		private Node prev;
		private final Node[] next;
		private final int[] dist;

		@SuppressWarnings("unchecked")
		private Node(final E element, final int size) {
			this.element = element;
			next = (Node[]) new Object[size];
			dist = new int[size];
		}

		private Node next() {
			return next[0];
		}
	}

	private int randomLevel() {
		int randomLevel = 1;
		while (randomLevel < MAX_LEVEL - 1 && random.nextDouble() < P)
			randomLevel++;
		return randomLevel;
	}

	private boolean remove(final Node node) {
		Node curr = head;
		for (int i = level - 1; i >= 0; i--) {
			while (curr.next[i] != head && curr.next[i] != node)
				curr = curr.next[i];
			update[i] = curr;
		}
		curr = curr.next();
		delete(curr, update);
		return true;
	}

	private void delete(final Node node, final Node[] update) {
		for (int i = 0; i < level; i++)
			if (update[i].next[i] == node) {
				update[i].next[i] = node.next[i];
				update[i].dist[i] += node.dist[i] - 1;
			} else
				update[i].dist[i]--;
		node.next().prev = node.prev;
		while (head.next[level - 1] == head && level > 1)
			level--;
		modCount++;
		size--;
	}

	Node search(final E element) {
		Node curr = head;
		for (int i = level - 1; i >= 0; i--)
			while (curr.next[i] != head
					&& comparator.compare(curr.next[i].element, element) < 0)
				curr = curr.next[i];
		curr = curr.next();
		if (curr != head && comparator.compare(curr.element, element) == 0)
			return curr;
		return null;
	}

	private Node search(final int index) {
		Node curr = head;
		int idx = -1;
		for (int i = level - 1; i >= 0; i--)
			while (idx + curr.dist[i] <= index) {
				idx += curr.dist[i];
				curr = curr.next[i];
			}
		return curr;
	}

	@SuppressWarnings("serial")
	private final class Sublist extends Skiplist<E> {
		private final Skiplist<E> list;
		private int offset;
		private Node from;
		private Node to;

		public Sublist(final Skiplist<E> list, final int fromIndex,
				final int toIndex) {
			super(list.comparator);
			this.list = list;
			this.modCount = list.modCount;
			offset = fromIndex;
			this.size = toIndex - fromIndex;
			from = list.search(fromIndex);
			to = list.search(toIndex - 1);
		}

		@Override
		public Skiplist<E> clone() throws CloneNotSupportedException {
			throw new CloneNotSupportedException();
		}

		// do we need this?
		private void writeObject(java.io.ObjectOutputStream out)
				throws IOException {
			throw new NotSerializableException();
		}

		// do we need this?
		private void readObject(java.io.ObjectInputStream in)
				throws IOException, ClassNotFoundException {
			throw new NotSerializableException();
		}

		private void checkForConcurrentModification() {
			if (this.modCount != list.modCount)
				throw new ConcurrentModificationException();
		}

		@Override
		public int size() {
			checkForConcurrentModification();
			return this.size;
		}

		@Override
		public boolean add(E e) {
			checkForConcurrentModification();
			checkNotNull(e);
			checkArgument(inRange(e, from, to));
			list.add(e);
			this.modCount = list.modCount;
			this.size++;
			return true;

		}

		@Override
		public boolean remove(Object o) {
			checkForConcurrentModification();
			checkNotNull(o);
			@SuppressWarnings("unchecked")
			E e = (E) o;
			checkArgument(inRange(e, from, to));
			if (comparator.compare(e, to.element) == 0) {
				list.remove(to);
				to = to.prev;
			} else
				list.remove(e);
			this.modCount = list.modCount;
			this.size--;
			return true;
		}

		@Override
		public void clear() {
			checkForConcurrentModification();
			final Iterator<?> iterator = iterator();
			while (iterator.hasNext()) {
				iterator.next();
				iterator.remove();
			}
		}

		@Override
		public E get(int index) {
			checkForConcurrentModification();
			checkElementIndex(index, this.size);
			return list.get(index + offset);
		}

		@Override
		public E remove(int index) {
			checkForConcurrentModification();
			checkElementIndex(index, this.size);
			if (index == this.size - 1)
				to = to.prev;
			if (index == 0)
				from = from.next();
			final E e = list.remove(index + offset);
			this.modCount = list.modCount;
			this.size--;
			return e;
		}

		@Override
		public int indexOf(Object o) {
			checkForConcurrentModification();
			checkNotNull(o);
			@SuppressWarnings("unchecked")
			E e = (E) o;
			if (!inRange(e, from, to))
				return -1;
			if (comparator.compare(e, from.element) == 0)
				return 0;
			final int result = list.indexOf(e);
			return result == -1 ? -1 : result - offset;
		}

		@Override
		public int lastIndexOf(Object o) {
			checkForConcurrentModification();
			checkNotNull(o);
			@SuppressWarnings("unchecked")
			E e = (E) o;
			if (!inRange(e, from, to))
				return -1;
			if (comparator.compare(to.element, e) == 0)
				return this.size - 1;
			final int result = list.lastIndexOf(e);
			return result == -1 ? -1 : result - offset;
		}

		@Override
		public ListIterator<E> listIterator(final int index) {
			checkForConcurrentModification();
			checkPositionIndex(index, Sublist.this.size);
			return new ListIterator<E>() {
				final ListIterator<E> li = list.listIterator(index + offset);

				@Override
				public boolean hasNext() {
					return nextIndex() < Sublist.this.size;
				}

				@Override
				public E next() {
					if (hasNext())
						return li.next();
					else
						throw new NoSuchElementException();
				}

				@Override
				public boolean hasPrevious() {
					return previousIndex() >= 0;
				}

				public E previous() {
					if (hasPrevious())
						return li.previous();
					else
						throw new NoSuchElementException();
				}

				public int nextIndex() {
					return li.nextIndex() - offset;
				}

				public int previousIndex() {
					return li.previousIndex() - offset;
				}

				@Override
				public void remove() {
					li.remove();
					Sublist.this.modCount = list.modCount;
					Sublist.this.size--;
				}

				@Override
				public void set(E e) {
					throw new UnsupportedOperationException();

				}

				@Override
				public void add(E e) {
					throw new UnsupportedOperationException();
				}

			};
		}

		// @Override
		// public Iterator<E> iterator() {
		// // TODO Auto-generated method stub
		// return null;
		// }
		//
		// @Override
		// // TODO Auto-generated method stub
		// public ListIterator<E> listIterator() {
		// return null;
		// }
		//
		// @Override
		// public boolean isEmpty() {
		// // TODO Auto-generated method stub
		// return false;
		// }
		//
		// @Override
		// public boolean contains(Object o) {
		// // TODO Auto-generated method stub
		// return false;
		// }
		//
		// @Override
		// public Object[] toArray() {
		// // TODO Auto-generated method stub
		// return null;
		// }
		//
		// @Override
		// public <T> T[] toArray(T[] a) {
		// // TODO Auto-generated method stub
		// return null;
		// }
		//
		// @Override
		// public boolean containsAll(Collection<?> c) {
		// // TODO Auto-generated method stub
		// return false;
		// }
		//
		// @Override
		// public boolean addAll(Collection<? extends E> c) {
		// // TODO Auto-generated method stub
		// return false;
		// }
		//
		// @Override
		// public boolean removeAll(Collection<?> c) {
		// // TODO Auto-generated method stub
		// return false;
		// }
		//
		// @Override
		// public boolean retainAll(Collection<?> c) {
		// // TODO Auto-generated method stub
		// return false;
		// }

		@Override
		Node search(final E e) {
			checkForConcurrentModification();
			if (!inRange(e, from, to))
				return null;
			if (comparator.compare(e, from.element) == 0)
				return from;
			if (comparator.compare(e, to.element) == 0)
				return to;
			return list.search(e);
		}

		private boolean inRange(final E e, final Node from, final Node to) {
			return (comparator.compare(from.element, e) < 1 && comparator
					.compare(e, to.element) < 1);
		}

	}

}