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

package com.googlecode.biscotti.collect;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Static utility methods which operate on or return {@link Collection}s and
 * {@link Map}s.
 * 
 * @author Zhenya Leonov
 */
public final class Collections3 {

	private Collections3() {
	}

	/**
	 * Returns the index of the first occurrence in the specified list of an
	 * element which satisfies the given predicate, or -1 if there is no such
	 * element.
	 * <p>
	 * Note: If the specified list allows {@code null} elements the given
	 * predicate must be able to handle {@code null} elements as well to avoid a
	 * {@code NullPointerException}.
	 * 
	 * @param list
	 *            the specified list
	 * @param predicate
	 *            the given predicate
	 * @return the index of the first occurrence in the specified list of an
	 *         element which satisfies the given predicate, or -1 if there is no
	 *         such element
	 */
	public static <E> int indexOf(List<E> list, Predicate<? super E> predicate) {
		checkNotNull(list);
		checkNotNull(predicate);
		final ListIterator<E> e = list.listIterator();
		while (e.hasNext())
			if (predicate.apply(e.next()))
				return e.previousIndex();
		return -1;
	}

	/**
	 * Returns the index of the last occurrence in the specified list of an
	 * element which satisfies the given predicate, or -1 if there is no such
	 * element.
	 * <p>
	 * Note: If the specified list allows {@code null} elements the given
	 * predicate must be able to handle {@code null} elements as well to avoid a
	 * {@code NullPointerException}.
	 * 
	 * @param list
	 *            the specified list
	 * @param predicate
	 *            the given predicate
	 * @return the index of the last occurrence in the specified list of an
	 *         element which satisfies the given predicate, or -1 if there is no
	 *         such element
	 */
	public static <E> int lastIndexOf(List<E> list,
			Predicate<? super E> predicate) {
		checkNotNull(list);
		checkNotNull(predicate);
		final ListIterator<E> e = list.listIterator(list.size());
		while (e.hasPrevious())
			if (predicate.apply(e.next()))
				return e.previousIndex();
		return -1;
	}

	/**
	 * Creates a {@code LinkedList} containing the specified initial elements.
	 * 
	 * @param first
	 *            the first element
	 * @param rest
	 *            an array of additional elements, possibly empty
	 * @return a {@code LinkedList} containing the specified initial elements
	 */
	public static <E> LinkedList<E> newLinkedList(final E first,
			final E... rest) {
		checkNotNull(first);
		checkNotNull(rest);
		final LinkedList<E> linkedList = Lists.newLinkedList();
		linkedList.add(first);
		Collections.addAll(linkedList, rest);
		return linkedList;
	}

	/**
	 * Creates an empty {@code ArrayDeque} with an initial capacity sufficient
	 * to hold 16 elements.
	 * 
	 * @return an empty {@code ArrayDeque} with an initial capacity sufficient
	 *         to hold 16 elements
	 */
	public static <E> ArrayDeque<E> newArrayDeque() {
		return new ArrayDeque<E>();
	}

	/**
	 * Creates an empty {@code ArrayDeque} with an initial capacity sufficient
	 * to hold the specified number of elements.
	 * 
	 * @param numElements
	 *            lower bound on initial capacity of the deque
	 * @return an empty {@code ArrayDeque} with an initial capacity sufficient
	 *         to hold the specified number of elements
	 */
	public static <E> ArrayDeque<E> newArrayDequeWithCapacity(
			final int numElements) {
		return new ArrayDeque<E>(numElements);
	}

	/**
	 * Creates an {@code ArrayDeque} containing the specified initial elements.
	 * 
	 * @param first
	 *            the first element
	 * @param rest
	 *            an array of additional elements, possibly empty
	 * @return an {@code ArrayDeque} containing the specified initial elements
	 */
	public static <E> ArrayDeque<E> newArrayDeque(final E first,
			final E... rest) {
		checkNotNull(first);
		checkNotNull(rest);
		final ArrayDeque<E> arrayDeque = new ArrayDeque<E>(rest.length + 1);
		arrayDeque.add(first);
		Collections.addAll(arrayDeque, rest);
		return arrayDeque;
	}

