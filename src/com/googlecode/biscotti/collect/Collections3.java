package com.googlecode.biscotti.collect;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

import com.google.common.base.Objects;
import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

/**
 * Static utility methods which operate on or return {@link Collection}s and
 * {@link Map}s.
 * 
 * @author Zhenya Leonov
 */
final public class Collections3 {

	private Collections3() {
	}

	/**
	 * Creates a new {@code TreeMap} containing the same mappings as the
	 * specified map, sorted according to the <i>natural ordering</i> of its
	 * keys.
	 * 
	 * @param map
	 *            the map whose mappings are to be placed in this map
	 * @return a new {@code TreeMap} containing the same mappings as the
	 *         specified map, sorted according to the <i>natural ordering</i> of
	 *         its keys
	 */

	public static <K extends Comparable<? super K>, V> TreeMap<K, V> newTreeMap(
			Map<? extends K, ? extends V> map) {
		return new TreeMap<K, V>(map);
	}

	/**
	 * Creates a new {@code ConcurrentSkipListMap} sorted according to the
	 * <i>natural ordering</i> of its keys.
	 * 
	 * @return a new {@code ConcurrentSkipListMap} sorted according to the
	 *         <i>natural ordering</i> of its keys
	 */

	public static <K extends Comparable<? super K>, V> ConcurrentSkipListMap<K, V> newConcurrentSkipListMap() {
		return new ConcurrentSkipListMap<K, V>();
	}

	/**
	 * Creates a new {@code ConcurrentSkipListMap} that orders its keys
	 * according to the specified comparator.
	 * 
	 * @param comparator
	 *            the {@code Comparator} used to order the keys in this map
	 * @return a new {@code ConcurrentSkipListMap} that orders its keys
	 *         according to the specified comparator
	 */

	public static <K, V> ConcurrentSkipListMap<K, V> newConcurrentSkipListMap(
			Comparator<? super K> comparator) {
		return new ConcurrentSkipListMap<K, V>(comparator);
	}

	/**
	 * Creates a new {@code ConcurrentSkipListMap} containing the same mappings
	 * as the specified map, sorted according to the <i>natural ordering</i> of
	 * its keys.
	 * 
	 * @param map
	 *            the map whose mappings are to be placed in this map
	 * @return a new {@code ConcurrentSkipListMap} containing the same mappings
	 *         as the specified map, sorted according to the <i>natural
	 *         ordering</i> of its keys
	 */

	public static <K extends Comparable<? super K>, V> ConcurrentSkipListMap<K, V> newConcurrentSkipListMap(
			Map<? extends K, ? extends V> map) {
		return new ConcurrentSkipListMap<K, V>(map);
	}

	/**
	 * Creates a new {@code ConcurrentSkipListMap} containing the same mappings
	 * and using the same ordering as the specified sorted map.
	 * 
	 * @param map
	 *            the sorted map whose mappings are to be placed in this map,
	 *            and whose comparator is to be used to sort this map
	 * @return a new {@code ConcurrentSkipListMap} containing the same mappings
	 *         and using the same ordering as the specified sorted map
	 */

	public static <K, V> ConcurrentSkipListMap<K, V> newConcurrentSkipListMap(
			SortedMap<K, ? extends V> map) {
		return new ConcurrentSkipListMap<K, V>(map);
	}

	/**
	 * Creates a new {@code ConcurrentSkipListSet} that orders its elements
	 * according to their <i>natural ordering</i>.
	 * 
	 * @return a new {@code ConcurrentSkipListSet} that orders its elements
	 *         according to their <i>natural ordering</i>
	 */
	public static <E extends Comparable<? super E>> ConcurrentSkipListSet<E> newConcurrentSkipListSet() {
		return new ConcurrentSkipListSet<E>();
	}

	/**
	 * Creates a new {@code ConcurrentSkipListSet} that orders its elements
	 * according to to the specified comparator.
	 * 
	 * @param comparator
	 *            the {@code Comparator} used to order the elements in this set
	 * @return a new {@code ConcurrentSkipListSet} that orders its elements
	 *         according to to the specified comparator
	 */
	public static <E> ConcurrentSkipListSet<E> newConcurrentSkipListSet(
			final Comparator<? super E> comparator) {
		checkNotNull(comparator);
		return new ConcurrentSkipListSet<E>(comparator);
	}

