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
import static com.google.common.base.Preconditions.checkState;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Random;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.googlecode.biscotti.base.CloneNotSupportedException;

/**
 * A {@code List} optimized for efficient <a
 * href="http://en.wikipedia.org/wiki/Random_access">random access</a> insertion
 * and removal operations. Implements all optional list operations, and permits
 * all elements, including {@code null}.
 * <p>
 * The iterators obtained from the {@link #iterator()} and
 * {@link #listIterator()} methods are <i>fail-fast</i>. Attempts to modify the
 * elements in this list at any time after an iterator is created, in any way
 * except through the iterator's own remove method, will result in a
 * {@code ConcurrentModificationException}.
 * <p>
 * This list is not <i>thread-safe</i>. If multiple threads modify this list
 * concurrently it must be synchronized externally.
 * <p>
 * The underlying implementation is based on a <a
 * href="http://en.wikipedia.org/wiki/Skip_list">skip list</a> modified to
 * provide logarithmic running time for all linear list operations (sometimes
 * called rank operations). A skip list is a probabilistic data structure for
 * storing items (in this case indices) in in sorted order. Strictly speaking it
 * is impossible to make any hard guarantees regarding the worst-case
 * performance of this class. Practical performance is <i>expected</i> to be
 * logarithmic with an extremely high degree of probability as the list grows.
 * <p>
 * The following table summarizes the performance of this class as compared to
 * an {@code ArrayList} and a {@code LinkedList}:
 * <p>
 * <table border cellpadding="3" cellspacing="1">
 *   <tr>
 *     <th align="center" rowspan="2">Method</th>
 *     <th align="center" colspan="3">Running Time</th>
 *   </tr>
 *   <tr>
 *     <td align="center"><b>RankList</b><br>(<i>expected</i>)</td>
 *     <td align="center"><b>ArrayList</b><br>(<i>amortized</i>)</td>
 *     <td align="center"><b>LinkedList</b><br>(<i>worst-case</i>)</td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #add(Object) add(E)}</br>
 *     </td>
 *     <td align="center" rowspan="5"><i>O(log n)</i></td>
 *     <td align="center" rowspan="3"><i>O(1)</i></td>
 *     <td align="center"><i>O(1)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #get(int)}</br>
 *       <td align="center" rowspan="4"><i>O(n)</i></td>
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #set(int, Object) set(int, E)}</br>
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #add(int, Object) add(int, E)}</br>
 *     </td>
 *     <td align="center" rowspan="2"><i>O(n)</i></td>
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #remove(int)}</br>
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #contains(Object) contains(Object)}</br>
 *     </td>
 *     <td align="center" rowspan="4" colspan="3"><i>O(n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #indexOf(Object) indexOf(Objecet)}</br>
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #lastIndexOf(Object)}</br>
 *     </td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #remove(Object) remove(Object)}</br>
 *     </td>
 *   </tr>
 * </table>
 * 
 * @author Zhenya Leonov
 * 
 * @param <E>
 * the type of elements maintained by this list
 */
