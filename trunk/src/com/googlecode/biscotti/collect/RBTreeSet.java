package com.googlecode.biscotti.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;

import com.google.common.collect.Ordering;

public class RBTreeSet<E> extends AbstractSet<E> implements SortedCollection<E> {

	private static final boolean RED = false;
	private static final boolean BLACK = true;
	private int size = 0;
	private Node<E> min = null;
	private Node<E> root = null;
	private int modCount = 0;
	private final Comparator<? super E> comparator;

	private RBTreeSet(final Comparator<? super E> comparator) {
		if (comparator != null)
			this.comparator = comparator;
		else
			this.comparator = (Comparator<? super E>) Ordering.natural();
	}

	private RBTreeSet(final Iterable<? extends E> elements) {
		Comparator<? super E> comparator = null;
		if (elements instanceof NavigableSet<?>)
			comparator = ((NavigableSet) elements).comparator();
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

	public static <E extends Comparable<? super E>> RBTreeSet<E> create() {
		return new RBTreeSet<E>((Comparator<? super E>) null);
	}

	public static <E> RBTreeSet<E> create(final Comparator<? super E> comparator) {
		checkNotNull(comparator);
		return new RBTreeSet<E>(comparator);
	}

	public static <E> RBTreeSet<E> create(final Iterable<? extends E> elements) {
		checkNotNull(elements);
		return new RBTreeSet<E>(elements);
	}

	@Override
	public Comparator<? super E> comparator() {
		return comparator;
	}

	@Override
	public boolean add(E element) {
		checkNotNull(element);
		Node<E> newNode;
		if (root == null)
			min = root = new Node<E>(element, null);
		else {
			int cmp;
			Node<E> parent;
			Node<E> t = root;
			do {
				parent = t;
				cmp = comparator.compare(element, t.element);
				if (cmp < 0)
					t = t.left;
				else
					t = t.right;
			} while (t != null);
			newNode = new Node<E>(element, parent);
			if (cmp < 0)
				parent.left = newNode;
			else
				parent.right = newNode;
			fixAfterInsertion(newNode);
			if (comparator.compare(element, min.element) < 0)
				min = newNode;
		}
		size++;
		modCount++;
		return true;
	}

	@Override
	public boolean contains(Object o) {
		return o != null && search((E) o) != null;
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private Node<E> next = min;
			private Node<E> last = null;
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
				Node<E> node = next;
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

	/*
	 * Red-Black Tree
	 */

	static class Node<E> {
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

	private Node search(final E e) {
		Node<E> node = root;
		while (node != null && e != null) {
			int cmp = comparator.compare(e, node.element);
			if (cmp == 0 && e.equals(node.element))
				return node;
			if (cmp < 0)
				node = node.left;
			else
				node = node.right;
		}
		return null;
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

	Node predecessor(final Node node) {
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
			if (getParent(node) == getLeftChild(getParent(getParent(node)))) {
				Node y = getRightChild(getParent(getParent(node)));
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
				Node y = getLeftChild(getParent(getParent(node)));
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

	private void fixAfterDeletion(Node node) {
		while (node != root && getColor(node) == BLACK) {
			if (node == getLeftChild(getParent(node))) {
				Node sib = getRightChild(getParent(node));
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
				Node sib = getLeftChild(getParent(node));
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

	private boolean getColor(final Node p) {
		return (p == null ? BLACK : p.color);
	}

	private Node getParent(final Node p) {
		return (p == null ? null : p.parent);
	}

	private void setColor(final Node p, final boolean c) {
		if (p != null)
			p.color = c;
	}

	private Node getLeftChild(final Node p) {
		return (p == null) ? null : p.left;
	}

	private Node getRightChild(final Node p) {
		return (p == null) ? null : p.right;
	}

}
