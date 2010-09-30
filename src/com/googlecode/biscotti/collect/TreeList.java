package com.googlecode.biscotti.collect;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;
import static com.google.common.base.Preconditions.checkPositionIndexes;
import static com.google.common.base.Preconditions.checkState;
import static com.googlecode.biscotti.base.Preconditions2.checkElementPosition;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

import com.google.common.collect.Ordering;

/**
 * A {@link SortedList} implementation, based on a modified <a
 * href="http://en.wikipedia.org/wiki/Red-black_tree">red-black tree</a>.
 * Elements are ordered from <i>least</i> to <i>greatest</i> according to their
 * <i>natural ordering</i>, or by an explicit {@link Comparator} provided at
 * creation. Attempting to remove or insert {@code null} elements is prohibited.
 * Querying for {@code null} elements is allowed. Inserting non-comparable
 * elements will result in a {@code ClassCastException}. The {@code add(int, E)}
 * , {@code addAll(int, Collection)}, and {@code set(int, E)} operations are not
 * supported.
 * <p>
 * The iterators obtained from the {@link #iterator()} and
 * {@link #listIterator()} methods are <i>fail-fast</i>. Attempts to modify the
 * elements in this list at any time after an iterator is created, in any way
 * except through the iterator's own remove method, will result in a
 * {@code ConcurrentModificationException}. Further, the list iterator does not
 * support the {@code add(E)} and {@code set(E)} operations.
 * <p>
 * This list is not <i>thread-safe</i>. If multiple threads modify this list
 * concurrently it must be synchronized externally, considering "wrapping" the
 * list using the {@link Collections3#synchronize(List)} method.
 * <p>
 * <b>Implementation Note:</b> This implementation uses a comparator (whether or
 * not one is explicitly provided) to both maintain sorted order and test for
 * element equality. Two elements which are deemed equal by the comparator's
 * {@code compare(E, E)} method are, from the standpoint of this list, equal.
 * Thus the ordering maintained by this list must be <i>consistent with
 * equals</i> if this list is to function correctly.
 * <p>
 * The underlying red-black tree provides the following worst case running time
 * for this list and its views (where <i>n</i> is the size of this list, and
 * <i>m</i> is the size of the specified collection):
 * <p>
 * <table border cellpadding="3" cellspacing="1">
 *   <tr>
 *     <th align="center">Method</th>
 *     <th align="center">Running Time</th>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #addAll(Collection) addAll(Collection)}</br>
 *       {@link #containsAll(Collection) containsAll(Collection)}</br>
 *       {@link #retainAll(Collection) retainAll(Collection)}</br>
 *       {@link #removeAll(Collection) removeAll(Collection)}
 *     </td>
 *     <td align="center"><i>O(m log n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #clear() clear()}</br>
 *       {@link #indexOf(Object)}</br>
 *       {@link #lastIndexOf(Object)}</br>
 *       {@link #get(int)}</br>
 *       {@link #remove(int)}</br>
 *     </td>
 *     <td align="center"><i>O(n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #add(Object) add(E)}</br>
 *       {@link #contains(Object)}</br>
 *       {@link #remove(Object)}</br>
 *     </td>
 *     <td align="center"><i>O(log n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #isEmpty() isEmpty()}</br>
 *       {@link #size()}</br>
 *     </td>
 *     <td align="center"><i>O(1)</i></td>
 *   </tr>
 * </table>
 * 
 * @author Zhenya Leonov
 * @param <E>
 *            the type of elements maintained by this list
 */
public class TreeList<E> extends AbstractList<E> implements SortedList<E> {

	private int size = 0;
	private Node max = null;
	private Node min = null;
	private Node root = null;
	private Comparator<? super E> comparator;

	private TreeList(final Comparator<? super E> comparator) {
		if (comparator != null)
			this.comparator = comparator;
		else
			this.comparator = (Comparator<? super E>) Ordering.natural();
	}

