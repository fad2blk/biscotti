package com.googlecode.biscotti.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.SortedSet;
import java.util.concurrent.PriorityBlockingQueue;

import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;

/**
 * An unbounded priority {@link Queue} based on a modified <a
 * href="http://en.wikipedia.org/wiki/Red-black_tree">red-black tree</a>. The
 * elements of this queue are ordered according to their <i>natural
 * ordering</i>, or by an explicit {@link Comparator} provided at creation.
 * Attempting to remove or insert {@code null} elements is prohibited. Querying
 * for {@code null} elements is allowed. Inserting non-comparable elements will
 * result in a {@code ClassCastException}.
 * <p>
 * The first element (the head) of this queue is considered to be the
 * <i>least</i> element with respect to the specified ordering. Elements with
 * equal priority are ordered according to their insertion order.
 * <p>
 * The {@link #iterator()} method returns a <i>fail-fast</i> iterator which is
 * guaranteed to traverse the elements of the queue in priority order. Attempts
 * to modify the elements in this queue at any time after the iterator is
 * created, in any way except through the iterator's own remove method, will
 * result in a {@code ConcurrentModificationException}.
 * <p>
 * This queue is not <i>thread-safe</i>. If multiple threads modify this queue
 * concurrently it must be synchronized externally. Consider using the
 * inherently <i>thread-safe</i> {@link PriorityBlockingQueue} instead, or
 * "wrapping" the queue using the {@link Collections3#synchronize((Queue)}
 * method.
 * <p>
 * <b>Implementation Note:</b> This implementation uses a comparator (whether or
 * not one is explicitly provided) to maintain priority order, and
 * {@code equals} when testing for element equality. The ordering imposed by the
 * comparator is not required to be <i>consistent with equals</i>. For any two
 * elements {@code e1} and {@code e2} such that {@code e1.compareTo(e2) == 0} it
 * is not necessary for {@code e1.equals(e2) == true}. This is allows duplicate
 * elements to have different priority.
 * <p>
 * The underlying red-black tree provides the following worst case running time
 * (where <i>n</i> is the size of this queue, and <i>m</i> is the size of the
 * specified collection):
 * <p>
 * <table border cellpadding="3" cellspacing="1">
 *   <tr>
 *     <th align="center">Method</th>
 *     <th align="center">Running Time</th>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #addAll(Collection)}<br>
 *       {@link #containsAll(Collection) containsAll(Collection)}</br>
 *       {@link #retainAll(Collection) retainAll(Collection)}</br>
 *       {@link #removeAll(Collection) removeAll(Collection)}
 *     </td>
 *     <td align="center"><i>O(m log n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #clear() clear()}<br>
 *     </td>
 *     <td align="center"><i>O(n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #add(Object) add(E)}</br>
 *       {@link #contains(Object)}</br>
 *       {@link #offer(Object) offer(E)}</br>
 *       {@link #remove(Object)}</br>
 *     </td>
 *     <td align="center"><i>O(log n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #element() element()}</br>
 *       {@link #isEmpty() isEmpty()}</br>
 *       {@link #peek()}</br> {@link #poll()}</br>
 *       {@link #remove() remove()}</br>
 *       {@link #size()}</br>
 *   </td>
 *   <td align="center"><i>O(1)</i></td>
 * </tr>
 * </table>
 * <p>
 * This queue uses the same ordering rules as {@link java.util.PriorityQueue
 * java.util.PriorityQueue}. In comparison it provides identical functionality,
 * faster overall running time and ordered traversals via its iterators.
 * 
 * @author Zhenya Leonov
 * @param <E>
 *            the type of elements held in this queue
 */
