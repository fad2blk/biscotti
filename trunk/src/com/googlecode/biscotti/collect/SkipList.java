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

import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.SortedSet;

import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

/**
 * A {@link List} whose elements are sorted from <i>least</i> to <i>greatest</i>
 * according to their <i>natural ordering</i>, or by an explicit
 * {@link Comparator} provided at creation. Attempting to remove or insert
 * {@code null} elements is prohibited. Querying for {@code null} elements is
 * allowed. Inserting non-comparable elements will result in a
 * {@code ClassCastException}. The {@code add(int, E)} ,
 * {@code addAll(int, Collection)}, and {@code set(int, E)} operations are not
 * supported.
 * <p>
 * The iterators obtained from the {@link #iterator()} and
 * {@link #listIterator()} methods are <i>fail-fast</i>. Attempts to modify the
 * elements in this list at any time after an iterator is created, in any way
 * except through the iterator's own remove method, will result in a
 * {@code ConcurrentModificationException}. Further, the list iterator does not
 * support the {@code add(E)} and {@code set(E)} operations.
 * <p>
 * This list is not <i>thread-safe</i>. If multiple threads modify this list
 * concurrently it must be synchronized externally.
 * <p>
 * The underlying implementation is based on a <a
 * href="http://en.wikipedia.org/wiki/Skip_list">Skip List</a> modified to
 * provide logarithmic running time for both insertion and removal operations
 * and linear list operations (e.g. get the element at the i<i>th</i> index).
 * <p>
 * A Skip List is a probabilistic data structure for maintaining items in sorted
 * order. Strictly speaking it is impossible to make any hard guarantees
 * regarding the worst-case performance of this class. Practical performance is
 * <i>expected</i> to be logarithmic with an extremely high degree of
 * probability as the list grows.
 * <p>
 * 
 * @author Zhenya Leonov
 * 
 * @param <E>
 *            the type of elements maintained by this list
 */