	/**
	 * Creates a new {@code ConcurrentSkipListSet} containing the specified
	 * initial elements, ordered according to their <i>natural ordering</i>.
	 * 
	 * @param elements
	 *            the specified initial elements this set should contain
	 * @return a new {@code ConcurrentSkipListSet} containing the specified
	 *         initial elements, ordered according to their <i>natural
	 *         ordering</i>
	 */
	public static <E extends Comparable<? super E>> ConcurrentSkipListSet<E> newConcurrentSkipListSet(
			final E... elements) {
		checkNotNull(elements);
		ConcurrentSkipListSet<E> concurrentSkipListSet = newConcurrentSkipListSet();
		Collections.addAll(concurrentSkipListSet, elements);
		return concurrentSkipListSet;
	}

	/**
	 * Creates a new {@code ConcurrentSkipListSet} containing the elements
	 * returned by the specified iterator, ordered according to their <i>natural
	 * ordering</i>.
	 * 
	 * @param elements
	 *            the iterator whose elements are to be placed into the set
	 * @return a new {@code ConcurrentSkipListSet} containing the elements
	 *         returned by the specified iterator, ordered according to their
	 *         <i>natural ordering</i>
	 */
	public static <E extends Comparable<? super E>> ConcurrentSkipListSet<E> newConcurrentSkipListSet(
			final Iterator<? extends E> elements) {
		checkNotNull(elements);
		ConcurrentSkipListSet<E> concurrentSkipListSet = newConcurrentSkipListSet();
		Iterators.addAll(concurrentSkipListSet, elements);
		return concurrentSkipListSet;
	}

	/**
	 * Creates a new {@code ConcurrentSkipListSet} containing the elements of
	 * the specified iterable. If the iterable is an instance of a
	 * {@link SortedSet}, {@link TreeQueue java.util.TreeQueue}, or
	 * {@link SortedCollection}, this set will be ordered according to the same
	 * ordering. Otherwise, this set will be ordered according to the <i>natural
	 * ordering</i> of its elements.
	 * 
	 * @param elements
	 *            the iterable whose elements are to be placed into the set
	 * @return a new {@code ConcurrentSkipListSet} containing the elements in
	 *         the specified iterable
	 */
	public static <E> ConcurrentSkipListSet<E> newConcurrentSkipListSet(
			final Iterable<? extends E> elements) {
		checkNotNull(elements);
		ConcurrentSkipListSet<E> concurrentSkipListSet;
		Comparator<? super E> c = null;
		if (elements instanceof SortedSet<?>)
			c = ((SortedSet) elements).comparator();
		else if (elements instanceof java.util.PriorityQueue<?>)
			c = ((java.util.PriorityQueue) elements).comparator();
		else if (elements instanceof SortedCollection<?>)
			c = ((SortedCollection) elements).comparator();

		if (c == null)
			concurrentSkipListSet = new ConcurrentSkipListSet<E>();
		else
			concurrentSkipListSet = newConcurrentSkipListSet(c);
		Iterables.addAll(concurrentSkipListSet, elements);
		return concurrentSkipListSet;
	}

	/**
	 * Creates a new {@code TreeSet} containing the specified initial elements
	 * sorted according to their <i>natural ordering</i>.
	 * 
	 * @param elements
	 *            the element this tree set should contain
	 * @return a new {@code TreeSet} containing the specified initial elements
	 *         sorted according to their <i>natural ordering</i>
	 */
	public static <E extends Comparable<? super E>> TreeSet<E> newTreeSet(
			final E... elements) {
		checkNotNull(elements);
		TreeSet<E> treeSet = Sets.newTreeSet();
		Collections.addAll(treeSet, elements);
		return treeSet;
	}

	/**
	 * Creates a new {@code TreeSet} containing the elements returned by the
	 * specified iterator, sorted according to their <i>natural ordering</i>.
	 * 
	 * @param elements
	 *            the iterator whose elements are to be placed into this set
	 * @return a new {@code TreeSet} containing the elements returned by the
	 *         specified iterator, sorted according to their <i>natural
	 *         ordering</i>
	 */
	public static <E extends Comparable<? super E>> TreeSet<E> newTreeSet(
			final Iterator<? extends E> elements) {
		checkNotNull(elements);
		TreeSet<E> treeSet = Sets.newTreeSet();
		Iterators.addAll(treeSet, elements);
		return treeSet;
	}

