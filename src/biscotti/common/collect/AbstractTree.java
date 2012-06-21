/*
 * Copyright (C) 2010 Zhenya Leonov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package biscotti.common.collect;

import static biscotti.common.collect.AbstractTree.Color.BLACK;
import static biscotti.common.collect.AbstractTree.Color.RED;

import java.util.Comparator;

abstract class AbstractTree<E> {

	transient int size = 0;
	transient Node<E> nil = new Node<E>();
	transient Node<E> min = nil;
	transient Node<E> max = nil;
	transient Node<E> root = nil;
	transient int modCount = 0;
	final Comparator<? super E> comparator;

	AbstractTree(final Comparator<? super E> comparator) {
		this.comparator = comparator;
	}

	/*
	 * Red-Black Tree
	 */

	static enum Color {
		BLACK, RED;
	}

	static class Node<E> {
		E element = null;
		private Node<E> parent;
		Node<E> left;
		Node<E> right;
		private Color color = BLACK;

		private Node() {
			this(null);
		}

		Node(final E element) {
			this.element = element;
			parent = this;
			right = this;
			left = this;
		}
	}

	abstract Node<E> search(final E e);

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
	void insert(Node<E> z) {
		size++;
		modCount++;
		Node<E> x = root;
		Node<E> y = nil;
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

	void delete(Node<E> z) {
		size--;
		modCount++;
		Node<E> x, y;
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
	Node<E> successor(Node<E> x) {
		if (x == nil)
			return nil;
		if (x.right != nil) {
			Node<E> y = x.right;
			while (y.left != nil)
				y = y.left;
			return y;
		}
		Node<E> y = x.parent;
		while (y != nil && x == y.right) {
			x = y;
			y = y.parent;
		}
		return y;
	}

	Node<E> predecessor(Node<E> x) {
		if (x == nil)
			return nil;
		if (x.left != nil) {
			Node<E> y = x.left;
			while (y.right != nil)
				y = y.right;
			return y;
		}
		Node<E> y = x.parent;
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
	private void leftRotate(final Node<E> x) {
		if (x != nil) {
			Node<E> n = x.right;
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

	private void rightRotate(final Node<E> x) {
		if (x != nil) {
			Node<E> n = x.left;
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
	private void fixAfterInsertion(Node<E> z) {
		z.color = RED;
		while (z.parent.color == RED) {
			if (z.parent == z.parent.parent.left) {
				Node<E> y = z.parent.parent.right;
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
				Node<E> y = z.parent.parent.left;
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
	private void fixAfterDeletion(Node<E> x) {
		while (x != root && x.color == BLACK) {
			if (x == x.parent.left) {
				Node<E> w = x.parent.right;
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
				Node<E> w = x.parent.left;
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

	private void verifyProperties() {
		verifyProperty1(root);
		verifyProperty2(root);
		// Property 3 is implicit
		verifyProperty4(root);
		verifyProperty5(root);
	}

	private void verifyProperty1(Node<E> n) {
		assert getColor(n) == Color.RED || getColor(n) == Color.BLACK;
		if (n == nil)
			return;
		verifyProperty1(n.left);
		verifyProperty1(n.right);
	}

	private void verifyProperty2(Node<E> root) {
		assert getColor(root) == Color.BLACK;
	}

	private void verifyProperty4(Node<E> n) {
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

	private void verifyProperty5(Node<E> root) {
		verifyProperty5Helper(root, 0, -1);
	}

	private int verifyProperty5Helper(Node<E> n, int blackCount,
			int pathBlackCount) {
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

	private Color getColor(final Node<E> n) {
		return (n == nil ? Color.BLACK : n.color);
	}

}
