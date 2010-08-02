package collections;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import com.google.common.base.Preconditions;

/**
 * Static methods which operate on or return {@link Collection}s.
 * 
 * @author Zhenya Leonov
 */
final public class Collections3 {

	private Collections3() {
	}

	/**
	 * Creates a new {@code ArrayDeque} with an initial capacity sufficient to
	 * hold 16 elements.
	 * 
	 * @param <E>
	 *            the type of elements held in this deque
	 * @return a new {@code ArrayDeque} with an initial capacity sufficient to
	 *         hold 16 elements
	 */
	public static <E> ArrayDeque<E> newArrayDeque() {
		return new ArrayDeque<E>();
	}

	/**
	 * Creates a new {@code ArrayDeque} with initial capacity sufficient to hold
	 * the specified number of elements.
	 * 
	 * @param <E>
	 *            the type of elements held in this deque
	 * @param numElements
	 *            lower bound on initial capacity of the deque
	 * @return a new {@code ArrayDeque} with initial capacity sufficient to hold
	 *         the specified number of elements
	 */
	public static <E> ArrayDeque<E> newArrayDequeWithInitialCapacity(
			final int numElements) {
		return new ArrayDeque<E>(numElements);
	}

	/**
	 * Creates a new {@code ArrayDeque} containing the provided elements.
	 * 
	 * @param <E>
	 *            the type of elements held in this deque
	 * @param elements
	 *            the elements this deque should contain
	 * @return a new {@code ArrayDeque} containing the provided elements
	 */
	public static <E> ArrayDeque<E> newArrayDeque(final E... elements) {
		Preconditions.checkNotNull(elements);
		ArrayDeque<E> arrayDeque = new ArrayDeque<E>(elements.length);
		Collections.addAll(arrayDeque, elements);
		return arrayDeque;
	}

	/**
	 * Creates a new {@code ArrayDeque} containing the elements of the provided
	 * {@code Iterable}.
	 * 
	 * @param <E>
	 *            the type of elements held in this deque
	 * @param elements
	 *            the iterable whose elements are to be placed into the deque
	 * @return a new {@code ArrayDeque} containing the elements of the provided
	 *         iterable
	 */
	public static <E> ArrayDeque<E> newArrayDeque(
			final Iterable<? extends E> elements) {
		Preconditions.checkNotNull(elements);
		if (elements instanceof Collection<?>)
			return new ArrayDeque<E>((Collection<? extends E>) elements);
		else
			return newArrayDeque(elements.iterator());
	}

	/**
	 * Creates a new {@code ArrayDeque} containing the elements returned by the
	 * provided iterator.
	 * 
	 * @param <E>
	 *            the type of elements held in this deque
	 * @param elements
	 *            the iterator whose elements are to be placed into the deque
	 * @return a new {@code ArrayDeque} containing the elements returned by the
	 *         provided iterator
	 */
	public static <E> ArrayDeque<E> newArrayDeque(
			final Iterator<? extends E> elements) {
		Preconditions.checkNotNull(elements);
		ArrayDeque<E> arrayDeque = new ArrayDeque<E>();
		while (elements.hasNext())
			arrayDeque.add(elements.next());
		return arrayDeque;
	}

	/**
	 * Returns a synchronized (thread-safe) {@code Queue} backed by the
	 * specified queue (consider using the inherently <i>thread-safe</i>
	 * {@link PriorityBlockingQueue} instead). In order to guarantee serial
	 * access, it is critical that <b>all</b> access to the backing queue is
	 * accomplished through the returned queue.
	 * <p>
	 * It is imperative that the user manually synchronize on the returned queue
	 * when iterating over it:
	 * 
	 * <pre>
	 *  Queue queue = Collections.synchronizedQueue(unsynchronizedQueue);
	 *      ...
	 *  synchronized(queue) {
	 *     for(Object o: queue)  // Must be in synchronized block
	 *        foo(o);
	 *  }
	 * </pre>
	 * 
	 * Failure to follow this advice may result in non-deterministic behavior.
	 * <p>
	 * The returned queue will be serializable if the specified queue is
	 * serializable.
	 * 
	 * @param queue
	 *            the queue to be "wrapped" in a synchronized queue
	 * @return a synchronized view of the specified queue
	 */
	public static <E> Queue<E> synchronizedQueue(Queue<E> queue) {
		return new SynchronizedQueue<E>(queue);
	}

