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

import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;
import static com.google.common.base.Preconditions.checkPositionIndexes;

import java.io.NotSerializableException;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.SortedSet;

import com.google.common.collect.Iterables;
import com.google.common.collect.MinMaxPriorityQueue;
import com.google.common.collect.Ordering;

/**
 * A {@link Sortedlist} implementation, based on a modified <a
 * href="http://en.wikipedia.org/wiki/Red-black_tree">Red-Black Tree</a>.
 * Elements are sorted from <i>least</i> to <i>greatest</i> according to their
 * <i>natural ordering</i>, or by an explicit {@link Comparator} provided at
 * creation. Attempting to remove or insert {@code null} elements is prohibited.
 * Querying for {@code null} elements is allowed. Inserting non-comparable
 * elements will result in a {@code ClassCastException}.
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
 * The underlying Red-Black Tree provides the following worst case running time
 * (where <i>n</i> is the size of this sorted-list and <i>m</i> is the size of
 * the specified collection which is iterable in linear time):
 * <p>
 * 
 * <pre>
 * <table border="1" cellpadding="3" cellspacing="1" style="width:400px;">
 *   <tr>
 *     <th style="text-align:center;">Method</th>
 *     <th style="text-align:center;">Running Time</th>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #addAll(Collection) addAll(Collection)}</br>
 *       {@link #containsAll(Collection) containsAll(Collection)}</br>
 *       {@link #retainAll(Collection) retainAll(Collection)}</br>
 *       {@link #removeAll(Collection) removeAll(Collection)}
 *     </td>
 *     <td style="text-align:center;"><i>O(m log n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #indexOf(Object)}</br>
 *       {@link #lastIndexOf(Object)}</br>
 *       {@link #get(int)}</br>
 *       {@link #remove(int)}</br>
 *       {@link #listIterator(int)}
 *     </td>
 *     <td style="text-align:center;"><i>O(n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #add(Object) add(E)}</br>
 *       {@link #contains(Object)}</br>
 *       {@link #remove(Object)}
 *     </td>
 *     <td style="text-align:center;"><i>O(log n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #clear() clear()}</br>
 *       {@link #isEmpty() isEmpty()}</br>
 *       {@link #size()}</br>
 *       {@link Iterator#remove()}</br>
 *       {@link ListIterator#remove()}
 *     </td>
 *     <td style="text-align:center;"><i>O(1)</i></td>
 *   </tr>
 * </table>
 * <p>
 * The sub-list views exhibit identical time complexity, with the exception of
 * the {@code clear()} operation which runs in linear time proportional to the
 * size of the views.
 * 
 * @author Zhenya Leonov
 * @param <E>
 *            the type of elements maintained by this list
 * @see Skiplist
 */
class Treelist2<E> extends SortedCollectionImpl<E> implements Sortedlist<E> {

	private static final long serialVersionUID = 1L;

	private Treelist2(final Comparator<? super E> comparator) {
		super(comparator);
	}

	/**
	 * Creates a new {@code Treelist} that orders its elements according to
	 * their <i>natural ordering</i>.
	 * 
	 * @return a new {@code Treelist} that orders its elements according to
	 *         their <i>natural ordering</i>
	 */
	public static <E extends Comparable<? super E>> Treelist2<E> create() {
		return new Treelist2<E>(Ordering.natural());
	}