public class PriorityQueue<E> extends AbstractQueue<E> implements
		SortedCollection<E> {

	private int size = 0;
	Node min = null;
	Node root = null;
	int modCount = 0;
	Comparator<? super E> comparator;

	PriorityQueue(final Comparator<? super E> comparator) {
		if (comparator != null)
			this.comparator = comparator;
		else
			this.comparator = (Comparator<? super E>) Ordering.natural();
	}

	PriorityQueue(final Iterable<? extends E> elements) {
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
	 * Creates a new {@code PriorityQueue} that orders its elements according to
	 * their <i>natural ordering</i>.
	 * 
	 * @return a new {@code PriorityQueue} that orders its elements according to
	 *         their <i>natural ordering</i>
	 */
	public static <E extends Comparable<? super E>> PriorityQueue<E> create() {
		return new PriorityQueue<E>((Comparator<? super E>) null);
	}

	/**
	 * Creates a new {@code PriorityQueue} that orders its elements according to
	 * the specified comparator.
	 * 
	 * @param comparator
	 *            the comparator that will be used to order this priority queue
	 * @return a new {@code PriorityQueue} that orders its elements according to
	 *         {@code comparator}
	 */
	public static <E> PriorityQueue<E> create(
			final Comparator<? super E> comparator) {
		Preconditions.checkNotNull(comparator);
		return new PriorityQueue<E>(comparator);
	}

	/**
	 * Creates a new {@code PriorityQueue} containing the elements of the
	 * specified {@code Iterable}. If the specified iterable is an instance of
	 * {@link SortedSet}, {@link java.util.PriorityQueue
	 * java.util.PriorityQueue}, or {@code SortedCollection} this queue will be
	 * ordered according to the same ordering. Otherwise, this priority queue
	 * will be ordered according to the <i>natural ordering</i> of its elements.
	 * 
	 * @param elements
	 *            the iterable whose elements are to be placed into the queue
	 * @return a new {@code PriorityQueue} containing the elements of the
	 *         specified iterable
	 * @throws ClassCastException
	 *             if elements of the specified iterable cannot be compared to
	 *             one another according to the priority queue's ordering
	 * @throws NullPointerException
	 *             if any of the elements of the specified iterable or the
	 *             iterable itself is {@code null}
	 */
	public static <E> PriorityQueue<E> create(
			final Iterable<? extends E> elements) {
		Preconditions.checkNotNull(elements);
		return new PriorityQueue<E>(elements);
	}

	/**
	 * Creates a new {@code PriorityQueue} containing the specified initial
	 * elements ordered according to their <i>natural ordering</i>.
	 * 
	 * @param elements
	 *            the initial elements to be stored in this queue
	 * @return a new {@code PriorityQueue} containing the specified initial
	 *         elements
	 * @throws ClassCastException
	 *             if specified elements cannot be compared to one another
	 *             according to their natural ordering
	 * @throws NullPointerException
	 *             if any of the specified elements are {@code null}
	 */
	public static <E extends Comparable<? super E>> PriorityQueue<E> create(
			final E... elements) {
		checkNotNull(elements);
		PriorityQueue<E> q = create();
		Collections.addAll(q, elements);
		return q;
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

	@Override
	public boolean offer(E e) {
		checkNotNull(e);
		Node newNode = new Node(e);
		insert(newNode);
		return true;
	}

	@Override
	public E peek() {
		if (isEmpty())
			return null;
		return min.element;
	}

	@Override
	public E poll() {
		if (isEmpty())
			return null;
		E e = min.element;
		delete(min);
		return e;
	}

	@Override
	public boolean contains(Object o) {
		return o != null && search((E) o) != null;
	}

	/**
	 * Returns an iterator over the elements in this queue in priority order
	 * from first (head) to last (tail).
	 * 
	 * @return an iterator over the elements in this queue in priority order
	 */
	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private Node next = min;
			private Node last = null;
			private int expectedModCount = modCount;

			@Override
			public boolean hasNext() {
				return next != null;
			}

			@Override
			public void remove() {
				checkForConcurrentModification();
				if (last == null)
					throw new IllegalStateException();
				if (last.left != null && last.right != null)
					next = last;
				delete(last);
				expectedModCount = modCount;
				last = null;
			}

			@Override
			public E next() {
				checkForConcurrentModification();
				Node node = next;
				if (node == null)
					throw new NoSuchElementException();
				next = successor(node);
				last = node;
				return node.element;
			}

			private void checkForConcurrentModification() {
				if (modCount != expectedModCount)
					throw new ConcurrentModificationException();
			}
		};
	}

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
	public int size() {
		return size;
	}

	// Red-Black-Tree methods

	private static enum Color {
		BLACK, RED;
	}

	class Node {
		E element = null;
		Node left = null;
		Node right = null;
		Node parent = null;
		private Color color = Color.BLACK;

		private Node(final E element) {
			this.element = element;
		}
	}

	private Node search(final E e) {
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

	void insert(final Node node) {
		size++;
		modCount++;
		if (root == null)
			root = node;
		else {
			int cmp;
			Node parent;
			Node n = root;
			do {
				parent = n;
				cmp = comparator.compare(node.element, n.element);
				if (cmp < 0)
					n = n.left;
				else
					n = n.right;
			} while (n != null);
			node.parent = parent;
			if (cmp < 0)
				parent.left = node;
			else
				parent.right = node;
			fixAfterInsertion(node);
		}
		if (min == null || comparator.compare(node.element, min.element) < 0)
			min = node;
	}

	void delete(Node node) {
		size--;
		modCount++;
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

//	public void verifyProperties() {
//		verifyProperty1(root);
//		verifyProperty2(root);
//		// Property 3 is implicit
//		verifyProperty4(root);
//		verifyProperty5(root);
//	}
//
//	private void verifyProperty1(Node n) {
//		assert getColor(n) == Color.RED || getColor(n) == Color.BLACK;
//		if (n == null)
//			return;
//		verifyProperty1(n.left);
//		verifyProperty1(n.right);
//	}
//
//	private void verifyProperty2(Node root) {
//		assert getColor(root) == Color.BLACK;
//	}
//
//	private void verifyProperty4(Node n) {
//		// System.out.println(getColor(n));
//		if (getColor(n) == Color.RED) {
//			assert getColor(n.left) == Color.BLACK;
//			// System.out.println(getColor(n.left));
//			assert getColor(n.right) == Color.BLACK;
//			// System.out.println(getColor(n.right));
//			assert getColor(n.parent) == Color.BLACK;
//			// System.out.println(getColor(n.parent));
//		}
//		if (n == null)
//			return;
//		verifyProperty4(n.left);
//		verifyProperty4(n.right);
//	}
//
//	private void verifyProperty5(Node root) {
//		verifyProperty5Helper(root, 0, -1);
//	}
//
//	private int verifyProperty5Helper(Node n, int blackCount, int pathBlackCount) {
//		if (getColor(n) == Color.BLACK) {
//			blackCount++;
//		}
//		if (n == null) {
//			if (pathBlackCount == -1) {
//				pathBlackCount = blackCount;
//			} else {
//				assert blackCount == pathBlackCount;
//			}
//			return pathBlackCount;
//		}
//		pathBlackCount = verifyProperty5Helper(n.left, blackCount,
//				pathBlackCount);
//		pathBlackCount = verifyProperty5Helper(n.right, blackCount,
//				pathBlackCount);
//		return pathBlackCount;
//	}
//
//	private Color getColor(final Node p) {
//		return (p == null ? Color.BLACK : p.color);
//	}

}
