package collections;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.concurrent.PriorityBlockingQueue;

import com.google.common.base.Preconditions;

/**
 * Static methods which operate on or return {@link Collection}s and {@link Map}
 * s.
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
	 *  Queue queue = Collections3.synchronizedQueue(unsynchronizedQueue);
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
	 *  BoundedQueue boundedQueue = Collections3.synchronizedBoundedQueue(unsynchronizedBoundedQueue);
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
	 *  Deque deque = Collections3.synchronizedDeque(unsynchronizedDeque);
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

	/**
	 * Returns an unmodifiable view of the specified {@code NavigableSet}.
	 * Attempts to modify the returned set directly, via its iterator, or via
	 * its {@code subSet}, {@code headSet}, {@code tailSet}, or {@code
	 * descendingSet} views, result in {@code UnsupportedOperationException}s.
	 * <p>
	 * The returned set will be serializable if the specified navigable set is
	 * serializable.
	 * 
	 * @param navigableSet
	 *            the specified navigable set
	 * @return an unmodifiable view of the given navigable set
	 */
	public static <E> NavigableSet<E> unmodifiableNavigableSet(
			final NavigableSet<? extends E> navigableSet) {
		return new UnmodifiableNavigableSet<E>(navigableSet);
	}

	/**
	 * Returns a synchronized (thread-safe) {@code NavigableSet} backed by the
	 * specified navigable set. In order to guarantee serial access, it is
	 * critical that <b>all</b> access to the backing set is accomplished
	 * through the returned navigable set (or its views).
	 * <p>
	 * Clients must manually synchronize on the returned set when iterating over
	 * it or any of its {@code subSet}, {@code headSet}, {@code tailSet}, or
	 * {@code descendingSet} views.
	 * 
	 * <pre>
	 *  NavigableSet navigableSet = Collections3.synchronizedNavigableSet(unsynchronizedNavigableSet);
	 *      ...
	 *  synchronized(navigableSet) {
	 *     for(Object o: navigableSet)  // Must be in synchronized block
	 *        foo(o);
	 *  }
	 * </pre>
	 * 
	 * or:
	 * 
	 * <pre>
	 *  NavigableSet navigableSet = Collections3.synchronizedNavigableSet(unsynchronizedNavigableSet);
	 *  SortedSet sortedSet = navigableSet.headSet(element);
	 *      ...
	 *  synchronized(navigableSet) { // Note: navigableSet, not sortedSet
	 *     for(Object o: sortedSet)  // Must be in synchronized block
	 *        foo(o);
	 *  }
	 * </pre>
	 * <p>
	 * The returned set will be serializable if the specified set is
	 * serializable.
	 * 
	 * @param navigableSet
	 *            the specified navigable set to be synchronized
	 * @return a synchronized view of the specified navigable set
	 */
	public static <E> NavigableSet<E> synchronizedNavigableSet(
			final NavigableSet<E> navigableSet) {
		return new SynchronizedNavigableSet<E>(navigableSet);
	}

	/**
	 * Creates a new <i>access-order/least-recently-used</i> (LRU) {@code
	 * LinkedHashMap}.
	 * 
	 * @param <K>
	 *            the type of keys maintained by this map
	 * @param <V>
	 *            the type of mapped values
	 * @return a new <i>access-order/least-recently-used</i> (LRU) {@code
	 *         LinkedHashMap}
	 * @see LinkedHashMap
	 */
	public static <K, V> Map<K, V> newAccessOrderMap() {
		return new LinkedHashMap<K, V>(16, .75F, true);
	}

	/**
	 * Creates a new <i>access-order/least-recently-used</i> (LRU) {@code
	 * LinkedHashMap} with the same mappings as the provided {@code Map}.
	 * 
	 * @param <K>
	 *            the type of keys maintained by this map
	 * @param <V>
	 *            the type of mapped values
	 * @param m
	 *            the given {@code Map}
	 * @return a new <i>access-order/least-recently-used</i> (LRU) {@code
	 *         LinkedHashMap} with the same mappings as the provided {@code Map}
	 * @see LinkedHashMap
	 */
	public static <K, V> Map<K, V> newAccessOrderMap(
			final Map<? extends K, ? extends V> m) {
		checkNotNull(m);
		Map<K, V> map = new LinkedHashMap<K, V>(Math.max(m.size(), 16), .75F,
				true);
		map.putAll(m);
		return map;
	}

	/**
	 * Creates a new <i>access-order/least-recently-used</i> (LRU) {@code
	 * LinkedHashMap} with the specified initial capacity.
	 * 
	 * @param <K>
	 *            the type of keys maintained by this map
	 * @param <V>
	 *            the type of mapped values
	 * @param initialCapacity
	 *            the initial capacity
	 * @return a new <i>access-order/least-recently-used</i> (LRU) {@code
	 *         LinkedHashMap} with the specified initial capacity
	 * @see LinkedHashMap
	 */
	public static <K, V> Map<K, V> newAccessOrderMapWithInitialCapacity(
			final int initialCapacity) {
		checkArgument(initialCapacity >= 0);
		return new LinkedHashMap<K, V>(initialCapacity, .75F, true);
	}

	/**
	 * Returns an unmodifiable view of the specified {@code NavigableMap}.
	 * Attempts to modify the returned map directly, via its collection views,
	 * or via its {@code subSet}, {@code headSet}, or {@code tailSet} views,
	 * will result in {@code UnsupportedOperationException}s.
	 * <p>
	 * The returned map will be serializable if the specified navigable map is
	 * serializable.
	 * 
	 * @param navigableMap
	 *            the given navigable map
	 * @return an unmodifiable view of the given navigable map
	 */
	public static <K, V> NavigableMap<K, V> unmodifiableNavigableMap(
			final NavigableMap<? extends K, ? extends V> navigableMap) {
		return new UnmodifiableNavigableMap<K, V>(navigableMap);
	}

	/**
	 * Returns a synchronized (thread-safe) {@code NavigableMap} backed by the
	 * specified navigable map. In order to guarantee serial access, it is
	 * critical that <b>all</b> access to the backing map is accomplished
	 * through the returned map (or its views).
	 * <p>
	 * Clients must manually synchronize on the returned navigable map when
	 * iterating over any of its collection views, or the collections views of
	 * any of its {@code subMap}, {@code headMap} or {@code tailMap} views.
	 * 
	 * <pre>
	 *  NavigableMap navigableMap = Collections3.synchronizedNavigableMap(unsynchronizedNavigableMap);
	 *      ...
	 *  Set set = navigableMap.keySet(); // Needn't be in synchronized block
	 *      ...
	 *  synchronized(navigableMap) { // Note navigableMap not set
	 *     for(Object o: set)  // Must be in synchronized block
	 *        foo(o);
	 *  }
	 * </pre>
	 * 
	 * or:
	 * 
	 * <pre>
	 *  NavigableMap navigableMap = Collections3.synchronizedNavigableMap(unsynchronizedNavigableMap);
	 *  SortedMap sortedMap = navigableMap.subMap(element, element2);
	 *      ...
	 *  Set set = sortedMap.keySet(); // Needn't be in synchronized block
	 *      ...
	 *  synchronized(navigableMap) { // Note: navigableMap, not sortedMap, and not set
	 *     for(Object o: set)  // Must be in synchronized block
	 *        foo(o);
	 *  }
	 * </pre>
	 * <p>
	 * The returned map will be serializable if the specified navigable map is
	 * serializable.
	 * 
	 * @param navigableMap
	 *            the specified map to synchronize
	 * @return a synchronized view of the specified {@code NavigableMap}
	 */
	public static <K, V> NavigableMap<K, V> synchronizedNavigableMap(
			final NavigableMap<K, V> navigableMap) {
		return new SynchronizedNavigableMap<K, V>(navigableMap);
	}

	/**
	 * Returns a synchronized (thread-safe) {@code BoundedMap} backed by the
	 * specified bounded map. In order to guarantee serial access, it is
	 * critical that <b>all</b> access to the backing map is accomplished
	 * through the returned map (or its views).
	 * <p>
	 * Clients must manually synchronize on the returned map when iterating over
	 * any of its collection views.
	 * 
	 * <pre>
	 *  BoundedMap boundedMap = Collections3.synchronizedBoundedMap(unsynchronizedBoundedMap);
	 *      ...
	 *  Set set = boundedMap.keySet(); // Needn't be in synchronized block
	 *      ...
	 *  synchronized(boundedMap) { // Note: boundedMap, not set
	 *     for(Object o: set)  // Must be in synchronized block
	 *        foo(o);
	 *  }
	 * </pre>
	 * <p>
	 * The returned bounded map will be serializable if the specified bounded
	 * map is serializable.
	 * 
	 * @param boundedMap
	 *            the specified bounded map to synchronize
	 * @return a synchronized view of the specified {@code BoundedMap}
	 */
	public static <K, V> BoundedMap<K, V> synchronizedBoundedMap(
			final BoundedMap<K, V> boundedMap) {
		return new SynchronizedBoundedMap<K, V>(boundedMap);
	}

	static class SynchronizedCollection<E> implements Collection<E>,
			Serializable {
		private static final long serialVersionUID = 3053995032091335093L;
		final Collection<E> collection;
		final Object mutex;

		SynchronizedCollection(Collection<E> collection) {
			if (collection == null)
				throw new NullPointerException();
			this.collection = collection;
			mutex = this;
		}

		SynchronizedCollection(Collection<E> c, Object mutex) {
			this.collection = c;
			this.mutex = mutex;
		}

		public int size() {
			synchronized (mutex) {
				return collection.size();
			}
		}

		public boolean isEmpty() {
			synchronized (mutex) {
				return collection.isEmpty();
			}
		}

		public boolean contains(Object o) {
			synchronized (mutex) {
				return collection.contains(o);
			}
		}

		public Object[] toArray() {
			synchronized (mutex) {
				return collection.toArray();
			}
		}

		public <T> T[] toArray(T[] a) {
			synchronized (mutex) {
				return collection.toArray(a);
			}
		}

		public Iterator<E> iterator() {
			return collection.iterator();
		}

		public boolean add(E e) {
			synchronized (mutex) {
				return collection.add(e);
			}
		}

		public boolean remove(Object o) {
			synchronized (mutex) {
				return collection.remove(o);
			}
		}

		public void clear() {
			synchronized (mutex) {
				collection.clear();
			}
		}

		public String toString() {
			synchronized (mutex) {
				return collection.toString();
			}
		}

		public boolean containsAll(Collection<?> coll) {
			synchronized (mutex) {
				return collection.containsAll(coll);
			}
		}

		public boolean addAll(Collection<? extends E> coll) {
			synchronized (mutex) {
				return collection.addAll(coll);
			}
		}

		public boolean removeAll(Collection<?> coll) {
			synchronized (mutex) {
				return collection.removeAll(coll);
			}
		}

		public boolean retainAll(Collection<?> coll) {
			synchronized (mutex) {
				return collection.retainAll(coll);
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
		private static final long serialVersionUID = 1L;
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
		private static final long serialVersionUID = 1L;
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
		private static final long serialVersionUID = 1L;
		private final BoundedQueue<E> boundedQueue;

		SynchronizedBoundedQueue(BoundedQueue<E> boundedQueue) {
			super(boundedQueue);
			this.boundedQueue = boundedQueue;
		}

		@Override
		public int maxSize() {
			synchronized (mutex) {
				return boundedQueue.maxSize();
			}
		}

		@Override
		public int remainingCapacity() {
			synchronized (mutex) {
				return boundedQueue.remainingCapacity();
			}
		}

		@Override
		public boolean offerAll(Collection<? extends E> c) {
			synchronized (mutex) {
				return boundedQueue.offerAll(c);
			}
		}
	}

	static class SynchronizedSet<E> extends
			Collections3.SynchronizedCollection<E> implements Set<E> {
		private static final long serialVersionUID = 487447009682186044L;

		SynchronizedSet(Set<E> set) {
			super(set);
		}

		SynchronizedSet(Set<E> set, Object mutex) {
			super(set, mutex);
		}

		public boolean equals(Object o) {
			synchronized (mutex) {
				return collection.equals(o);
			}
		}

		public int hashCode() {
			synchronized (mutex) {
				return collection.hashCode();
			}
		}
	}

	static class SynchronizedSortedSet<E> extends SynchronizedSet<E> implements
			SortedSet<E> {
		private static final long serialVersionUID = 8695801310862127406L;
		final private SortedSet<E> sortedSet;

		SynchronizedSortedSet(SortedSet<E> sortedSet) {
			super(sortedSet);
			this.sortedSet = sortedSet;
		}

		SynchronizedSortedSet(SortedSet<E> sortedSet, Object mutex) {
			super(sortedSet, mutex);
			this.sortedSet = sortedSet;
		}

		public Comparator<? super E> comparator() {
			synchronized (mutex) {
				return sortedSet.comparator();
			}
		}

		public SortedSet<E> subSet(E fromElement, E toElement) {
			synchronized (mutex) {
				return new SynchronizedSortedSet<E>(sortedSet.subSet(
						fromElement, toElement), mutex);
			}
		}

		public SortedSet<E> headSet(E toElement) {
			synchronized (mutex) {
				return new SynchronizedSortedSet<E>(sortedSet
						.headSet(toElement), mutex);
			}
		}

		public SortedSet<E> tailSet(E fromElement) {
			synchronized (mutex) {
				return new SynchronizedSortedSet<E>(sortedSet
						.tailSet(fromElement), mutex);
			}
		}

		public E first() {
			synchronized (mutex) {
				return sortedSet.first();
			}
		}

		public E last() {
			synchronized (mutex) {
				return sortedSet.last();
			}
		}
	}

	final static class SynchronizedNavigableSet<E> extends
			SynchronizedSortedSet<E> implements NavigableSet<E> {
		private static final long serialVersionUID = 1L;
		private final NavigableSet<E> navigableSet;

		SynchronizedNavigableSet(final NavigableSet<E> navigableSet) {
			super(Preconditions.checkNotNull(navigableSet));
			this.navigableSet = navigableSet;
		}

		SynchronizedNavigableSet(final NavigableSet<E> ns, final Object mutex) {
			super(Preconditions.checkNotNull(ns), Preconditions
					.checkNotNull(mutex));
			this.navigableSet = ns;
		}

		@Override
		public E ceiling(E e) {
			return navigableSet.ceiling(e);
		}

		@Override
		public Iterator<E> descendingIterator() {
			return navigableSet.descendingIterator();
		}

		@Override
		public NavigableSet<E> descendingSet() {
			return new SynchronizedNavigableSet<E>(
					navigableSet.descendingSet(), mutex);
		}

		@Override
		public E floor(E e) {
			return navigableSet.floor(e);
		}

		@Override
		public NavigableSet<E> headSet(E toElement, boolean inclusive) {
			return new SynchronizedNavigableSet<E>(navigableSet.headSet(
					toElement, inclusive), mutex);
		}

		@Override
		public E higher(E e) {
			return navigableSet.higher(e);
		}

		@Override
		public E lower(E e) {
			return navigableSet.lower(e);
		}

		@Override
		public E pollFirst() {
			return navigableSet.pollFirst();
		}

		@Override
		public E pollLast() {
			return navigableSet.pollLast();
		}

		@Override
		public NavigableSet<E> subSet(E fromElement, boolean fromInclusive,
				E toElement, boolean toInclusive) {
			return new SynchronizedNavigableSet<E>(navigableSet.subSet(
					fromElement, fromInclusive, toElement, toInclusive), mutex);
		}

		@Override
		public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
			return new SynchronizedNavigableSet<E>(navigableSet.tailSet(
					fromElement, inclusive), mutex);
		}
	}

	static class SynchronizedMap<K, V> implements Map<K, V>, Serializable {
		private static final long serialVersionUID = 1978198479659022715L;
		private final Map<K, V> m;
		final Object mutex;
		private transient Set<K> keySet = null;
		private transient Set<Map.Entry<K, V>> entrySet = null;
		private transient Collection<V> values = null;

		SynchronizedMap(Map<K, V> map) {
			if (map == null)
				throw new NullPointerException();
			this.m = map;
			mutex = this;
		}

		SynchronizedMap(Map<K, V> m, Object mutex) {
			this.m = m;
			this.mutex = mutex;
		}

		public int size() {
			synchronized (mutex) {
				return m.size();
			}
		}

		public boolean isEmpty() {
			synchronized (mutex) {
				return m.isEmpty();
			}
		}

		public boolean containsKey(Object key) {
			synchronized (mutex) {
				return m.containsKey(key);
			}
		}

		public boolean containsValue(Object value) {
			synchronized (mutex) {
				return m.containsValue(value);
			}
		}

		public V get(Object key) {
			synchronized (mutex) {
				return m.get(key);
			}
		}

		public V put(K key, V value) {
			synchronized (mutex) {
				return m.put(key, value);
			}
		}

		public V remove(Object key) {
			synchronized (mutex) {
				return m.remove(key);
			}
		}

		public void putAll(Map<? extends K, ? extends V> map) {
			synchronized (mutex) {
				m.putAll(map);
			}
		}

		public void clear() {
			synchronized (mutex) {
				m.clear();
			}
		}

		public Set<K> keySet() {
			synchronized (mutex) {
				if (keySet == null)
					keySet = new SynchronizedSet<K>(m.keySet(), mutex);
				return keySet;
			}
		}

		public Set<Map.Entry<K, V>> entrySet() {
			synchronized (mutex) {
				if (entrySet == null)
					entrySet = new SynchronizedSet<Map.Entry<K, V>>(m
							.entrySet(), mutex);
				return entrySet;
			}
		}

		public Collection<V> values() {
			synchronized (mutex) {
				if (values == null)
					values = new Collections3.SynchronizedCollection<V>(m
							.values(), mutex);
				return values;
			}
		}

		public boolean equals(Object o) {
			synchronized (mutex) {
				return m.equals(o);
			}
		}

		public int hashCode() {
			synchronized (mutex) {
				return m.hashCode();
			}
		}

		public String toString() {
			synchronized (mutex) {
				return m.toString();
			}
		}

		private void writeObject(ObjectOutputStream s) throws IOException {
			synchronized (mutex) {
				s.defaultWriteObject();
			}
		}
	}

	static class SynchronizedSortedMap<K, V> extends SynchronizedMap<K, V>
			implements SortedMap<K, V> {
		private static final long serialVersionUID = -8798146769416483793L;
		private final SortedMap<K, V> sortedMap;

		SynchronizedSortedMap(SortedMap<K, V> sortedMap) {
			super(sortedMap);
			this.sortedMap = sortedMap;
		}

		SynchronizedSortedMap(SortedMap<K, V> m, Object mutex) {
			super(m, mutex);
			sortedMap = m;
		}

		public Comparator<? super K> comparator() {
			synchronized (mutex) {
				return sortedMap.comparator();
			}
		}

		public SortedMap<K, V> subMap(K fromKey, K toKey) {
			synchronized (mutex) {
				return new SynchronizedSortedMap<K, V>(sortedMap.subMap(
						fromKey, toKey), mutex);
			}
		}

		public SortedMap<K, V> headMap(K toKey) {
			synchronized (mutex) {
				return new SynchronizedSortedMap<K, V>(
						sortedMap.headMap(toKey), mutex);
			}
		}

		public SortedMap<K, V> tailMap(K fromKey) {
			synchronized (mutex) {
				return new SynchronizedSortedMap<K, V>(sortedMap
						.tailMap(fromKey), mutex);
			}
		}

		public K firstKey() {
			synchronized (mutex) {
				return sortedMap.firstKey();
			}
		}

		public K lastKey() {
			synchronized (mutex) {
				return sortedMap.lastKey();
			}
		}
	}

	static final class SynchronizedNavigableMap<K, V> extends
			SynchronizedSortedMap<K, V> implements NavigableMap<K, V> {
		private static final long serialVersionUID = 1L;
		private final NavigableMap<K, V> navigableMap;

		SynchronizedNavigableMap(NavigableMap<K, V> navigableMap) {
			super(Preconditions.checkNotNull(navigableMap));
			this.navigableMap = navigableMap;
		}

		SynchronizedNavigableMap(NavigableMap<K, V> m, Object mutex) {
			super(Preconditions.checkNotNull(m), Preconditions
					.checkNotNull(mutex));
			this.navigableMap = m;
		}

		@Override
		public java.util.Map.Entry<K, V> ceilingEntry(K key) {
			return new AbstractMap.SimpleImmutableEntry<K, V>(navigableMap
					.ceilingEntry(key));
		}

		@Override
		public K ceilingKey(K key) {
			return navigableMap.ceilingKey(key);
		}

		@Override
		public NavigableSet<K> descendingKeySet() {
			return new SynchronizedNavigableSet<K>(navigableMap
					.descendingKeySet());
		}

		@Override
		public NavigableMap<K, V> descendingMap() {
			return new SynchronizedNavigableMap<K, V>(navigableMap
					.descendingMap());
		}

		@Override
		public java.util.Map.Entry<K, V> firstEntry() {
			return new AbstractMap.SimpleImmutableEntry<K, V>(navigableMap
					.firstEntry());
		}

		@Override
		public java.util.Map.Entry<K, V> floorEntry(K key) {
			return new AbstractMap.SimpleImmutableEntry<K, V>(navigableMap
					.floorEntry(key));
		}

		@Override
		public K floorKey(K key) {
			return navigableMap.floorKey(key);
		}

		@Override
		public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
			return new SynchronizedNavigableMap<K, V>(navigableMap.headMap(
					toKey, inclusive));
		}

		@Override
		public java.util.Map.Entry<K, V> higherEntry(K key) {
			return new AbstractMap.SimpleImmutableEntry<K, V>(navigableMap
					.higherEntry(key));
		}

		@Override
		public K higherKey(K key) {
			return navigableMap.higherKey(key);
		}

		@Override
		public java.util.Map.Entry<K, V> lastEntry() {
			return new AbstractMap.SimpleImmutableEntry<K, V>(navigableMap
					.lastEntry());
		}

		@Override
		public java.util.Map.Entry<K, V> lowerEntry(K key) {
			return new AbstractMap.SimpleImmutableEntry<K, V>(navigableMap
					.lowerEntry(key));
		}

		@Override
		public K lowerKey(K key) {
			return navigableMap.lowerKey(key);
		}

		@Override
		public NavigableSet<K> navigableKeySet() {
			return new SynchronizedNavigableSet<K>(navigableMap
					.navigableKeySet(), mutex);
		}

		@Override
		public java.util.Map.Entry<K, V> pollFirstEntry() {
			return navigableMap.pollFirstEntry();
		}

		@Override
		public java.util.Map.Entry<K, V> pollLastEntry() {
			return navigableMap.pollLastEntry();
		}

		@Override
		public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive,
				K toKey, boolean toInclusive) {
			return new SynchronizedNavigableMap<K, V>(navigableMap.subMap(
					fromKey, fromInclusive, toKey, toInclusive), mutex);
		}

		@Override
		public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
			return new SynchronizedNavigableMap<K, V>(navigableMap.tailMap(
					fromKey, inclusive), mutex);
		}
	}

	static class SynchronizedBoundedMap<K, V> extends SynchronizedMap<K, V>
			implements BoundedMap<K, V> {
		private static final long serialVersionUID = 1L;
		private final BoundedMap<K, V> boundedMap;

		SynchronizedBoundedMap(BoundedMap<K, V> boundedMap) {
			super(boundedMap);
			this.boundedMap = boundedMap;
		}

		@Override
		public int maxSize() {
			synchronized (mutex) {
				return boundedMap.maxSize();
			}
		}

		@Override
		public int remainingCapacity() {
			synchronized (mutex) {
				return boundedMap.remainingCapacity();
			}
		}
	}

}