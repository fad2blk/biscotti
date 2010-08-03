package collections;

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

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Ordering;

/**
 * An unbounded priority {@link Queue} based on a modified <a
 * href="http://en.wikipedia.org/wiki/Red-black_tree">red-black tree</a>. The
 * elements of this queue are ordered according to their <i>natural
 * ordering</i>, or by an explicit {@link Comparator} provided at creation.
 * Attempting to remove or insert {@code null} elements will fail cleanly and
 * safely leaving this queue unmodified. Querying for {@code null} elements is
 * allowed. Inserting non-comparable elements will result in a {@code
 * ClassCastException}.
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
 * "wrapping" the queue using the {@code Collections3.synchronizedQueue(Queue)}
 * method.
 * <p>
 * <b>Implementation Note:</b>This implementation uses a comparator (whether or
 * not one is explicitly provided) to maintain priority order, and {@code
 * equals} when testing for element equality. The ordering imposed by the
 * comparator must be <i>consistent with equals</i> if this queue is to function
 * correctly.
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
 *       {@link #removeAll(Collection) removeAll(Collection)}</td>
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
 *       {@link #add(Object) add(E)}</br> {@link #contains(Object)}</br>
 *       {@link #offer(Object) offer(E)}</br> {@link #remove(Object)}</br></td>
 *     <td align="center"><i>O(log n)</i></td>
 *   </tr>
 *   <tr>
 *     <td>
 *       {@link #element() element()}</br> {@link #isEmpty() isEmpty()}</br>
 *       {@link #peek()}</br> {@link #poll()}</br> {@link #remove() remove()}</br>
 *       {@link #size()}<br>
 *     </td>
 *     <td align="center"><i>O(1)</i></td>
 *   </tr>
 * </table>
 * <p>
 * Note: This queue uses the same ordering rules as
 * {@link java.util.PriorityQueue java.util.PriorityQueue}. In comparison it
 * provides identical functionality, faster overall running time and ordered
 * traversals via its iterators.
 * 
 * @author Zhenya Leonov
 * @param <E>
 *            the type of elements held in this queue
 */
public class PriorityQueue<E> extends AbstractQueue<E> implements Serializable {

	private int size = 0;
	protected Node max = null;
	protected Node min = null;
	private Node root = null;
	transient protected int count = 0;
	private static final boolean RED = false;
	private static final boolean BLACK = true;
	transient protected Comparator<? super E> comparator;
	private static final long serialVersionUID = 1L;

	protected PriorityQueue(final Comparator<? super E> comparator) {
		if (comparator != null)
			this.comparator = comparator;
		else
			this.comparator = (Comparator<? super E>) Ordering.natural();
	}

	protected PriorityQueue(final Iterable<? extends E> elements) {
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
	 * Creates a new {@code PriorityQueue} that orders its elements according to
	 * their <i>natural ordering</i>.
	 * 
	 * @return a new {@code PriorityQueue} that orders its elements according to
	 *         their <i>natural ordering</i>
	 */
	public static <E> PriorityQueue<E> create() {
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
	 * specified {@code Iterable}. If the specified iterable is an instance of a
	 * {@link SortedList}, {@link SortedSet}, {@link java.util.PriorityQueue
	 * java.util.PriorityQueue}, or this {@code PriorityQueue} this queue will
	 * be ordered according to the same ordering. Otherwise, this priority queue
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
	@SuppressWarnings("unchecked")
	public static <E> PriorityQueue<E> create(
			final Iterable<? extends E> elements) {
		Preconditions.checkNotNull(elements);
		return new PriorityQueue(elements);
	}

	/**
	 * Returns the comparator used to order the elements in this queue. If one
	 * was not explicitly provided a <i>natural order</i> comparator is
	 * returned.
	 * 
	 * @return the comparator used to order this queue
	 */
	public Comparator<? super E> comparator() {
		return comparator;
	}

	@Override
	public boolean offer(E element) {
		Preconditions.checkNotNull(element);
		Node newNode;
		if (root == null)
			min = max = root = new Node(element, null);
		else {
			int cmp;
			Node parent;
			Node t = root;
			do {
				parent = t;
				cmp = comparator.compare(element, t.element);
				if (cmp <= 0)
					t = t.left;
				else
					t = t.right;
			} while (t != null);
			newNode = new Node(element, parent);
			if (cmp <= 0)
				parent.left = newNode;
			else
				parent.right = newNode;
			fixAfterInsertion(newNode);
			if (comparator.compare(element, max.element) > 0)
				max = newNode;
			else if (comparator.compare(element, min.element) <= 0)
				min = newNode;
		}
		size++;
		count++;
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

	/**
	 * Adds all of the elements in the specified collection to this queue.
	 * Attempts to {@code addAll} of a queue to itself will result in an {@code
	 * IllegalArgumentException}. Further, the behavior of this operation is
	 * undefined if the specified collection is modified while the operation is
	 * in progress.
	 * <p>
	 * This implementation iterates over the specified collection, and adds each
	 * element returned by the iterator to this queue, in turn.
	 * <p>
	 * Attempts to {@code addAll} of a collection which contains {@code null}
	 * elements will fail cleanly and safely leaving this queue unmodified. If
	 * you are not sure whether or not your collection contains {@code null}
	 * elements considering filtering it by calling {@link Collections2#filter
	 * Collections2.filter(Collection, Predicate)} with a predicate obtained
	 * from {@link Predicates#notNull()}. Other runtime exceptions encountered
	 * while trying to add an element may result in only some of the elements
	 * having been successfully added when the associated exception is thrown.
	 * 
	 * @param c
	 *            collection containing elements to be added to this queue
	 * @return {@code true} if this queue changed as a result of the call
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
		for (E e : c)
			Preconditions.checkNotNull(e);
		return super.addAll(c);
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
			private int expectedModCount = count;

			@Override
			public boolean hasNext() {
				return next != null;
			}

			@Override
			public void remove() {
				if (last == null)
					throw new IllegalStateException();
				if (count != expectedModCount)
					throw new ConcurrentModificationException();
				if (last.left != null && last.right != null)
					next = last;
				delete(last);
				expectedModCount = count;
				last = null;
			}

			@Override
			public E next() {
				Node node = next;
				if (node == null)
					throw new NoSuchElementException();
				if (count != expectedModCount)
					throw new ConcurrentModificationException();
				next = successor(node);
				last = node;
				return node.element;
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

}
