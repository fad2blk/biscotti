package collections;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.common.base.Preconditions;

public class TreeCollection<E> extends AbstractCollection<E>{

	private int size = 0;
	protected Node max = null;
	protected Node min = null;
	private Node root = null;
	private static final boolean RED = false;
	private static final boolean BLACK = true;
	protected Comparator<? super E> comparator;
	
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
		return true;
	}

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

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private Node next = min;
			private Node last = null;

			@Override
			public boolean hasNext() {
				return next != null;
			}

			@Override
			public void remove() {
				if (last == null)
					throw new IllegalStateException();
				if (last.left != null && last.right != null)
					next = last;
				delete(last);
				last = null;
			}

			@Override
			public E next() {
				Node node = next;
				if (node == null)
					throw new NoSuchElementException();
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
