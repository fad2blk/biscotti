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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.SortedSet;

import com.google.common.collect.Ordering;

public class RankList<E> extends AbstractCollection<E> implements
		SortedCollection<E>, List<E> {

	private int size = 0;
	private static final double P = .5;
	private static final int MAX_LEVEL = 32;
	private static int level = 1;
	private final Random random = new Random();
	private final Node<E> head = new Node<E>(null, MAX_LEVEL);
	private final Node<E> tail = new Node<E>(null, MAX_LEVEL);
	private final Comparator<? super E> comparator;

	private RankList(final Comparator<? super E> comparator,
			final Iterable<? extends E> elements) {
		this.comparator = comparator;
		Arrays.fill(head.next, tail);
		Arrays.fill(head.distance, 1);
		if (elements != null)
			for (E element : elements)
				add(element);
	}

	public static <E extends Comparable<? super E>> RankList<E> create() {
		return new RankList<E>(Ordering.natural(), null);
	}

	public static <E> RankList<E> create(final Comparator<? super E> comparator) {
		checkNotNull(comparator);
		return new RankList<E>(comparator, null);
	}

	public static <E> RankList<E> create(final Iterable<? extends E> elements) {
		checkNotNull(elements);
		final Comparator<? super E> comparator;
		if (elements instanceof SortedSet<?>)
			comparator = ((SortedSet) elements).comparator();
		else if (elements instanceof java.util.PriorityQueue<?>)
			comparator = ((PriorityQueue) elements).comparator();
		else if (elements instanceof SortedCollection<?>)
			comparator = ((SortedCollection) elements).comparator();
		else
			comparator = (Comparator<? super E>) Ordering.natural();
		return new RankList<E>(comparator, elements);
	}

	@Override
	public Comparator<? super E> comparator() {
		return comparator;
	}

	@Override
	public boolean add(E e) {
		checkNotNull(e);
		add(size, e);
		return true;
	}

	@Override
	public Iterator<E> iterator() {
		return listIterator();
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void add(int index, E element) {
		checkNotNull(element);
		checkPositionIndex(index, size);
		int pos = 0;
		Node<E> curr = head;
		final int newLevel = randomLevel();
		final Node<E> newNode = new Node<E>(element, newLevel);
		if (newLevel > level) {
			for (int i = level; i < newLevel; i++)
				head.distance[i] = size + 1;
			level = newLevel;
		}
		for (int i = level - 1; i >= 0; i--) {
			while (pos + curr.distance[i] <= index) {
				pos = pos + curr.distance[i];
				curr = curr.next[i];
			}
			if (i > newLevel - 1)
				curr.distance[i]++;
			else {
				newNode.next[i] = curr.next[i];
				curr.next[i] = newNode;
				newNode.distance[i] = pos + curr.distance[i] - index;
				curr.distance[i] = index + 1 - pos;
			}
		}
		newNode.prev = curr;
		newNode.next[0].prev = newNode;
		size++;
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends E> arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public E get(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int indexOf(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int lastIndexOf(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<E> listIterator() {
		return new ListIterator<E>() {

			private Node<E> node = head.next[0];
			private int index = 0;

			@Override
			public void add(E arg0) {
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
				if (!hasNext())
					throw new NoSuchElementException();
				index++;
				node = node.next[0];
				return node.prev.e;
			}

			@Override
			public int nextIndex() {
				return index;
			}

			@Override
			public E previous() {
				if (index == 0)
					throw new NoSuchElementException();
				index--;
				node = node.prev;
				return node.e;
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
			public void set(E arg0) {
				throw new UnsupportedOperationException();
			}

		};
	}

	@Override
	public ListIterator<E> listIterator(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E remove(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E set(int arg0, E arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<E> subList(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	// Skip List

	private int randomLevel() {
		int level = 1;
		while (level < MAX_LEVEL && random.nextDouble() < P)
			level++;
		return level;
	}

	private static class Node<E> {
		private final E e;
		private Node<E> prev;
		private final Node<E>[] next;
		private final int[] distance;

		private Node(final E element, final int size) {
			this.e = element;
			next = new Node[size];
			distance = new int[size];
		}
	}

}
