package com.googlecode.biscotti.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.AbstractCollection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

import com.google.common.collect.Ordering;

class SkipListCollection<E> extends AbstractCollection<E> {

	private class Node {
		public Node[] next;
		public E element;

		public Node(E value, int level) {
			this.element = value;
			this.next = (Node[]) new Object[level];
		}
	}

	private int size = 0;
	private int levels = 1;
	private Random _random = new Random();
	private Node head = new Node(null, 33);
	private Comparator<? super E> comparator;

	private SkipListCollection(final Comparator<? super E> comparator) {
		if (comparator != null)
			this.comparator = comparator;
		else
			this.comparator = (Comparator<? super E>) Ordering.natural();
	}

	public static <E> SkipListCollection<E> create() {
		return new SkipListCollection<E>((Comparator<? super E>) null);
	}

	/**
	 * Creates a new {@code TreeList} that orders its elements according to the
	 * specified comparator.
	 * 
	 * @param comparator
	 *            the comparator that will be used to order this priority list
	 * @return a new {@code TreeList} that orders its elements according to
	 *         {@code comparator}
	 */
	public static <E> SkipListCollection<E> create(
			final Comparator<? super E> comparator) {
		checkNotNull(comparator);
		return new SkipListCollection<E>(comparator);
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean contains(Object o) {
		Node itor = head;
		for (int i = levels - 1; i >= 0; i--) {
			for (; itor.next[i] != null; itor = itor.next[i]) {
				if (comparator.compare(itor.next[i].element, (E) o) > 0)
					break;
				if (itor.next[i].element.equals(0))
					return true;
			}
		}
		return false;

	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			int index = 0;
			Node next = head.next[0];

			@Override
			public boolean hasNext() {
				return index < size;
			}

			@Override
			public E next() {
				E element = next.element;
				next = next.next[0];
				index++;
				return element;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public boolean add(E e) {
		size++;
		int level = 0;
		for (int r = _random.nextInt(); (r & 1) == 1; r >>= 1) {
			level++;
			if (level == levels) {
				levels++;
				break;
			}
		}
		Node newNode = new Node(e, level + 1);
		Node itor = head;
		for (int i = levels - 1; i >= 0; i--) {
			for (; itor.next[i] != null; itor = itor.next[i]) {
				if (comparator.compare(itor.next[i].element, e) > 0)
					break;
			}
			if (i <= level) {
				newNode.next[i] = itor.next[i];
				itor.next[i] = newNode;
			}
		}
		return true;
	}

	@Override 
	public boolean remove(Object o) {
		Node itor = head;
		boolean found = false;
		for (int i = levels - 1; i >= 0; i--) {
			for (; itor.next[i] != null; itor = itor.next[i]) {
				if (itor.next[i].element.equals(o)) {
					found = true;
					itor.next[i] = itor.next[i].next[i];
					break;
				}
				if (comparator.compare(itor.next[i].element, (E) o) > 0)
					break;
			}
		}
		return found;
	}

}
