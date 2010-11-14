package com.googlecode.biscotti.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.AbstractQueue;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Random;

import com.google.common.collect.Ordering;

public class SkipListQueue<E> extends AbstractQueue<E> implements
		SortedCollection<E> {

	private static int MAX_LEVEL = 5;
	private static double P = .5;
	private Random random = new Random();
	private final Node<E> header = new Node<E>(null, MAX_LEVEL);
	private int level = 1;
	private int size = 0;
	private int modCount = 0;
	private final Comparator<? super E> comparator;

	SkipListQueue(final Comparator<? super E> comparator) {
		if (comparator != null)
			this.comparator = comparator;
		else
			this.comparator = (Comparator<? super E>) Ordering.natural();
	}

	SkipListQueue(final Iterable<? extends E> elements) {
		Comparator<? super E> comparator = null;
		if (elements instanceof NavigableSet<?>)
			comparator = ((NavigableSet) elements).comparator();
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

	public static <E extends Comparable<? super E>> SkipListQueue<E> create() {
		return new SkipListQueue<E>((Comparator<? super E>) null);
	}

	public static <E> SkipListQueue<E> create(
			final Comparator<? super E> comparator) {
		checkNotNull(comparator);
		return new SkipListQueue<E>(comparator);
	}

	public static <E> SkipListQueue<E> create(
			final Iterable<? extends E> elements) {
		checkNotNull(elements);
		return new SkipListQueue<E>(elements);
	}

	@Override
	public Comparator<? super E> comparator() {
		return comparator;
	}

	@Override
	public boolean offer(E e) {
		checkNotNull(e);
		size++;
		modCount++;
		Node<E>[] update = new Node[MAX_LEVEL];
		Node<E> node = header;
		for (int i = level - 1; i >= 0; i--) {
			while (node.forward[i] != null
					&& comparator.compare(node.forward[i].element, e) <= 0)
				node = node.forward[i];
			update[i] = node;
		}
		int randomLevel = randomLevel();
		if (randomLevel > level) {
			for (int i = level; i <= randomLevel; i++)
				update[i] = header;
			level = randomLevel;
		}

		node = new Node<E>(e, randomLevel);

		for (int i = 0; i < randomLevel; i++) {
			node.forward[i] = update[i].forward[i];
			update[i].forward[i] = node;
		}

		return true;
	}

	@Override
	public E peek() {
		if (isEmpty())
			return null;
		return null;
	}

	@Override
	public E poll() {
		if (isEmpty())
			return null;
		return null;
	}

	@Override
	public boolean contains(Object o) {
		// return o != null && search((E) o) != null;
		return false;
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private int expectedModCount = modCount;
			protected Node<E> node = header;

			@Override
			public boolean hasNext() {
				checkForConcurrentModification();
				return node.forward[0] != null;
			}

			@Override
			public E next() {
				checkForConcurrentModification();
				if (hasNext()) {
					node = node.forward[0];
					return node.element;
				} else
					throw new NoSuchElementException();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			private void checkForConcurrentModification() {
				if (expectedModCount != modCount)
					throw new ConcurrentModificationException();
			}
		};
	}

	@Override
	public boolean remove(Object o) {
		checkNotNull(o);
		return false;
	}

	@Override
	public int size() {
		return size;
	}

	/*
	 * Skip-List
	 */

	private class Node<E> {
		E element = null;
		Node<E>[] forward;

		private Node(E element, int level) {
			this.element = element;
			forward = new Node[level];
		}
	}

	private int randomLevel() {
		int lvl = 1;
		while (random.nextDouble() < P && lvl < MAX_LEVEL)
			lvl = lvl + 1;
		return lvl;
	}

	// /**
	// * Returns a random level for inserting a new node. Hardwired to k=1,
	// p=0.5,
	// * max 31 (see above and Pugh's "Skip List Cookbook", sec 3.4).
	// *
	// * This uses the simplest of the generators described in George
	// Marsaglia's
	// * "Xorshift RNGs" paper. This is not a high-quality generator but is
	// * acceptable here.
	// */
	// private int randomLevel() {
	// int x = seed;
	// x ^= x << 13;
	// x ^= x >>> 17;
	// seed = x ^= x << 5;
	// if ((x & 0x8001) != 0) // test highest and lowest bits
	// return 0;
	// int level = 1;
	// while (((x >>>= 1) & 1) != 0)
	// ++level;
	// return level;
	// }
}