	/**
	 * Creates a new {@code Treelist} containing the specified initial elements.
	 * If {@code elements} is an instance of {@link SortedSet},
	 * {@link PriorityQueue}, {@link MinMaxPriorityQueue}, or
	 * {@code SortedCollection}, this list will be ordered according to the same
	 * ordering. Otherwise, this list will be ordered according to the
	 * <i>natural ordering</i> of its elements.
	 * 
	 * @param elements
	 *            the collection whose elements are to be placed into the list
	 * @return a new {@code Treelist} containing the elements of the specified
	 *         collection
	 * @throws ClassCastException
	 *             if elements of the specified collection cannot be compared to
	 *             one another according to this list's ordering
	 * @throws NullPointerException
	 *             if any of the elements of the specified collection or the
	 *             collection itself is {@code null}
	 */
	@SuppressWarnings({ "unchecked" })
	public static <E> Treelist2<E> create(final Collection<? extends E> elements) {
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
	 * Creates and returns a new builder, configured to build {@code Treelist}
	 * instances that use the specified comparator ordering.
	 * 
	 * @param comparator
	 *            the specified comparator
	 * @return a new building which builds {@code Treelist} instances that use
	 *         the specified comparator for ordering
	 */
	public static <B> Builder<B> orderedBy(final Comparator<B> comparator) {
		checkNotNull(comparator);
		return new Builder<B>(comparator);
	}

	/**
	 * A builder for the creation of {@code Treelist} instances. Instances of
	 * this builder are obtained calling {@link Treelist2#orderedBy(Comparator)}
	 * .
	 * 
	 * @author Zhenya Leonov
	 * @param <B>
	 *            the upper bound of the type of queues this builder can produce
	 *            (for example a {@code Builder<Number>} can produce a
	 *            {@code Treelist<Float>} or a {@code Treelist<Integer>}
	 */
	public static final class Builder<B> {

		private final Comparator<B> comparator;

		private Builder(final Comparator<B> comparator) {
			this.comparator = comparator;
		}

		/**
		 * Builds an empty {@code Treelist} using the previously specified
		 * comparator.
		 * 
		 * @return an empty {@code Treelist} using the previously specified
		 *         comparator.
		 */
		public <T extends B> Treelist2<T> create() {
			return new Treelist2<T>(comparator);
		}

		/**
		 * Builds a new {@code Treelist} using the previously specified
		 * comparator, and having the given initial elements.
		 * 
		 * @param elements
		 *            the initial elements to be placed in this queue
		 * @return a new {@code Treelist} using the previously specified
		 *         comparator, and having the given initial elements
		 */
		public <T extends B> Treelist2<T> create(
				final Iterable<? extends T> elements) {
			checkNotNull(elements);
			final Treelist2<T> list = new Treelist2<T>(comparator);
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

	@Override
	public E get(int index) {
		checkElementIndex(index, size);
		Iterator<E> itor = iterator();
		for (int i = 0; i < index; i++)
			itor.next();
		return itor.next();
	}

	@Override
	public int indexOf(Object o) {
		if (o != null) {
			@SuppressWarnings("unchecked")
			E e = (E) o;
			ListIterator<E> itor = listIterator();
			while (itor.hasNext())
				if (comparator.compare(itor.next(), e) == 0)
					return itor.previousIndex();
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		if (o != null) {
			@SuppressWarnings("unchecked")
			E e = (E) o;
			ListIterator<E> itor = listIterator();
			while (itor.hasNext())
				if (comparator.compare(itor.next(), e) == 0) {
					while (itor.hasNext()
							&& comparator.compare(itor.next(), e) == 0)
						;
					return itor.previousIndex();
				}
		}
		return -1;
	}

	@Override
	public Iterator<E> iterator() {
		return listIterator();
	}

	@Override
	public ListIterator<E> listIterator() {
		return new ListIteratorImpl();
	}

	private class ListIteratorImpl extends IteratorImpl implements
			ListIterator<E> {
		private int index = 0;
		private Node prev = nil;

		@Override
		public void add(E e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasNext() {
			return index < size();
		}

		@Override
		public boolean hasPrevious() {
			return index > 0;
		}

		@Override
		public E next() {
			E e = super.next();
			index++;
			return e;
		}

		@Override
		public int nextIndex() {
			return index;
		}

		@Override
		public E previous() {
			super.checkForConcurrentModification();
			if (index == 0)
				throw new NoSuchElementException();
			Node node = next = prev;
			index--;
			prev = predecessor(node);
			last = node;
			return node.element;
		}

		@Override
		public int previousIndex() {
			return index - 1;
		}

		@Override
		public void remove() {
			super.remove();
			index--;
		}

		@Override
		public void set(E e) {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		checkPositionIndex(index, size);
		ListIterator<E> listIterator = listIterator();
		for (int i = 0; i < index; i++)
			listIterator.next();
		return listIterator;
	}

	@Override
	public E remove(int index) {
		checkElementIndex(index, size);
		ListIterator<E> li = listIterator(index);
		E e = li.next();
		li.remove();
		return e;
	}

	@Override
	public int size() {
		return size;
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
	public Treelist2<E> subList(int fromIndex, int toIndex) {
		checkPositionIndexes(fromIndex, toIndex, size());
		return new SubList(this, fromIndex, toIndex);
	}

	@SuppressWarnings("serial")
	private class SubList extends Treelist2<E> {
		private final Treelist2<E> list;
		private final int offset;
		private Node from;
		private Node to;

		private void checkForConcurrentModification() {
			if (modCount != list.modCount)
				throw new ConcurrentModificationException();
		}

		public SubList(Treelist2<E> list, int fromIndex, int toIndex) {
			super(list.comparator);
			this.list = list;
			from = list.min;
			offset = fromIndex;
			modCount = list.modCount;
			size = toIndex - fromIndex;
			int i = 0;
			for (; i < fromIndex; i++)
				from = successor(from);
			to = from;
			for (; i < toIndex - 1; i++)
				to = successor(to);
		}

		@Override
		public boolean add(E e) {
			checkForConcurrentModification();
			if (comparator.compare(e, from.element) < 0
					|| comparator.compare(e, to.element) > 0)
				throw new IllegalArgumentException("element out of range");
			list.add(e);
			modCount = list.modCount;
			size++;
			if (comparator.compare(to.element, e) <= 0)
				to = successor(to);
			return true;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean contains(Object o) {
			checkForConcurrentModification();
			return o != null && search((E) o) != null;
		}

		@Override
		public E get(int index) {
			checkForConcurrentModification();
			checkElementIndex(index, size);
			return list.get(index + offset);
		}

		@Override
		public ListIterator<E> listIterator() {
			return listIterator(0);
		}

		@Override
		public ListIterator<E> listIterator(final int index) {
			checkForConcurrentModification();
			checkPositionIndex(index, size);
			return new ListIterator<E>() {
				private ListIterator<E> i = list.listIterator(index + offset);

				@Override
				public boolean hasNext() {
					return nextIndex() < size;
				}

				@Override
				public E next() {
					if (hasNext())
						return i.next();
					else
						throw new NoSuchElementException();
				}

				@Override
				public boolean hasPrevious() {
					return previousIndex() >= 0;
				}

				@Override
				public E previous() {
					if (hasPrevious())
						return i.previous();
					else
						throw new NoSuchElementException();
				}

				@Override
				public int nextIndex() {
					return i.nextIndex() - offset;
				}

				@Override
				public int previousIndex() {
					return i.previousIndex() - offset;
				}

				@Override
				public void remove() {
					i.remove();
					modCount = list.modCount;
					size--;
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

		@SuppressWarnings("unchecked")
		@Override
		public boolean remove(Object o) {
			checkForConcurrentModification();
			checkNotNull(o);
			final Node node = search((E) o);
			if (node == null)
				return false;
			if (node == to)
				to = predecessor(to);
			if (node == from)
				from = successor(from);
			list.delete(node);
			modCount = list.modCount;
			size--;
			return true;
		}

		@Override
		public E remove(int index) {
			checkForConcurrentModification();
			checkElementIndex(index, size);
			if (index == 0)
				from = successor(from);
			if (index == size - 1)
				to = predecessor(to);
			E e = list.remove(index + offset);
			modCount = list.modCount;
			size--;
			return e;
		}

		@Override
		public int size() {
			checkForConcurrentModification();
			return size;
		}

		@Override
		public void clear() {
			checkForConcurrentModification();
			final Iterator<E> iterator = iterator();
			while (iterator.hasNext())
				iterator.remove();
		}

		@Override
		public Treelist2<E> clone() throws CloneNotSupportedException {
			throw new CloneNotSupportedException();
		}

		private void writeObject(java.io.ObjectOutputStream oos)
				throws NotSerializableException {
			throw new NotSerializableException();
		}

		private void readObject(java.io.ObjectInputStream ois)
				throws NotSerializableException {
			throw new NotSerializableException();
		}

		// Red-Black-Tree

		@Override
		protected Node search(final E e) {
			int compareFrom = comparator.compare(e, from.element);
			int compareTo = comparator.compare(e, to.element);
			if (compareFrom < 0 || compareTo > 0)
				return null;
			if (compareFrom == 0)
				return from;
			else if (compareTo == 0)
				return to;
			else
				return list.search(e);
		}
	}

}