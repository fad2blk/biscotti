package biscotti.collections;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * Static methods which operate on or return {@link Map}s.
 * 
 * @author Zhenya Leonov
 * @see Maps
 */
public final class Maps2 {

	private Maps2() {
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

	// copied directly from java.util.Collections
	static class SynchronizedMap<K, V> implements Map<K, V>, Serializable {
		// use serialVersionUID from JDK 1.2.2 for interoperability
		private static final long serialVersionUID = 1978198479659022715L;

		private final Map<K, V> m; // Backing Map
		final Object mutex; // Object on which to synchronize

		SynchronizedMap(Map<K, V> m) {
			if (m == null)
				throw new NullPointerException();
			this.m = m;
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

		private transient Set<K> keySet = null;
		private transient Set<Map.Entry<K, V>> entrySet = null;
		private transient Collection<V> values = null;

		public Set<K> keySet() {
			synchronized (mutex) {
				if (keySet == null)
					keySet = new Sets2.SynchronizedSet<K>(m.keySet(), mutex);
				return keySet;
			}
		}

		public Set<Map.Entry<K, V>> entrySet() {
			synchronized (mutex) {
				if (entrySet == null)
					entrySet = new Sets2.SynchronizedSet<Map.Entry<K, V>>(m
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

	// copied directly from java.util.Collections
	static class SynchronizedSortedMap<K, V> extends SynchronizedMap<K, V>
			implements SortedMap<K, V> {
		private static final long serialVersionUID = -8798146769416483793L;

		private final SortedMap<K, V> sm;

		SynchronizedSortedMap(SortedMap<K, V> m) {
			super(m);
			sm = m;
		}

		SynchronizedSortedMap(SortedMap<K, V> m, Object mutex) {
			super(m, mutex);
			sm = m;
		}

		public Comparator<? super K> comparator() {
			synchronized (mutex) {
				return sm.comparator();
			}
		}

		public SortedMap<K, V> subMap(K fromKey, K toKey) {
			synchronized (mutex) {
				return new SynchronizedSortedMap<K, V>(sm
						.subMap(fromKey, toKey), mutex);
			}
		}

		public SortedMap<K, V> headMap(K toKey) {
			synchronized (mutex) {
				return new SynchronizedSortedMap<K, V>(sm.headMap(toKey), mutex);
			}
		}

		public SortedMap<K, V> tailMap(K fromKey) {
			synchronized (mutex) {
				return new SynchronizedSortedMap<K, V>(sm.tailMap(fromKey),
						mutex);
			}
		}

		public K firstKey() {
			synchronized (mutex) {
				return sm.firstKey();
			}
		}

		public K lastKey() {
			synchronized (mutex) {
				return sm.lastKey();
			}
		}
	}

	static final class SynchronizedNavigableMap<K, V> extends
			SynchronizedSortedMap<K, V> implements NavigableMap<K, V> {

		private static final long serialVersionUID = 4634428579220835280L;
		private final NavigableMap<K, V> m;

		SynchronizedNavigableMap(NavigableMap<K, V> m) {
			super(Preconditions.checkNotNull(m));
			this.m = m;
		}

		SynchronizedNavigableMap(NavigableMap<K, V> m, Object mutex) {
			super(Preconditions.checkNotNull(m), Preconditions
					.checkNotNull(mutex));
			this.m = m;
		}

		@Override
		public java.util.Map.Entry<K, V> ceilingEntry(K key) {
			return new AbstractMap.SimpleImmutableEntry<K, V>(m
					.ceilingEntry(key));
		}

		@Override
		public K ceilingKey(K key) {
			return m.ceilingKey(key);
		}

		@Override
		public NavigableSet<K> descendingKeySet() {
			return new Sets2.SynchronizedNavigableSet<K>(m.descendingKeySet());
		}

		@Override
		public NavigableMap<K, V> descendingMap() {
			return new SynchronizedNavigableMap<K, V>(m.descendingMap());
		}

		@Override
		public java.util.Map.Entry<K, V> firstEntry() {
			return new AbstractMap.SimpleImmutableEntry<K, V>(m.firstEntry());
		}

		@Override
		public java.util.Map.Entry<K, V> floorEntry(K key) {
			return new AbstractMap.SimpleImmutableEntry<K, V>(m.floorEntry(key));
		}

		@Override
		public K floorKey(K key) {
			return m.floorKey(key);
		}

		@Override
		public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
			return new SynchronizedNavigableMap<K, V>(m.headMap(toKey,
					inclusive));
		}

		@Override
		public java.util.Map.Entry<K, V> higherEntry(K key) {
			return new AbstractMap.SimpleImmutableEntry<K, V>(m
					.higherEntry(key));
		}

		@Override
		public K higherKey(K key) {
			return m.higherKey(key);
		}

		@Override
		public java.util.Map.Entry<K, V> lastEntry() {
			return new AbstractMap.SimpleImmutableEntry<K, V>(m.lastEntry());
		}

		@Override
		public java.util.Map.Entry<K, V> lowerEntry(K key) {
			return new AbstractMap.SimpleImmutableEntry<K, V>(m.lowerEntry(key));
		}

		@Override
		public K lowerKey(K key) {
			return m.lowerKey(key);
		}

		@Override
		public NavigableSet<K> navigableKeySet() {
			return new Sets2.SynchronizedNavigableSet<K>(m.navigableKeySet(),
					mutex);
		}

		@Override
		public java.util.Map.Entry<K, V> pollFirstEntry() {
			return m.pollFirstEntry();
		}

		@Override
		public java.util.Map.Entry<K, V> pollLastEntry() {
			return m.pollLastEntry();
		}

		@Override
		public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive,
				K toKey, boolean toInclusive) {
			return new SynchronizedNavigableMap<K, V>(m.subMap(fromKey,
					fromInclusive, toKey, toInclusive), mutex);
		}

		@Override
		public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
			return new SynchronizedNavigableMap<K, V>(m.tailMap(fromKey,
					inclusive), mutex);
		}
	}

	static class SynchronizedBoundedMap<K, V> extends SynchronizedMap<K, V>
			implements BoundedMap<K, V> {

		private static final long serialVersionUID = -1802189798040735132L;
		private final BoundedMap<K, V> bm;

		SynchronizedBoundedMap(BoundedMap<K, V> bq) {
			super(bq);
			this.bm = bq;
		}

		@Override
		public int maxSize() {
			synchronized (mutex) {
				return bm.maxSize();
			}
		}

		@Override
		public int remainingCapacity() {
			synchronized (mutex) {
				return bm.remainingCapacity();
			}
		}
	}

}