	/**
	 * Creates an {@code ArrayDeque} containing the elements of the specified
	 * iterable.
	 * 
	 * @param elements
	 *            the iterable whose elements are to be placed into the deque
	 * @return an {@code ArrayDeque} containing the elements of the specified
	 *         iterable.
	 */
	public static <E> ArrayDeque<E> newArrayDeque(
			final Iterable<? extends E> elements) {
		checkNotNull(elements);
		if (elements instanceof Collection<?>)
			return new ArrayDeque<E>((Collection<? extends E>) elements);
		else
			return newArrayDeque(elements.iterator());
	}

	/**
	 * Creates an {@code ArrayDeque} containing the elements returned by the
	 * specified iterator.
	 * 
	 * @param elements
	 *            the iterator whose elements are to be placed into the deque
	 * @return an {@code ArrayDeque} containing the elements returned by the
	 *         specified iterator
	 */
	public static <E> ArrayDeque<E> newArrayDeque(
			final Iterator<? extends E> elements) {
		checkNotNull(elements);
		final ArrayDeque<E> arrayDeque = new ArrayDeque<E>();
		Iterators.addAll(arrayDeque, elements);
		return arrayDeque;
	}

	/**
	 * Creates a {@code TreeMap} containing the same mappings as the specified
	 * map, sorted according to the <i>natural ordering</i> of its keys.
	 * 
	 * @param map
	 *            the map whose mappings are to be placed in this map
	 * @return a {@code TreeMap} containing the same mappings as the specified
	 *         map, sorted according to the <i>natural ordering</i> of its keys
	 */
	public static <K, V> TreeMap<K, V> newTreeMap(
			Map<? extends K, ? extends V> map) {
		return new TreeMap<K, V>(map);
	}

	/**
	 * Creates a {@code TreeMap} containing the same mappings and using the same
	 * ordering as the specified sorted map.
	 * 
	 * @param map
	 *            m the sorted map whose mappings are to be placed in this map,
	 *            and whose comparator is to be used to sort this map
	 * @return a {@code TreeMap} containing the same mappings and using the same
	 *         ordering as the specified sorted map
	 */
	public static <K extends Comparable<? super K>, V> TreeMap<K, V> newTreeMap(
			SortedMap<K, ? extends V> map) {
		return new TreeMap<K, V>(map);
	}

	/**
	 * Creates a {@code TreeSet} containing the specified initial elements
	 * sorted according to their <i>natural ordering</i>.
	 * 
	 * @param first
	 *            the first element
	 * @param rest
	 *            an array of additional elements, possibly empty
	 * @return a {@code TreeSet} containing the specified initial elements
	 *         sorted according to their <i>natural ordering</i>
	 */
	public static <E extends Comparable<? super E>> TreeSet<E> newTreeSet(
			final E first, final E... rest) {
		checkNotNull(first);
		checkNotNull(rest);
		final TreeSet<E> treeSet = Sets.newTreeSet();
		treeSet.add(first);
		Collections.addAll(treeSet, rest);
		return treeSet;
	}

	/**
	 * Creates a {@code TreeSet} containing the elements returned by the
	 * specified iterator, sorted according to their <i>natural ordering</i>.
	 * 
	 * @param elements
	 *            the iterator whose elements are to be placed into this set
	 * @return a {@code TreeSet} containing the elements returned by the
	 *         specified iterator, sorted according to their <i>natural
	 *         ordering</i>
	 */
	public static <E extends Comparable<? super E>> TreeSet<E> newTreeSet(
			final Iterator<? extends E> elements) {
		checkNotNull(elements);
		final TreeSet<E> treeSet = Sets.newTreeSet();
		Iterators.addAll(treeSet, elements);
		return treeSet;
	}

