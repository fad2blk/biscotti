package biscotti.collect;

import static biscotti.collect.SortedCollectionImpl.Color.BLACK;
import static biscotti.collect.SortedCollectionImpl.Color.RED;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.SortedSet;

import com.google.common.collect.Iterables;
import com.google.common.collect.MinMaxPriorityQueue;
import com.google.common.collect.Ordering;

/**
 * An implementation of {@code SortedCollection}, based on a modified <a
 * href="http://en.wikipedia.org/wiki/Red-black_tree">Red-Black Tree</a>.
 * Elements are sorted from <i>least</i> to <i>greatest</i> according to their
 * <i>natural ordering</i>, or by an explicit {@link Comparator} provided at
 * creation. Attempting to remove or insert {@code null} elements is prohibited.
 * Querying for {@code null} elements is allowed. Inserting non-comparable
 * elements will result in a {@code ClassCastException}.
 * <p>
 * The iterator obtained from the {@link #iterator()} method is
 * <i>fail-fast</i>. Attempts to modify the elements in this collection at any
 * time after an iterator is created, in any way except through the iterator's
 * own remove method, will result in a {@code ConcurrentModificationException}.
 * <p>
 * This collection not <i>thread-safe</i>. If multiple threads modify this
 * collection concurrently it must be synchronized externally.
 * <p>
 * This implementation uses a comparator (whether or not one is explicitly
 * provided) to perform all element comparisons. Two elements which are deemed
 * equal by the comparator's {@code compare(E, E)} method are, from the
 * standpoint of this collection, equal.
 * <p>
 * The underlying Red-Black Tree provides the following worst case running time
 * (where <i>n</i> is the size of this collection and <i>m</i> is the size of
 * the specified collection which is iterable in linear time):
 * <p>
 * <table border="2" cellpadding="3" cellspacing="1" style="width:400px;">
 *   <tr>
 *     <th style="text-align:center;">Method</th>
 *     <th style="text-align:center;">Running Time</th>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #addAll(Collection) addAll(Collection)}</br>
 *       {@link #containsAll(Collection) containsAll(Collection)}</br>
 *       {@link #retainAll(Collection) retainAll(Collection)}</br>
 *       {@link #removeAll(Collection) removeAll(Collection)}
 *     </td>
 *     <td style="text-align:center;"><i>O(m log n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #add(Object) add(E)}</br>
 *       {@link #contains(Object)}</br>
 *       {@link #remove(Object)}
 *     </td>
 *     <td style="text-align:center;"><i>O(log n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #clear() clear()}</br>
 *       {@link #isEmpty() isEmpty()}</br>
 *       {@link #size()}</br>
 *       {@link Iterator#remove()}</br>
 *     </td>
 *     <td style="text-align:center;"><i>O(1)</i></td>
 *   </tr>
 * </table>
 * 
 * @author Zhenya Leonov
 * @param <E>
 * the type of elements maintained by this collection
 */
