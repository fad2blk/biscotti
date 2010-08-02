package collections;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

import base.Preconditions2;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

/**
 * A <i>sorted</i> {@link List} based on a modified <a
 * href="http://en.wikipedia.org/wiki/Red-black_tree">red-black tree</a>.
 * Elements are ordered from <i>least</i> to <i>greatest</i> according to their
 * <i>natural ordering</i>, or by an explicit {@link Comparator} provided at
 * creation. Attempting to remove or insert {@code null} elements will fail
 * cleanly and safely leaving this queue unmodified. Querying for {@code null}
 * elements is allowed. Inserting non-comparable elements will result in a
 * {@code ClassCastException}. The {@code add(int, E)} , {@code addAll(int,
 * Collection)}, and {@code set(int, E)} operations are not supported.
 * <p>
 * The iterators obtained from the {@link #iterator()} and
 * {@link #listIterator()} methods are <i>fail-fast</i>. Attempts to modify the
 * elements in this list at any time after an iterator is created, in any way
 * except through the iterator's own remove method, will result in a {@code
 * ConcurrentModificationException}. Further, the list iterator does not support
 * the {@code add(E)} and {@code set(E)} operations.
 * <p>
 * This list not <i>thread-safe</i>. If multiple threads modify this list
 * concurrently it must be synchronized externally, considering "wrapping" the
 * list using the {@code Collections.synchronizedList(List)} method.
 * <p>
 * <b>Implementation Note:</b>This implementation uses a comparator (whether or
 * not one is explicitly provided) to maintain priority order, and {@code
 * equals} when testing for element equality. The ordering imposed by the
 * comparator must be <i>consistent with equals</i> if this list is to function
 * correctly.
 * <p>
 * The underlying red-black tree provides the following worst case running time
 * (where <i>n</i> is the size of this queue, and <i>m</i> is the size of the
 * specified collection):
 * <p>
 * <table border cellpadding="3" cellspacing="1">
 * <tr>
 * <th align="center">Method</th>
 * <th align="center">Running Time</th>
 * </tr>
 * <tr>
 * <td>
 * {@link #addAll(Collection)}<br>
 * {@link #containsAll(Collection) containsAll(Collection)}</br>
 * {@link #retainAll(Collection) retainAll(Collection)}</br>
 * {@link #removeAll(Collection) removeAll(Collection)}</td>
 * <td align="center"><i>O(m log n)</i></td>
 * </tr>
 * <tr>
 * <td>
 * {@link #clear() clear()}<br>
 * {@link #indexOf(Object)}<br>
 * {@link #lastIndexOf(Object)}<br>
 * {@link #get(int)}<br>
 * {@link #remove(int)}</br></td>
 * <td align="center"><i>O(n)</i></td>
 * </tr>
 * <tr>
 * <td>
 * {@link #add(Object) add(E)}</br> {@link #contains(Object)}</br>
 * {@link #remove(Object)}</br></td>
 * <td align="center"><i>O(log n)</i></td>
 * </tr>
 * <tr>
 * <td>
 * {@link #isEmpty() isEmpty()}</br> {@link #size()}<br>
 * </td>
 * <td align="center"><i>O(1)</i></td>
 * </tr>
 * </table>
 * <p>
 * 
 * @author Zhenya Leonov
 * @param <E>
 *            the type of elements maintained by this list
 */