	/**
	 * Creates a {@code TreeSet} containing the elements of the specified
	 * iterable. If the iterable is an instance of a {@link SortedSet},
	 * {@link PriorityQueue PriorityQueue}, or {@link SortedCollection}, this
	 * set will be sorted according to the same ordering. Otherwise, this set
	 * will be sorted according to the <i>natural ordering</i> of its elements.
	 * 
	 * @param elements
	 *            the iterable whose elements are to be placed into this set
	 * @return a {@code TreeSet} containing the elements of the specified
	 *         iterable
	 */
	public static <E> TreeSet<E> newTreeSet(final Iterable<? extends E> elements) {
		checkNotNull(elements);
		Comparator<? super E> c = null;
		if (elements instanceof SortedSet<?>)
			c = ((SortedSet) elements).comparator();
		else if (elements instanceof java.util.PriorityQueue<?>)
			c = ((java.util.PriorityQueue) elements).comparator();
		else if (elements instanceof SortedCollection<?>)
			c = ((SortedCollection) elements).comparator();
		final TreeSet<E> treeSet = c == null ? new TreeSet<E>()
				: new TreeSet<E>(c);
		Iterables.addAll(treeSet, elements);
		return treeSet;
	}

	/**
	 * Creates a {@code TreeList} containing the specified initial elements
	 * sorted according to their <i>natural ordering</i>.
	 * 
	 * @param first
	 *            the first element
	 * @param rest
	 *            an array of additional elements, possibly empty
	 * @return a {@code TreeList} containing the specified initial elements
	 *         sorted according to their <i>natural ordering</i>
	 */
	public static <E extends Comparable<? super E>> TreeList<E> newTreeList(
			final E first, final E... rest) {
		checkNotNull(first);
		checkNotNull(rest);
		final TreeList<E> treeList = TreeList.create();
		treeList.add(first);
		Collections.addAll(treeList, rest);
		return treeList;
	}

	/**
	 * Creates an empty {@code LinkedHashMap} which orders its keys according to
	 * their <i>access-order</i>.
	 * 
	 * @return an empty {@code LinkedHashMap} which orders its keys according to
	 *         their <i>access-order</i>
	 * @see LinkedHashMap
	 */
	public static <K, V> Map<K, V> newAccessOrderMap() {
		return new LinkedHashMap<K, V>(16, .75F, true);
	}

	/**
	 * Creates a {@code LinkedHashMap} which orders its keys according to their
	 * <i>access-order</i>, containing the same mappings as the specified map.
	 * 
	 * @param m
	 *            the map whose mappings this map should contain
	 * @return a {@code LinkedHashMap} which orders its keys according to their
	 *         <i>access-order</i>, containing the same mappings as the
	 *         specified map
	 * @see LinkedHashMap
	 */
	public static <K, V> Map<K, V> newAccessOrderMap(
			final Map<? extends K, ? extends V> m) {
		checkNotNull(m);
		final Map<K, V> map = new LinkedHashMap<K, V>(Math.max(
				(int) (m.size() / .75F) + 1, 16), .75F, true);
		map.putAll(m);
		return map;
	}

	/**
	 * Creates an empty {@code LinkedHashMap} which orders its keys according to
	 * their <i>access-order</i>, with enough capacity to hold the specified
	 * number of entries without rehashing.
	 * 
	 * @param expectedSize
	 *            the expected size
	 * @return an empty {@code LinkedHashMap} which orders its keys according to
	 *         their <i>access-order</i>, with enough capacity to hold the
	 *         specified number of entries without rehashing
	 * @see LinkedHashMap
	 */
	public static <K, V> Map<K, V> newAccessOrderMapWithExpectedSize(
			final int expectedSize) {
		checkArgument(expectedSize >= 0);
		return new LinkedHashMap<K, V>(Math.max(expectedSize * 2, 16), .75F,
				true);
	}

