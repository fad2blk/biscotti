package com.googlecode.biscotti.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.AbstractCollection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.SortedSet;

import com.google.common.collect.Ordering;

public class SkipList<E> extends AbstractCollection<E> implements
		SortedCollection<E> {

	private static final double P = .5;
	private static final int MAX_LEVEL = 32;
	private int size = 0;
	private final Comparator<? super E> comparator;
	private final Node<E> head = new Node<E>(null, MAX_LEVEL);
	private final Node<E> tail = new Node<E>(null, MAX_LEVEL);
	private final Random random = new Random();
	private int level = 1;

	// private int randomSeed = (int) System.nanoTime();

	private SkipList(final Comparator<? super E> comparator,
			final Iterable<? extends E> elements) {
		this.comparator = comparator;
		
		for (int i = 0; i < MAX_LEVEL; i++)
			head.link[i] = tail;
		
		tail.prev = head;
		
		if (elements != null)
			for (E element : elements)
				add(element);
	}

	public static <E extends Comparable<? super E>> SkipList<E> create() {
		return new SkipList<E>(Ordering.natural(), null);
	}

	public static <E> SkipList<E> create(final Comparator<? super E> comparator) {
		checkNotNull(comparator);
		return new SkipList<E>(comparator, null);
	}

	public static <E> SkipList<E> create(final Iterable<? extends E> elements) {
		checkNotNull(elements);
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
	public Comparator<? super E> comparator() {
		return comparator;
	}

	@Override
	public int size() {
		return size;
	}

	public boolean add(E e) {
		final int newLevel = randomLevel();
		final Node<E> newNode = new Node<E>(e, newLevel);
		Node<E> x = head;
		Node<E> y = tail;
		int index;
		for (index = level - 1; index >= newLevel; index--) {
			while (x.get(index) /*x.link[index]*/ != y
					&& comparator.compare(x.get(index).element   /*x.link[index].element*/, e) < 0)
				//x = x.link[index];
				x = x.get(index);
			//y = x.link[index];
			y = x.get(index);
		}
		do {
			while (x.get(index) /*x.link[index]*/ != y
					&& comparator.compare(x.get(index).element   /*x.link[index].element*/, e) < 0)
				//x = x.link[index];
				x = x.get(index);
			//y = x.link[index];
			y = x.get(index);
			//newNode.link[i] = y;
			newNode.link(index, y);			
			//x.link[i] = newNode;
			x.link(index, newNode);
			//newNode.prev = x;
			newNode.linkPrevious(x);
			//y.prev = newNode;
			y.linkPrevious(newNode);
		} while (--index >= 0);
		if (newLevel > level)
			level = newLevel;
		size++;
		return true;
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private Node<E> node = head.next();

			@Override
			public boolean hasNext() {
				return node != tail;
			}

			@Override
			public E next() {
				if (!hasNext())
					throw new NoSuchElementException();
				E e = node.element;
				node = node.next();
				return e;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	private static class Node<E> {
		private final E element;
		private final Node<E>[] link;
		private Node<E> prev;

		private Node(final E element, final int level) {
			this.element = element;
			link = new Node[level];
		}
		
		private Node<E> prev(){
			return prev;
		}
		
		private void linkPrevious(final Node<E> node){
			prev = node;
		}
		
		private Node<E> next(){
			return get(0);
		}
		
		private Node<E> get(final int index){
			return link[index];
		}
		
		private void link(final int index, final Node<E> node){
			link[index] = node;
		}
		
	}

	private int randomLevel() {
		int l = 1;
		while (random.nextDouble() < P && l < MAX_LEVEL)
			l++;
		return l;
	}

	// private int randomLevel() {
	//
	// int level = 0;
	// int r = randomSeed;
	// randomSeed = r * 134775813 + 1;
	// if (r < 0) {
	// while ((r <<= 1) > 0)
	// ++level;
	// }
	// return level +1;
	// }

}