public class SortedCollectionImpl<E> extends AbstractCollection<E> implements
		SortedCollection<E>, Serializable {

	private static final long serialVersionUID = 1L;
	protected transient int size = 0;
	protected transient Node nil = new Node();
	protected transient Node min = nil;
	protected transient Node max = nil;
	protected transient Node root = nil;
	protected transient int modCount = 0;
	protected final Comparator<? super E> comparator;

	protected SortedCollectionImpl(final Comparator<? super E> comparator) {
		this.comparator = comparator;
	}

	/**
	 * Creates a new {@code TreeCollection} containing the specified initial
	 * elements. If {@code elements} is an instance of {@link SortedSet},
	 * {@link PriorityQueue}, {@link MinMaxPriorityQueue}, or
	 * {@code SortedCollection}, this collection will be ordered according to
	 * the same ordering. Otherwise, this collection will be ordered according
	 * to the <i>natural ordering</i> of its elements.
	 * 
	 * @param elements
	 *            the initial elements to place in this collection
	 * @return a new {@code TreeCollection} containing the specified initial
	 *         elements
	 * @throws ClassCastException
	 *             if specified elements cannot be compared to one another
	 *             according to this collection's ordering
	 * @throws NullPointerException
	 *             if any of the specified elements are {@code null}
	 */
	@SuppressWarnings({ "unchecked" })
	public static <E> SortedCollectionImpl<E> create(
			final Iterable<? extends E> elements) {
		checkNotNull(elements);
		final Comparator<? super E> comparator;
		if (elements instanceof SortedSet<?>)
			comparator = ((SortedSet<? super E>) elements).comparator();
		else if (elements instanceof PriorityQueue<?>)
			comparator = ((PriorityQueue<? super E>) elements).comparator();
		else if (elements instanceof SortedCollection<?>)
			comparator = ((SortedCollection<? super E>) elements).comparator();
		else if (elements instanceof MinMaxPriorityQueue<?>)
			comparator = ((MinMaxPriorityQueue<? super E>) elements)
					.comparator();
		else
			comparator = (Comparator<? super E>) Ordering.natural();
		final SortedCollectionImpl<E> collection = new SortedCollectionImpl<E>(
				comparator);
		Iterables.addAll(collection, elements);
		return collection;
	}

	/**
	 * Creates a new {@code TreeCollection} that orders its elements according
	 * to their <i>natural ordering</i>.
	 * 
	 * @return a new {@code TreeCollection} that orders its elements according
	 *         to their <i>natural ordering</i>
	 */
	public static <E extends Comparable<? super E>> SortedCollectionImpl<E> create() {
		return new SortedCollectionImpl<E>(Ordering.natural());
	}

	@Override
	public Comparator<? super E> comparator() {
		return comparator;
	}

	@Override
	public boolean add(E e) {
		checkNotNull(e);
		Node newNode = new Node(e);
		insert(newNode);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(Object o) {
		return o != null && search((E) o) != null;
	}

	@Override
	public Iterator<E> iterator() {
		return new IteratorImpl();
	}

	@SuppressWarnings("unchecked")
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
	public void clear() {
		modCount++;
		root = nil;
		min = nil;
		max = nil;
		size = 0;
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		for (E e : this)
			hashCode = 31 * hashCode + e.hashCode();
		return hashCode;
	}

	/**
	 * Compares the specified object with this collection for equality. Returns
	 * {@code true} if the given object is also a {@code TreeCollection}, the
	 * two collection have the same size, and every member of the given
	 * collection is contained in this collection.
	 * 
	 * @param o
	 *            the specified object to compare with this collection
	 * @return {@code true} is the specified object is equal to this collection
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof SortedCollectionImpl))
			return false;
		@SuppressWarnings("unchecked")
		Collection<E> c = (Collection<E>) o;
		if (c.size() != this.size)
			return false;
		try {
			return containsAll(c);
		} catch (ClassCastException unused) {
			return false;
		} catch (NullPointerException unused) {
			return false;
		}
	}

	private void writeObject(java.io.ObjectOutputStream oos)
			throws java.io.IOException {
		oos.defaultWriteObject();
		oos.writeInt(size);
		for (E e : this)
			oos.writeObject(e);
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream ois)
			throws java.io.IOException, ClassNotFoundException {
		ois.defaultReadObject();
		nil = new Node();
		root = nil;
		min = nil;
		max = nil;
		int size = ois.readInt();
		for (int i = 0; i < size; i++)
			add((E) ois.readObject());
	}
	
	protected class IteratorImpl implements Iterator<E> {
		protected Node next = min;
		protected Node last = nil;
		private int expectedModCount = modCount;

		@Override
		public boolean hasNext() {
			return next != nil;
		}

		@Override
		public E next() {
			checkForConcurrentModification();
			if (next == nil)
				throw new NoSuchElementException();
			last = next;
			next = successor(next);
			return last.element;
		}

		@Override
		public void remove() {
			checkForConcurrentModification();
			checkState(last != nil);
			if (last.left != nil && last.right != nil)
				next = last;
			delete(last);
			expectedModCount = modCount;
			last = nil;
		}

		protected void checkForConcurrentModification() {
			if (expectedModCount != modCount)
				throw new ConcurrentModificationException();
		}
	}

	// Red-Black-Tree

	static enum Color {
		BLACK, RED;
	}

	protected class Node {
		protected E element = null;
		protected Node parent;
		protected Node left;
		protected Node right;
		private Color color = BLACK;

		protected Node() {
			parent = this;
			right = this;
			left = this;
		}

		protected Node(final E element) {
			this.element = element;
			parent = nil;
			right = nil;
			left = nil;
		}
	}

	protected Node search(final E e) {
		Node n = root;
		while (n != nil) {
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
	protected void insert(Node z) {
		size++;
		modCount++;
		Node x = root;
		Node y = nil;
		while (x != nil) {
			y = x;
			if (comparator.compare(z.element, x.element) < 0)
				x = x.left;
			else
				x = x.right;
		}
		z.parent = y;
		if (y == nil)
			root = z;
		else if (comparator.compare(z.element, y.element) < 0)
			y.left = z;
		else
			y.right = z;
		fixAfterInsertion(z);
		if (max == nil || comparator.compare(z.element, max.element) >= 0)
			max = z;
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
	protected void delete(Node z) {
		size--;
		modCount++;
		Node x, y;
		if (min == z)
			min = successor(z);
		if (max == z)
			max = predecessor(z);
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
	protected Node successor(Node x) {
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

	protected Node predecessor(Node x) {
		if (x == nil)
			return nil;
		if (x.left != nil) {
			Node y = x.left;
			while (y.right != nil)
				y = y.right;
			return y;
		}
		Node y = x.parent;
		while (y != nil && x == y.left) {
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

}
