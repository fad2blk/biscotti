package com.googlecode.biscotti.collect;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Static utility methods which operate on or return {@link Collection}s and
 * {@link Map}s.
 * 
 * @author Zhenya Leonov
 */
final public class Collections4 {

	private Collections4() {
	}

	/**
	 * Creates a {@code LinkedList} containing the specified initial elements.
	 * 
	 * @param elements
	 *            the elements this list should contain
	 * @return a {@code LinkedList} containing the specified initial elements
	 */
	public static <E> LinkedList<E> newLinkedList(final E... elements) {
		checkNotNull(elements);
		LinkedList<E> linkedList = Lists.newLinkedList();
		Collections.addAll(linkedList, elements);
		return linkedList;
	}

	/**
	 * Creates an empty {@code ArrayDeque} with an initial capacity sufficient
	 * to hold 16 elements.
	 * 
	 * @return an empty {@code ArrayDeque} with an initial capacity sufficient
	 *         to hold 16 elements
	 */
	public static <E> ArrayDeque<E> newArrayDeque() {
		return new ArrayDeque<E>();
	}

	/**
	 * Creates an empty {@code ArrayDeque} with an initial capacity sufficient
	 * to hold the specified number of elements.
	 * 
	 * @param numElements
	 *            lower bound on initial capacity of the deque
	 * @return an empty {@code ArrayDeque} with an initial capacity sufficient
	 *         to hold the specified number of elements
	 */
	public static <E> ArrayDeque<E> newArrayDequeWithInitialCapacity(
			final int numElements) {
		return new ArrayDeque<E>(numElements);
	}

	/**
	 * Creates an {@code ArrayDeque} containing the specified initial elements.
	 * 
	 * @param elements
	 *            the elements this deque should contain
	 * @return an {@code ArrayDeque} containing the specified initial elements
	 */
	public static <E> ArrayDeque<E> newArrayDeque(final E... elements) {
		checkNotNull(elements);
		ArrayDeque<E> arrayDeque = new ArrayDeque<E>(elements.length);
		Collections.addAll(arrayDeque, elements);
		return arrayDeque;
	}

	/**
	 * Creates an {@code ArrayDeque} containing the elements of the specified
	 * iterable.
	 * 
	 * @param elements
	 *            the iterable whose elements are to be placed into the deque
	 * @return an {@code ArrayDeque} containing the elements of the specified
	 *         iterable.
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
	 * Creates an {@code ArrayDeque} containing the elements returned by the
	 * specified iterator.
	 * 
	 * @param elements
	 *            the iterator whose elements are to be placed into the deque
	 * @return an {@code ArrayDeque} containing the elements returned by the
	 *         specified iterator
	 */
	public static <E> ArrayDeque<E> newArrayDeque(
			final Iterator<? extends E> elements) {
		checkNotNull(elements);
		ArrayDeque<E> arrayDeque = new ArrayDeque<E>();
		Iterators.addAll(arrayDeque, elements);
		return arrayDeque;
	}

	/**
	 * Creates a {@code TreeMap} containing the same mappings as the specified
	 * map, sorted according to the <i>natural ordering</i> of its keys.
	 * 
	 * @param map
	 *            the map whose mappings are to be placed in this map
	 * @return a {@code TreeMap} containing the same mappings as the specified
	 *         map, sorted according to the <i>natural ordering</i> of its keys
	 */
	public static <K extends Comparable<? super K>, V> TreeMap<K, V> newTreeMap(
			Map<? extends K, ? extends V> map) {
		return new TreeMap<K, V>(map);
	}

	/**
	 * Creates a {@code TreeMap} containing the same mappings and using the same
	 * ordering as the specified navigable map.
	 * 
	 * @param map
	 *            m the navigable map whose mappings are to be placed in this
	 *            map, and whose comparator is to be used to sort this map
	 * @return a {@code TreeMap} containing the same mappings and using the same
	 *         ordering as the specified navigable map
	 */
	public static <K extends Comparable<? super K>, V> TreeMap<K, V> newTreeMap(
			NavigableMap<K, ? extends V> map) {
		return new TreeMap<K, V>(map);
	}

