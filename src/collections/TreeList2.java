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

public class TreeList2<E> extends AbstractList<E> {

	private int size = 0;
	private Node<E> maximum = null;
	private Node<E> minimum = null;
	private Node<E> root = null;
	private static final boolean RED = false;
	private static final boolean BLACK = true;
	transient private Comparator<? super E> comparator;

	private TreeList2(final Node<E> root, final Node<E> minimum,
			final Node<E> maximum, final Comparator<? super E> comparator,
			final int size) {
		// System.out.println(root.element + ":" + minimum.element + ":" +
		// maximum.element + ":" + size);
		this.root = root;
		this.maximum = maximum;
		this.minimum = minimum;
		this.comparator = comparator;
		this.size = size;
		// System.out.println(this.root.element + ":" + this.minimum.element +
		// ":" + this.maximum.element + ":" + this.size);
	}

	private TreeList2(final Comparator<? super E> comparator) {
		if (comparator != null)
			this.comparator = comparator;
		else
			this.comparator = (Comparator<? super E>) Ordering.natural();
	}

	private TreeList2(final Iterable<? extends E> elements) {
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
	 * Creates a new {@code TreeList} that orders its elements according to
	 * their natural ordering.
	 * 
	 * @return a new {@code TreeList} that orders its elements according to
	 *         their natural ordering
	 */
	public static <E> TreeList2<E> create() {
		return new TreeList2<E>((Comparator<? super E>) null);
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
	public static <E> TreeList2<E> create(final Comparator<? super E> comparator) {
		checkNotNull(comparator);
		return new TreeList2<E>(comparator);
	}

	/**
	 * Creates a new {@code TreeList} containing the elements of the specified
	 * {@code Iterable}. If the specified iterable is an instance of a {@code
	 * SortedSet}, {@code java.util.PriorityQueue java.util.PriorityQueue},
	 * {@code PriorityQueue}, or this {@code TreeList}, this list will be
	 * ordered according to the same ordering. Otherwise, this list will be
	 * ordered according to the {@link Comparable natural ordering} of its
	 * elements.
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
	@SuppressWarnings("unchecked")
	public static <E> TreeList2<E> create(final Iterable<? extends E> elements) {
		checkNotNull(elements);
		return new TreeList2(elements);
	}

	public Comparator<? super E> comparator() {
		return comparator;
	}

	/**
	 * Inserts the specified element into this list in sorted order.
	 */
	@Override
	public boolean add(E e) {
		checkNotNull(e);
		Node<E> newNode;
		if (root == null)
			minimum = maximum = root = new Node<E>(e, null);
		else {
			int cmp;
			Node<E> parent;
			Node<E> t = root;
			do {
				parent = t;
				cmp = comparator.compare(e, t.element);
				if (cmp <= 0)
					t = t.left;
				else
					t = t.right;
			} while (t != null);
			newNode = new Node<E>(e, parent);
			if (cmp <= 0)
				parent.left = newNode;
			else
				parent.right = newNode;
			fixAfterInsertion(newNode);
			if (comparator.compare(e, maximum.element) > 0)
				maximum = newNode;
			else if (comparator.compare(e, minimum.element) <= 0)
				minimum = newNode;
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
	 * The returned iterator does not support the {@code add(E)} and {@code
	 * set(E)} operations.
	 */
	@Override
	public ListIterator<E> listIterator() {
		return new ListItor();
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
		Node<E> node = search((E) o);
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

	public List<E> subList(int fromIndex, int toIndex) {
		checkPositionIndexes(fromIndex, toIndex, size);
		return new SubList(this, fromIndex, toIndex);
	}

	private class SubList extends TreeList2<E> {
		TreeList2 l;
		
		public SubList(TreeList2 l, int fromIndex, int toIndex){
			super(l.comparator);
			
			
			this.l = l;
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
		public void clear() {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean contains(Object o) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public E get(int index) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int indexOf(Object o) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Iterator<E> iterator() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int lastIndexOf(Object o) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public ListIterator<E> listIterator() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ListIterator<E> listIterator(int index) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean remove(Object o) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public E remove(int index) {
			// TODO Auto-generated method stub
			return null;
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
		public E set(int index, E element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public List<E> subList(int fromIndex, int toIndex) {
			// TODO Auto-generated method stub
			return null;
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

	private class ListItor implements ListIterator<E> {
		int index = 0;
		Node<E> next = minimum;
		Node<E> prev = null;
		Node<E> last = null;
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
			Node<E> node = prev = next;
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
			Node<E> node = next = prev;
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

		protected void checkForConcurrentModification() {
			if (expectedModCount != modCount)
				throw new ConcurrentModificationException();
		}
	}

	// Red-Black-Tree methods

	private static class Node<E> {
		E element;
		Node<E> left = null;
		Node<E> right = null;
		Node<E> parent;
		boolean color = BLACK;

		Node(final E element, final Node<E> parent) {
			this.element = element;
			this.parent = parent;
		}
	}

	private Node<E> search(final E e) {
		Node<E> node = root;
		while (node != null) {
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

	private void delete(Node<E> node) {
		size--;
		modCount++;
		if (maximum == node)
			maximum = predecessor(node);
		if (minimum == node)
			minimum = successor(node);
		if (node.left != null && node.right != null) {
			Node<E> successor = successor(node);
			node.element = successor.element;
			node = successor;
		}
		Node<E> replacement = (node.left != null ? node.left : node.right);
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

	private static <E> Node<E> successor(final Node<E> node) {
		if (node == null)
			return null;
		else if (node.right != null) {
			Node<E> successor = node.right;
			while (successor.left != null)
				successor = successor.left;
			return successor;
		} else {
			Node<E> parent = node.parent;
			Node<E> child = node;
			while (parent != null && child == parent.right) {
				child = parent;
				parent = parent.parent;
			}
			return parent;
		}
	}

	private static <E> Node<E> predecessor(final Node<E> node) {
		if (node == null)
			return null;
		else if (node.left != null) {
			Node<E> predecessor = node.left;
			while (predecessor.right != null)
				predecessor = predecessor.right;
			return predecessor;
		} else {
			Node<E> parent = node.parent;
			Node<E> child = node;
			while (parent != null && child == parent.left) {
				child = parent;
				parent = parent.parent;
			}
			return parent;
		}
	}

	private void rotateLeft(final Node<E> node) {
		if (node != null) {
			Node<E> temp = node.right;
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

	private void rotateRight(final Node<E> node) {
		if (node != null) {
			Node<E> temp = node.left;
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

	private void fixAfterInsertion(Node<E> node) {
		node.color = RED;
		while (node != null && node != root && node.parent.color == RED) {
			if (getParent(node) == getLeftChild(getParent(getParent(node)))) {
				Node<E> y = getRightChild(getParent(getParent(node)));
				if (getColor(y) == RED) {
					setColor(getParent(node), BLACK);
					setColor(y, BLACK);
					setColor(getParent(getParent(node)), RED);
					node = getParent(getParent(node));
				} else {
					if (node == getRightChild(getParent(node))) {
						node = getParent(node);
						rotateLeft(node);
					}
					setColor(getParent(node), BLACK);
					setColor(getParent(getParent(node)), RED);
					rotateRight(getParent(getParent(node)));
				}
			} else {
				Node<E> y = getLeftChild(getParent(getParent(node)));
				if (getColor(y) == RED) {
					setColor(getParent(node), BLACK);
					setColor(y, BLACK);
					setColor(getParent(getParent(node)), RED);
					node = getParent(getParent(node));
				} else {
					if (node == getLeftChild(getParent(node))) {
						node = getParent(node);
						rotateRight(node);
					}
					setColor(getParent(node), BLACK);
					setColor(getParent(getParent(node)), RED);
					rotateLeft(getParent(getParent(node)));
				}
			}
		}
		root.color = BLACK;
	}

	private void fixAfterDeletion(Node<E> node) {
		while (node != root && getColor(node) == BLACK) {
			if (node == getLeftChild(getParent(node))) {
				Node<E> sib = getRightChild(getParent(node));
				if (getColor(sib) == RED) {
					setColor(sib, BLACK);
					setColor(getParent(node), RED);
					rotateLeft(getParent(node));
					sib = getRightChild(getParent(node));
				}
				if (getColor(getLeftChild(sib)) == BLACK
						&& getColor(getRightChild(sib)) == BLACK) {
					setColor(sib, RED);
					node = getParent(node);
				} else {
					if (getColor(getRightChild(sib)) == BLACK) {
						setColor(getLeftChild(sib), BLACK);
						setColor(sib, RED);
						rotateRight(sib);
						sib = getRightChild(getParent(node));
					}
					setColor(sib, getColor(getParent(node)));
					setColor(getParent(node), BLACK);
					setColor(getRightChild(sib), BLACK);
					rotateLeft(getParent(node));
					node = root;
				}
			} else {
				Node<E> sib = getLeftChild(getParent(node));
				if (getColor(sib) == RED) {
					setColor(sib, BLACK);
					setColor(getParent(node), RED);
					rotateRight(getParent(node));
					sib = getLeftChild(getParent(node));
				}
				if (getColor(getRightChild(sib)) == BLACK
						&& getColor(getLeftChild(sib)) == BLACK) {
					setColor(sib, RED);
					node = getParent(node);
				} else {
					if (getColor(getLeftChild(sib)) == BLACK) {
						setColor(getRightChild(sib), BLACK);
						setColor(sib, RED);
						rotateLeft(sib);
						sib = getLeftChild(getParent(node));
					}
					setColor(sib, getColor(getParent(node)));
					setColor(getParent(node), BLACK);
					setColor(getLeftChild(sib), BLACK);
					rotateRight(getParent(node));
					node = root;
				}
			}
		}
		setColor(node, BLACK);
	}

	private static <E> boolean getColor(final Node<E> p) {
		return (p == null ? BLACK : p.color);
	}

	private static <E> Node<E> getParent(final Node<E> p) {
		return (p == null ? null : p.parent);
	}

	private static <E> void setColor(final Node<E> p, final boolean c) {
		if (p != null)
			p.color = c;
	}

	private static <E> Node<E> getLeftChild(final Node<E> p) {
		return (p == null) ? null : p.left;
	}

	private static <E> Node<E> getRightChild(final Node<E> p) {
		return (p == null) ? null : p.right;
	}

}