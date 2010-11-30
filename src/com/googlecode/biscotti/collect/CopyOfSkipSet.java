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

public class CopyOfSkipSet<E> extends AbstractSet<E> implements SortedCollection<E> {

	private int size = 0;
	private static final double P = .25;
	private static final int MAX_LEVEL = 15;
	private static int level = 1;
	private final Random random = new Random();
	private final Node<E> header = new Node(null, MAX_LEVEL);
	private final Comparator<? super E> comparator;

	CopyOfSkipSet(final Comparator<? super E> comparator) {
		if (comparator != null)
			this.comparator = comparator;
		else
			this.comparator = (Comparator<? super E>) Ordering.natural();
	}

	CopyOfSkipSet(final Iterable<? extends E> elements) {
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

	public static <E extends Comparable<? super E>> CopyOfSkipSet<E> create() {
		return new CopyOfSkipSet<E>((Comparator<? super E>) null);
	}

	public static <E> CopyOfSkipSet<E> create(final Comparator<? super E> comparator) {
		checkNotNull(comparator);
		return new CopyOfSkipSet<E>(comparator);
	}

	public static <E> CopyOfSkipSet<E> create(final Iterable<? extends E> elements) {
		checkNotNull(elements);
		return new CopyOfSkipSet<E>(elements);
	}

	@Override
	public Comparator<? super E> comparator() {
		return comparator;
	}

	public boolean add(E e) {
		checkNotNull(e);
		final Node<E>[] update = new Node[level];
		Node<E> x = header;
		int i;
		for (i = level - 1; i >= 0; i--) {
			while (x.next[i] != null
					&& comparator.compare(x.next[i].element, e) < 0)
				x = x.next[i];
			update[i] = x;
		}

		x = x.next[0];
		if (x != null && comparator.compare(x.element, e) == 0)
			return false;
		
		final int newLevel = randomLevel();
		x = new Node<E>(e, newLevel);
		
		if(newLevel > level){
			Arrays.fill(header.next, level, newLevel, x);
		}
		
		for (i = 0; i < Math.min(newLevel, level); i++) {
			x.next[i] = update[i].next[i];
			update[i].next[i] = x;
		}
		if(newLevel > level){
			level = newLevel;
		}
		size++;
		

//		for (i = level - 1; i >= 0; i--) {
//			System.out.println(update[i] == null ? null : update[i].element);
//			update[i] = x;
//		}
		
		return true;

		// x = x.next[0];
	}
//
//	@Override
//	public boolean add(E e) {
//		checkNotNull(e);
//		Node<E> x = header;
//		final Node<E>[] update = new Node[MAX_LEVEL];
//		int i;
//		for (i = level - 1; i >= 0; i--) {
//			while (x.next[i] != null
//					&& comparator.compare(x.next[i].element, e) < 0)
//				x = x.next[i];
//			update[i] = x;
//		}
//		x = x.next[0];
//		if (x != null && comparator.compare(x.element, e) == 0)
//			return false;
//		int newLevel = randomLevel();
//		if (newLevel > level) {
//			Arrays.fill(update, level, newLevel, header);
//			level = newLevel;
//		}
//		x = new Node<E>(e, newLevel);
//		for (i = 0; i < newLevel; i++) {
//			x.next[i] = update[i].next[i];
//			update[i].next[i] = x;
//		}
//		size++;
//		return true;
//	}
//
//	public boolean add1(E e, int randomLevel) {
//		checkNotNull(e);
//		Node<E> x = header;
//		final Node<E>[] update = new Node[MAX_LEVEL];
//		int i;
//		for (i = level - 1; i >= 0; i--) {
//			while (x.next[i] != null
//					&& comparator.compare(x.next[i].element, e) < 0)
//				x = x.next[i];
//			update[i] = x;
//		}
//		x = x.next[0];
//		if (x != null && comparator.compare(x.element, e) == 0)
//			return false;
//		int newLevel = randomLevel;
//		if (newLevel > level) {
//			Arrays.fill(update, level, newLevel, header);
//			level = newLevel;
//		}
//		x = new Node<E>(e, newLevel);
//		for (i = 0; i < newLevel; i++) {
//			x.next[i] = update[i].next[i];
//			update[i].next[i] = x;
//		}
//		size++;
//		return true;
//	}

	@Override
	public boolean remove(Object o) {
		E e = (E) o;
		checkNotNull(e);
		Node<E>[] update = new Node[MAX_LEVEL];
		Node<E> x = header;
		int i;
		for (i = level - 1; i >= 0; i--) {
			while (x.next[i] != null
					&& comparator.compare(x.next[i].element, e) < 0)
				x = x.next[i];
			update[i] = x;
		}
		// x = x.next[0];
		// if (x != null && comparator.compare(x.element, e) == 0) {
		// for (i = 0; i < level; i++) {
		// if (update[i].next[i] != x)
		// break;
		// update[i].next[i] = x.next[i];
		// }
		// while (level > 0 && header.next[level] != null)
		// level--;
		// size--;
		// return true;
		// }
		// return false;
		return remove(e, x, update);
	}

	private boolean remove(E e, Node<E> x, Node<E>[] update) {
		if (x != null && comparator.compare(x.element, e) == 0) {
			for (int i = 0; i < level; i++) {
				if (update[i].next[i] != x)
					break;
				update[i].next[i] = x.next[i];
			}
			while (level > 0 && header.next[level] != null)
				level--;
			size--;
			return true;
		}
		return false;
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			Node<E> node = header.next[0];

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

	public void print() {
		// Node<E> x = header.next[0];
		for (Node<E> x = header.next[0]; x != null; x = x.next[0]) {
			System.out.print("[" + x.element + ":" + x.next.length + "]");
		}
		System.out.println();

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
	}

}