package com.googlecode.biscotti.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.AbstractMap.SimpleImmutableEntry;

import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;

/**
 * Static utility methods which operate on or return {@link Collection}s and
 * {@link Map}s.
 * 
 * @author Zhenya Leonov
 */
final public class Collections4 {

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

		private UnmodifiableSortedSet(SortedSet<E> s) {
			super(s);
			ss = s;
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
			return new UnmodifiableNavigableSet<E>(ns.descendingSet());
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
			return ns.pollFirst();
		}

		@Override
		public E pollLast() {
			return ns.pollLast();
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
				for (Entry<K, V> e : delegate().entrySet()) {
					builder.add(new SimpleImmutableEntry<K, V>(e));
				}
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

		private transient K firstKey = null;
		private transient K lastKey = null;
		
		private UnmodifiableSortedMap(SortedMap<K, ? extends V> m) {
			super(m);
			sm = m;
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
			if( firstKey == null )
				firstKey = sm.firstKey();
			return firstKey;
		}

		@Override
		public K lastKey() {
			if( lastKey == null )
				lastKey = sm.lastKey();
			return lastKey;
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
		
		private UnmodifiableNavigableMap(final NavigableMap<K, ? extends V> m) {
			super(m);
			nm = m;
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
			if( descendingKeySet == null )
				descendingKeySet = new UnmodifiableNavigableSet<K>(nm.descendingKeySet());
			return descendingKeySet;
		}

		@Override
		public NavigableMap<K, V> descendingMap() {
			if( descendingMap == null )
				descendingMap = new UnmodifiableNavigableMap<K, V>(nm.descendingMap());
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
			return new UnmodifiableNavigableSet<K>(nm.navigableKeySet());
		}

		@Override
		public Entry<K, V> pollFirstEntry() {
			return new SimpleImmutableEntry<K, V>(nm.pollFirstEntry());
		}

		@Override
		public Entry<K, V> pollLastEntry() {
			return new SimpleImmutableEntry<K, V>(nm.pollLastEntry());
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