public final class SkipList<E> extends AbstractList<E> implements List<E>,
		SortedCollection<E> {

	private static final double P = .5;
	private static final int MAX_LEVEL = 32;
	private int size = 0;
	private int level = 1;
	private Random random = new Random();
	private Node<E> header = new Node<E>(null, MAX_LEVEL);
	private final Comparator<? super E> comparator;

	private SkipList(final Comparator<? super E> comparator) {
		this.comparator = comparator;
		for (int i = 0; i < MAX_LEVEL; i++) {
			header.next[i] = header;
			header.dist[i] = 1;
		}
		header.prev = header;
	}

	private SkipList(final Comparator<? super E> comparator,
			final Iterable<? extends E> elements) {
		this(comparator);
		Iterables.addAll(this, elements);
	}

	public static <E extends Comparable<? super E>> SkipList<E> create() {
		return new SkipList<E>(Ordering.natural());
	}

	public static <E extends Comparable<? super E>> SkipList<E> create(
			final Comparator<? super E> comparator) {
		checkNotNull(comparator);
		return new SkipList<E>(comparator);
	}

	public static <E> SkipList<E> create(final Iterable<? extends E> elements) {
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
		return new SkipList<E>(comparator, elements);
	}

	@Override
	public void add(int index, E element) {
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
	public Comparator<? super E> comparator() {
		return comparator;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean contains(Object o) {
		return o != null && search((E) o) != null;
	}

	@Override
	public boolean add(E element) {
		checkNotNull(element);
		final Node<E>[] update = new Node[MAX_LEVEL];
		final int newLevel = randomLevel();
		final int[] indices = new int[MAX_LEVEL];
		Node<E> curr = header;
		int idx = 0;
		for (int i = level - 1; i >= 0; i--) {
			while (curr.next[i] != header
					&& comparator.compare(curr.next[i].element, element) < 0) {
				idx += curr.dist[i];
				curr = curr.next[i];
			}
			update[i] = curr;
			indices[i] = idx;
		}
		if (newLevel > level) {
			for (int i = level; i < newLevel; i++) {
				header.dist[i] = size + 1;
				update[i] = header;
			}
			level = newLevel;
		}
		curr = new Node<E>(element, newLevel);
		for (int i = 0; i < level; i++) {
			if (i > newLevel - 1)
				update[i].dist[i]++;
			else {
				curr.next[i] = update[i].next[i];
				update[i].next[i] = curr;
				curr.dist[i] = indices[i] + update[i].dist[i] - idx;
				update[i].dist[i] = idx + 1 - indices[i];

			}
		}
		curr.prev = update[0];
		curr.next[0].prev = curr;
		modCount++;
		size++;
		return true;
	}

	@Override
	public boolean remove(Object o) {
		checkNotNull(o);
		final E element = (E) o;
		final Node<E>[] update = new Node[MAX_LEVEL];
		Node<E> curr = header;
		for (int i = level - 1; i >= 0; i--) {
			while (curr.next[i] != header
					&& comparator.compare(curr.next[i].element, element) < 0)
				curr = curr.next[i];
			update[i] = curr;
		}
		curr = curr.next[0];
		if (curr == header || comparator.compare(curr.element, element) != 0)
			return false;
		delete(curr, update);
		return true;
	}

	@Override
	public void clear() {
		for (int i = 0; i < MAX_LEVEL; i++) {
			header.next[i] = header;
			header.dist[i] = 1;
		}
		header.prev = header;
		modCount++;
		size = 0;
	}

	@Override
	public E get(int index) {
		checkElementIndex(index, size);
		return search(index).element;
	}

	@Override
	public E remove(int index) {
		checkElementIndex(index, size);
		final Node<E>[] update = new Node[level];
		Node<E> curr = header;
		int idx = 0;
		for (int i = level - 1; i >= 0; i--) {
			while (idx + curr.dist[i] <= index) {
				idx += curr.dist[i];
				curr = curr.next[i];
			}
			update[i] = curr;
		}
		curr = curr.next[0];
		delete(curr, update);
		return curr.element;
	}

	@Override
	public int indexOf(Object o) {
		if (o != null) {
			Node<E> curr = header;
			int idx = 0;
			final E element = (E) o;
			for (int i = level - 1; i >= 0; i--)
				while (curr.next[i] != header
						&& comparator.compare(curr.next[i].element, element) < 0) {
					idx += curr.dist[i];
					curr = curr.next[i];
				}
			curr = curr.next[0];
			if (curr != header
					&& comparator.compare(curr.element, element) == 0)
				return idx;
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		if (o != null) {
			Node<E> curr = header;
			int idx = 0;
			final E element = (E) o;
			for (int i = level - 1; i >= 0; i--)
				while (curr.next[i] != header
						&& comparator.compare(curr.next[i].element, element) <= 0) {
					idx += curr.dist[i];
					curr = curr.next[i];
				}
			if (curr != header
					&& comparator.compare(curr.element, element) == 0)
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
		return new ListItor();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		checkPositionIndex(index, size);
		return new ListItor(index);
	}

	private class ListItor implements ListIterator<E> {
		private Node<E> node;
		private Node<E> last = null;
		private int offset;
		private int index = 0;
		private int expectedModCount = modCount;

		private ListItor() {
			node = header.next[0];
			offset = 0;
		}

		private ListItor(final int index) {
			node = search(index);
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
			node = node.next[0];
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
			throw new UnsupportedOperationException();
		}

		@Override
		public void set(E element) {
			throw new UnsupportedOperationException();
		}

		private void checkForConcurrentModification() {
			if (expectedModCount != modCount)
				throw new ConcurrentModificationException();
		}
	}

	// Skip List

	private static class Node<E> {
		private E element;
		private Node<E> prev;
		private final Node<E>[] next;
		private final int[] dist;

		private Node(final E element, final int size) {
			this.element = element;
			next = new Node[size];
			dist = new int[size];
		}
	}

	private int randomLevel() {
		int randomLevel = 1;
		while (randomLevel < MAX_LEVEL - 1 && random.nextDouble() < P)
			randomLevel++;
		return randomLevel;
	}

	private void delete(final Node<E> node, final Node<E>[] update) {
		for (int i = 0; i < level; i++)
			if (update[i].next[i] == node) {
				update[i].next[i] = node.next[i];
				update[i].dist[i] += node.dist[i] - 1;
			} else
				update[i].dist[i]--;
		node.next[0].prev = node.prev;
		while (header.next[level - 1] == header && level > 0)
			level--;
		modCount++;
		size--;
	}

	private Node<E> search(final E element) {
		Node<E> curr = header;
		for (int i = level - 1; i >= 0; i--)
			while (curr.next[i] != header
					&& comparator.compare(curr.next[i].element, element) < 0)
				curr = curr.next[i];
		curr = curr.next[0];
		if (curr != header && comparator.compare(curr.element, element) == 0)
			return curr;
		return null;
	}

	private Node<E> search(final int index) {
		Node<E> curr = header;
		int idx = -1;
		for (int i = level - 1; i >= 0; i--)
			while (idx + curr.dist[i] <= index) {
				idx = idx + curr.dist[i];
				curr = curr.next[i];
			}
		return curr;
	}

}