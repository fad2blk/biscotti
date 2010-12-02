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

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.SortedSet;

import com.google.common.collect.Ordering;

public class SkipListSet<E> extends AbstractSet<E> implements
		SortedCollection<E> {

	private int size = 0;
	private static final double P = .25;
	private static final int MAX_LEVEL = 15;
	private static int level = 1;
	private final Random random = new Random();
	// private final Node<E> header = new Node(null, MAX_LEVEL);
	private final Node<E>[] header = new Node[MAX_LEVEL];

	private final Comparator<? super E> comparator;

	SkipListSet(final Comparator<? super E> comparator) {
		if (comparator != null)
			this.comparator = comparator;
		else
			this.comparator = (Comparator<? super E>) Ordering.natural();
	}

	SkipListSet(final Iterable<? extends E> elements) {
		Comparator<? super E> comparator = null;
		if (elements instanceof SortedSet<?>)
			comparator = ((SortedSet) elements).comparator();
		else if (elements instanceof java.util.PriorityQueue<?>)
			comparator = ((java.util.PriorityQueue) elements).comparator();
		else if (elements instanceof SortedCollection<?>)
			comparator = ((SortedCollection) elements).comparator();
		if (comparator == null)
			this.comparator = (Comparator<? super E>) Ordering.natural();
		else
			this.comparator = comparator;
		for (E element : elements)
			add(element);
	}

	public static <E extends Comparable<? super E>> SkipListSet<E> create() {
		return new SkipListSet<E>((Comparator<? super E>) null);
	}

	public static <E> SkipListSet<E> create(
			final Comparator<? super E> comparator) {
		checkNotNull(comparator);
		return new SkipListSet<E>(comparator);
	}

	public static <E> SkipListSet<E> create(final Iterable<? extends E> elements) {
		checkNotNull(elements);
		return new SkipListSet<E>(elements);
	}

	@Override
	public Comparator<? super E> comparator() {
		return comparator;
	}

	@Override
	public boolean add(E e) {
		checkNotNull(e);
		final Node<E>[] update = new Node[level];
		Node<E> x;
		int i = level - 1;
		do {
			x = header[i];
			update[i] = x;
			while (x != null && comparator.compare(x.element, e) < 0)
				x = x.next[i];
		} while (i-- >= 0);

		x = x.next[0];
		if (x != null && comparator.compare(x.element, e) == 0)
			return false;

		final int newLevel = randomLevel();
		final Node<E> newNode = new Node<E>(e, newLevel);

		if (newLevel > level)
			Arrays.fill(header, level, newLevel, newNode);
		
		for (i = level - 1; i >= 0; i--) {
			
		}

		//
		// Node<E> x = header;
		// int i = level - 1;
		// int newLevel = randomLevel();
		//
		// Node<E> newNode = new Node<E>(e, newLevel);
		//
		// if (newLevel > level) {
		// Arrays.fill(header.next, level, newLevel, newNode);
		// level = newLevel;
		// } else if (level > newLevel) {
		// do {
		// Node<E> node = x.next[i];
		// while ((node != null)
		// && (comparator.compare(node.element, e) < 0)) {
		// x = node;
		// node = x.next[i];
		// }
		// i--;
		// } while (i > newLevel - 1);
		// }
		// do {
		// Node<E> node = x.next[i];
		// while ((node != null) && (comparator.compare(node.element, e) < 0)) {
		// x = node;
		// node = x.next[i];
		// }
		// if (node != null && comparator.compare(node.element, e) == 0)
		// return false;
		// newNode.next[i] = node;
		// x.next[i] = newNode;
		// i--;
		// } while (i >= 0);
		// size++;
		 return true;

	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			Node<E> node = header[0];

			@Override
			public boolean hasNext() {
				return node != null;
			}

			@Override
			public E next() {
				if (node == null)
					throw new NoSuchElementException();
				E e = node.element;
				node = node.next[0];
				return e;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public int size() {
		return size;
	}

	public int randomLevel() {
		int randomLevel = 1;
		while (randomLevel < MAX_LEVEL && random.nextDouble() < P)
			randomLevel++;
		return Math.min(randomLevel, level + 1);
	}

	private static class Node<E> {
		private final E element;
		private final Node<E>[] next;

		private Node(final E element, final int level) {
			this.element = element;
			next = new Node[level];
		}

		private int level() {
			return next.length;
		}
	}

}