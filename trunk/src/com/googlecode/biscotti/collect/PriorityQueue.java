package com.googlecode.biscotti.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.AbstractQueue;
import java.util.Collection;
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
 * "wrapping" the queue using the {@link Collections3#synchronize(Queue)}
 * method.
 * <p>
 * <b>Implementation Note:</b> This implementation uses a comparator (whether or
 * not one is explicitly provided) to maintain priority order, and
 * {@code equals} when testing for element equality. The ordering imposed by the
 * comparator is not required to be <i>consistent with equals</i>. Given a
 * comparator {@code c}, for any two elements {@code e1} and {@code e2} such
 * that {@code c.compare(e1, e2) == 0} it is not necessary true that
 * {@code e1.equals(e2) == true}. This is allows duplicate elements to have
 * different priority.
 * <p>
 * The underlying red-black tree provides the following worst case running time
 * (where <i>n</i> is the size of this queue, <i>k</i> is the highest number of
 * duplicate elements of each other, and <i>m</i> is the size of the specified
 * collection):
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
 *     <td align="center"><i>O(m lg n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #clear() clear()}<br>
 *     </td>
 *     <td align="center"><i>O(m(lg(n - k) + k))</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #add(Object) add(E)}</br>
 *       {@link #contains(Object)}</br>
 *       {@link #offer(Object) offer(E)}</br>
 *       {@link #remove(Object)}</br>
 *     </td>
 *     <td align="center"><i>O(lg(n - k) + k)</i></td>
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
		checkNotNull(o);
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
		while (n != null) {
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

	/**
	 * Introduction to Algorithms (CLR) Second Edition
	 * 
	 * <pre>
	 * RB-INSERT(T, z)
	 * y = nil[T ]
	 * x = root[T ]
	 * while x != nil[T ]
	 *    do y = x
	 *       if key[z] < key[x]
	 *          then x = left[x]
	 *          else x = right[x]
	 * p[z] = y
	 * if y = nil[T ]
	 *    then root[T] = z
	 *    else if key[z] < key[y]
	 *            then left[y] = z
	 *            else right[y] = z
	 * left[z] = nil[T ]
	 * right[z] = nil[T ]
	 * color[z] = RED
	 * RB-INSERT-FIXUP(T, z)
	 */
	void insert(final Node z) {
		size++;
		modCount++;
		Node y = null;
		Node x = root;
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
		if (min == null || comparator.compare(z.element, min.element) < 0)
			min = z;
	}

	void delete(Node z) {
		size--;
		modCount++;
		Node x, y;
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

	/**
	 * <pre>
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	public void verifyProperties() {
		verifyProperty1(root);
		verifyProperty2(root);
		// Property 3 is implicit
		verifyProperty4(root);
		verifyProperty5(root);
	}

	private void verifyProperty1(Node n) {
		assert getColor(n) == Color.RED || getColor(n) == Color.BLACK;
		if (n == null)
			return;
		verifyProperty1(n.left);
		verifyProperty1(n.right);
	}

	private void verifyProperty2(Node root) {
		assert getColor(root) == Color.BLACK;
	}

	private void verifyProperty4(Node n) {
		// System.out.println(getColor(n));
		if (getColor(n) == Color.RED) {
			assert getColor(n.left) == Color.BLACK;
			// System.out.println(getColor(n.left));
			assert getColor(n.right) == Color.BLACK;
			// System.out.println(getColor(n.right));
			assert getColor(n.parent) == Color.BLACK;
			// System.out.println(getColor(n.parent));
		}
		if (n == null)
			return;
		verifyProperty4(n.left);
		verifyProperty4(n.right);
	}

	private void verifyProperty5(Node root) {
		verifyProperty5Helper(root, 0, -1);
	}

	private int verifyProperty5Helper(Node n, int blackCount, int pathBlackCount) {
		if (getColor(n) == Color.BLACK) {
			blackCount++;
		}
		if (n == null) {
			if (pathBlackCount == -1) {
				pathBlackCount = blackCount;
			} else {
				assert blackCount == pathBlackCount;
			}
			return pathBlackCount;
		}
		pathBlackCount = verifyProperty5Helper(n.left, blackCount,
				pathBlackCount);
		pathBlackCount = verifyProperty5Helper(n.right, blackCount,
				pathBlackCount);
		return pathBlackCount;
	}

	private Color getColor(final Node n) {
		return (n == null ? Color.BLACK : n.color);
	}

}