	/**
	 * Creates a {@code TreeSet} containing the specified initial elements
	 * sorted according to their <i>natural ordering</i>.
	 * 
	 * @param elements
	 *            the elements this tree set should contain
	 * @return a {@code TreeSet} containing the specified initial elements
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
	 * Creates a {@code TreeSet} containing the elements returned by the
	 * specified iterator, sorted according to their <i>natural ordering</i>.
	 * 
	 * @param elements
	 *            the iterator whose elements are to be placed into this set
	 * @return a {@code TreeSet} containing the elements returned by the
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

	/**
	 * Creates a {@code TreeSet} containing the elements of the specified
	 * iterable. If the iterable is an instance of a {@link NavigableSet},
	 * {@link PriorityQueue java.util.PriorityQueue}, or
	 * {@link SortedCollection}, this set will be sorted according to the same
	 * ordering. Otherwise, this set will be sorted according to the <i>natural
	 * ordering</i> of its elements.
	 * 
	 * @param elements
	 *            the iterable whose elements are to be placed into this set
	 * @return a {@code TreeSet} containing the elements of the specified
	 *         iterable
	 */
	public static <E> TreeSet<E> newTreeSet(final Iterable<? extends E> elements) {
		checkNotNull(elements);
		Comparator<? super E> c = null;

		if (elements instanceof NavigableSet<?>)
			c = ((NavigableSet) elements).comparator();
		else if (elements instanceof java.util.PriorityQueue<?>)
			c = ((java.util.PriorityQueue) elements).comparator();
		else if (elements instanceof SortedCollection<?>)
			c = ((SortedCollection) elements).comparator();
		TreeSet<E> treeSet = c == null ? new TreeSet<E>() : new TreeSet<E>(c);
		Iterables.addAll(treeSet, elements);
		return treeSet;
	}

	/**
	 * Creates a {@code TreeQueue} containing the specified initial elements
	 * sorted according to their <i>natural ordering</i>.
	 * 
	 * @param elements
	 *            the initial elements to be stored in this queue
	 * @return a {@code TreeQueue} containing the specified initial elements
	 *         sorted according to their <i>natural ordering</i>
	 */
	public static <E extends Comparable<? super E>> TreeQueue<E> newTreeQueue(
			final E... elements) {
		checkNotNull(elements);
		TreeQueue<E> q = TreeQueue.create();
		Collections.addAll(q, elements);
		return q;
	}

	/**
	 * Creates a {@code TreeQueue} containing the specified initial elements
	 * sorted according to their <i>natural ordering</i>.
	 * 
	 * @param elements
	 *            the initial elements to be stored in this deque
	 * @return a {@code TreeQueue} containing the specified initial elements
	 *         sorted according to their <i>natural ordering</i>
	 */
	public static <E extends Comparable<? super E>> TreeDeque<E> newTreeDeque(
			final E... elements) {
		checkNotNull(elements);
		TreeDeque<E> d = TreeDeque.create();
		Collections.addAll(d, elements);
		return d;
	}

	/**
	 * Creates a {@code TreeList} containing the specified initial elements
	 * sorted according to their <i>natural ordering</i>.
	 * 
	 * @param elements
	 *            the initial elements to be stored in this list
	 * @return a {@code TreeList} containing the specified initial elements
	 *         sorted according to their <i>natural ordering</i>
	 */
	public static <E extends Comparable<? super E>> TreeList<E> newTreeList(
			final E... elements) {
		checkNotNull(elements);
		TreeList<E> l = TreeList.create();
		Collections.addAll(l, elements);
		return l;
	}

	/**
	 * Creates an empty <i>LinkedHashMap</i> which orders its keys according to
	 * their <i>access-order</i>.
	 * 
	 * @return an empty <i>LinkedHashMap</i> which orders its keys according to
	 *         their <i>access-order</i>
	 * @see LinkedHashMap
	 */
	public static <K, V> Map<K, V> newAccessOrderMap() {
		return new LinkedHashMap<K, V>(16, .75F, true);
	}

