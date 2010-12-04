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

public class SkipSet<E> extends AbstractSet<E> implements SortedCollection<E> {

	private int size = 0;
	private static final double P = .5;
	private static final int MAX_LEVEL = 31;
	private static int level = 1;
	private final Random random = new Random();
	private final Node<E> header = new Node(null, MAX_LEVEL);
	// final Node<E>[] update = new Node[MAX_LEVEL];
	private final Comparator<? super E> comparator;

	SkipSet(final Comparator<? super E> comparator) {
		if (comparator != null)
			this.comparator = comparator;
		else
			this.comparator = (Comparator<? super E>) Ordering.natural();
	}

	SkipSet(final Iterable<? extends E> elements) {
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

	public static <E extends Comparable<? super E>> SkipSet<E> create() {
		return new SkipSet<E>((Comparator<? super E>) null);
	}

	public static <E> SkipSet<E> create(final Comparator<? super E> comparator) {
		checkNotNull(comparator);
		return new SkipSet<E>(comparator);
	}

	public static <E> SkipSet<E> create(final Iterable<? extends E> elements) {
		checkNotNull(elements);
		return new SkipSet<E>(elements);
	}

	@Override
	public Comparator<? super E> comparator() {
		return comparator;
	}

	// @Override
	// public boolean add(E e) {
	// checkNotNull(e);
	// Node<E> x = header;
	// int i;
	// for (i = level - 1; i >= 0; i--) {
	// while (x.forward[i] != null
	// && comparator.compare(x.forward[i].element, e) < 0)
	// x = x.forward[i];
	// update[i] = x;
	// }
	// x = x.forward[0];
	// if (x != null && comparator.compare(x.element, e) == 0)
	// return false;
	// int newLevel = randomLevel();
	// if (newLevel > level) {
	// Arrays.fill(update, level, newLevel, header);
	// level = newLevel;
	// }
	// x = new Node<E>(e, newLevel);
	// for (i = 0; i < newLevel; i++) {
	// x.forward[i] = update[i].forward[i];
	// update[i].forward[i] = x;
	// }
	// size++;
	// return true;
	// }

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			Node<E> node = header.forward[0];

			@Override
			public boolean hasNext() {
				return node != null;
			}

			@Override
			public E next() {
				if (node == null)
					throw new NoSuchElementException();
				E e = node.element;
				node = node.forward[0];
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
		int l = 1;
		while (l < MAX_LEVEL && random.nextDouble() < P)
			l++;
		return l;
	}

	private static class Node<E> {
		private final E element;
		private final Node<E>[] forward;

		private Node(final E element, final int level) {
			this.element = element;
			forward = new Node[level];
		}
	}

	// private Node<E> find(E element) {
	// Node<E> x = header;
	// for (int i = level - 1; i >= 0; --i) {
	// while (x.forward[i] != null
	// && comparator.compare(x.forward[i].element, element) < 0)
	// x = x.forward[i];
	// update[i] = x;
	// }
	// return x.forward[0];
	// }

	public boolean add(E e) {
		checkNotNull(e);
		Node<E> x = header;
		final int newLevel = randomLevel();
		final Node<E> newNode = new Node<E>(e, newLevel);		
		//int i;
		for(int i = level - 1; i > newLevel; i--){
			while ((x.forward[i] != null)
					&& (comparator.compare(x.forward[i].element, e) < 0))
				x = x.forward[i];
		}
		for(int i = newLevel - 1; i>=0; i--){
			Node<E> node = x.forward[i];
			while ((node != null) && (comparator.compare(node.element, e) < 0)) {
				x = node;
				node = x.forward[i];
			}
			if (x.forward[0] != null
					&& comparator.compare(x.forward[0].element, e) == 0)
				return false;
			newNode.forward[i] = node;
			x.forward[i] = newNode;
		}
		if (newLevel > level) {
			Arrays.fill(header.forward, level, newLevel, newNode);
			level = newLevel;
		}
		size++;
		return true;

	}

	public boolean add1(E e) {
		checkNotNull(e);
		Node<E> x = header;
		final Node<E>[] update = new Node[MAX_LEVEL];
		int i = level;
		while (--i >= 0) {
			while (x.forward[i] != null
					&& comparator.compare(x.forward[i].element, e) < 0)
				x = x.forward[i];
			update[i] = x;
		}
		x = x.forward[0];
		if (x != null && comparator.compare(x.element, e) == 0)
			return false;
		int randomLevel = randomLevel();
		Node<E> newNode = new Node<E>(e, randomLevel);
		if (randomLevel > level) {
			Arrays.fill(update, level, randomLevel, header);
			level = randomLevel;
		}
		for (i = 0; i < randomLevel; ++i) {
			newNode.forward[i] = update[i].forward[i];
			update[i].forward[i] = newNode;
		}
		size++;
		return true;
	}

}