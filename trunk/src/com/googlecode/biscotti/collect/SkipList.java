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
		final int[] pos = new int[MAX_LEVEL];
		Node<E> x = header;
		int idx = 0;
		for (int i = level - 1; i >= 0; i--) {
			while (x.next[i] != header
					&& comparator.compare(x.next[i].element, element) < 0) {
				idx += x.dist[i];
				x = x.next[i];
			}
			update[i] = x;
			pos[i] = idx;
		}
		if (newLevel > level) {
			for (int i = level; i < newLevel; i++) {
				header.dist[i] = size + 1;
				update[i] = header;
			}
			level = newLevel;
		}
		x = new Node<E>(element, newLevel);
		for (int i = 0; i < level; i++) {
			if (i > newLevel - 1)
				update[i].dist[i]++;
			else {
				x.next[i] = update[i].next[i];
				update[i].next[i] = x;
				x.dist[i] = pos[i] + update[i].dist[i] - idx;
				update[i].dist[i] = idx + 1 - pos[i];

			}
		}
		x.prev = update[0];
		x.next[0].prev = x;
		modCount++;
		size++;
		return true;
	}

	@Override
	public boolean remove(Object o) {
		checkNotNull(o);
		final E element = (E) o;
		final Node<E>[] update = new Node[MAX_LEVEL];
		final int[] pos = new int[MAX_LEVEL];
		Node<E> x = header;
		int idx = 0;
		for (int i = level - 1; i >= 0; i--) {
			while (x.next[i] != header
					&& comparator.compare(x.next[i].element, element) < 0) {
				idx += x.dist[i];
				x = x.next[i];
			}
			update[i] = x;
			pos[i] = idx;
		}
		x = x.next[0];
		if (x == header || comparator.compare(x.element, element) != 0)
			return false;
		for (int i = 0; i < level; i++) {
			if (update[i].next[i] == x) {
				update[i].next[i] = x.next[i];
				update[i].dist[i] += x.dist[i] - 1;
			} else
				update[i].dist[i]--;
		}
		x.next[0].prev = update[0];
		while (header.next[level - 1] == header && level > 0)
			level--;
		size--;
		modCount++;
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
		return find(index).element;
	}

	@Override
	public E remove(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int indexOf(Object o) {
		if (o != null) {
			Node<E> x = header;
			int idx = 0;
			final E element = (E) o;
			for (int i = level - 1; i >= 0; i--)
				while (x.next[i] != header
						&& comparator.compare(x.next[i].element, element) < 0) {
					idx += x.dist[i];
					x = x.next[i];
				}
			x = x.next[0];
			if (x != header && comparator.compare(x.element, element) == 0)
				return idx;
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		if (o != null) {
			Node<E> x = header;
			int idx = 0;
			final E element = (E) o;
			for (int i = level - 1; i >= 0; i--)
				while (x.next[i] != header
						&& comparator.compare(x.next[i].element, element) < 0) {
					idx += x.dist[i];
					x = x.next[i];
				}
			x = x.next[0];
			if (x != header && comparator.compare(x.element, element) == 0){
				while(x.next[0] != header && comparator.compare(x.next[0].element, element) == 0){
					x = x.next[0];
					idx++;
				}
				return idx;
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
			node = find(index);
			offset = index;
		}

		@Override
		public void add(E element) {
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
			return index;
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
			return index - 1;
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

	private Node<E> search(final E element) {
		Node<E> x = header;
		for (int i = level - 1; i >= 0; i--)
			while (x.next[i] != header
					&& comparator.compare(x.next[i].element, element) < 0)
				x = x.next[i];
		x = x.next[0];
		if (x != header && comparator.compare(x.element, element) == 0)
			return x;
		return null;
	}

	private Node<E> find(final int index) {
		Node<E> x = header;
		int idx = -1;
		for (int i = level - 1; i >= 0; i--)
			while (idx + x.dist[i] <= index) {
				idx = idx + x.dist[i];
				x = x.next[i];
			}
		return x;
	}

}