	/**
	 * Creates a <i>LinkedHashMap</i> which orders its keys according to their
	 * <i>access-order</i>, containing the same mappings as the specified map.
	 * 
	 * @param m
	 *            the map whose mappings this map should contain
	 * @return a <i>LinkedHashMap</i> which orders its keys according to their
	 *         <i>access-order</i>, containing the same mappings as the
	 *         specified map
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
	 * Creates an empty <i>LinkedHashMap</i> which orders its keys according to
	 * their <i>access-order</i>, having the specified initial capacity.
	 * 
	 * @param initialCapacity
	 *            the initial capacity
	 * @return an empty <i>LinkedHashMap</i> which orders its keys according to
	 *         their <i>access-order</i>, having the specified initial capacity
	 * @see LinkedHashMap
	 */
	public static <K, V> Map<K, V> newAccessOrderMapWithInitialCapacity(
			final int initialCapacity) {
		checkArgument(initialCapacity >= 0);
		return new LinkedHashMap<K, V>(initialCapacity, .75F, true);
	}

	/**
	 * Returns an unmodifiable view of the specified {@code Queue}. This method
	 * allows modules to provide users with "read-only" access to internal
	 * queues. Query operations on the returned queue "read through" to the
	 * specified queue. Attempts to modify the returned queue, whether direct,
	 * via its iterator, result in an {@code UnsupportedOperationException}.
	 * <p>
	 * The returned queue will be serializable if the specified queue is
	 * serializable.
	 * 
	 * @param q
	 *            the queue for which an unmodifiable view is to be returned
	 * @return an unmodifiable view of the specified queue
	 */
	public static <E> Queue<E> unmodifiable(final Queue<? extends E> q) {
		return new UnmodifiableQueue<E>(q);
	}

	/**
	 * Returns an unmodifiable view of the specified {@code Deque}. This method
	 * allows modules to provide users with "read-only" access to internal
	 * deques. Query operations on the returned deque "read through" to the
	 * specified deque. Attempts to modify the returned deque, whether direct,
	 * via its iterators, result in an {@code UnsupportedOperationException}.
	 * <p>
	 * The returned deque will be serializable if the specified deque is
	 * serializable.
	 * 
	 * @param d
	 *            the deque for which an unmodifiable view is to be returned
	 * @return an unmodifiable view of the specified deque
	 */
	public static <E> Deque<E> unmodifiable(final Deque<? extends E> d) {
		return new UnmodifiableDeque<E>(d);
	}

	/**
	 * Returns an unmodifiable view of the specified {@code SortedCollection}.
	 * This method allows modules to provide users with "read-only" access to
	 * internal sorted collections. Query operations on the returned collection
	 * "read through" to the specified collection. Attempts to modify the
	 * returned sorted collection, whether direct, via its iterator, result in
	 * an {@code UnsupportedOperationException}.
	 * <p>
	 * The returned sorted collection will be serializable if the specified
	 * collection is serializable.
	 * 
	 * @param sc
	 *            the sorted collection for which an unmodifiable view is to be
	 *            returned
	 * @return an unmodifiable view of the specified sorted collection
	 */
	public static <E> SortedCollection<E> unmodifiable(
			final SortedCollection<E> sc) {
		return new UnmodifiableSortedCollection<E>(sc);
	}

	/**
	 * Returns an unmodifiable view of the specified {@code SortedList}. This
	 * method allows modules to provide users with "read-only" access to
	 * internal sorted lists. Query operations on the returned list
	 * "read through" to the specified list. Attempts to modify the returned
	 * sorted list, whether direct, via its iterators, or via its
	 * {@code subList}, {@code headList}, or {@code tailList} views, result in
	 * an {@code UnsupportedOperationException}.
	 * <p>
	 * The returned sorted list will be serializable if the specified list is
	 * serializable.
	 * 
	 * @param sl
	 *            the sorted list for which an unmodifiable view is to be
	 *            returned
	 * @return an unmodifiable view of the specified sorted list
	 */
	public static <E> SortedList<E> unmodifiable(final SortedList<E> sl) {
		return new UnmodifiableSortedList<E>(sl);
	}

