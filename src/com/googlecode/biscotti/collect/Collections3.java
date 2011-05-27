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
	 * @param elements
	 *            the elements this list should contain
	 * @return a {@code LinkedList} containing the specified initial elements
	 */
	public static <E> LinkedList<E> newLinkedList(final E... elements) {
		checkNotNull(elements);
		LinkedList<E> linkedList = Lists.newLinkedList();
		Collections.addAll(linkedList, elements);
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
	 * @param elements
	 *            the elements this deque should contain
	 * @return an {@code ArrayDeque} containing the specified initial elements
	 */
	public static <E> ArrayDeque<E> newArrayDeque(final E... elements) {
		checkNotNull(elements);
		ArrayDeque<E> arrayDeque = new ArrayDeque<E>(elements.length);
		Collections.addAll(arrayDeque, elements);
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
		ArrayDeque<E> arrayDeque = new ArrayDeque<E>();
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
	 * @param elements
	 *            the elements this tree set should contain
	 * @return a {@code TreeSet} containing the specified initial elements
	 *         sorted according to their <i>natural ordering</i>
	 */
	public static <E extends Comparable<? super E>> TreeSet<E> newTreeSet(
			final E... elements) {
		checkNotNull(elements);
		TreeSet<E> treeSet = Sets.newTreeSet();
		Collections.addAll(treeSet, elements);
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
		TreeSet<E> treeSet = Sets.newTreeSet();
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
		TreeSet<E> treeSet = c == null ? new TreeSet<E>() : new TreeSet<E>(c);
		Iterables.addAll(treeSet, elements);
		return treeSet;
	}

	/**
	 * Creates a {@code TreeList} containing the specified initial elements
	 * sorted according to their <i>natural ordering</i>.
	 * 
	 * @param elements
	 *            the initial elements to be stored in this list
	 * @return a {@code TreeList} containing the specified initial elements
	 *         sorted according to their <i>natural ordering</i>
	 */
	public static <E extends Comparable<? super E>> TreeList<E> newTreeList(
			final E... elements) {
		checkNotNull(elements);
		TreeList<E> l = TreeList.create();
		Collections.addAll(l, elements);
		return l;
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
		Map<K, V> map = new LinkedHashMap<K, V>(Math.max(
				(int) (m.size() / .75F) + 1, 16), .75F, true);
		map.putAll(m);
		return map;
	}

	/**
	 * Creates an empty {@code LinkedHashMap} which orders its keys according to
	 * their <i>access-order</i>, with enough capacity to hold the specified
	 * number of entries without rehashing.
	 * 
	 * @param initialCapacity
	 *            the initial capacity
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

}