package com.googlecode.biscotti.collect;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.googlecode.biscotti.collect.TreeQueue.Color.BLACK;
import static com.googlecode.biscotti.collect.TreeQueue.Color.RED;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.SortedSet;
import java.util.concurrent.PriorityBlockingQueue;

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
 * not one is explicitly provided) to maintain priority order, and {@code
 * equals} when testing for element equality. The ordering imposed by the
 * comparator is not required to be <i>consistent with equals</i>. Given a
 * comparator {@code c}, for any two elements {@code e1} and {@code e2} such
 * that {@code c.compare(e1, e2) == 0} it is not necessary true that {@code
 * e1.equals(e2) == true}. This is allows duplicate elements to have different
 * priority.
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
 * This queue uses the same ordering rules as {@link java.util.TreeQueue
 * java.util.TreeQueue}. In comparison it provides identical functionality,
 * faster overall running time and ordered traversals via its iterators.
 * 
 * @author Zhenya Leonov
 * @param <E>
 *            the type of elements held in this queue
 */
public class TreeQueue<E> extends AbstractQueue<E> implements
		SortedCollection<E>, Cloneable, Serializable{

	private static final long serialVersionUID = 1L;
	transient private int size = 0;
	transient Node nil = new Node();
	transient Node min = nil;
	transient Node root = nil;
	transient int modCount = 0;
	Comparator<? super E> comparator; 
    
	TreeQueue(final Comparator<? super E> comparator) {
		if (comparator != null)
			this.comparator = comparator;
		else
			this.comparator = (Comparator<? super E>) Ordering.natural();
	}

	TreeQueue(final Iterable<? extends E> elements) {
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
	 * Creates a new {@code TreeQueue} that orders its elements according to
	 * their <i>natural ordering</i>.
	 * 
	 * @return a new {@code TreeQueue} that orders its elements according to
	 *         their <i>natural ordering</i>
	 */
	public static <E extends Comparable<? super E>> TreeQueue<E> create() {
		return new TreeQueue<E>((Comparator<? super E>) null);
	}

	/**
	 * Creates a new {@code TreeQueue} that orders its elements according to the
	 * specified comparator.
	 * 
	 * @param comparator
	 *            the comparator that will be used to order this priority queue
	 * @return a new {@code TreeQueue} that orders its elements according to
	 *         {@code comparator}
	 */
	public static <E> TreeQueue<E> create(final Comparator<? super E> comparator) {
		checkNotNull(comparator);
		return new TreeQueue<E>(comparator);
	}

	/**
	 * Creates a new {@code TreeQueue} containing the elements of the specified
	 * {@code Iterable}. If the specified iterable is an instance of
	 * {@link SortedSet}, {@link java.util.TreeQueue java.util.TreeQueue}, or
	 * {@code SortedCollection} this queue will be ordered according to the same
	 * ordering. Otherwise, this priority queue will be ordered according to the
	 * <i>natural ordering</i> of its elements.
	 * 
	 * @param elements
	 *            the iterable whose elements are to be placed into the queue
	 * @return a new {@code TreeQueue} containing the elements of the specified
	 *         iterable
	 * @throws ClassCastException
	 *             if elements of the specified iterable cannot be compared to
	 *             one another according to the priority queue's ordering
	 * @throws NullPointerException
	 *             if any of the elements of the specified iterable or the
	 *             iterable itself is {@code null}
	 */
	public static <E> TreeQueue<E> create(final Iterable<? extends E> elements) {
		checkNotNull(elements);
		return new TreeQueue<E>(elements);
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
			private Node last = nil;
			private int expectedModCount = modCount;

			@Override
			public boolean hasNext() {
				return next != nil;
			}

			@Override
			public void remove() {
				checkForConcurrentModification();
				if (last == nil)
					throw new IllegalStateException();
				if (last.left != nil && last.right != nil)
					next = last;
				delete(last);
				expectedModCount = modCount;
				last = nil;
			}

			@Override
			public E next() {
				checkForConcurrentModification();
				Node node = next;
				if (node == nil)
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
	
	@Override
	public TreeQueue<E> clone() throws CloneNotSupportedException{
		TreeQueue<E> clone = (TreeQueue<E>) super.clone();
		clone.root = nil;
		clone.min = nil;
		clone.size = 0;
		clone.modCount = 0;
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
		min = root = nil = new Node();
		int size = ois.readInt();
		for (int i = 0; i < size; i++)
			add((E) ois.readObject());
	}

	/*
	 * Red-Black Tree
	 */

	static enum Color {
		BLACK, RED;
	}

	class Node {
		E element = null;
		Node parent, left, right;
		private Color color = BLACK;

		private Node() {
			parent = left = right = this;
		}

		private Node(final E element) {
			this.element = element;
			parent = left = right = nil;
		}
	}

	private Node search(final E e) {
		Node n = root;
		while (n != nil) {
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
	 * y = nil[T]
	 * x = root[T]
	 * while x != nil[T]
	 *    do y = x
	 *       if key[z] < key[x]
	 *          then x = left[x]
	 *          else x = right[x]
	 * p[z] = y
	 * if y = nil[T]
	 *    then root[T] = z
	 *    else if key[z] < key[y]
	 *            then left[y] = z
	 *            else right[y] = z
	 * left[z] = nil[T]
	 * right[z] = nil[T]
	 * color[z] = RED
	 * RB-INSERT-FIXUP(T, z)
	 */
	void insert(final Node z) {
		size++;
		modCount++;
		Node parent = nil;
		Node x = root;
		while (x != nil) {
			parent = x;
			if (comparator.compare(z.element, x.element) < 0)
				x = x.left;
			else
				x = x.right;
		}
		z.parent = parent;
		if (parent == nil)
			root = z;
		else if (comparator.compare(z.element, parent.element) < 0)
			parent.left = z;
		else
			parent.right = z;
		fixAfterInsertion(z);
		if (min == nil || comparator.compare(z.element, min.element) < 0)
			min = z;
	}

	/**
	 * Introduction to Algorithms (CLR) Second Edition
	 * 
	 * <pre>
	 * RB-DELETE-FIXUP(T, z)
	 * if left[z] = nil[T] or right[z] = nil[T]
	 *    then y = z
	 *    else y = TREE-SUCCESSOR(z)
	 * if left[y] != nil[T]
	 *    then x = left[y]
	 *    else x = right[y]
	 * p[x] = p[y]
	 * if p[y] = nil[T]
	 *    then root[T] = x
	 *    else if y = left[p[y]]
	 *            then left[p[y]] = x
	 *            else right[p[y]] = x
	 * if y != z
	 *    then key[z] = key[y]
	 *         copy y's satellite data into z
	 * if color[y] = BLACK
	 *    then RB-DELETE-FIXUP(T, x)
	 * return y
	 */
	void delete(Node z) {
		size--;
		modCount++;
		Node x, y;
		if (min == z)
			min = successor(z);
		if (z.left == nil || z.right == nil)
			y = z;
		else
			y = successor(z);
		if (y.left != nil)
			x = y.left;
		else
			x = y.right;
		x.parent = y.parent;
		if (y.parent == nil)
			root = x;
		else if (y == y.parent.left)
			y.parent.left = x;
		else
			y.parent.right = x;
		if (y != z)
			z.element = y.element;
		if (y.color == Color.BLACK)
			fixAfterDeletion(x);
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
		if (x == nil)
			return nil;
		if (x.right != nil) {
			Node y = x.right;
			while (y.left != nil)
				y = y.left;
			return y;
		}
		Node y = x.parent;
		while (y != nil && x == y.right) {
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
	 * if left[y] != nil[T]
	 *    then p[left[y]] = x
	 * p[y] = p[x]							Link x's parent to y.
	 * if p[x] = nil[T]
	 *    then root[T] = y
	 *    else if x = left[p[x]]
	 *            then left[p[x]] = y
	 *            else right[p[x]] = y
	 * left[y] = x							Put x on y's left.
	 * p[x] = y
	 */
	private void leftRotate(final Node x) {
		if (x != nil) {
			Node n = x.right;
			x.right = n.left;
			if (n.left != nil)
				n.left.parent = x;
			n.parent = x.parent;
			if (x.parent == nil)
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
		if (x != nil) {
			Node n = x.left;
			x.left = n.right;
			if (n.right != nil)
				n.right.parent = x;
			n.parent = x.parent;
			if (x.parent == nil)
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
	private void fixAfterInsertion(Node z) {
		z.color = RED;
		while (z.parent.color == RED) {
			if (z.parent == z.parent.parent.left) {
				Node y = z.parent.parent.right;
				if (y.color == RED) {
					z.parent.color = BLACK;
					y.color = BLACK;
					z.parent.parent.color = RED;
					z = z.parent.parent;
				} else {
					if (z == z.parent.right) {
						z = z.parent;
						leftRotate(z);
					}
					z.parent.color = BLACK;
					z.parent.parent.color = RED;
					rightRotate(z.parent.parent);
				}
			} else {
				Node y = z.parent.parent.left;
				if (y.color == RED) {
					z.parent.color = BLACK;
					y.color = BLACK;
					z.parent.parent.color = RED;
					z = z.parent.parent;
				} else {
					if (z == z.parent.left) {
						z = z.parent;
						rightRotate(z);
					}
					z.parent.color = BLACK;
					z.parent.parent.color = RED;
					leftRotate(z.parent.parent);
				}
			}
		}
		root.color = BLACK;
	}

	/**
	 * Introduction to Algorithms (CLR) Second Edition
	 * 
	 * <pre>
	 * RB-DELETE-FIXUP(T, x)
	 * while x != root[T] and color[x] = BLACK
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
	 *                       x = root[T]									Case 4
	 *          else (same as then clause with right and left exchanged)
	 * color[x] = BLACK
	 */
	private void fixAfterDeletion(Node x) {
		while (x != root && x.color == BLACK) {
			if (x == x.parent.left) {
				Node w = x.parent.right;
				if (w.color == RED) {
					w.color = BLACK;
					x.parent.color = RED;
					leftRotate(x.parent);
					w = x.parent.right;
				}
				if (w.left.color == BLACK && w.right.color == BLACK) {
					w.color = RED;
					x = x.parent;
				} else {
					if (w.right.color == BLACK) {
						w.left.color = BLACK;
						w.color = RED;
						rightRotate(w);
						w = x.parent.right;
					}
					w.color = x.parent.color;
					x.parent.color = BLACK;
					x.right.color = BLACK;
					leftRotate(x.parent);
					x = root;
				}
			} else {
				Node w = x.parent.left;
				if (w.color == RED) {
					w.color = BLACK;
					x.parent.color = RED;
					rightRotate(x.parent);
					w = x.parent.left;
				}
				if (w.left.color == BLACK && w.right.color == BLACK) {
					w.color = RED;
					x = x.parent;
				} else {
					if (w.left.color == BLACK) {
						w.right.color = BLACK;
						w.color = RED;
						leftRotate(w);
						w = x.parent.left;
					}
					w.color = x.parent.color;
					x.parent.color = BLACK;
					w.left.color = BLACK;
					rightRotate(x.parent);
					x = root;
				}
			}
		}
		x.color = BLACK;
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
		if (n == nil)
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
		if (n == nil)
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
		if (n == nil) {
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
		return (n == nil ? Color.BLACK : n.color);
	}

}
