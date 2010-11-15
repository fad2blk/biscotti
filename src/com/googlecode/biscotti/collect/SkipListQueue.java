package com.googlecode.biscotti.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.AbstractQueue;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Random;

import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;

public class SkipListQueue<E> extends AbstractQueue<E> implements
		SortedCollection<E> {

	private static int MAX_LEVEL = 5;
	private static double P = .5;
	private Random random = new Random();
	private final Node<E> header = new Node<E>(null, MAX_LEVEL);
	private int level = 0;
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
		int i, newLevel;
		Node<E>[] update = new Node[MAX_LEVEL];
		Node<E> x = header;
		for (i = level - 1; i >= 0; i--) {
			while (x.forward[i] != null
					&& comparator.compare(x.forward[i].element, e) < 0)
				x = x.forward[i];
			update[i] = x;
		}

		x = x.forward[0];
		newLevel = randomLevel();
		if (newLevel > level) {
			for (i = level; i < newLevel; i++)
				update[i] = header;
			level = newLevel;
		}
		x = new Node<E>(e, newLevel);
		for (i = 0; i < newLevel; i++) {
			x.forward[i] = update[i].forward[i];
			update[i].forward[i] = x;
		}
		return true;
	}

	@Override
	public E peek() {
		if (isEmpty())
			return null;
		return header.forward[0].element;
	}

	@Override
	public E poll() {
		if (isEmpty())
			return null;
		Iterator<E> itor = iterator();
		E e = itor.next();
		itor.remove();
		return e;
	}

	@Override
	public boolean contains(Object o) {
		if (o != null) {
			E e = (E) o;
			Node<E> x = header;
			for (int i = level - 1; i >= 0; i--)
				while (x.forward[i] != null
						&& comparator.compare(x.forward[i].element, e) < 0)
					x = x.forward[i];
			x = x.forward[0];
			if (x != null && comparator.compare(x.element, e) == 0)
				return true;
		}
		return false;
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private int expectedModCount = modCount;
			private Node<E> next = header.forward[0];
			private Node<E> last = null;

			@Override
			public boolean hasNext() {
				checkForConcurrentModification();
				return next != null;
			}

			@Override
			public E next() {
				checkForConcurrentModification();
				if (hasNext()) {
					last = next;
					next = next.forward[0];
					return last.element;
				} else
					throw new NoSuchElementException();
			}

			@Override
			public void remove() {
				checkForConcurrentModification();
				Preconditions.checkState(last != null);
				SkipListQueue.this.remove(last.element);
				last = null;
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
		E e = (E) o;
		int i;
		Node<E>[] update = new Node[MAX_LEVEL];
		Node<E> x = header;
		for (i = level - 1; i >= 0; i--) {
			while (x.forward[i] != null
					&& comparator.compare(x.forward[i].element, e) < 0)
				x = x.forward[i];
			update[i] = x;
		}
		x = x.forward[0];
		if (x != null && comparator.compare(x.element, e) == 0) {
			size--;
			modCount++;
			for (i = 0; i < level; i++) {
				if (update[i].forward[i] != x)
					break;
				update[i].forward[i] = x.forward[i];
			}
			while (level > 0 && header.forward[level - 1] == null)
				level = level - 1;
			return true;
		}
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
		int level = 1;
		while (random.nextDouble() < P && level < MAX_LEVEL)
			level = level + 1;
		return level;
	}

	// public void print() {
	// Node<E> n = header.forward[0];
	// while (n != null) {
	// if (n.prev != null)
	// System.out.print("prev:" + n.prev.element);
	// else
	// System.out.print("prev:null");
	// System.out.print(" this:" + n.element);
	// if (n.forward[0] != null)
	// System.out.println(" next:" + n.forward[0].element);
	// else
	// System.out.println(" next:null");
	// // System.out
	// // .println("prev:" + n.prev != null ? n.prev.element
	// // : "null" + " this:" + n.element + " next:"
	// // + n.forward[0] != null ? n.forward[0].element
	// // : "null");
	// n = n.forward[0];
	// }
	// }

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