	/**
	 * Returns an unmodifiable view of the specified sorted list. This method
	 * allows modules to provide users with "read-only" access to internal
	 * sorted lists. Query operations on the returned list "read through" to the
	 * specified list, and attempts to modify the returned sorted list, whether
	 * direct or via its iterator, result in an
	 * {@code UnsupportedOperationException}.
	 * <p>
	 * The returned list will be serializable if the specified list is
	 * serializable.
	 * <p>
	 * Note: The returned list does not implement {@code RandomAccess}.
	 * 
	 * @param sortedList
	 *            the list for which an unmodifiable view is to be returned
	 * @return an unmodifiable view of the specified sorted list
	 */
	public static <E> SortedList<E> unmodifiable(
			SortedList<? extends E> sortedList) {
		return new UnmodifiableSortedList<E>(sortedList);
	}

	private static class UnmodifiableSortedList<E> extends
			ForwardingSortedList<E> implements Serializable {

		private static final long serialVersionUID = 1L;

		final SortedList<E> sortedList;

		UnmodifiableSortedList(final SortedList<? extends E> sortedList) {
			checkNotNull(sortedList);
			this.sortedList = (SortedList<E>) sortedList;
		}

		@Override
		public Iterator<E> iterator() {
			return Iterators.unmodifiableIterator(sortedList.iterator());
		}

		@Override
		public boolean add(E e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(Collection<? extends E> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(int index, Collection<? extends E> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public E set(int index, E element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(int index, E element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public E remove(int index) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ListIterator<E> listIterator() {
			return Iterators2.unmodifiable(sortedList.listIterator());
		}

		@Override
		public ListIterator<E> listIterator(int index) {
			return Iterators2.unmodifiable(sortedList.listIterator(index));
		}

		@Override
		public SortedList<E> headList(E toElement) {
			return new UnmodifiableSortedList<E>(sortedList.headList(toElement));
		}

		@Override
		public SortedList<E> subList(E fromElement, E toElement) {
			return new UnmodifiableSortedList<E>(sortedList.subList(
					fromElement, toElement));
		}

		@Override
		public SortedList<E> subList(int fromIndex, int toIndex) {
			return new UnmodifiableSortedList<E>(sortedList.subList(fromIndex,
					toIndex));
		}

		@Override
		public SortedList<E> tailList(E fromElement) {
			return new UnmodifiableSortedList<E>(
					sortedList.tailList(fromElement));
		}

		@Override
		protected SortedList<E> delegate() {
			return sortedList;
		}
	}

	/**
	 * Returns a synchronized (thread-safe) {@code SortedList} backed by the
	 * specified sorted list. In order to guarantee serial access, it is
	 * critical that <b>all</b> access to the backing list is accomplished
	 * through the returned list.
	 * <p>
	 * It is imperative that the user manually synchronize on the returned list
	 * when iterating over it or its views:
	 * 
	 * <pre>
	 *  SortedList sortedList = Collections3.synchronize(...);
	 *      ...
	 *  synchronized(sortedList) {
	 *     for(Object o: sortedList)  // Must be in synchronized block
	 *        foo(o);
	 *  }
	 * </pre>
	 * 
	 * or:
	 * 
	 * <pre>
	 *  SortedList sortedList = Collections3.synchronize(...);
	 *  List subList = sortedList.subList(...)
	 *      ...
	 *  synchronized(sortedList) { // Note: sortedList not subList
	 *     for(Object o: subList)  // Must be in synchronized block
	 *        foo(o);
	 *  }
	 * </pre>
	 * 
	 * Failure to follow this advice may result in non-deterministic behavior.
	 * <p>
	 * The returned list will be serializable if the specified list is
	 * serializable.
	 * 
	 * @param sortedList
	 *            the sorted list to be "wrapped" in a synchronized sorted list
	 * @return a synchronized view of the specified sorted list
	 */
	public static <E> SortedList<E> synchronize(final SortedList<E> sortedList) {
		return new SynchronizedSortedList<E>(sortedList);
	}

	static class SynchronizedSortedList<E> implements SortedList<E> {

		final SortedList<E> sortedList;
		final Object mutex;

		SynchronizedSortedList(SortedList<E> sortedList) {
			checkNotNull(sortedList);
			this.sortedList = sortedList;
			mutex = this;
		}

		SynchronizedSortedList(SortedList<E> sortedList, Object mutex) {
			checkNotNull(sortedList);
			this.sortedList = sortedList;
			this.mutex = mutex;
		}

		public boolean equals(Object o) {
			synchronized (mutex) {
				return sortedList.equals(o);
			}
		}

		public int hashCode() {
			synchronized (mutex) {
				return sortedList.hashCode();
			}
		}

		public E get(int index) {
			synchronized (mutex) {
				return sortedList.get(index);
			}
		}

		public E set(int index, E element) {
			synchronized (mutex) {
				return sortedList.set(index, element);
			}
		}

		public void add(int index, E element) {
			synchronized (mutex) {
				sortedList.add(index, element);
			}
		}

		public E remove(int index) {
			synchronized (mutex) {
				return sortedList.remove(index);
			}
		}

		public int indexOf(Object o) {
			synchronized (mutex) {
				return sortedList.indexOf(o);
			}
		}

		public int lastIndexOf(Object o) {
			synchronized (mutex) {
				return sortedList.lastIndexOf(o);
			}
		}

		public boolean addAll(int index, Collection<? extends E> c) {
			synchronized (mutex) {
				return sortedList.addAll(index, c);
			}
		}

		public ListIterator<E> listIterator() {
			return sortedList.listIterator();
		}

		public ListIterator<E> listIterator(int index) {
			return sortedList.listIterator(index);
		}

		public int size() {
			synchronized (mutex) {
				return sortedList.size();
			}
		}

		public boolean isEmpty() {
			synchronized (mutex) {
				return sortedList.isEmpty();
			}
		}

		public boolean contains(Object o) {
			synchronized (mutex) {
				return sortedList.contains(o);
			}
		}

		public Object[] toArray() {
			synchronized (mutex) {
				return sortedList.toArray();
			}
		}

		public <T> T[] toArray(T[] a) {
			synchronized (mutex) {
				return sortedList.toArray(a);
			}
		}

		public Iterator<E> iterator() {
			return sortedList.iterator();
		}

		public boolean add(E e) {
			synchronized (mutex) {
				return sortedList.add(e);
			}
		}

		public boolean remove(Object o) {
			synchronized (mutex) {
				return sortedList.remove(o);
			}
		}

		public void clear() {
			synchronized (mutex) {
				sortedList.clear();
			}
		}

		public String toString() {
			synchronized (mutex) {
				return sortedList.toString();
			}
		}

		public boolean containsAll(Collection<?> coll) {
			synchronized (mutex) {
				return sortedList.containsAll(coll);
			}
		}

		public boolean addAll(Collection<? extends E> coll) {
			synchronized (mutex) {
				return sortedList.addAll(coll);
			}
		}

		public boolean removeAll(Collection<?> coll) {
			synchronized (mutex) {
				return sortedList.removeAll(coll);
			}
		}

		public boolean retainAll(Collection<?> coll) {
			synchronized (mutex) {
				return sortedList.retainAll(coll);
			}
		}

		private void writeObject(ObjectOutputStream s) throws IOException {
			synchronized (mutex) {
				s.defaultWriteObject();
			}
		}

		@Override
		public Comparator<? super E> comparator() {
			synchronized (mutex) {
				return sortedList.comparator();
			}
		}

		@Override
		public SortedList<E> headList(E toElement) {
			synchronized (mutex) {
				return new SynchronizedSortedList<E>(
						sortedList.headList(toElement), mutex);
			}
		}

		@Override
		public SortedList<E> subList(E fromElement, E toElement) {
			synchronized (mutex) {
				return new SynchronizedSortedList<E>(sortedList.subList(
						fromElement, toElement), mutex);
			}
		}

		@Override
		public SortedList<E> subList(int fromIndex, int toIndex) {
			synchronized (mutex) {
				return new SynchronizedSortedList<E>(sortedList.subList(
						fromIndex, toIndex), mutex);
			}
		}

		@Override
		public SortedList<E> tailList(E fromElement) {
			synchronized (mutex) {
				return new SynchronizedSortedList<E>(
						sortedList.tailList(fromElement), mutex);
			}
		}

	}
}