public class SortedList<E> extends AbstractCollection<E> implements List<E>,
		Serializable {

	private static final long serialVersionUID = 1L;
	private int size = 0;
	private Node max = null;
	private Node min = null;
	private Node root = null;
	private int count = 0;
	private static final boolean RED = false;
	private static final boolean BLACK = true;
	private Comparator<? super E> comparator;

	protected SortedList() {
	};

	private SortedList(final Comparator<? super E> comparator) {
		if (comparator != null)
			this.comparator = comparator;
		else
			this.comparator = (Comparator<? super E>) Ordering.natural();
	}

	private SortedList(final Iterable<? extends E> elements) {
		if (elements instanceof SortedList<?>)
			comparator = ((SortedList) elements).comparator();
		else if (elements instanceof SortedSet<?>)
			comparator = ((SortedSet) elements).comparator();
		else if (elements instanceof java.util.PriorityQueue<?>)
			comparator = ((java.util.PriorityQueue) elements).comparator();
		else if (elements instanceof PriorityQueue<?>)
			comparator = ((PriorityQueue) elements).comparator();
		else
			comparator = (Comparator<? super E>) Ordering.natural();
		for (E element : elements)
			add(element);
	}

	/**
	 * Creates a new {@code SortedList} that orders its elements according to
	 * their natural ordering.
	 * 
	 * @return a new {@code SortedList} that orders its elements according to
	 *         their natural ordering
	 */
	public static <E> SortedList<E> create() {
		return new SortedList<E>((Comparator<? super E>) null);
	}

	/**
	 * Creates a new {@code SortedList} that orders its elements according to
	 * the specified comparator.
	 * 
	 * @param comparator
	 *            the comparator that will be used to order this priority list
	 * @return a new {@code SortedList} that orders its elements according to
	 *         {@code comparator}
	 */
	public static <E> SortedList<E> create(
			final Comparator<? super E> comparator) {
		Preconditions.checkNotNull(comparator);
		return new SortedList<E>(comparator);
	}

	/**
	 * Creates a new {@code SortedList} containing the elements of the specified
	 * {@code Iterable}. If the specified iterable is an instance of a {@code
	 * SortedSet}, {@code java.util.PriorityQueue java.util.PriorityQueue},
	 * {@code PriorityQueue}, or this {@code SortedList}, this list will be
	 * ordered according to the same ordering. Otherwise, this priority queue
	 * will be ordered according to the {@link Comparable natural ordering} of
	 * its elements.
	 * 
	 * @param elements
	 *            the iterable whose elements are to be placed into the queue
	 * @return a new {@code SortedList} containing the elements of the specified
	 *         iterable
	 * @throws ClassCastException
	 *             if elements of the specified iterable cannot be compared to
	 *             one another according to the priority queue's ordering
	 * @throws NullPointerException
	 *             if any of the elements of the specified iterable or the
	 *             iterable itself is {@code null}
	 */
	@SuppressWarnings("unchecked")
	public static <E> SortedList<E> create(final Iterable<? extends E> elements) {
		Preconditions.checkNotNull(elements);
		return new SortedList(elements);
	}

	/**
	 * Returns the comparator used to order the elements in this list. If one
	 * was not explicitly provided a <i>natural order</i> comparator is
	 * returned.
	 * 
	 * @return the comparator used to order this queue
	 */
	public Comparator<? super E> comparator() {
		return comparator == Ordering.natural() ? null : comparator;
	}

	/**
	 * Inserts the specified element into this list in sorted order.
	 */
	@Override
	public boolean add(E e) {
		Preconditions.checkNotNull(e);
		Node newNode;
		if (root == null)
			min = max = root = new Node(e, null);
		else {
			int cmp;
			Node parent;
			Node t = root;
			do {
				parent = t;
				cmp = comparator.compare(e, t.element);
				if (cmp <= 0)
					t = t.left;
				else
					t = t.right;
			} while (t != null);
			newNode = new Node(e, parent);
			if (cmp <= 0)
				parent.left = newNode;
			else
				parent.right = newNode;
			fixAfterInsertion(newNode);
			if (comparator.compare(e, max.element) > 0)
				max = newNode;
			else if (comparator.compare(e, min.element) <= 0)
				min = newNode;
		}
		size++;
		count++;
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
	 * Adds all of the elements in the specified collection to this list.
	 * Attempts to {@code addAll} of a list to itself will result in an {@code
	 * IllegalArgumentException}. Further, the behavior of this operation is
	 * undefined if the specified collection is modified while the operation is
	 * in progress.
	 * <p>
	 * This implementation iterates over the specified collection, and adds each
	 * element returned by the iterator to this list, in turn.
	 * <p>
	 * Attempts to {@code addAll} of a collection which contains {@code null}
	 * elements will fail cleanly and safely leaving this list unmodified. If
	 * you are not sure whether or not your collection contains {@code null}
	 * elements considering filtering it by calling {@link Collections2#filter
	 * Collections2.filter(Collection, Predicate)} with a predicate obtained
	 * from {@link Predicates#notNull()}. Other runtime exceptions encountered
	 * while trying to add an element may result in only some of the elements
	 * having been successfully added when the associated exception is thrown.
	 * 
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             {@inheritDoc}
	 * @throws IllegalArgumentException
	 *             {@inheritDoc}
	 * @throws IllegalStateException
	 *             {@inheritDoc}
	 * @see #add(Object) add(E)
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		Preconditions.checkNotNull(c);
		Preconditions.checkState(c != this);
		for (E e : c)
			Preconditions.checkNotNull(e);
		boolean modified = false;
		for (E e : c)
			if (add(e))
				modified = true;
		return modified;
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
		Preconditions.checkElementIndex(index, size);
		Iterator<E> itor = iterator();
		for (int i = 0; i < index; i++)
			itor.next();
		return itor.next();
	}

	@Override
	public int indexOf(Object o) {
		if (o != null) {
			Iterator<E> itor = iterator();
			for (int i = 0; i < size; i++)
				if (itor.next().equals(o))
					return i;
		}
		return -1;
	}

	/**
	 * Returns an iterator over the elements in this list in sorted order.
	 * 
	 * @return an iterator over the elements in this list in sorted order
	 */
	@Override
	public Iterator<E> iterator() {
		return listIterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		if (o != null) {
			Iterator<E> itor = iterator();
			for (int i = 0; i < size; i++)
				if (itor.next().equals(o)) {
					while (itor.hasNext() && itor.next().equals(o))
						i++;
					return i;
				}
		}
		return -1;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The returned iterator does not support the {@code add(E)} and {@code
	 * set(E)} operations.
	 */
	@Override
	public ListIterator<E> listIterator() {
		return new ListItor(0);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The returned iterator does not support the {@code add(E)} and {@code
	 * set(E)} operations.
	 */
	@Override
	public ListIterator<E> listIterator(int index) {
		Preconditions.checkPositionIndex(index, size);
		ListIterator<E> li = listIterator();
		for (int i = 0; i < index; i++)
			li.next();
		return li;
	}

	/**
	 * Removes the first occurrence of the specified element from this list, if
	 * it is present (optional operation). If this list does not contain the
	 * element, it is unchanged.
	 */
	@Override
	public boolean remove(Object o) {
		Preconditions.checkNotNull(o);
		Node node = search((E) o);
		if (node == null)
			return false;
		delete(node);
		return true;
	}

	@Override
	public E remove(int index) {
		Preconditions.checkElementIndex(index, size);
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

	/**
	 * Returns an unmodifiable view of the portion of this list between the
	 * specified {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.
	 * (If fromIndex and toIndex are equal, the returned list is empty.) The
	 * returned list is not backed by this list, changes in to this list are not
	 * reflected in the returned list.
	 * 
	 * @param fromIndex
	 *            {@inheritDoc}
	 * @param toIndex
	 *            {@inheritDoc}
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	@Override
	public ImmutableList<E> subList(int fromIndex, int toIndex) {
		Preconditions2.checkElementIndexes(fromIndex, toIndex, size);
		ImmutableList.Builder<E> builder = ImmutableList.builder();
		return builder.addAll(this).build().subList(fromIndex, toIndex);
	}

	// serializable object
	private void writeObject(java.io.ObjectOutputStream s)
			throws java.io.IOException {
		s.defaultWriteObject();
		s.writeInt(size);
		s.writeObject(comparator);
		for (E e : this)
			s.writeObject(e);
	}

	// deserializable object
	private void readObject(java.io.ObjectInputStream s)
			throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		int size = s.readInt();
		comparator = (Comparator<? super E>) s.readObject();
		for (int i = 0; i < size; i++)
			add((E) s.readObject());
	}

	private class ListItor implements ListIterator<E> {
		int start;
		int index = 0;
		Node next = min;
		Node prev = null;
		Node last = null;
		int count = SortedList.this.count;

		ListItor(int start) {
			this.start = start;
		}

		@Override
		public void add(E e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasNext() {
			return index < size;
		}

		@Override
		public boolean hasPrevious() {
			return index > start;
		}

		@Override
		public E next() {
			if (index == size)
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
			if (index == start)
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
			if (last == null)
				throw new IllegalStateException();
			checkForConcurrentModification();
			if (last.left != null && last.right != null)
				next = last;
			delete(last);
			index--;
			count = SortedList.this.count;
			last = null;
		}

		@Override
		public void set(E e) {
			throw new UnsupportedOperationException();
		}

		private void checkForConcurrentModification() {
			if (count != SortedList.this.count)
				throw new ConcurrentModificationException();
		}
	}

	// Red-Black-Tree methods

	protected class Node {
		E element;
		Node left = null;
		Node right = null;
		Node parent;
		boolean color = BLACK;

		Node(final E element, final Node parent) {
			this.element = element;
			this.parent = parent;
		}
	}

	protected Node search(final E e) {
		Node node = root;
		while (node != null && e != null) {
			int cmp = comparator.compare(e, node.element);
			if (cmp == 0 && e.equals(node.element))
				return node;
			if (cmp <= 0)
				node = node.left;
			else
				node = node.right;
		}
		return null;
	}

	protected void delete(Node node) {
		size--;
		count++;
		if (max == node)
			max = predecessor(node);
		if (min == node)
			min = successor(node);
		if (node.left != null && node.right != null) {
			Node successor = successor(node);
			node.element = successor.element;
			node = successor;
		}
		Node replacement = (node.left != null ? node.left : node.right);
		if (replacement != null) {
			replacement.parent = node.parent;
			if (node.parent == null)
				root = replacement;
			else if (node == node.parent.left)
				node.parent.left = replacement;
			else
				node.parent.right = replacement;
			node.left = node.right = node.parent = null;
			if (node.color == BLACK)
				fixAfterDeletion(replacement);
		} else if (node.parent == null) {
			root = null;
		} else {
			if (node.color == BLACK)
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

	private Node successor(Node node) {
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

	protected Node predecessor(Node node) {
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
			Node temp = node.right;
			node.right = temp.left;
			if (temp.left != null)
				temp.left.parent = node;
			temp.parent = node.parent;
			if (node.parent == null)
				root = temp;
			else if (node.parent.left == node)
				node.parent.left = temp;
			else
				node.parent.right = temp;
			temp.left = node;
			node.parent = temp;
		}
	}

	private void rotateRight(final Node node) {
		if (node != null) {
			Node temp = node.left;
			node.left = temp.right;
			if (temp.right != null)
				temp.right.parent = node;
			temp.parent = node.parent;
			if (node.parent == null)
				root = temp;
			else if (node.parent.right == node)
				node.parent.right = temp;
			else
				node.parent.left = temp;
			temp.right = node;
			node.parent = temp;
		}
	}

	private void fixAfterInsertion(Node node) {
		node.color = RED;
		while (node != null && node != root && node.parent.color == RED) {
			if (parentOf(node) == leftOf(parentOf(parentOf(node)))) {
				Node y = rightOf(parentOf(parentOf(node)));
				if (colorOf(y) == RED) {
					setColor(parentOf(node), BLACK);
					setColor(y, BLACK);
					setColor(parentOf(parentOf(node)), RED);
					node = parentOf(parentOf(node));
				} else {
					if (node == rightOf(parentOf(node))) {
						node = parentOf(node);
						rotateLeft(node);
					}
					setColor(parentOf(node), BLACK);
					setColor(parentOf(parentOf(node)), RED);
					rotateRight(parentOf(parentOf(node)));
				}
			} else {
				Node y = leftOf(parentOf(parentOf(node)));
				if (colorOf(y) == RED) {
					setColor(parentOf(node), BLACK);
					setColor(y, BLACK);
					setColor(parentOf(parentOf(node)), RED);
					node = parentOf(parentOf(node));
				} else {
					if (node == leftOf(parentOf(node))) {
						node = parentOf(node);
						rotateRight(node);
					}
					setColor(parentOf(node), BLACK);
					setColor(parentOf(parentOf(node)), RED);
					rotateLeft(parentOf(parentOf(node)));
				}
			}
		}
		root.color = BLACK;
	}

	private void fixAfterDeletion(Node node) {
		while (node != root && colorOf(node) == BLACK) {
			if (node == leftOf(parentOf(node))) {
				Node sib = rightOf(parentOf(node));
				if (colorOf(sib) == RED) {
					setColor(sib, BLACK);
					setColor(parentOf(node), RED);
					rotateLeft(parentOf(node));
					sib = rightOf(parentOf(node));
				}
				if (colorOf(leftOf(sib)) == BLACK
						&& colorOf(rightOf(sib)) == BLACK) {
					setColor(sib, RED);
					node = parentOf(node);
				} else {
					if (colorOf(rightOf(sib)) == BLACK) {
						setColor(leftOf(sib), BLACK);
						setColor(sib, RED);
						rotateRight(sib);
						sib = rightOf(parentOf(node));
					}
					setColor(sib, colorOf(parentOf(node)));
					setColor(parentOf(node), BLACK);
					setColor(rightOf(sib), BLACK);
					rotateLeft(parentOf(node));
					node = root;
				}
			} else {
				Node sib = leftOf(parentOf(node));
				if (colorOf(sib) == RED) {
					setColor(sib, BLACK);
					setColor(parentOf(node), RED);
					rotateRight(parentOf(node));
					sib = leftOf(parentOf(node));
				}
				if (colorOf(rightOf(sib)) == BLACK
						&& colorOf(leftOf(sib)) == BLACK) {
					setColor(sib, RED);
					node = parentOf(node);
				} else {
					if (colorOf(leftOf(sib)) == BLACK) {
						setColor(rightOf(sib), BLACK);
						setColor(sib, RED);
						rotateLeft(sib);
						sib = leftOf(parentOf(node));
					}
					setColor(sib, colorOf(parentOf(node)));
					setColor(parentOf(node), BLACK);
					setColor(leftOf(sib), BLACK);
					rotateRight(parentOf(node));
					node = root;
				}
			}
		}
		setColor(node, BLACK);
	}

	private boolean colorOf(final Node p) {
		return (p == null ? BLACK : p.color);
	}

	private Node parentOf(final Node p) {
		return (p == null ? null : p.parent);
	}

	private void setColor(final Node p, final boolean c) {
		if (p != null)
			p.color = c;
	}

	private Node leftOf(final Node p) {
		return (p == null) ? null : p.left;
	}

	private Node rightOf(final Node p) {
		return (p == null) ? null : p.right;
	}

	// private static class SubList1<E> extends AbstractList<E> {
	//		
	// int size;
	//		
	// SubList1(int fromIndex, int toIndex) {
	// size = toIndex - fromIndex;
	// }
	//
	// @Override
	// public boolean add(E e) {
	// throw new UnsupportedOperationException();
	// }
	//
	// @Override
	// public void add(int index, E element) {
	// throw new UnsupportedOperationException();
	// }
	//
	// @Override
	// public boolean addAll(Collection<? extends E> c) {
	// throw new UnsupportedOperationException();
	// }
	//
	// @Override
	// public boolean addAll(int index, Collection<? extends E> c) {
	// throw new UnsupportedOperationException();
	// }
	//
	// @Override
	// public void clear() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public boolean contains(Object o) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public boolean containsAll(Collection<?> c) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public E get(int index) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public int indexOf(Object o) {
	// // TODO Auto-generated method stub
	// return 0;
	// }
	//
	// @Override
	// public boolean isEmpty() {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public Iterator<E> iterator() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public int lastIndexOf(Object o) {
	// // TODO Auto-generated method stub
	// return 0;
	// }
	//
	// @Override
	// public ListIterator<E> listIterator() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public ListIterator<E> listIterator(int index) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public boolean remove(Object o) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public E remove(int index) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public boolean removeAll(Collection<?> c) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public boolean retainAll(Collection<?> c) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public E set(int index, E element) {
	// throw new UnsupportedOperationException();
	// }
	//
	// @Override
	// public int size() {
	// // TODO Auto-generated method stub
	// return 0;
	// }
	//
	// @Override
	// public List<E> subList(int fromIndex, int toIndex) {
	// return new SubList1<E>(fromIndex, toIndex);
	// }
	//
	// @Override
	// public Object[] toArray() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public <T> T[] toArray(T[] a) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// }
	//	
	private static class SubList<E> extends SortedList<E> {

		private final int offset;
		private final SortedList<E> sl;
		private int count;
		private int size;

		SubList(SortedList<E> sl, int fromIndex, int toIndex) {
			offset = fromIndex;
			this.sl = sl;
			size = toIndex - fromIndex;
			count = sl.size();
		}

		@Override
		public boolean add(E e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(int index, E element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(Collection<? extends E> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(int index, Collection<? extends E> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public E set(int index, E element) {
			throw new UnsupportedOperationException();
		}

		public E get(int index) {
			if (index < 0 || index >= size)
				throw new IndexOutOfBoundsException(String.valueOf(index));
			checkForConcurrentModification();
			return sl.get(index + offset);
		}

		public int size() {
			checkForConcurrentModification();
			return size;
		}

		public E remove(int index) {
			if (index < 0 || index >= size)
				throw new IndexOutOfBoundsException(String.valueOf(index));
			checkForConcurrentModification();
			E result = sl.remove(index + offset);
			count = sl.size();
			size--;
			return result;
		}

		public Iterator<E> iterator() {
			return listIterator();
		}

		// public ListIterator<E> listIterator(final int index) {
		// checkForConcurrentModification();
		// rangeCheckForAdd(index);
		//
		// return new ListIterator<E>() {
		// private final ListIterator<E> i = SortedList.this.listIterator(index
		// + offset);
		//
		// public boolean hasNext() {
		// return nextIndex() < size;
		// }
		//
		// public E next() {
		// if (hasNext())
		// return i.next();
		// else
		// throw new NoSuchElementException();
		// }
		//
		// public boolean hasPrevious() {
		// return previousIndex() >= 0;
		// }
		//
		// public E previous() {
		// if (hasPrevious())
		// return i.previous();
		// else
		// throw new NoSuchElementException();
		// }
		//
		// public int nextIndex() {
		// return i.nextIndex() - offset;
		// }
		//
		// public int previousIndex() {
		// return i.previousIndex() - offset;
		// }
		//
		// public void remove() {
		// i.remove();
		// SubList.this.modCount = sortedList.modCount;
		// size--;
		// }
		//
		// public void set(E e) {
		// throw new UnsupportedOperationException();
		// }
		//
		// public void add(E e) {
		// throw new UnsupportedOperationException();
		// }
		// };
		// }

		public ImmutableList<E> subList(int fromIndex, int toIndex) {
			return null;
		}

		private void checkForConcurrentModification() {
			if (this.count != sl.size())
				throw new ConcurrentModificationException();
		}

		@Override
		public int indexOf(Object o) {
			if (o != null) {
				ListIterator<E> itor = sl.listIterator(offset);
				for (int i = 0; i < size; i++)
					if (itor.next().equals(o))
						return i;
			}
			return -1;
		}

		@Override
		public int lastIndexOf(Object o) {
			if (o != null) {
				ListIterator<E> itor = sl.listIterator(offset);
				for (int i = 0; i < size; i++)
					if (itor.next().equals(o)) {
						while (i < size && itor.next().equals(o))
							i++;
						return i;
					}
			}
			return -1;
		}

		@Override
		public ListIterator<E> listIterator() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean contains(Object o) {
			return super.contains(o);
		}

		@Override
		public boolean remove(Object o) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public ListIterator<E> listIterator(int index) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean containsAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Object[] toArray() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T> T[] toArray(T[] a) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