	/**
	 * Returns an unmodifiable view of the specified {@code NavigableSet}. This
	 * method allows modules to provide users with "read-only" access to
	 * internal navigable sets. Query operations on the returned set
	 * "read through" to the specified set. Attempts to modify the returned
	 * navigable set, whether direct, via its iterator, or via its
	 * {@code subSet}, {@code headSet}, or {@code tailSet} views, result in an
	 * {@code UnsupportedOperationException}.
	 * <p>
	 * The returned navigable set will be serializable if the specified set is
	 * serializable.
	 * 
	 * @param ns
	 *            the navigable set for which an unmodifiable view is to be
	 *            returned
	 * @return an unmodifiable view of the specified navigable set
	 */
	public static <E> NavigableSet<E> unmodifiable(final NavigableSet<E> ns) {
		return new UnmodifiableNavigableSet<E>(ns);
	}

	/**
	 * Returns an unmodifiable view of the specified {@code NavigableMap}. This
	 * method allows modules to provide users with "read-only" access to
	 * internal navigable maps. Query operations on the returned map
	 * "read through" to the specified map. Attempts to modify the returned
	 * navigable map, whether direct, via its collection views, or via its
	 * {@code subMap}, {@code headMap}, or {@code tailMap} views, result in an
	 * {@code UnsupportedOperationException}.
	 * <p>
	 * The returned navigable map will be serializable if the specified map is
	 * serializable.
	 * 
	 * @param nm
	 *            the navigable map for which an unmodifiable view is to be
	 *            returned
	 * @return an unmodifiable view of the specified navigable map
	 */
	public static <K, V> NavigableMap<K, V> unmodifiable(
			final NavigableMap<K, ? extends V> nm) {
		return new UnmodifiableNavigableMap<K, V>(nm);
	}