	/**
	 * Returns a synchronized (thread-safe) {@code BoundedQueue} backed by the
	 * specified bounded queue. In order to guarantee serial access, it is
	 * critical that <b>all</b> access to the backing queue is accomplished
	 * through the returned queue.
	 * <p>
	 * It is imperative that the user manually synchronize on the returned
	 * bounded queue when iterating over it:
	 * 
	 * <pre>
	 *  BoundedQueue boundedQueue = Collections.synchronizedBoundedQueue(unsynchronizedBoundedQueue);
	 *      ...
	 *  synchronized(boundedQueue) {
	 *     for(Object o: boundedQueue)  // Must be in synchronized block
	 *        foo(o);
	 *  }
	 * </pre>
	 * 
	 * Failure to follow this advice may result in non-deterministic behavior.
	 * <p>
	 * The returned bounded queue will be serializable if the specified bounded
	 * queue is serializable.
	 * 
	 * @param boundedQueue
	 *            the bounded queue to be synchronized
	 * @return a synchronized view of the specified bounded queue
	 */
	public static <E> Queue<E> synchronizedBoundedQueue(
			BoundedQueue<E> boundedQueue) {
		return new SynchronizedBoundedQueue<E>(boundedQueue);
	}

	/**
	 * Returns a synchronized (thread-safe) {@code Deque} backed by the
	 * specified deque. In order to guarantee serial access, it is critical that
	 * <b>all</b> access to the backing deque is accomplished through the
	 * returned deque.
	 * <p>
	 * It is imperative that the user manually synchronize on the returned deque
	 * when iterating over it:
	 * 
	 * <pre>
	 *  Deque deque = Collections.synchronizedDeque(unsynchronizedDeque);
	 *      ...
	 *  synchronized(deque) {
	 *     for(Object o: deque)  // Must be in synchronized block
	 *       foo(o);
	 *  }
	 *  
	 *  synchronized(deque) {
	 *  Iterator i = deque.descendingIterator();  // Must be in synchronized block
	 *     while (i.hasNext())
	 *        foo(i.next());
	 *  }
	 * </pre>
	 * 
	 * Failure to follow this advice may result in non-deterministic behavior.
	 * <p>
	 * The returned deque will be serializable if the specified deque is
	 * serializable.
	 * 
	 * @param deque
	 *            the deque to be "wrapped" in a synchronized deque
	 * @return a synchronized view of the specified deque
	 */
	public static <E> Deque<E> synchronizedDeque(Deque<E> deque) {
		return new SynchronizedDeque<E>(deque);
	}

	static class SynchronizedCollection<E> implements Collection<E>,
			Serializable {

		private static final long serialVersionUID = 3053995032091335093L; // JDK6
		final Collection<E> c;
		final Object mutex;

		SynchronizedCollection(Collection<E> c) {
			if (c == null)
				throw new NullPointerException();
			this.c = c;
			mutex = this;
		}

		SynchronizedCollection(Collection<E> c, Object mutex) {
			this.c = c;
			this.mutex = mutex;
		}

		public int size() {
			synchronized (mutex) {
				return c.size();
			}
		}

		public boolean isEmpty() {
			synchronized (mutex) {
				return c.isEmpty();
			}
		}

		public boolean contains(Object o) {
			synchronized (mutex) {
				return c.contains(o);
			}
		}

		public Object[] toArray() {
			synchronized (mutex) {
				return c.toArray();
			}
		}

		public <T> T[] toArray(T[] a) {
			synchronized (mutex) {
				return c.toArray(a);
			}
		}

		public Iterator<E> iterator() {
			return c.iterator();
		}

		public boolean add(E e) {
			synchronized (mutex) {
				return c.add(e);
			}
		}

		public boolean remove(Object o) {
			synchronized (mutex) {
				return c.remove(o);
			}
		}

		public void clear() {
			synchronized (mutex) {
				c.clear();
			}
		}

		public String toString() {
			synchronized (mutex) {
				return c.toString();
			}
		}

		public boolean containsAll(Collection<?> coll) {
			synchronized (mutex) {
				return c.containsAll(coll);
			}
		}

		public boolean addAll(Collection<? extends E> coll) {
			synchronized (mutex) {
				return c.addAll(coll);
			}
		}

		public boolean removeAll(Collection<?> coll) {
			synchronized (mutex) {
				return c.removeAll(coll);
			}
		}

		public boolean retainAll(Collection<?> coll) {
			synchronized (mutex) {
				return c.retainAll(coll);
			}
		}

		private void writeObject(ObjectOutputStream s) throws IOException {
			synchronized (mutex) {
				s.defaultWriteObject();
			}
		}
	}