	// /**
	// * Creates a new {@code TreeSet} containing the elements of the specified
	// * iterable. If the iterable is an instance of a {@link SortedSet},
	// * {@link TreeQueue java.util.TreeQueue}, or
	// * {@link SortedCollection}, this set will be ordered according to the
	// same
	// * ordering. Otherwise, this set will be ordered according to the
	// <i>natural
	// * ordering</i> of its elements.
	// *
	// * @param <E>
	// * the type of elements held in this tree set
	// * @param elements
	// * the iterable whose elements are to be placed into this set
	// * @return a new {@code TreeSet} containing the elements of the specified
	// * iterable
	// */
	// public static <E> TreeSet<E> newTreeSet(final Iterable<? extends E>
	// elements) {
	// checkNotNull(elements);
	// Comparator<? super E> c = null;
	// if (elements instanceof SortedSet<?>)
	// c = ((SortedSet) elements).comparator();
	// else if (elements instanceof java.util.TreeQueue<?>)
	// c = ((java.util.TreeQueue) elements).comparator();
	// else if (elements instanceof SortedCollection<?>)
	// c = ((SortedCollection) elements).comparator();
	// TreeSet<E> treeSet = c == null ? new TreeSet<E>() : new TreeSet<E>(c);
	// Iterables.addAll(treeSet, elements);
	// return treeSet;
	// }

	/**
	 * Creates a new {@code ArrayDeque} with an initial capacity sufficient to
	 * hold 16 elements.
	 * 
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
	 * @param elements
	 *            the elements this deque should contain
	 * @return a new {@code ArrayDeque} containing the provided elements
	 */
	public static <E> ArrayDeque<E> newArrayDeque(final E... elements) {
		checkNotNull(elements);
		ArrayDeque<E> arrayDeque = new ArrayDeque<E>(elements.length);
		Collections.addAll(arrayDeque, elements);
		return arrayDeque;
	}

	/**
	 * Creates a new {@code ArrayDeque} containing the elements of the provided
	 * {@code Iterable}.
	 * 
	 * @param elements
	 *            the iterable whose elements are to be placed into the deque
	 * @return a new {@code ArrayDeque} containing the elements of the provided
	 *         iterable
	 */
	public static <E> ArrayDeque<E> newArrayDeque(
			final Iterable<? extends E> elements) {
		checkNotNull(elements);
		if (elements instanceof Collection<?>)
			return new ArrayDeque<E>((Collection<? extends E>) elements);
		else
			return newArrayDeque(elements.iterator());
	}

	/**
	 * Creates a new {@code ArrayDeque} containing the elements returned by the
	 * provided iterator.
	 * 
	 * @param elements
	 *            the iterator whose elements are to be placed into the deque
	 * @return a new {@code ArrayDeque} containing the elements returned by the
	 *         provided iterator
	 */
	public static <E> ArrayDeque<E> newArrayDeque(
			final Iterator<? extends E> elements) {
		checkNotNull(elements);
		ArrayDeque<E> arrayDeque = new ArrayDeque<E>();
		Iterators.addAll(arrayDeque, elements);
		return arrayDeque;
	}

	/**
	 * Creates a new {@code TreeQueue} containing the specified initial elements
	 * ordered according to their <i>natural ordering</i>.
	 * 
	 * @param elements
	 *            the initial elements to be stored in this queue
	 * @return a new {@code TreeQueue} containing the specified initial elements
	 */
	public static <E extends Comparable<? super E>> TreeQueue<E> newTreeQueue(
			final E... elements) {
		checkNotNull(elements);
		TreeQueue<E> q = TreeQueue.create();
		Collections.addAll(q, elements);
		return q;
	}

	/**
	 * Creates a new {@code PriorityDeque} containing the specified initial
	 * elements ordered according to their <i>natural ordering</i>.
	 * 
	 * @param elements
	 *            the initial elements to be stored in this deque
	 * @return a new {@code PriorityDeque} containing the specified initial
	 *         elements
	 */
	public static <E extends Comparable<? super E>> TreeDeque<E> newPriorityDeque(
			final E... elements) {
		checkNotNull(elements);
		TreeDeque<E> d = TreeDeque.create();
		Collections.addAll(d, elements);
		return d;
	}

	/**
	 * Creates a new {@code TreeList} containing the specified initial elements
	 * ordered according to their <i>natural ordering</i>.
	 * 
	 * @param elements
	 *            the initial elements to be stored in this list
	 * @return a new {@code TreeList} containing the specified initial elements
	 */
	public static <E extends Comparable<? super E>> TreeList<E> newTreeList(
			final E... elements) {
		checkNotNull(elements);
		TreeList<E> l = TreeList.create();
		Collections.addAll(l, elements);
		return l;
	}