	private TreeList(final Iterable<? extends E> elements) {
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

	/**
	 * Creates a new {@code TreeList} that orders its elements according to
	 * their natural ordering.
	 * 
	 * @return a new {@code TreeList} that orders its elements according to
	 *         their natural ordering
	 */
	public static <E extends Comparable<? super E>> TreeList<E> create() {
		return new TreeList<E>((Comparator<? super E>) null);
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
	public static <E> TreeList<E> create(final Comparator<? super E> comparator) {
		checkNotNull(comparator);
		return new TreeList<E>(comparator);
	}

	/**
	 * Creates a new {@code TreeList} containing the elements of the specified
	 * {@code Iterable}. If the specified iterable is an instance of of
	 * {@link SortedSet}, {@link java.util.PriorityQueue
	 * java.util.PriorityQueue}, or {@code SortedCollection}, this list will be
	 * ordered according to the same ordering. Otherwise, this list will be
	 * ordered according to the <i>natural ordering</i> of its elements.
	 * 
	 * @param elements
	 *            the iterable whose elements are to be placed into the list
	 * @return a new {@code TreeList} containing the elements of the specified
	 *         iterable
	 * @throws ClassCastException
	 *             if elements of the specified iterable cannot be compared to
	 *             one another according to this list's ordering
	 * @throws NullPointerException
	 *             if any of the elements of the specified iterable or the
	 *             iterable itself is {@code null}
	 */
	public static <E> TreeList<E> create(final Iterable<? extends E> elements) {
		checkNotNull(elements);
		return new TreeList<E>(elements);
	}

	/**
	 * Creates a new {@code TreeList} containing the specified initial elements
	 * ordered according to their <i>natural ordering</i>.
	 * 
	 * @param elements
	 *            the initial elements to be stored in this list
	 * @return a new {@code TreeList} containing the specified initial elements
	 * @throws ClassCastException
	 *             if specified elements cannot be compared to one another
	 *             according to their natural ordering
	 * @throws NullPointerException
	 *             if any of the specified elements are {@code null}
	 */
	public static <E extends Comparable<? super E>> TreeList<E> create(
			final E... elements) {
		checkNotNull(elements);
		TreeList<E> l = create();
		Collections.addAll(l, elements);
		return l;
	}

	/**
	 * {@inheritDoc} If one was not explicitly provided a <i>natural order</i>
	 * comparator is returned.
	 * 
	 * @return the comparator used to order this queue
	 */
	@Override
	public Comparator<? super E> comparator() {
		return comparator;
	}

	/**
	 * Inserts the specified element into this list in sorted order.
	 */
	@Override
	public boolean add(E e) {
		checkNotNull(e);
		Node newNode = new Node(e);
		if (root == null)
			root = newNode;
		else {
			int cmp;
			Node parent;
			Node t = root;
			do {
				parent = t;
				cmp = comparator.compare(e, t.element);
				if (cmp < 0)
					t = t.left;
				else
					t = t.right;
			} while (t != null);
			newNode.parent = parent;
			if (cmp < 0)
				parent.left = newNode;
			else
				parent.right = newNode;
			fixAfterInsertion(newNode);
			if (max == null || comparator.compare(e, max.element) >= 0)
				max = newNode;
			if (min == null || comparator.compare(e, min.element) < 0)
				min = newNode;
		}
		size++;
		modCount++;
		return true;
	}

	/**
	 * Guaranteed to throw an {@code UnsupportedOperationException} exception
	 * and leave the underlying data unmodified.
	 * 
	 * @throws UnsupportedOperationException
	 *             always
	 */
	@Override
	public void add(int index, E element) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Guaranteed to throw an {@code UnsupportedOperationException} exception
	 * and leave the underlying data unmodified.
	 * 
	 * @throws UnsupportedOperationException
	 *             always
	 */
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Object o) {
		return o != null && search((E) o) != null;
	}

	@Override
	public E get(int index) {
		checkElementIndex(index, size);
		Iterator<E> itor = iterator();
		for (int i = 0; i < index; i++)
			itor.next();
		return itor.next();
	}

	@Override
	public int indexOf(Object o) {
		if (o != null)
			super.indexOf(o);
		return -1;
	}

	@Override
	public Iterator<E> iterator() {
		return listIterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		if (o != null) {
			ListIterator<E> itor = listIterator();
			while (itor.hasNext())
				if (itor.next().equals(o)) {
					while (itor.hasNext() && itor.next().equals(o))
						;
					return itor.previousIndex();
				}
		}
		return -1;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The returned iterator does not support the {@code add(E)} and
	 * {@code set(E)} operations.
	 */
	@Override
	public ListIterator<E> listIterator() {
		// return new BoundedListIterator(min, max, this);
		return new ListIterator<E>() {
			int index = 0;
			Node next = min;
			Node prev = null;
			Node last = null;
			int expectedModCount = modCount;

			@Override
			public void add(E e) {
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
				if (index == size())
					throw new NoSuchElementException();
				checkForConcurrentModification();
				Node node = prev = next;
				index++;
				next = successor(node);
				last = node;
				return node.element;
			}

			@Override
			public int nextIndex() {
				return index;
			}

			@Override
			public E previous() {
				if (index == 0)
					throw new NoSuchElementException();
				checkForConcurrentModification();
				Node node = next = prev;
				index--;
				prev = predecessor(node);
				last = node;
				return node.element;
			}

			@Override
			public int previousIndex() {
				return index - 1;
			}

			@Override
			public void remove() {
				checkState(last != null);
				checkForConcurrentModification();
				if (last.left != null && last.right != null)
					next = last;
				delete(last);
				index--;
				expectedModCount = modCount;
				last = null;
			}

			@Override
			public void set(E e) {
				throw new UnsupportedOperationException();
			}

			private void checkForConcurrentModification() {
				if (expectedModCount != modCount)
					throw new ConcurrentModificationException();
			}
		};
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The returned iterator does not support the {@code add(E)} and
	 * {@code set(E)} operations.
	 */
	@Override
	public ListIterator<E> listIterator(int index) {
		checkPositionIndex(index, size);
		ListIterator<E> listIterator = listIterator();
		for (int i = 0; i < index; i++)
			listIterator.next();
		return listIterator;
	}

	@Override
	public boolean remove(Object o) {
		checkNotNull(o);
		Node node = search((E) o);
		if (node == null)
			return false;
		delete(node);
		return true;
	}

	@Override
	public E remove(int index) {
		checkElementIndex(index, size);
		ListIterator<E> li = listIterator(index);
		E e = li.next();
		li.remove();
		return e;
	}

	/**
	 * Guaranteed to throw an {@code UnsupportedOperationException} exception
	 * and leave the underlying data unmodified.
	 * 
	 * @throws UnsupportedOperationException
	 *             always
	 */
	@Override
	public E set(int index, E element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public TreeList<E> headList(E toElement) {
		checkNotNull(toElement);
		Iterator<E> itor = iterator();
		int toIndex = 0;
		while (itor.hasNext() && comparator.compare(itor.next(), toElement) < 0)
			toIndex++;
		return new SubList(this, 0, toIndex);
	}

	@Override
	public TreeList<E> subList(int fromIndex, int toIndex) {
		checkPositionIndexes(fromIndex, toIndex, size);
		return new SubList(this, fromIndex, toIndex);
	}

	@Override
	public TreeList<E> subList(E fromElement, E toElement) {
		checkNotNull(fromElement);
		checkNotNull(toElement);
		checkArgument(comparator.compare(fromElement, toElement) <= 0);
		Iterator<E> itor = iterator();
		int fromIndex = 0;
		while (itor.hasNext()
				&& comparator.compare(itor.next(), fromElement) < 0)
			fromIndex++;
		int toIndex = fromIndex + 1;
		while (itor.hasNext() && comparator.compare(itor.next(), toElement) < 0)
			toIndex++;
		return new SubList(this, fromIndex, toIndex);
	}

	@Override
	public TreeList<E> tailList(E fromElement) {
		checkNotNull(fromElement);
		Iterator<E> itor = iterator();
		int fromIndex = 0;
		while (itor.hasNext()
				&& comparator.compare(itor.next(), fromElement) < 0)
			fromIndex++;
		return new SubList(this, fromIndex, size);
	}

	// not supported
	private void readObject(java.io.ObjectInputStream ois)
			throws ClassNotFoundException, java.io.IOException {
		throw new java.io.NotSerializableException();
	}

	// not supported
	private void writeObject(java.io.ObjectOutputStream ois)
			throws java.io.IOException {
		throw new java.io.NotSerializableException();
	}

	private class SubList extends TreeList<E> {
		private TreeList<E> l;
		private int offset;
		private int size;
		private Node minimum;
		private Node maximum;
		private int expectedModCount;

		private void checkForConcurrentModification() {
			if (expectedModCount != l.modCount)
				throw new ConcurrentModificationException();
		}

		public SubList(TreeList<E> l, int fromIndex, int toIndex) {
			super(l.comparator);
			this.l = l;
			offset = fromIndex;
			expectedModCount = l.modCount;
			this.size = toIndex - fromIndex;
			minimum = l.min;

			int i = 0;
			for (; i < fromIndex; i++)
				minimum = successor(minimum);
			maximum = minimum;
			for (; i < toIndex - 1; i++)
				maximum = successor(maximum);
		}

		@Override
		public boolean add(E e) {
			checkElementPosition(e, minimum.element, maximum.element,
					comparator);
			l.add(e);
			expectedModCount = l.modCount;
			size++;
			if (comparator.compare(maximum.element, e) == 0)
				maximum = successor(maximum);
			return true;
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
		public boolean contains(Object o) {
			checkForConcurrentModification();
			return o != null && search((E) o) != null;
		}

		@Override
		public E get(int index) {
			checkElementIndex(index, size);
			checkForConcurrentModification();
			return l.get(index + offset);
		}

		@Override
		public ListIterator<E> listIterator() {
			return listIterator(0);
		}

		@Override
		public ListIterator<E> listIterator(final int index) {
			checkForConcurrentModification();
			checkPositionIndex(index, size);

			return new ListIterator<E>() {
				private ListIterator<E> i = l.listIterator(index + offset);

				@Override
				public boolean hasNext() {
					return nextIndex() < size;
				}

				@Override
				public E next() {
					if (hasNext())
						return i.next();
					else
						throw new NoSuchElementException();
				}

				@Override
				public boolean hasPrevious() {
					return previousIndex() >= 0;
				}

				@Override
				public E previous() {
					if (hasPrevious())
						return i.previous();
					else
						throw new NoSuchElementException();
				}

				@Override
				public int nextIndex() {
					return i.nextIndex() - offset;
				}

				@Override
				public int previousIndex() {
					return i.previousIndex() - offset;
				}

				@Override
				public void remove() {
					i.remove();
					expectedModCount = l.modCount;
					size--;
					// modCount++;
				}

				@Override
				public void set(E e) {
					throw new UnsupportedOperationException();
				}

				@Override
				public void add(E e) {
					throw new UnsupportedOperationException();
				}
			};
		}

		@Override
		public boolean remove(Object o) {
			checkForConcurrentModification();
			checkNotNull(o);
			Node node = search((E) o);
			if (node == null)
				return false;
			if (node == maximum)
				maximum = predecessor(maximum);
			if (node == minimum)
				minimum = successor(minimum);
			l.delete(node);
			expectedModCount = l.modCount;
			// modCount++;
			size--;
			return true;
		}

		@Override
		public E remove(int index) {
			checkForConcurrentModification();
			checkElementIndex(index, size);
			if (index == 0)
				minimum = successor(minimum);
			if (index == size - 1)
				maximum = predecessor(maximum);
			E e = l.remove(index + offset);
			expectedModCount = l.modCount;
			// modCount++;
			size--;
			return e;
		}

		@Override
		public E set(int index, E element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int size() {
			checkForConcurrentModification();
			return size;
		}

		// not supported by sub views
		private void readObject(java.io.ObjectInputStream ois)
				throws ClassNotFoundException, java.io.IOException {
			throw new java.io.NotSerializableException();
		}

		// not supported by sub views
		private void writeObject(java.io.ObjectOutputStream ois)
				throws java.io.IOException {
			throw new java.io.NotSerializableException();
		}

		@Override
		Node search(final E e) {
			Node node;
			int compareMin = comparator.compare(e, minimum.element);
			int compareMax = comparator.compare(e, maximum.element);
			if (compareMin < 0 || compareMax > 0)
				return null;
			if (compareMin == 0) {
				for (node = minimum; comparator.compare(e, node.element) == 0; node = successor(node))
					if (e.equals(node.element))
						return node;
				return null;
			}
			if (compareMax == 0) {
				for (node = maximum; comparator.compare(e, node.element) == 0; node = predecessor(node))
					if (e.equals(node.element))
						return node;
				return null;
			}
			return l.search(e);
		}
	}

	// Red-Black-Tree methods

	private static enum Color {
		BLACK, RED;
	}

	private class Node {
		private E element = null;
		private Node left = null;
		private Node right = null;
		private Node parent = null;
		private Color color = Color.BLACK;

		private Node(final E element) {
			this.element = element;
		}
	}

	Node search(final E e) {
		Node n = root;
		while (n != null && e != null) {
			int cmp = comparator.compare(e, n.element);
			if (cmp == 0 && e.equals(n.element))
				return n;
			if (cmp < 0)
				n = n.left;
			else
				n = n.right;
		}
		return null;
	}

	private void delete(Node node) {
		size--;
		modCount++;
		if (max == node)
			max = predecessor(node);
		if (min == node)
			min = successor(node);
		if (node.left != null && node.right != null) {
			Node successor = successor(node);
			node.element = successor.element;
			node = successor;
		}
		Node n;
		if (node.left != null)
			n = node.left;
		else
			n = node.right;
		if (n != null) {
			n.parent = node.parent;
			if (node.parent == null)
				root = n;
			else if (node == node.parent.left)
				node.parent.left = n;
			else
				node.parent.right = n;
			node.left = node.right = node.parent = null;
			if (node.color == Color.BLACK)
				fixAfterDeletion(n);
		} else if (node.parent == null) {
			root = null;
		} else {
			if (node.color == Color.BLACK)
				fixAfterDeletion(node);
			if (node.parent != null) {
				if (node == node.parent.left)
					node.parent.left = null;
				else if (node == node.parent.right)
					node.parent.right = null;
				node.parent = null;
			}
		}
	}

	private Node successor(final Node node) {
		if (node == null)
			return null;
		else if (node.right != null) {
			Node successor = node.right;
			while (successor.left != null)
				successor = successor.left;
			return successor;
		} else {
			Node parent = node.parent;
			Node child = node;
			while (parent != null && child == parent.right) {
				child = parent;
				parent = parent.parent;
			}
			return parent;
		}
	}

	private Node predecessor(final Node node) {
		if (node == null)
			return null;
		else if (node.left != null) {
			Node predecessor = node.left;
			while (predecessor.right != null)
				predecessor = predecessor.right;
			return predecessor;
		} else {
			Node parent = node.parent;
			Node child = node;
			while (parent != null && child == parent.left) {
				child = parent;
				parent = parent.parent;
			}
			return parent;
		}
	}

	private void rotateLeft(final Node node) {
		if (node != null) {
			Node n = node.right;
			node.right = n.left;
			if (n.left != null)
				n.left.parent = node;
			n.parent = node.parent;
			if (node.parent == null)
				root = n;
			else if (node.parent.left == node)
				node.parent.left = n;
			else
				node.parent.right = n;
			n.left = node;
			node.parent = n;
		}
	}

	private void rotateRight(final Node node) {
		if (node != null) {
			Node n = node.left;
			node.left = n.right;
			if (n.right != null)
				n.right.parent = node;
			n.parent = node.parent;
			if (node.parent == null)
				root = n;
			else if (node.parent.right == node)
				node.parent.right = n;
			else
				node.parent.left = n;
			n.right = node;
			node.parent = n;
		}
	}

	private void fixAfterInsertion(Node node) {
		makeRed(node);
		while (node != null && node != root && isRed(node.parent)) {
			if (parent(node) == leftUncle(node)) {
				Node n = rightUncle(node);
				if (isRed(n)) {
					makeBlack(parent(node));
					makeBlack(n);
					makeRed(grandParent(node));
					node = grandParent(node);
				} else {
					if (node == rightSibling(node)) {
						node = parent(node);
						rotateLeft(node);
					}
					makeBlack(parent(node));
					makeRed(grandParent(node));
					rotateRight(grandParent(node));
				}
			} else {
				Node n = leftUncle(node);
				if (isRed(n)) {
					makeBlack(parent(node));
					makeBlack(n);
					makeRed(grandParent(node));
					node = grandParent(node);
				} else {
					if (node == leftSibling(node)) {
						node = parent(node);
						rotateRight(node);
					}
					makeBlack(parent(node));
					makeRed(grandParent(node));
					rotateLeft(grandParent(node));
				}
			}
		}
		makeBlack(root);
	}

	private void fixAfterDeletion(Node node) {
		while (node != root && isBlack(node)) {
			if (node == leftSibling(node)) {
				Node n = rightSibling(node);
				if (isRed(n)) {
					makeBlack(n);
					makeRed(parent(node));
					rotateLeft(parent(node));
					n = rightSibling(node);
				}
				if (isBlack(left(n)) && isBlack(right(n))) {
					makeRed(n);
					node = parent(node);
				} else {
					if (isBlack(right(n))) {
						makeBlack(left(n));
						makeRed(n);
						rotateRight(n);
						n = rightSibling(node);
					}
					copyColor(n, parent(node));
					makeBlack(parent(node));
					makeBlack(right(n));
					rotateLeft(parent(node));
					node = root;
				}
			} else {
				Node n = leftSibling(node);
				if (isRed(n)) {
					makeBlack(n);
					makeRed(parent(node));
					rotateRight(parent(node));
					n = leftSibling(node);
				}
				if (isBlack(right(n)) && isBlack(left(n))) {
					makeRed(n);
					node = parent(node);
				} else {
					if (isBlack(left(n))) {
						makeBlack(right(n));
						makeRed(n);
						rotateLeft(n);
						n = leftSibling(node);
					}
					copyColor(n, parent(node));
					makeBlack(parent(node));
					makeBlack(left(n));
					rotateRight(parent(node));
					node = root;
				}
			}
		}
		makeBlack(node);
	}

	private Node parent(final Node node) {
		return node == null ? null : node.parent;
	}

	private Node left(final Node node) {
		return node == null ? null : node.left;
	}

	private Node right(final Node node) {
		return node == null ? null : node.right;
	}

	private Node grandParent(final Node node) {
		return (node == null || node.parent == null) ? null
				: node.parent.parent;
	}

	private Node leftSibling(final Node node) {
		return (node == null || node.parent == null) ? null : node.parent.left;
	}

	private Node rightSibling(final Node node) {
		return (node == null || node.parent == null) ? null : node.parent.right;
	}

	private Node leftUncle(final Node node) {
		return (node == null || node.parent == null || node.parent.parent == null) ? null
				: node.parent.parent.left;
	}

	private Node rightUncle(final Node node) {
		return (node == null || node.parent == null || node.parent.parent == null) ? null
				: node.parent.parent.right;
	}

	private boolean isRed(final Node node) {
		return (node == null || node.color == Color.BLACK) ? false : true;
	}

	private boolean isBlack(final Node node) {
		return (node == null || node.color == Color.BLACK) ? true : false;
	}

	private void makeRed(final Node node) {
		if (node != null)
			node.color = Color.RED;
	}

	private void makeBlack(final Node node) {
		if (node != null)
			node.color = Color.BLACK;
	}

	private void copyColor(final Node from, final Node to) {
		if (from != null)
			from.color = to == null ? Color.BLACK : to.color;
	}

	// private class BoundedListIterator implements ListIterator<E>{
	//
	// int index = 0;
	// Node next;
	// Node prev = null;
	// Node last = null;
	// TreeList2<E> l;
	// int expectedModCount;
	//
	// private BoundedListIterator(Node min, Node max, TreeList2<E> l){
	// this.next = min;
	// this.l = l;
	// expectedModCount = l.modCount;
	// }
	//
	//
	//
	// @Override
	// public void add(E e) {
	// throw new UnsupportedOperationException();
	// }
	//
	// @Override
	// public boolean hasNext() {
	// return index < l.size();
	// }
	//
	// @Override
	// public boolean hasPrevious() {
	// return index > 0;
	// }
	//
	// @Override
	// public E next() {
	// checkForConcurrentModification();
	// if (index == l.size())
	// throw new NoSuchElementException();
	// Node node = prev = next;
	// index++;
	// next = successor(node);
	// last = node;
	// return node.element;
	// }
	//
	// @Override
	// public int nextIndex() {
	// return index;
	// }
	//
	// @Override
	// public E previous() {
	// checkForConcurrentModification();
	// if (index == 0)
	// throw new NoSuchElementException();
	// Node node = next = prev;
	// index--;
	// prev = predecessor(node);
	// last = node;
	// return node.element;
	// }
	//
	// @Override
	// public int previousIndex() {
	// return index - 1;
	// }
	//
	// @Override
	// public void remove() {
	// checkForConcurrentModification();
	// checkState(last != null);
	// if (last.left != null && last.right != null)
	// next = last;
	// l.delete(last);
	// index--;
	// expectedModCount = l.modCount;
	// last = null;
	// }
	//
	// @Override
	// public void set(E e) {
	// throw new UnsupportedOperationException();
	// }
	//
	// private void checkForConcurrentModification() {
	// if (expectedModCount != l.modCount)
	// throw new ConcurrentModificationException();
	// }
	// }

}