package com.googlecode.biscotti.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.AbstractQueue;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;

public class TreeQueue<E> extends AbstractQueue<E> implements
		SortedCollection<E> {
	private int size = 0;
	// private Node max = null;
	//private Node minNode = null;
	// private int minIndex;
	private Node root = null;
	private int modCount = 0;
	// private static final boolean RED = false;
	// private static final boolean BLACK = true;
	private Comparator<? super E> comparator;

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
	public static <K extends Comparable<? super K>> TreeQueue<K> create() {
		return new TreeQueue<K>((Comparator<? super K>) null);
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
	public static <K> TreeQueue<K> create(final Comparator<? super K> comparator) {
		Preconditions.checkNotNull(comparator);
		return new TreeQueue<K>(comparator);
	}

	/**
	 * Creates a new {@code TreeQueue} containing the elements of the specified
	 * {@code Iterable}. If the specified iterable is an instance of
	 * {@link SortedSet}, {@link java.util.PriorityQueue
	 * java.util.PriorityQueue}, or {@code SortedCollection} this queue will be
	 * ordered according to the same ordering. Otherwise, this priority queue
	 * will be ordered according to the <i>natural ordering</i> of its elements.
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
	public static <K> TreeQueue<K> create(final Iterable<? extends K> elements) {
		Preconditions.checkNotNull(elements);
		return new TreeQueue<K>(elements);
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean contains(Object o) {
		return o != null && search((E) o) != null;
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			int expectedModCount = modCount;
			Node next = minimum();
			Node last;
			int offset = next == null ? 0 : next.rightIndex - next.leftIndex;
			int lastOffset;

			@Override
			public boolean hasNext() {
				return next != null;
			}

			@Override
			public E next() {
				checkForConcurrentModification();
				if (next == null)
					throw new NoSuchElementException();
				last = next;
				lastOffset = offset;
				if (offset != 0)
					offset--;
				else {
					next = next.next;
					if (next != null)
						offset = next.rightIndex - next.leftIndex;
				}
				return last.elements[last.rightIndex - lastOffset];
			}

			@Override
			public void remove() {
				checkForConcurrentModification();
				if (last != null) {
					int idx = last.rightIndex - lastOffset;
					removeFromIterator(last, idx);
					last = null;
					expectedModCount++;
				} else
					throw new IllegalStateException();
			}

			private void checkForConcurrentModification() {
				if (expectedModCount != modCount)
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
		removeElement(node, node.elementIndex);
		return true;

	}

	@Override
	public boolean offer(E key) {
		checkNotNull(key);
		if (root == null) {
			root = createNode(key);
			//minNode = root;
			size = 1;
			modCount++;
			return true;
		}
		Comparable<E> object = comparator == null ? toComparable((E) key)
				: null;
		E keyK = (E) key;
		Node node = root;
		Node prevNode = null;
		int result = 0;
		while (node != null) {
			prevNode = node;
			E[] keys = node.elements;
			int left_idx = node.leftIndex;
			result = cmp(object, keyK, keys[left_idx]);
			if (result < 0) {
				node = node.left;
			} else {
				int right_idx = node.rightIndex;
				if (left_idx != right_idx) {
					result = cmp(object, keyK, keys[right_idx]);
				}
				if (result >= 0) {
					node = node.right;
				} else { /* search in node */
					int low = left_idx + 1, mid = 0, high = right_idx - 1;
					while (low <= high) {
						mid = (low + high) >>> 1;
						result = cmp(object, keyK, keys[mid]);
						if (result >= 0) {
							low = mid + 1;
						} else {
							high = mid - 1;
						}
					}
					result = low;
					break;
				}
			}
		}
		size++;
		modCount++;
		if (node == null) {
			if (prevNode == null) {
				// case of empty Tree
				//minNode = root = createNode(key);
			} else if (prevNode.size < Node.NODE_SIZE) {
				// there is a place for insert
				if (result < 0) {
					appendFromLeft(prevNode, key);
				} else {
					appendFromRight(prevNode, key);
				}
//				if (comparator.compare(key, minNode.elements[minNode.leftIndex]) < 0)
//					minNode = prevNode;
			} else {
				// create and link
				Node newNode = createNode(key);
				if (result < 0) {
					attachToLeft(prevNode, newNode);
				} else {
					attachToRight(prevNode, newNode);
				}
				balance(newNode);
//				if (comparator.compare(key, minNode.elements[minNode.leftIndex]) < 0)
//					minNode = newNode;
			}
		} else {
			// insert into node.
			// result - index where it should be inserted.
			if (node.size < Node.NODE_SIZE) { // insert and ok
				int left_idx = node.leftIndex;
				int right_idx = node.rightIndex;
				if (left_idx == 0
						|| ((right_idx != Node.NODE_SIZE - 1) && (right_idx
								- result <= result - left_idx))) {
					int right_idxPlus1 = right_idx + 1;
					System.arraycopy(node.elements, result, node.elements,
							result + 1, right_idxPlus1 - result);

					node.rightIndex = right_idxPlus1;
					node.elements[result] = key;
				} else {
					int left_idxMinus1 = left_idx - 1;
					System.arraycopy(node.elements, left_idx, node.elements,
							left_idxMinus1, result - left_idx);
					node.leftIndex = left_idxMinus1;
					node.elements[result - 1] = key;
				}
				node.size++;
			} else {
				// there are no place here
				// insert and push old pair
				Node previous = node.prev;
				Node nextNode = node.next;
				boolean removeFromStart;
				boolean attachFromLeft = false;
				Node attachHere = null;
				if (previous == null) {
					if (nextNode != null && nextNode.size < Node.NODE_SIZE) {
						// move last pair to next
						removeFromStart = false;
					} else {
						// next node doesn't exist or full
						// left==null
						// drop first pair to new node from left
						removeFromStart = true;
						attachFromLeft = true;
						attachHere = node;
					}
				} else if (nextNode == null) {
					if (previous.size < Node.NODE_SIZE) {
						// move first pair to prev
						removeFromStart = true;
					} else {
						// right == null;
						// drop last pair to new node from right
						removeFromStart = false;
						attachFromLeft = false;
						attachHere = node;
					}
				} else {
					if (previous.size < Node.NODE_SIZE) {
						if (nextNode.size < Node.NODE_SIZE) {
							// choose prev or next for moving
							removeFromStart = previous.size < nextNode.size;
						} else {
							// move first pair to prev
							removeFromStart = true;
						}
					} else {
						if (nextNode.size < Node.NODE_SIZE) {
							// move last pair to next
							removeFromStart = false;
						} else {
							// prev & next are full
							// if node.right!=null then node.next.left==null
							// if node.left!=null then node.prev.right==null
							if (node.right == null) {
								attachHere = node;
								attachFromLeft = false;
								removeFromStart = false;
							} else {
								attachHere = nextNode;
								attachFromLeft = true;
								removeFromStart = false;
							}
						}
					}
				}
				E movedKey;
				if (removeFromStart) {
					// node.left_idx == 0
					movedKey = node.elements[0];
					int resMunus1 = result - 1;
					System.arraycopy(node.elements, 1, node.elements, 0,
							resMunus1);
					node.elements[resMunus1] = key;
				} else {
					// node.right_idx == Node.NODE_SIZE - 1
					movedKey = node.elements[Node.NODE_SIZE - 1];
					System.arraycopy(node.elements, result, node.elements,
							result + 1, Node.NODE_SIZE - 1 - result);
					node.elements[result] = key;
				}
				if (attachHere == null) {
					if (removeFromStart) {
						appendFromRight(previous, movedKey);
					} else {
						appendFromLeft(nextNode, movedKey);
					}
				} else {
					Node newNode = createNode(movedKey);
					if (attachFromLeft) {
						attachToLeft(attachHere, newNode);
					} else {
						attachToRight(attachHere, newNode);
					}
					balance(newNode);
				}
			}
//			if (comparator.compare(key, minNode.elements[minNode.leftIndex]) < 0)
//				minNode = prevNode;
		}
		return true;
	}

	@Override
	public E poll() {
		if (isEmpty())
			return null;
		Node minNode = minimum();
		E e = minNode.elements[minNode.leftIndex];
		removeLeftmost(minNode);
		return e;
		
		
		
//		E e = minNode.elements[minNode.leftIndex];
//		removeLeftmost(minNode);
//		return e;
	
	}

	@Override
	public E peek() {
		if (isEmpty())
			return null;
		Node minNode = minimum();
		return minNode.elements[minNode.leftIndex];
	}

	@Override
	public Comparator<? super E> comparator() {
		return comparator;
	}

	/** util classes **/

	private void removeFromIterator(final Node node, final int index) {
		if (node.size == 1) {
			// it is safe to delete the whole node here.
			// iterator already moved to the next node;
			deleteNode(node);
		} else {
			int left_idx = node.leftIndex;
			if (index == left_idx) {
				Node prev = node.prev;
				if (prev != null && prev.size == 1) {
					node.elements[left_idx] = prev.elements[prev.leftIndex];
					deleteNode(prev);
				} else {
					node.elements[left_idx] = null;
					node.leftIndex++;
					node.size--;
				}
			} else if (index == node.rightIndex) {
				node.elements[index] = null;
				node.rightIndex--;
				node.size--;
			} else {
				int moveFromRight = node.rightIndex - index;
				int moveFromLeft = index - left_idx;
				if (moveFromRight <= moveFromLeft) {
					System.arraycopy(node.elements, index + 1, node.elements,
							index, moveFromRight);
					node.elements[node.rightIndex] = null;
					node.rightIndex--;
					node.size--;
				} else {
					System.arraycopy(node.elements, left_idx, node.elements,
							left_idx + 1, moveFromLeft);
					node.elements[left_idx] = null;
					node.leftIndex++;
					node.size--;
				}
			}
		}
		modCount++;
		size--;
	}

	private class Node {
		static final int NODE_SIZE = 64;
		Node prev, next;
		Node parent, left, right;
		E[] elements;
		boolean color;
		int elementIndex = -1;
		int leftIndex = 0;
		int rightIndex = -1;
		int size = 0;

		public Node() {
			elements = (E[]) new Object[NODE_SIZE];
		}

		Node setElementIndex(final int elementIndex) {
			this.elementIndex = elementIndex;
			return this;
		}

	}

	private Node createNode(final E e) {
		Node node = new Node();
		node.elements[0] = e;
		node.size = 1;
		return node;
	}

	private Node search(E e) {
		Node node = root;
		while (node != null) {
			E[] elements = node.elements;
			int leftIndex = node.leftIndex;
			int result = comparator.compare(e, elements[leftIndex]);
			if (result < 0)
				node = node.left;
			else if (result == 0){
				return node.setElementIndex(leftIndex);
				// use equals

			}else {
				int rightIndex = node.rightIndex;
				if (leftIndex != rightIndex)
					result = comparator.compare(e, elements[rightIndex]);
				if (result > 0)
					node = node.right;
				else if (result == 0){
					
					
					
					
					
					return node.setElementIndex(rightIndex);
					// use equals
				
				}else { /* search in node */
					int low = leftIndex + 1;
					int mid = 0;
					int high = rightIndex - 1;
					while (low <= high) {
						mid = (low + high) >>> 1;
						result = comparator.compare(e, elements[mid]);
						if (result > 0)
							low = mid + 1;
						else if (result == 0){
							return node.setElementIndex(mid);
							// use equals
						
						
						
						
						
						}else
							high = mid - 1;
					}
					return null;
				}
			}
		}
		return null;
	}
	