	/**
	 * Returns a synchronized (thread-safe) {@code SortedCollection} backed by
	 * the specified sorted collection. In order to guarantee serial access, it
	 * is critical that <b>all</b> access to the backing collection is
	 * accomplished through the returned collection.
	 * <p>
	 * It is imperative that the user manually synchronize on the returned
	 * collection when iterating over it:
	 * 
	 * <pre>
	 *  SortedCollection sc = Collections3.synchronized(...);
	 *      ...
	 *  synchronized(sc) {
	 *     for(Object o: sc)  // Must be in synchronized block
	 *        foo(o);
	 *  }
	 * </pre>
	 * 
	 * Failure to follow this advice may result in non-deterministic behavior.
	 * <p>
	 * The returned sorted collection will be serializable if the specified
	 * collection is serializable.
	 * 
	 * @param sortedCollection
	 *            the sorted collection to be "wrapped" in a synchronized sorted
	 *            collection
	 * @return a synchronized view of the specified sorted collection
	 */
	public static <E> SortedCollection<E> synchronize(
			final SortedCollection<E> sortedCollection) {
		return new SynchronizedSortedCollection<E>(sortedCollection);
	}

	/**
	 * Returns a synchronized (thread-safe) {@code SortedList} backed by the
	 * specified sorted list. In order to guarantee serial access, it is
	 * critical that <b>all</b> access to the backing list is accomplished
	 * through the returned list.
	 * <p>
	 * It is imperative that the user manually synchronize on the returned list
	 * when iterating over it or its views:
	 * 
	 * <pre>
	 *  SortedList sortedList = Collections3.synchronizedSortedList(...);
	 *      ...
	 *  synchronized(sortedList) {
	 *     for(Object o: sortedList)  // Must be in synchronized block
	 *        foo(o);
	 *  }
	 * </pre>
	 * 
	 * or:
	 * 
	 * <pre>
	 *  SortedList sortedList = Collections3.synchronizedSortedList(...);
	 *  List subList = sortedList.subList(...)
	 *      ...
	 *  synchronized(sortedList) { // Note: sortedList not subList
	 *     for(Object o: subList)  // Must be in synchronized block
	 *        foo(o);
	 *  }
	 * </pre>
	 * 
	 * Failure to follow this advice may result in non-deterministic behavior.
	 * <p>
	 * The returned list will be serializable if the specified list is
	 * serializable.
	 * 
	 * @param sortedList
	 *            the sorted list to be "wrapped" in a synchronized sorted list
	 * @return a synchronized view of the specified sorted list
	 */
	public static <E> SortedList<E> synchronize(final SortedList<E> sortedList) {
		return new SynchronizedSortedList<E>(sortedList);
	}

	/**
	 * Returns a synchronized (thread-safe) {@code Queue} backed by the
	 * specified queue. In order to guarantee serial access, it is critical that
	 * <b>all</b> access to the backing queue is accomplished through the
	 * returned queue.
	 * <p>
	 * It is imperative that the user manually synchronize on the returned queue
	 * when iterating over it:
	 * 
	 * <pre>
	 *  Queue queue = Collections3.synchronizedQueue(...);
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
	public static <E> Queue<E> synchronize(final Queue<E> queue) {
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
	 *  BoundedQueue boundedQueue = Collections3.synchronizedBoundedQueue(...);
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
	public static <E> Queue<E> synchronize(final BoundedQueue<E> boundedQueue) {
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
	 *  Deque deque = Collections3.synchronizedDeque(...);
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
	public static <E> Deque<E> synchronize(final Deque<E> deque) {
		return new SynchronizedDeque<E>(deque);
	}

	/**
	 * Creates a new <i>access-order/least-recently-used</i>
	 * {@code LinkedHashMap}.
	 * 
	 * @param <K>
	 *            the type of keys maintained by this map
	 * @param <V>
	 *            the type of mapped values
	 * @return a new (LRU) {@code LinkedHashMap}
	 * @see LinkedHashMap
	 */
	public static <K, V> Map<K, V> newAccessOrderMap() {
		return new LinkedHashMap<K, V>(16, .75F, true);
	}