	static class SynchronizedQueue<E> extends SynchronizedCollection<E>
			implements Queue<E> {

		private static final long serialVersionUID = 8972730767624708352L;
		private final Queue<E> queue;

		SynchronizedQueue(Queue<E> queue) {
			super(queue);
			this.queue = queue;
		}

		@Override
		public E element() {
			synchronized (mutex) {
				return queue.element();
			}
		}

		@Override
		public boolean offer(E e) {
			synchronized (mutex) {
				return queue.offer(e);
			}
		}

		@Override
		public E peek() {
			synchronized (mutex) {
				return queue.peek();
			}
		}

		@Override
		public E poll() {
			synchronized (mutex) {
				return queue.poll();
			}
		}

		@Override
		public E remove() {
			synchronized (mutex) {
				return queue.remove();
			}
		}
	}

	static class SynchronizedDeque<E> extends SynchronizedQueue<E> implements
			Deque<E> {

		private static final long serialVersionUID = 6606665956791200153L;
		private final Deque<E> deque;

		SynchronizedDeque(Deque<E> deque) {
			super(deque);
			this.deque = deque;
		}

		@Override
		public void addFirst(E e) {
			synchronized (mutex) {
				deque.addFirst(e);
			}
		}

		@Override
		public void addLast(E e) {
			synchronized (mutex) {
				deque.addLast(e);
			}
		}

		@Override
		public Iterator<E> descendingIterator() {
			return deque.descendingIterator();
		}

		@Override
		public E getFirst() {
			synchronized (mutex) {
				return deque.getFirst();
			}
		}

		@Override
		public E getLast() {
			synchronized (mutex) {
				return deque.getLast();
			}
		}

		@Override
		public boolean offerFirst(E e) {
			synchronized (mutex) {
				return deque.offerFirst(e);
			}
		}

		@Override
		public boolean offerLast(E e) {
			synchronized (mutex) {
				return deque.offerLast(e);
			}
		}

		@Override
		public E peekFirst() {
			synchronized (mutex) {
				return deque.peekFirst();
			}
		}

		@Override
		public E peekLast() {
			synchronized (mutex) {
				return deque.peekLast();
			}
		}

		@Override
		public E pollFirst() {
			synchronized (mutex) {
				return deque.pollFirst();
			}
		}

		@Override
		public E pollLast() {
			synchronized (mutex) {
				return deque.pollLast();
			}
		}

		@Override
		public E pop() {
			synchronized (mutex) {
				return deque.pop();
			}
		}

		@Override
		public void push(E e) {
			synchronized (mutex) {
				push(e);
			}
		}

		@Override
		public E removeFirst() {
			synchronized (mutex) {
				return deque.removeFirst();
			}
		}

		@Override
		public boolean removeFirstOccurrence(Object o) {
			synchronized (mutex) {
				return deque.removeFirstOccurrence(o);
			}
		}

		@Override
		public E removeLast() {
			synchronized (mutex) {
				return deque.removeLast();
			}
		}

		@Override
		public boolean removeLastOccurrence(Object o) {
			synchronized (mutex) {
				return deque.removeLastOccurrence(o);
			}
		}
	}

	static class SynchronizedBoundedQueue<E> extends SynchronizedQueue<E>
			implements BoundedQueue<E> {
		private static final long serialVersionUID = -31589221827886339L;
		private final BoundedQueue<E> bq;

		SynchronizedBoundedQueue(BoundedQueue<E> bq) {
			super(bq);
			this.bq = bq;
		}

		@Override
		public int maxSize() {
			synchronized (mutex) {
				return bq.maxSize();
			}
		}

		@Override
		public int remainingCapacity() {
			synchronized (mutex) {
				return bq.remainingCapacity();
			}
		}

		@Override
		public boolean offerAll(Collection<? extends E> c) {
			synchronized (mutex) {
				return bq.offerAll(c);
			}
		}
	}

}