// 	private Node nextElement

	private int cmp(final Comparable<E> object, final E key1, final E key2) {
		return object != null ? object.compareTo(key2) : comparator.compare(
				key1, key2);
	}

	private static <T> Comparable<T> toComparable(T obj) {
		if (obj == null) {
			throw new NullPointerException();
		}
		return (Comparable) obj;
	}

	private int appendFromLeft(final Node node, final E keyObj) {
		if (node.leftIndex == 0) {
			int new_right = node.rightIndex + 1;
			System.arraycopy(node.elements, 0, node.elements, 1, new_right);
			node.rightIndex = new_right;
		} else {
			node.leftIndex--;
		}
		node.size++;
		node.elements[node.leftIndex] = keyObj;
		return node.leftIndex;
	}

	private void attachToLeft(final Node node, final Node newNode) {
		newNode.parent = node;
		// node.left==null - attach here
		node.left = newNode;
		Node predecessor = node.prev;
		newNode.prev = predecessor;
		newNode.next = node;
		if (predecessor != null) {
			predecessor.next = newNode;
		}
		node.prev = newNode;
	}

	private int appendFromRight(final Node node, final E keyObj) {
		if (node.rightIndex == Node.NODE_SIZE - 1) {
			int left_idx = node.leftIndex;
			int left_idxMinus1 = left_idx - 1;
			System.arraycopy(node.elements, left_idx, node.elements,
					left_idxMinus1, Node.NODE_SIZE - left_idx);
			node.leftIndex = left_idxMinus1;
		} else {
			node.rightIndex++;
		}
		node.size++;
		node.elements[node.rightIndex] = keyObj;
		return node.rightIndex;
	}

	private void attachToRight(final Node node, final Node newNode) {
		newNode.parent = node;
		// - node.right==null - attach here
		node.right = newNode;
		newNode.prev = node;
		Node successor = node.next;
		newNode.next = successor;
		if (successor != null) {
			successor.prev = newNode;
		}
		node.next = newNode;
	}

	private void balance(Node x) {
		Node y;
		x.color = true;
		while (x != root && x.parent.color) {
			if (x.parent == x.parent.parent.left) {
				y = x.parent.parent.right;
				if (y != null && y.color) {
					x.parent.color = false;
					y.color = false;
					x.parent.parent.color = true;
					x = x.parent.parent;
				} else {
					if (x == x.parent.right) {
						x = x.parent;
						leftRotate(x);
					}
					x.parent.color = false;
					x.parent.parent.color = true;
					rightRotate(x.parent.parent);
				}
			} else {
				y = x.parent.parent.left;
				if (y != null && y.color) {
					x.parent.color = false;
					y.color = false;
					x.parent.parent.color = true;
					x = x.parent.parent;
				} else {
					if (x == x.parent.left) {
						x = x.parent;
						rightRotate(x);
					}
					x.parent.color = false;
					x.parent.parent.color = true;
					leftRotate(x.parent.parent);
				}
			}
		}
		root.color = false;
	}

	private void rightRotate(Node x) {
		Node y = x.left;
		x.left = y.right;
		if (y.right != null) {
			y.right.parent = x;
		}
		y.parent = x.parent;
		if (x.parent == null) {
			root = y;
		} else {
			if (x == x.parent.right) {
				x.parent.right = y;
			} else {
				x.parent.left = y;
			}
		}
		y.right = x;
		x.parent = y;
	}

	private void leftRotate(Node x) {
		Node y = x.right;
		x.right = y.left;
		if (y.left != null) {
			y.left.parent = x;
		}
		y.parent = x.parent;
		if (x.parent == null) {
			root = y;
		} else {
			if (x == x.parent.left) {
				x.parent.left = y;
			} else {
				x.parent.right = y;
			}
		}
		y.left = x;
		x.parent = y;
	}

	private void removeElement(final Node node, final int index) {
		if (node.elementIndex == node.rightIndex)
			removeLeftmost(node);
		if (node.elementIndex == node.rightIndex)
			removeRightmost(node);
		else
			removeMiddleElement(node, node.elementIndex);
		node.setElementIndex(-1);
	}

	private void removeLeftmost(Node node) {
		int index = node.leftIndex;
		if (node.size == 1) {
//			minNode = node.prev;
			deleteNode(node);
		} else if (node.prev != null
				&& (Node.NODE_SIZE - 1 - node.prev.rightIndex) > node.size) {
			// move all to prev node and kill it
//			minNode = node.prev;
			Node prev = node.prev;
			int size = node.rightIndex - index;
			System.arraycopy(node.elements, index + 1, prev.elements,
					prev.rightIndex + 1, size);
			prev.rightIndex += size;
			prev.size += size;
			deleteNode(node);
		} else if (node.next != null && (node.next.leftIndex) > node.size) {
			// move all to next node and kill it
//			minNode = node.next;
			Node next = node.next;
			int size = node.rightIndex - index;
			int next_new_left = next.leftIndex - size;
			next.leftIndex = next_new_left;
			System.arraycopy(node.elements, index + 1, next.elements,
					next_new_left, size);
			next.size += size;
			deleteNode(node);
		} else {
			node.elements[index] = null;
			node.leftIndex++;
			node.size--;
			Node prev = node.prev;
			if (prev != null && prev.size == 1) {
				node.size++;
				node.leftIndex--;
				node.elements[node.leftIndex] = prev.elements[prev.leftIndex];
				deleteNode(prev);
			}
		}
		modCount++;
		size--;
	}

	private void removeRightmost(Node node) {
		int index = node.rightIndex;
		if (node.size == 1) {
//			minNode = node.prev;
			deleteNode(node);
		} else if (node.prev != null
				&& (Node.NODE_SIZE - 1 - node.prev.rightIndex) > node.size) {
			// move all to prev node and kill it
//			minNode = node.prev;
			Node prev = node.prev;
			int left_idx = node.leftIndex;
			int size = index - left_idx;
			System.arraycopy(node.elements, left_idx, prev.elements,
					prev.rightIndex + 1, size);
			prev.rightIndex += size;
			prev.size += size;
			deleteNode(node);
		} else if (node.next != null && (node.next.leftIndex) > node.size) {
			// move all to next node and kill it
//			minNode = node.next;
			Node next = node.next;
			int left_idx = node.leftIndex;
			int size = index - left_idx;
			int next_new_left = next.leftIndex - size;
			next.leftIndex = next_new_left;
			System.arraycopy(node.elements, left_idx, next.elements,
					next_new_left, size);
			next.size += size;
			deleteNode(node);
		} else {
			node.elements[index] = null;
			node.rightIndex--;
			node.size--;
			Node next = node.next;
			if (next != null && next.size == 1) {
				node.size++;
				node.rightIndex++;
				node.elements[node.rightIndex] = next.elements[next.leftIndex];
				deleteNode(next);
			}
		}
		modCount++;
		size--;
	}

	void removeMiddleElement(Node node, int index) {
		// this function is called iff index if some middle element;
		// so node.left_idx < index < node.right_idx
		// condition above assume that node.size > 1
		if (node.prev != null
				&& (Node.NODE_SIZE - 1 - node.prev.rightIndex) > node.size) {
			// move all to prev node and kill it
			Node prev = node.prev;
			int left_idx = node.leftIndex;
			int size = index - left_idx;
			System.arraycopy(node.elements, left_idx, prev.elements,
					prev.rightIndex + 1, size);
			prev.rightIndex += size;
			size = node.rightIndex - index;
			System.arraycopy(node.elements, index + 1, prev.elements,
					prev.rightIndex + 1, size);
			prev.rightIndex += size;
			prev.size += (node.size - 1);
			deleteNode(node);
		} else if (node.next != null && (node.next.leftIndex) > node.size) {
			// move all to next node and kill it
			Node next = node.next;
			int left_idx = node.leftIndex;
			int next_new_left = next.leftIndex - node.size + 1;
			next.leftIndex = next_new_left;
			int size = index - left_idx;
			System.arraycopy(node.elements, left_idx, next.elements,
					next_new_left, size);
			next_new_left += size;
			size = node.rightIndex - index;
			System.arraycopy(node.elements, index + 1, next.elements,
					next_new_left, size);
			next.size += (node.size - 1);
			deleteNode(node);
		} else {
			int moveFromRight = node.rightIndex - index;
			int left_idx = node.leftIndex;
			int moveFromLeft = index - left_idx;
			if (moveFromRight <= moveFromLeft) {
				System.arraycopy(node.elements, index + 1, node.elements,
						index, moveFromRight);
				Node next = node.next;
				if (next != null && next.size == 1) {
					node.elements[node.rightIndex] = next.elements[next.leftIndex];
					deleteNode(next);
				} else {
					node.elements[node.rightIndex] = null;
					node.rightIndex--;
					node.size--;
				}
			} else {
				System.arraycopy(node.elements, left_idx, node.elements,
						left_idx + 1, moveFromLeft);
				Node prev = node.prev;
				if (prev != null && prev.size == 1) {
					node.elements[left_idx] = prev.elements[prev.leftIndex];
					deleteNode(prev);
				} else {
					node.elements[left_idx] = null;
					node.leftIndex++;
					node.size--;
				}
			}
		}
		modCount++;
		size--;
	}

	private void deleteNode(final Node node) {
		
		if (node.right == null) {
			if (node.left != null) {
				attachToParent(node, node.left);
			} else {
				attachNullToParent(node);
			}
			fixNextChain(node);
		} else if (node.left == null) { // node.right != null
			attachToParent(node, node.right);
			fixNextChain(node);
		} else {
			Node toMoveUp = node.next;
			fixNextChain(node);
			if (toMoveUp.right == null) {
				attachNullToParent(toMoveUp);
			} else {
				attachToParent(toMoveUp, toMoveUp.right);
			}
			// Here toMoveUp is ready to replace node
			toMoveUp.left = node.left;
			if (node.left != null) {
				node.left.parent = toMoveUp;
			}
			toMoveUp.right = node.right;
			if (node.right != null) {
				node.right.parent = toMoveUp;
			}
			attachToParentNoFixUp(node, toMoveUp);
			toMoveUp.color = node.color;
		}
	}

	private void attachToParentNoFixUp(final Node toDelete, final Node toConnect) {
		Node parent = toDelete.parent;
		toConnect.parent = parent;
		if (parent == null)
			root = toConnect;
		else if (toDelete == parent.left)
			parent.left = toConnect;
		else
			parent.right = toConnect;
	}

	private void attachToParent(final Node toDelete, final Node toConnect) {
		attachToParentNoFixUp(toDelete, toConnect);
		if (!toDelete.color)
			fixUp(toConnect);
	}

	private void fixUp(Node x) {
		Node w;
		while (x != root && !x.color) {
			if (x == x.parent.left) {
				w = x.parent.right;
				if (w == null) {
					x = x.parent;
					continue;
				}
				if (w.color) {
					w.color = false;
					x.parent.color = true;
					leftRotate(x.parent);
					w = x.parent.right;
					if (w == null) {
						x = x.parent;
						continue;
					}
				}
				if ((w.left == null || !w.left.color)
						&& (w.right == null || !w.right.color)) {
					w.color = true;
					x = x.parent;
				} else {
					if (w.right == null || !w.right.color) {
						w.left.color = false;
						w.color = true;
						rightRotate(w);
						w = x.parent.right;
					}
					w.color = x.parent.color;
					x.parent.color = false;
					w.right.color = false;
					leftRotate(x.parent);
					x = root;
				}
			} else {
				w = x.parent.left;
				if (w == null) {
					x = x.parent;
					continue;
				}
				if (w.color) {
					w.color = false;
					x.parent.color = true;
					rightRotate(x.parent);
					w = x.parent.left;
					if (w == null) {
						x = x.parent;
						continue;
					}
				}
				if ((w.left == null || !w.left.color)
						&& (w.right == null || !w.right.color)) {
					w.color = true;
					x = x.parent;
				} else {
					if (w.left == null || !w.left.color) {
						w.right.color = false;
						w.color = true;
						leftRotate(w);
						w = x.parent.left;
					}
					w.color = x.parent.color;
					x.parent.color = false;
					w.left.color = false;
					rightRotate(x.parent);
					x = root;
				}
			}
		}
		x.color = false;
	}

	private void fixNextChain(final Node node) {
		if (node.prev != null)
			node.prev.next = node.next;
		if (node.next != null)
			node.next.prev = node.prev;
	}

	private void attachNullToParent(final Node toDelete) {
		Node parent = toDelete.parent;
		if (parent == null)
			root = null;
		else {
			if (toDelete == parent.left)
				parent.left = null;
			else
				parent.right = null;
			if (!toDelete.color)
				fixUp(parent);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
    private Node minimum() {
    	Node x = root;
        if (x == null)
            return null;
        while (x.left != null)
            x = x.left;
        return x;
    }
    
    
}