	/**
	 * Creates a new <i>access-order/least-recently-used</i>
	 * {@code LinkedHashMap} with the same mappings as the provided {@code Map}.
	 * 
	 * @param <K>
	 *            the type of keys maintained by this map
	 * @param <V>
	 *            the type of mapped values
	 * @param m
	 *            the given {@code Map}
	 * @return a new (LRU) {@code LinkedHashMap} with the same mappings as the
	 *         provided {@code Map}
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
	 * Creates a new <i>access-order/least-recently-used</i>
	 * {@code LinkedHashMap} with the specified initial capacity.
	 * 
	 * @param <K>
	 *            the type of keys maintained by this map
	 * @param <V>
	 *            the type of mapped values
	 * @param initialCapacity
	 *            the initial capacity
	 * @return a (LRU) {@code LinkedHashMap} with the specified initial capacity
	 * @see LinkedHashMap
	 */
	public static <K, V> Map<K, V> newAccessOrderMapWithInitialCapacity(
			final int initialCapacity) {
		checkArgument(initialCapacity >= 0);
		return new LinkedHashMap<K, V>(initialCapacity, .75F, true);
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
	public static <K, V> BoundedMap<K, V> synchronize(
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

	static class SynchronizedSortedCollection<E> extends
			SynchronizedCollection<E> implements SortedCollection<E> {
		private static final long serialVersionUID = 1L;
		private final SortedCollection<E> sortedCollection;

		SynchronizedSortedCollection(SortedCollection<E> sortedCollection) {
			super(sortedCollection);
			this.sortedCollection = sortedCollection;
		}

		@Override
		public Comparator<? super E> comparator() {
			synchronized (mutex) {
				return sortedCollection.comparator();
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

		// @Override
		// public boolean offerAll(Collection<? extends E> c) {
		// synchronized (mutex) {
		// return boundedQueue.offerAll(c);
		// }
		// }
	}

	static class SynchronizedSet<E> extends SynchronizedCollection<E> implements
			Set<E> {
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
				return new SynchronizedSortedSet<E>(
						sortedSet.headSet(toElement), mutex);
			}
		}

		public SortedSet<E> tailSet(E fromElement) {
			synchronized (mutex) {
				return new SynchronizedSortedSet<E>(
						sortedSet.tailSet(fromElement), mutex);
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
					entrySet = new SynchronizedSet<Map.Entry<K, V>>(
							m.entrySet(), mutex);
				return entrySet;
			}
		}

		public Collection<V> values() {
			synchronized (mutex) {
				if (values == null)
					values = new SynchronizedCollection<V>(m.values(), mutex);
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

	static class SynchronizedList<E> extends SynchronizedCollection<E>
			implements List<E> {
		static final long serialVersionUID = -7754090372962971524L;
		final List<E> list;

		SynchronizedList(List<E> list) {
			super(list);
			this.list = list;
		}

		SynchronizedList(List<E> list, Object mutex) {
			super(list, mutex);
			this.list = list;
		}

		public boolean equals(Object o) {
			synchronized (mutex) {
				return list.equals(o);
			}
		}

		public int hashCode() {
			synchronized (mutex) {
				return list.hashCode();
			}
		}

		public E get(int index) {
			synchronized (mutex) {
				return list.get(index);
			}
		}

		public E set(int index, E element) {
			synchronized (mutex) {
				return list.set(index, element);
			}
		}

		public void add(int index, E element) {
			synchronized (mutex) {
				list.add(index, element);
			}
		}

		public E remove(int index) {
			synchronized (mutex) {
				return list.remove(index);
			}
		}

		public int indexOf(Object o) {
			synchronized (mutex) {
				return list.indexOf(o);
			}
		}

		public int lastIndexOf(Object o) {
			synchronized (mutex) {
				return list.lastIndexOf(o);
			}
		}

		public boolean addAll(int index, Collection<? extends E> c) {
			synchronized (mutex) {
				return list.addAll(index, c);
			}
		}

		public ListIterator<E> listIterator() {
			return list.listIterator();
		}

		public ListIterator<E> listIterator(int index) {
			return list.listIterator(index);
		}

		public List<E> subList(int fromIndex, int toIndex) {
			synchronized (mutex) {
				return new SynchronizedList<E>(
						list.subList(fromIndex, toIndex), mutex);
			}
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

		@Override
		public boolean offer(K key, V value) {
			synchronized (mutex) {
				return boundedMap.offer(key, value);
			}
		}
	}

	static class SynchronizedSortedList<E> extends SynchronizedList<E>
			implements SortedList<E> {
		private static final long serialVersionUID = 1L;
		private final SortedList<E> sortedList;

		SynchronizedSortedList(SortedList<E> sortedList) {
			super(sortedList);
			this.sortedList = sortedList;
		}

		SynchronizedSortedList(SortedList<E> sortedList, Object mutex) {
			super(sortedList, mutex);
			this.sortedList = sortedList;
		}

		@Override
		public Comparator<? super E> comparator() {
			synchronized (mutex) {
				return sortedList.comparator();
			}
		}

		@Override
		public SortedList<E> headList(E toElement) {
			synchronized (mutex) {
				return new SynchronizedSortedList<E>(
						sortedList.headList(toElement), mutex);
			}
		}

		@Override
		public SortedList<E> subList(E fromElement, E toElement) {
			synchronized (mutex) {
				return new SynchronizedSortedList<E>(sortedList.subList(
						fromElement, toElement), mutex);
			}
		}

		@Override
		public SortedList<E> subList(int fromIndex, int toIndex) {
			synchronized (mutex) {
				return new SynchronizedSortedList<E>(sortedList.subList(
						fromIndex, toIndex), mutex);
			}
		}

		@Override
		public SortedList<E> tailList(E fromElement) {
			synchronized (mutex) {
				return new SynchronizedSortedList<E>(
						sortedList.tailList(fromElement), mutex);
			}
		}
	}

	/**
	 * Returns an unmodifiable view of the specified sorted collection. This
	 * method allows modules to provide users with "read-only" access to
	 * internal collections. Query operations on the returned collection
	 * "read through" to the specified sorted collection, and attempts to modify
	 * the returned collection, whether direct or via its iterator, result in an
	 * {@code UnsupportedOperationException}.
	 * <p>
	 * The returned sorted collection does <i>not</i> pass the {@code hashCode}
	 * and {@code equals} operations through to the backing collection, but
	 * relies on {@code Object}'s {@code equals(Object)} and {@code hashCode()}
	 * methods. This is necessary to preserve the contracts of these operations
	 * in the case that the backing collection is a set or a list.
	 * <p>
	 * The returned collection will be serializable if the specified collection
	 * is serializable.
	 * 
	 * @param c
	 *            the sorted collection for which an unmodifiable view is to be
	 *            returned
	 * @return an unmodifiable view of the specified sorted collection
	 */
	public static <E> SortedCollection<E> unmodifiable(
			SortedCollection<? extends E> c) {
		return new UnmodifiableSortedCollection<E>(c);
	}

	private static class UnmodifiableSortedCollection<E> extends
			ForwardingCollection<E> implements SortedCollection<E>,
			Serializable {

		private static final long serialVersionUID = 1L;

		final SortedCollection<E> c;

		UnmodifiableSortedCollection(final SortedCollection<? extends E> c) {
			checkNotNull(c);
			this.c = (SortedCollection<E>) c;
		}

		public Iterator<E> iterator() {
			return Iterators.unmodifiableIterator(c.iterator());
		}

		public boolean add(E e) {
			throw new UnsupportedOperationException();
		}

		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		public boolean addAll(Collection<? extends E> coll) {
			throw new UnsupportedOperationException();
		}

		public boolean removeAll(Collection<?> coll) {
			throw new UnsupportedOperationException();
		}

		public boolean retainAll(Collection<?> coll) {
			throw new UnsupportedOperationException();
		}

		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Comparator<? super E> comparator() {
			return c.comparator();
		}

		@Override
		protected SortedCollection<E> delegate() {
			return c;
		}
	}

	/**
	 * Returns an unmodifiable view of the specified sorted list. This method
	 * allows modules to provide users with "read-only" access to internal
	 * sorted lists. Query operations on the returned list "read through" to the
	 * specified list, and attempts to modify the returned sorted list, whether
	 * direct or via its iterator, result in an
	 * {@code UnsupportedOperationException}.
	 * <p>
	 * The returned list will be serializable if the specified list is
	 * serializable.
	 * <p>
	 * Note: The returned list does not implement {@code RandomAccess}.
	 * 
	 * @param l
	 *            the list for which an unmodifiable view is to be returned
	 * @return an unmodifiable view of the specified sorted list
	 */
	public static <E> SortedList<E> unmodifiable(SortedList<? extends E> l) {
		return new UnmodifiableSortedList<E>(l);
	}

	private static class UnmodifiableSortedList<E> extends
			ForwardingSortedList<E> implements Serializable {

		private static final long serialVersionUID = 1L;

		final SortedList<E> l;

		UnmodifiableSortedList(final SortedList<? extends E> l) {
			checkNotNull(l);
			this.l = (SortedList<E>) l;
		}

		@Override
		public Iterator<E> iterator() {
			return Iterators.unmodifiableIterator(l.iterator());
		}

		@Override
		public boolean add(E e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(Collection<? extends E> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(int index, Collection<? extends E> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public E set(int index, E element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(int index, E element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public E remove(int index) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ListIterator<E> listIterator() {
			return Iterators2.unmodifiable(l.listIterator());
		}

		@Override
		public ListIterator<E> listIterator(int index) {
			return Iterators2.unmodifiable(l.listIterator(index));
		}

		@Override
		public SortedList<E> headList(E toElement) {
			return new UnmodifiableSortedList<E>(l.headList(toElement));
		}

		@Override
		public SortedList<E> subList(E fromElement, E toElement) {
			return new UnmodifiableSortedList<E>(l.subList(fromElement,
					toElement));
		}

		@Override
		public SortedList<E> subList(int fromIndex, int toIndex) {
			return new UnmodifiableSortedList<E>(l.subList(fromIndex, toIndex));
		}

		@Override
		public SortedList<E> tailList(E fromElement) {
			return new UnmodifiableSortedList<E>(l.tailList(fromElement));
		}

		@Override
		protected SortedList<E> delegate() {
			return l;
		}
	}

	/**
	 * Returns an unmodifiable view of the specified bounded map. This method
	 * allows modules to provide users with "read-only" access to internal
	 * bounded maps. Query operations on the returned map "read through" to the
	 * specified map, and attempts to modify the returned bounded map, whether
	 * direct or via its collection views, result in an
	 * {@code nsupportedOperationException}.
	 * <p>
	 * The returned map will be serializable if the specified map is
	 * serializable.
	 * 
	 * @param m
	 *            the bounded map for which an unmodifiable view is to be
	 *            returned
	 * @return an unmodifiable view of the specified bounded map
	 */
	public static <K, V> Map<K, V> unmodifiable(
			BoundedMap<? extends K, ? extends V> m) {
		return new UnmodifiableBoundedMap<K, V>(m);
	}

	/**
	 * @serial include
	 */
	private static class UnmodifiableBoundedMap<K, V> extends
			ForwardingMap<K, V> implements BoundedMap<K, V>, Serializable {

		private static final long serialVersionUID = 1L;

		final BoundedMap<K, V> m;
		private transient Set<K> keySet = null;
		private transient Set<Map.Entry<K, V>> entrySet = null;
		private transient Collection<V> values = null;

		UnmodifiableBoundedMap(BoundedMap<? extends K, ? extends V> m) {
			checkNotNull(m);
			this.m = (BoundedMap<K, V>) m;
		}

		public V put(K key, V value) {
			throw new UnsupportedOperationException();
		}

		public V remove(Object key) {
			throw new UnsupportedOperationException();
		}

		public void putAll(Map<? extends K, ? extends V> m) {
			throw new UnsupportedOperationException();
		}

		public void clear() {
			throw new UnsupportedOperationException();
		}

		public Set<K> keySet() {
			if (keySet == null)
				keySet = new UnmodifiableSet<K>(m.keySet());
			return keySet;
		}

		public Set<Map.Entry<K, V>> entrySet() {
			if (entrySet == null)
				entrySet = new UnmodifiableEntrySet<K, V>(m.entrySet());
			return entrySet;
		}

		public Collection<V> values() {
			if (values == null)
				values = new UnmodifiableCollection<V>(m.values());
			return values;
		}

		/**
		 * We need this class in addition to UnmodifiableSet as Map.Entries
		 * themselves permit modification of the backing Map via their setValue
		 * operation. This class is subtle: there are many possible attacks that
		 * must be thwarted.
		 * 
		 * @serial include
		 */
		static class UnmodifiableEntrySet<K, V> extends
				UnmodifiableSet<Map.Entry<K, V>> {
			private static final long serialVersionUID = 7854390611657943733L;

			UnmodifiableEntrySet(
					Set<? extends Map.Entry<? extends K, ? extends V>> s) {
				super((Set) s);
			}

			public Iterator<Map.Entry<K, V>> iterator() {
				return new Iterator<Map.Entry<K, V>>() {
					Iterator<? extends Map.Entry<? extends K, ? extends V>> i = c
							.iterator();

					public boolean hasNext() {
						return i.hasNext();
					}

					public Map.Entry<K, V> next() {
						return new UnmodifiableEntry<K, V>(i.next());
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}

			public Object[] toArray() {
				Object[] a = c.toArray();
				for (int i = 0; i < a.length; i++)
					a[i] = new UnmodifiableEntry<K, V>((Map.Entry<K, V>) a[i]);
				return a;
			}

			public <T> T[] toArray(T[] a) {
				// We don't pass a to c.toArray, to avoid window of
				// vulnerability wherein an unscrupulous multithreaded client
				// could get his hands on raw (unwrapped) Entries from c.
				Object[] arr = c.toArray(a.length == 0 ? a : Arrays
						.copyOf(a, 0));

				for (int i = 0; i < arr.length; i++)
					arr[i] = new UnmodifiableEntry<K, V>(
							(Map.Entry<K, V>) arr[i]);

				if (arr.length > a.length)
					return (T[]) arr;

				System.arraycopy(arr, 0, a, 0, arr.length);
				if (a.length > arr.length)
					a[arr.length] = null;
				return a;
			}

			/**
			 * This method is overridden to protect the backing set against an
			 * object with a nefarious equals function that senses that the
			 * equality-candidate is Map.Entry and calls its setValue method.
			 */
			public boolean contains(Object o) {
				if (!(o instanceof Map.Entry))
					return false;
				return c.contains(new UnmodifiableEntry<K, V>(
						(Map.Entry<K, V>) o));
			}

			/**
			 * The next two methods are overridden to protect against an
			 * unscrupulous List whose contains(Object o) method senses when o
			 * is a Map.Entry, and calls o.setValue.
			 */
			public boolean containsAll(Collection<?> coll) {
				Iterator<?> e = coll.iterator();
				while (e.hasNext())
					if (!contains(e.next())) // Invokes safe contains() above
						return false;
				return true;
			}

			public boolean equals(Object o) {
				if (o == this)
					return true;

				if (!(o instanceof Set))
					return false;
				Set<?> s = (Set<?>) o;
				if (s.size() != c.size())
					return false;
				return containsAll(s); // Invokes safe containsAll() above
			}

			/**
			 * This "wrapper class" serves two purposes: it prevents the client
			 * from modifying the backing Map, by short-circuiting the setValue
			 * method, and it protects the backing Map against an ill-behaved
			 * Map.Entry that attempts to modify another Map Entry when asked to
			 * perform an equality check.
			 */
			private static class UnmodifiableEntry<K, V> implements
					Map.Entry<K, V> {
				private Map.Entry<? extends K, ? extends V> e;

				UnmodifiableEntry(Map.Entry<? extends K, ? extends V> e) {
					this.e = e;
				}

				public K getKey() {
					return e.getKey();
				}

				public V getValue() {
					return e.getValue();
				}

				public V setValue(V value) {
					throw new UnsupportedOperationException();
				}

				public int hashCode() {
					return e.hashCode();
				}

				public boolean equals(Object o) {
					if (!(o instanceof Map.Entry))
						return false;
					Map.Entry<?, ?> t = (Map.Entry<?, ?>) o;
					return Objects.equal(e.getKey(), t.getKey())
							&& Objects.equal(e.getValue(), t.getValue());
				}

				public String toString() {
					return e.toString();
				}
			}
		}

		@Override
		public int maxSize() {
			return m.maxSize();
		}

		@Override
		public boolean offer(K key, V value) {
			return m.offer(key, value);
		}

		@Override
		public int remainingCapacity() {
			return m.remainingCapacity();
		}

		@Override
		protected BoundedMap<K, V> delegate() {
			return m;
		}
	}

	static class UnmodifiableCollection<E> extends ForwardingCollection<E>
			implements Collection<E>, Serializable {

		private static final long serialVersionUID = 1820017752578914078L;
		final Collection<? extends E> c;

		UnmodifiableCollection(Collection<? extends E> c) {
			this.c = checkNotNull(c);
		}

		@Override
		public Iterator<E> iterator() {
			return Iterators.unmodifiableIterator(delegate().iterator());
		}

		@Override
		public boolean add(E e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(Collection<? extends E> coll) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> coll) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> coll) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		protected Collection<E> delegate() {
			return (Collection<E>) c;
		}
	}

	static class UnmodifiableSet<E> extends UnmodifiableCollection<E> implements
			Set<E>, Serializable {

		private static final long serialVersionUID = -9215047833775013803L;

		UnmodifiableSet(Set<? extends E> s) {
			super(s);
		}

		@Override
		public boolean equals(Object o) {
			return o == this || c.equals(o);
		}

		@Override
		public int hashCode() {
			return c.hashCode();
		}
	}

}