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
 * list using the {@link Collections3#synchronize(SortedList)} method.
 * <p>
 * <b>Implementation Note:</b> The the ordering maintained by this list must be
 * <i>consistent with equals</i> if this it is to function correctly. This is so
 * because this implementation uses a comparator (whether or not one is
 * explicitly provided) to perform all element comparisons. Two elements which
 * are deemed equal by the comparator's {@code compare(E, E)} method are, from
 * the standpoint of this list, equal.
 * <p>
 * The underlying red-black tree provides the following worst case running time
 * for this list and its views (where <i>n</i> is the size of this list,
 * <i>k</i> is the highest number of duplicate elements of each other, and
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
 *     <td align="center"><i>O(m(lg(n - k) + k))</i></td>
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
 *     <td align="center"><i>O(lg(n - k) + k)</i></td>
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
		size++;
		modCount++;
		Node x = root;
		Node y = null;
		Node z = new Node(e);
		while (x != null) {
			y = x;
			if (comparator.compare(z.element, x.element) < 0)
				x = x.left;
			else
				x = x.right;
		}
		z.parent = y;
		if (y == null)
			root = z;
		else if (comparator.compare(z.element, y.element) < 0)
			y.left = z;
		else
			y.right = z;
		insertFixUp(z);
		if (max == null || comparator.compare(e, max.element) >= 0)
			max = z;
		if (min == null || comparator.compare(z.element, min.element) < 0)
			min = z;
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
		while (n != null) {
			int cmp = comparator.compare(e, n.element);
			if (cmp == 0)
				return n;
			if (cmp < 0)
				n = n.left;
			else
				n = n.right;
		}
		return null;
	}

	void delete(Node z) {
		size--;
		modCount++;
		Node x, y;
		if (max == z)
			max = predecessor(z);
		if (min == z)
			min = successor(z);
		if (z.left != null && z.right != null) {
			y = successor(z);
			z.element = y.element;
			z = y;
		}
		if (z.left != null)
			x = z.left;
		else
			x = z.right;
		if (x != null) {
			x.parent = z.parent;
			if (z.parent == null)
				root = x;
			else if (z == z.parent.left)
				z.parent.left = x;
			else
				z.parent.right = x;
			// z.left = z.right = z.parent = null;
			if (z.color == Color.BLACK)
				deleteFixUp(x);
		} else if (z.parent == null)
			root = null;
		else {
			if (z.color == Color.BLACK)
				deleteFixUp(z);
			if (z.parent != null) {
				if (z == z.parent.left)
					z.parent.left = null;
				else if (z == z.parent.right)
					z.parent.right = null;
				z.parent = null;
			}
		}
	}

	/**
	 * Introduction to Algorithms (CLR) Second Edition
	 * 
	 * <pre>
	 * TREE-SUCCESSOR(x)
	 * if right[x] != NIL
	 *    then return TREE-MINIMUM(right[x])
	 * y = p[x]
	 * while y != NIL and x = right[y]
	 *    do x = y
	 *       y = p[y]
	 * return y
	 */
	private Node successor(Node x) {
		Node y;
		if (x == null)
			return null;
		if (x.right != null) {
			y = x.right;
			while (y.left != null)
				y = y.left;
			return y;
		}
		y = x.parent;
		while (y != null && x == y.right) {
			x = y;
			y = y.parent;
		}
		return y;
	}

	private Node predecessor(Node x) {
		Node y;
		if (x == null)
			return null;
		if (x.left != null) {
			y = x.left;
			while (y.right != null)
				y = y.right;
			return y;
		}
		y = x.parent;
		while (y != null && x == y.left) {
			x = y;
			y = y.left;
		}
		return y;
	}

	/**
	 * Introduction to Algorithms (CLR) Second Edition
	 * 
	 * <pre>
	 * LEFT-ROTATE(T, x)
	 * y = right[x]							Set y.
	 * right[x] = left[y]					Turn y's left subtree into x's right subtree.
	 * if left[y] != nil[T ]
	 *    then p[left[y]] = x
	 * p[y] = p[x]							Link x's parent to y.
	 * if p[x] = nil[T ]
	 *    then root[T ] = y
	 *    else if x = left[p[x]]
	 *            then left[p[x]] = y
	 *            else right[p[x]] = y
	 * left[y] = x							Put x on y's left.
	 * p[x] = y
	 */
	private void leftRotate(final Node x) {
		if (x != null) {
			Node n = x.right;
			x.right = n.left;
			if (n.left != null)
				n.left.parent = x;
			n.parent = x.parent;
			if (x.parent == null)
				root = n;
			else if (x.parent.left == x)
				x.parent.left = n;
			else
				x.parent.right = n;
			n.left = x;
			x.parent = n;
		}
	}

	private void rightRotate(final Node x) {
		if (x != null) {
			Node n = x.left;
			x.left = n.right;
			if (n.right != null)
				n.right.parent = x;
			n.parent = x.parent;
			if (x.parent == null)
				root = n;
			else if (x.parent.right == x)
				x.parent.right = n;
			else
				x.parent.left = n;
			n.right = x;
			x.parent = n;
		}
	}

	/**
	 * Introduction to Algorithms (CLR) Second Edition
	 * 
	 * <pre>
	 * RB-INSERT-FIXUP(T, z)
	 * while color[p[z]] = RED
	 *    do if p[z] = left[p[p[z]]]
	 *          then y = right[p[p[z]]]
	 *               if color[y] = RED
	 *                  then color[p[z]] = BLACK					Case 1
	 *                       color[y] = BLACK						Case 1 
	 *                       color[p[p[z]]] = RED					Case 1
	 *                       z = p[p[z]]							Case 1
	 *                  else if z = right[p[z]]
	 *                          then z = p[z]						Case 2
	 *                               LEFT-ROTATE(T, z)				Case 2
	 *                       color[p[z]] = BLACK					Case 3
	 *                       color[p[p[z]]] = RED					Case 3
	 *                       RIGHT-ROTATE(T, p[p[z]])				Case 3
	 *          else (same as then clause
	 *                        with right and left exchanged)
	 * color[root[T]] = BLACK
	 */
	private void insertFixUp(Node z) {
		Node y;
		setRed(z);
		while (z != root && isRed(z.parent)) {
			if (parent(z) == left(parent(parent(z)))) {
				y = right(parent(parent(z)));
				if (isRed(y)) {
					setBlack(parent(z));
					setBlack(y);
					setRed(parent(parent(z)));
					z = parent(parent(z));
				} else {
					if (z == right(parent(z))) {
						z = parent(z);
						leftRotate(z);
					}
					setBlack(parent(z));
					setRed(parent(parent(z)));
					rightRotate(parent(parent(z)));
				}
			} else { // symmetric
				y = left(parent(parent(z)));
				if (isRed(y)) {
					setBlack(parent(z));
					setBlack(y);
					setRed(parent(parent(z)));
					z = parent(parent(z));
				} else {
					if (z == left(parent(z))) {
						z = parent(z);
						rightRotate(z);
					}
					setBlack(parent(z));
					setRed(parent(parent(z)));
					leftRotate(parent(parent(z)));
				}
			}
		}
		setBlack(root);
	}

	/**
	 * Introduction to Algorithms (CLR) Second Edition
	 * 
	 * <pre>
	 * RB-DELETE-FIXUP(T, x)
	 * while x != root[T ] and color[x] = BLACK
	 *    do if x = left[p[x]]
	 *          then w = right[p[x]]
	 *               if color[w] = RED
	 *                  then color[w] = BLACK								Case 1
	 *                       color[p[x]] = RED								Case 1
	 *                       LEFT-ROTATE(T, p[x])							Case 1
	 *                       w = right[p[x]]								Case 1
	 *               if color[left[w]] = BLACK and color[right[w]] = BLACK
	 *                  then color[w] = RED									Case 2
	 *                       x = p[x]										Case 2
	 *                  else if color[right[w]] = BLACK
	 *                          then color[left[w]] = BLACK					Case 3
	 *                               color[w] = RED							Case 3
	 *                               RIGHT-ROTATE(T,w)						Case 3
	 *                               w = right[p[x]]						Case 3
	 *                       color[w] = color[p[x]]							Case 4
	 *                       color[p[x]] = BLACK							Case 4
	 *                       color[right[w]] = BLACK						Case 4
	 *                       LEFT-ROTATE(T, p[x])							Case 4
	 *                       x = root[T ]									Case 4
	 *          else (same as then clause with right and left exchanged)
	 * color[x] = BLACK
	 */
	private void deleteFixUp(Node x) {
		Node w;
		while (x != root && isBlack(x)) {
			if (x == left(parent(x))) {
				w = right(parent(x));
				if (isRed(w)) {
					setBlack(w);
					setRed(parent(x));
					leftRotate(parent(x));
					w = right(parent(x));
				}
				if (isBlack(left(w)) && isBlack(right(w))) {
					setRed(w);
					x = parent(x);
				} else {
					if (isBlack(right(w))) {
						setBlack(left(w));
						setRed(w);
						rightRotate(w);
						w = right(parent(x));
					}
					copyColor(w, parent(x));
					setBlack(parent(x));
					setBlack(right(w));
					leftRotate(parent(x));
					x = root;
				}
			} else {
				w = left(parent(x));
				if (isRed(w)) {
					setBlack(w);
					setRed(parent(x));
					rightRotate(parent(x));
					w = left(parent(x));
				}
				if (isBlack(right(w)) && isBlack(left(w))) {
					setRed(w);
					x = parent(x);
				} else {
					if (isBlack(left(w))) {
						setBlack(right(w));
						setRed(w);
						leftRotate(w);
						w = left(parent(x));
					}
					copyColor(w, parent(x));
					setBlack(parent(x));
					setBlack(left(w));
					rightRotate(parent(x));
					x = root;
				}
			}
		}
		setBlack(x);
	}

	private Node parent(final Node n) {
		return n != null ? n.parent : null;
	}

	private Node left(final Node n) {
		return n != null ? n.left : null;
	}

	private Node right(final Node n) {
		return n != null ? n.right : null;
	}

	private boolean isRed(final Node n) {
		return n != null ? n.color == Color.RED : false;
	}

	private boolean isBlack(final Node n) {
		return n != null ? n.color == Color.BLACK : true;
	}

	private void setRed(final Node n) {
		if (n != null)
			n.color = Color.RED;
	}

	private void setBlack(final Node n) {
		if (n != null)
			n.color = Color.BLACK;
	}

	private void copyColor(final Node to, final Node from) {
		if (to != null)
			to.color = from != null ? from.color : Color.BLACK;
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