public class RankList<E> extends AbstractList<E> implements List<E>,
		Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	private int size = 0;
	private static final double P = .5;
	private static final int MAX_LEVEL = 32;
	private int level = 1;
	private Random random = new Random();
	private Node<E> head = new Node<E>(null, MAX_LEVEL);
	private Node<E> tail = new Node<E>(null, MAX_LEVEL);

	private RankList() {
		Arrays.fill(head.distance, 1);
		Arrays.fill(head.next, tail);
	}

	public static <E extends Comparable<? super E>> RankList<E> create() {
		return new RankList<E>();
	}

	public static <E> RankList<E> create(final Iterable<? extends E> elements) {
		checkNotNull(elements);
		RankList<E> list = new RankList<E>();
		Iterables.addAll(list, elements);
		return list;
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
		checkPositionIndex(index, size);
		Node<E> curr = head;
		int pos = 0;
		final int newLevel = randomLevel();
		final Node<E> newNode = new Node<E>(element, newLevel);
		if (newLevel > level) {
			for (int i = level; i < newLevel; i++)
				head.distance[i] = size + 1;
			level = newLevel;
		}
		for (int i = level - 1; i >= 0; i--) {
			while (pos + curr.distance[i] <= index) {
				pos += curr.distance[i];
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
		modCount++;
		size++;
	}

	@Override
	public E get(int index) {
		checkElementIndex(index, size);
		return search(index).element;

	}

	@Override
	public int lastIndexOf(Object o) {
		Node<E> node = tail;
		for (int i = size - 1; i >= 0; i--) {
			node = node.prev;
			if (Objects.equal(node.element, o))
				return i;
		}
		return -1;
	}

	@Override
	public ListIterator<E> listIterator() {
		return new ListIterator<E>() {
			private Node<E> node = head.next[0];
			private Node<E> last = null;
			private int index = 0;
			private int expectedModCount = modCount;

			@Override
			public void add(E element) {
				checkForConcurrentModification();
				RankList.this.add(index++, element);
				expectedModCount = modCount;
				last = null;
				node = node.next[0];
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
				checkForConcurrentModification();
				checkState(last != null);
				RankList.this.remove(--index);
				expectedModCount = modCount;
				last = null;
			}

			@Override
			public void set(E element) {
				checkForConcurrentModification();
				checkState(last != null);
				last.element = element;
			}

			private void checkForConcurrentModification() {
				if (expectedModCount != modCount)
					throw new ConcurrentModificationException();
			}
		};
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		checkPositionIndex(index, size);
		ListIterator<E> listIterator = listIterator();
		for (int i = 0; i < index; i++)
			listIterator.next();
		return listIterator;
	}

	@Override
	public E remove(int index) {
		checkElementIndex(index, size);
		final Node<E>[] update = new Node[level];
		Node<E> node = head;
		int pos = 0;
		for (int i = level - 1; i >= 0; i--) {
			while (pos + node.distance[i] <= index) {
				pos += node.distance[i];
				node = node.next[i];
			}
			update[i] = node;
		}
		node = node.next[0];
		delete(node, update);
		return node.element;
	}

	@Override
	public void clear() {
		Arrays.fill(head.next, tail);
		Arrays.fill(head.distance, 1);
		modCount++;
		size = 0;
	}

	@Override
	public E set(int index, E element) {
		checkElementIndex(index, size);
		Node<E> node = search(index);
		E e = node.element;
		node.element = element;
		return e;
	}

	/**
	 * Returns a shallow copy of this {@code RankList}. The elements themselves
	 * are not cloned.
	 * 
	 * @return a shallow copy of this list
	 * @throws CloneNotSupportedException
	 *             if an attempt is made to clone is a {@code subList},
	 *             {@code headList}, or {@code tailList} view of the parent list
	 */
	@Override
	public RankList<E> clone() {
		RankList<E> clone;
		try {
			clone = (RankList<E>) super.clone();
		} catch (java.lang.CloneNotSupportedException e) {
			throw new InternalError();
		}
		clone.head = new Node<E>(null, MAX_LEVEL);
		clone.tail = new Node<E>(null, MAX_LEVEL);
		Arrays.fill(clone.head.next, clone.tail);
		Arrays.fill(clone.head.distance, 1);
		clone.random = new Random();
		clone.modCount = 0;
		clone.size = 0;
		clone.addAll(this);
		return clone;
	}

	private void writeObject(java.io.ObjectOutputStream oos)
			throws java.io.IOException {
		oos.defaultWriteObject();
		oos.writeInt(size);
		for (E e : this)
			oos.writeObject(e);
	}

	private void readObject(java.io.ObjectInputStream ois)
			throws java.io.IOException, ClassNotFoundException {
		ois.defaultReadObject();
		Arrays.fill(head.distance, 1);
		Arrays.fill(head.next, tail);
		random = new Random();
		level = 1;
		int size = ois.readInt();
		for (int i = 0; i < size; i++)
			add((E) ois.readObject());
	}

	// Skip List

	private static class Node<E> {
		private E element;
		private Node<E> prev;
		private final Node<E>[] next;
		private final int[] distance;

		private Node(final E element, final int size) {
			this.element = element;
			next = new Node[size];
			distance = new int[size];
		}
	}

	private Node<E> search(final int index) {
		Node<E> curr = head;
		int pos = -1;
		for (int i = level - 1; i >= 0; i--)
			while (pos + curr.distance[i] <= index) {
				pos = pos + curr.distance[i];
				curr = curr.next[i];
			}
		return curr;
	}

	private void delete(final Node<E> node, final Node<E>[] update) {
		node.next[0].prev = node.prev;
		for (int i = 0; i < level; i++)
			if (update[i].next[i] == node) {
				update[i].next[i] = node.next[i];
				update[i].distance[i] += node.distance[i] - 1;
			} else
				update[i].distance[i]--;
		while (head.next[level - 1] == tail && level > 0)
			level--;
		modCount++;
		size--;
	}

	private int randomLevel() {
		int randomLevel = 1;
		while (randomLevel < MAX_LEVEL - 1 && random.nextDouble() < P)
			randomLevel++;
		return randomLevel;
	}

}