	/**
	 * @serial include
	 */
	private static class UnmodifiableCollection<E> extends
			ForwardingCollection<E> implements Serializable {

		private static final long serialVersionUID = 1820017752578914078L;
		final Collection<? extends E> c;

		private UnmodifiableCollection(Collection<? extends E> c) {
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

	/**
	 * @serial include
	 */
	private static class UnmodifiableSortedCollection<E> extends
			UnmodifiableCollection<E> implements SortedCollection<E>,
			Serializable {

		private static final long serialVersionUID = 1L;
		private final SortedCollection<E> sc;

		private UnmodifiableSortedCollection(final SortedCollection<E> sc) {
			super(sc);
			this.sc = sc;
		}

		@Override
		public Comparator<? super E> comparator() {
			return sc.comparator();
		}
	}

	/**
	 * @serial include
	 */
	private static class UnmodifiableSortedList<E> extends
			UnmodifiableSortedCollection<E> implements SortedList<E>,
			Serializable {

		private static final long serialVersionUID = 1L;
		private final SortedList<E> sl;

		private UnmodifiableSortedList(final SortedList<E> sl) {
			super(sl);
			this.sl = sl;
		}

		@Override
		public SortedList<E> headList(E toElement) {
			return new UnmodifiableSortedList<E>(sl.headList(toElement));
		}

		@Override
		public SortedList<E> subList(E fromElement, E toElement) {
			return new UnmodifiableSortedList<E>(sl.subList(fromElement,
					toElement));
		}

		@Override
		public SortedList<E> subList(int fromIndex, int toIndex) {
			return new UnmodifiableSortedList<E>(sl.subList(fromIndex, toIndex));
		}

		@Override
		public SortedList<E> tailList(E fromElement) {
			return new UnmodifiableSortedList<E>(sl.tailList(fromElement));
		}

		@Override
		public void add(int index, E element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(int index, Collection<? extends E> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ListIterator<E> listIterator() {
			return Iterators2.unmodifiable(sl.listIterator());
		}

		@Override
		public ListIterator<E> listIterator(int index) {
			return Iterators2.unmodifiable(sl.listIterator(index));
		}

		@Override
		public E remove(int index) {
			throw new UnsupportedOperationException();
		}

		@Override
		public E set(int index, E element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public E get(int index) {
			return sl.get(index);
		}

		@Override
		public int indexOf(Object o) {
			return sl.indexOf(o);
		}

		@Override
		public int lastIndexOf(Object o) {
			return sl.lastIndexOf(o);
		}
	}

	/**
	 * @serial include
	 */
	private static class UnmodifiableQueue<E> extends UnmodifiableCollection<E>
			implements Queue<E>, Serializable {

		private static final long serialVersionUID = 1L;
		private final Queue<? extends E> q;

		private UnmodifiableQueue(final Queue<? extends E> q) {
			super(q);
			this.q = q;
		}

		@Override
		public E element() {
			return q.element();
		}

		@Override
		public boolean offer(E e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public E peek() {
			return q.peek();
		}

		@Override
		public E poll() {
			throw new UnsupportedOperationException();
		}

		@Override
		public E remove() {
			throw new UnsupportedOperationException();
		}

	}

	/**
	 * @serial include
	 */
	private static class UnmodifiableDeque<E> extends UnmodifiableQueue<E>
			implements Deque<E>, Serializable {

		private static final long serialVersionUID = 1L;
		private final Deque<E> d;

		private UnmodifiableDeque(final Deque<? extends E> d) {
			super(d);
			this.d = (Deque<E>) d;
		}

		@Override
		public void addFirst(E e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addLast(E e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Iterator<E> descendingIterator() {
			return Iterators.unmodifiableIterator(d.descendingIterator());
		}

		@Override
		public E getFirst() {
			return d.getFirst();
		}

		@Override
		public E getLast() {
			return d.getLast();
		}

		@Override
		public boolean offerFirst(E e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean offerLast(E e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public E peekFirst() {
			return d.peekFirst();
		}

		@Override
		public E peekLast() {
			return d.peekLast();
		}

		@Override
		public E pollFirst() {
			throw new UnsupportedOperationException();
		}

		@Override
		public E pollLast() {
			throw new UnsupportedOperationException();
		}

		@Override
		public E pop() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void push(E e) {
			throw new UnsupportedOperationException();

		}

		@Override
		public E removeFirst() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeFirstOccurrence(Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public E removeLast() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeLastOccurrence(Object o) {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * @serial include
	 */
	private static class UnmodifiableSet<E> extends UnmodifiableCollection<E>
			implements Set<E>, Serializable {

		private static final long serialVersionUID = -9215047833775013803L;

		private UnmodifiableSet(Set<? extends E> s) {
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

	/**
	 * @serial include
	 */
	private static class UnmodifiableSortedSet<E> extends UnmodifiableSet<E>
			implements SortedSet<E>, Serializable {

		private static final long serialVersionUID = -4929149591599911165L;
		private final SortedSet<E> ss;

		private UnmodifiableSortedSet(SortedSet<E> ss) {
			super(ss);
			this.ss = ss;
		}

		@Override
		public Comparator<? super E> comparator() {
			return ss.comparator();
		}

		@Override
		public SortedSet<E> subSet(E fromElement, E toElement) {
			return new UnmodifiableSortedSet<E>(ss.subSet(fromElement,
					toElement));
		}

		@Override
		public SortedSet<E> headSet(E toElement) {
			return new UnmodifiableSortedSet<E>(ss.headSet(toElement));
		}

		@Override
		public SortedSet<E> tailSet(E fromElement) {
			return new UnmodifiableSortedSet<E>(ss.tailSet(fromElement));
		}

		@Override
		public E first() {
			return ss.first();
		}

		@Override
		public E last() {
			return ss.last();
		}
	}

	/**
	 * @serial include
	 */
	private static class UnmodifiableNavigableSet<E> extends
			UnmodifiableSortedSet<E> implements NavigableSet<E>, Serializable {

		private static final long serialVersionUID = 1L;
		private final NavigableSet<E> ns;
		private transient NavigableSet<E> descendingSet = null;

		private UnmodifiableNavigableSet(NavigableSet<E> ns) {
			super(ns);
			this.ns = ns;
		}

		@Override
		public E ceiling(E e) {
			return ns.ceiling(e);
		}

		@Override
		public Iterator<E> descendingIterator() {
			return Iterators.unmodifiableIterator(ns.iterator());
		}

		@Override
		public NavigableSet<E> descendingSet() {
			if (descendingSet == null)
				descendingSet = new UnmodifiableNavigableSet<E>(
						ns.descendingSet());
			return descendingSet;
		}

		@Override
		public E floor(E e) {
			return ns.floor(e);
		}

		@Override
		public NavigableSet<E> headSet(E toElement, boolean inclusive) {
			return new UnmodifiableNavigableSet<E>(ns.headSet(toElement,
					inclusive));
		}

		@Override
		public E higher(E e) {
			return ns.higher(e);
		}

		@Override
		public E lower(E e) {
			return ns.lower(e);
		}

		@Override
		public E pollFirst() {
			throw new UnsupportedOperationException();
		}

		@Override
		public E pollLast() {
			throw new UnsupportedOperationException();
		}

		@Override
		public NavigableSet<E> subSet(E fromElement, boolean fromInclusive,
				E toElement, boolean toInclusive) {
			return new UnmodifiableNavigableSet<E>(ns.subSet(fromElement,
					fromInclusive, toElement, toInclusive));
		}

		@Override
		public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
			return new UnmodifiableNavigableSet<E>(ns.tailSet(fromElement,
					inclusive));
		}
	}

	/**
	 * @serial include
	 */
	private static class UnmodifiableMap<K, V> extends ForwardingMap<K, V>
			implements Serializable {

		private static final long serialVersionUID = -1034234728574286014L;
		private transient Set<K> keySet = null;
		private transient Set<Map.Entry<K, V>> entrySet = null;
		private transient Collection<V> values = null;
		private final Map<? extends K, ? extends V> m;

		private UnmodifiableMap(Map<? extends K, ? extends V> m) {
			this.m = checkNotNull(m);
		}

		@Override
		public Set<K> keySet() {
			if (keySet == null)
				keySet = new UnmodifiableSet<K>(m.keySet());
			return keySet;
		}

		@Override
		public Set<Map.Entry<K, V>> entrySet() {
			if (entrySet == null) {
				ImmutableSet.Builder<Entry<K, V>> builder = ImmutableSet
						.builder();
				for (Entry<K, V> entry : delegate().entrySet())
					builder.add(new SimpleImmutableEntry<K, V>(entry));
				return builder.build();
			}
			return entrySet;
		}

		@Override
		public Collection<V> values() {
			if (values == null)
				values = new UnmodifiableCollection<V>(m.values());
			return values;
		}

		@Override
		public V put(K key, V value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public V remove(Object key) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void putAll(Map<? extends K, ? extends V> m) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean equals(Object o) {
			return o == this || m.equals(o);
		}

		@Override
		public int hashCode() {
			return m.hashCode();
		}

		@Override
		public String toString() {
			return m.toString();
		}

		@Override
		protected Map<K, V> delegate() {
			return (Map<K, V>) m;
		}
	}

	/**
	 * @serial include
	 */
	private static class UnmodifiableSortedMap<K, V> extends
			UnmodifiableMap<K, V> implements SortedMap<K, V>, Serializable {

		private static final long serialVersionUID = -8806743815996713206L;
		private final SortedMap<K, ? extends V> sm;

		private UnmodifiableSortedMap(SortedMap<K, ? extends V> sm) {
			super(sm);
			this.sm = sm;
		}

		@Override
		public Comparator<? super K> comparator() {
			return sm.comparator();
		}

		@Override
		public SortedMap<K, V> subMap(K fromKey, K toKey) {
			return new UnmodifiableSortedMap<K, V>(sm.subMap(fromKey, toKey));
		}

		@Override
		public SortedMap<K, V> headMap(K toKey) {
			return new UnmodifiableSortedMap<K, V>(sm.headMap(toKey));
		}

		@Override
		public SortedMap<K, V> tailMap(K fromKey) {
			return new UnmodifiableSortedMap<K, V>(sm.tailMap(fromKey));
		}

		@Override
		public K firstKey() {
			return sm.firstKey();
		}

		@Override
		public K lastKey() {
			return sm.lastKey();
		}
	}

	/**
	 * @serial include
	 */
	final static class UnmodifiableNavigableMap<K, V> extends
			UnmodifiableSortedMap<K, V> implements NavigableMap<K, V>,
			Serializable {

		private static final long serialVersionUID = 1L;
		private final NavigableMap<K, ? extends V> nm;
		private transient Entry<K, V> lastEntry = null;
		private transient Entry<K, V> firstEntry = null;
		private transient NavigableMap<K, V> descendingMap = null;
		private transient NavigableSet<K> descendingKeySet = null;
		private transient NavigableSet<K> navigableKeySet = null;

		private UnmodifiableNavigableMap(final NavigableMap<K, ? extends V> nm) {
			super(nm);
			this.nm = nm;
		}

		@Override
		public Entry<K, V> ceilingEntry(K key) {
			return new SimpleImmutableEntry<K, V>(nm.ceilingEntry(key));
		}

		@Override
		public K ceilingKey(K key) {
			return nm.ceilingKey(key);
		}

		@Override
		public NavigableSet<K> descendingKeySet() {
			if (descendingKeySet == null)
				descendingKeySet = new UnmodifiableNavigableSet<K>(
						nm.descendingKeySet());
			return descendingKeySet;
		}

		@Override
		public NavigableMap<K, V> descendingMap() {
			if (descendingMap == null)
				descendingMap = new UnmodifiableNavigableMap<K, V>(
						nm.descendingMap());
			return descendingMap;
		}

		@Override
		public Entry<K, V> firstEntry() {
			if (firstEntry == null)
				firstEntry = new SimpleImmutableEntry<K, V>(nm.firstEntry());
			return firstEntry;
		}

		@Override
		public Entry<K, V> floorEntry(K key) {
			return new SimpleImmutableEntry<K, V>(nm.floorEntry(key));
		}

		@Override
		public K floorKey(K key) {
			return nm.floorKey(key);
		}

		@Override
		public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
			return new UnmodifiableNavigableMap<K, V>(nm.headMap(toKey,
					inclusive));
		}

		@Override
		public Entry<K, V> higherEntry(K key) {
			return new SimpleImmutableEntry<K, V>(nm.higherEntry(key));
		}

		@Override
		public K higherKey(K key) {
			return nm.higherKey(key);
		}

		@Override
		public Entry<K, V> lastEntry() {
			if (lastEntry == null)
				lastEntry = new SimpleImmutableEntry<K, V>(nm.lastEntry());
			return lastEntry;
		}

		@Override
		public Entry<K, V> lowerEntry(K key) {
			return new SimpleImmutableEntry<K, V>(nm.lowerEntry(key));
		}

		@Override
		public K lowerKey(K key) {
			return nm.lowerKey(key);
		}

		@Override
		public NavigableSet<K> navigableKeySet() {
			if (navigableKeySet == null)
				navigableKeySet = new UnmodifiableNavigableSet<K>(
						nm.navigableKeySet());
			return navigableKeySet;
		}

		@Override
		public Entry<K, V> pollFirstEntry() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Entry<K, V> pollLastEntry() {
			throw new UnsupportedOperationException();
		}

		@Override
		public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive,
				K toKey, boolean toInclusive) {
			return new UnmodifiableNavigableMap<K, V>(nm.subMap(fromKey,
					fromInclusive, toKey, toInclusive));
		}

		@Override
		public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
			return new UnmodifiableNavigableMap<K, V>(nm.tailMap(fromKey,
					inclusive));
		}
	}

}