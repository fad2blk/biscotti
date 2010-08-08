package collections;

import static com.google.common.base.Preconditions.*;

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
 * A {@link List} that maintains its elements in sorted order, based on a
 * modified <a href="http://en.wikipedia.org/wiki/Red-black_tree">red-black
 * tree</a>. Elements are ordered from <i>least</i> to <i>greatest</i> according
 * to their <i>natural ordering</i>, or by an explicit {@link Comparator}
 * provided at creation. Attempting to remove or insert {@code null} elements
 * will fail cleanly and safely leaving this queue unmodified. Querying for
 * {@code null} elements is allowed. Inserting non-comparable elements will
 * result in a {@code ClassCastException}. The {@code add(int, E)} , {@code
 * addAll(int, Collection)}, and {@code set(int, E)} operations are not
 * supported.
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
public class TreeList<E> extends AbstractList<E> {

	private int size = 0;
	private Node max = null;
	private Node min = null;
	private Node root = null;
	private static final boolean RED = false;
	private static final boolean BLACK = true;
	private Comparator<? super E> comparator;

	private TreeList(final Comparator<? super E> comparator) {
		if (comparator != null)
			this.comparator = comparator;
		else
			this.comparator = (Comparator<? super E>) Ordering.natural();
	}

	private TreeList(final Iterable<? extends E> elements) {
		if (elements instanceof TreeList<?>)
			comparator = ((TreeList) elements).comparator();
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
	 * Creates a new {@code TreeList} that orders its elements according to
	 * their natural ordering.
	 * 
	 * @return a new {@code TreeList} that orders its elements according to
	 *         their natural ordering
	 */
	public static <E> TreeList<E> create() {
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
	 * {@code Iterable}. If the specified iterable is an instance of a {@code
	 * SortedSet}, {@code java.util.PriorityQueue java.util.PriorityQueue},
	 * {@code PriorityQueue}, or this {@code TreeList}, this list will be
	 * ordered according to the same ordering. Otherwise, this priority queue
	 * will be ordered according to the {@link Comparable natural ordering} of
	 * its elements.
	 * 
	 * @param elements
	 *            the iterable whose elements are to be placed into the queue
	 * @return a new {@code TreeList} containing the elements of the specified
	 *         iterable
	 * @throws ClassCastException
	 *             if elements of the specified iterable cannot be compared to
	 *             one another according to the priority queue's ordering
	 * @throws NullPointerException
	 *             if any of the elements of the specified iterable or the
	 *             iterable itself is {@code null}
	 */
	@SuppressWarnings("unchecked")
	public static <E> TreeList<E> create(final Iterable<? extends E> elements) {
		checkNotNull(elements);
		return new TreeList(elements);
	}

	/**
	 * Returns the comparator used to order the elements in this list. If one
	 * was not explicitly provided a <i>natural order</i> comparator is
	 * returned.
	 * 
	 * @return the comparator used to order this list
	 */
	public Comparator<? super E> comparator() {
		return comparator;
	}

	/**
	 * Inserts the specified element into this list in sorted order.
	 */
	@Override
	public boolean add(E e) {
		checkNotNull(e);
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
	public boolean addAll(Collection<? extends E> c) {
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
		if (o != null) {
			Iterator<E> itor = iterator();
			for (int i = 0; i < size; i++)
				if (itor.next().equals(o))
					return i;
		}
		return -1;
	}

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
				return index < size;
			}

			@Override
			public boolean hasPrevious() {
				return index > 0;
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
	 * The returned iterator does not support the {@code add(E)} and {@code
	 * set(E)} operations.
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
	public List<E> subList(int fromIndex, int toIndex) {
		checkPositionIndexes(fromIndex, toIndex, size);
		return super.subList(fromIndex, toIndex);
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

	protected Node predecessor(final Node node) {
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

}
