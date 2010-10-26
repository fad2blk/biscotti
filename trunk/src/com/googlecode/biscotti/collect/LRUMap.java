package com.googlecode.biscotti.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

/**
 * A {@link LinkedHashMap} implementation of {@link BoundedMap} which removes
 * stale mappings in <i>least-recently-accessed</i> order.
 * <p>
 * This implementation is not <i>thread-safe</i>. If multiple threads modify
 * this map concurrently it must be synchronized externally, consider "wrapping"
 * the map using the {@link Collections3#synchronize(BoundedMap)} method.
 * 
 * @author Zhenya Leonov
 * @param <K>
 *            the type of keys maintained by this map
 * @param <V>
 *            the type of mapped values
 * @see FIFOMap
 */
public final class LRUMap<K, V> extends LinkedHashMap<K, V> implements
		BoundedMap<K, V> {

	private static final long serialVersionUID = 1L;
	private final int maxSize;

	private LRUMap(final int maxSize, final int initialCapacity,
			final float loadFactor) {
		super(initialCapacity, loadFactor, true);
		this.maxSize = maxSize;
	}

	/**
	 * Creates a new {@code LRUMap} having the specified maximum size.
	 * 
	 * @param maxSize
	 *            the maximum size of this map
	 * @return a new {@code LRUMap} having the specified maximum size
	 * @throws IllegalArgumentException
	 *             if {@code maxSize} is less than 1
	 */
	public static <K, V> LRUMap<K, V> create(final int maxSize) {
		Preconditions.checkArgument(maxSize > 0);
		return new LRUMap<K, V>(maxSize, 16, .75F);
	}

	/**
	 * Creates a new {@code LRUMap} with the same mappings, iteration order, and
	 * having the maximum size equal to the size specified map.
	 * 
	 * @param m
	 *            the map whose mappings are to be placed in this map
	 * @return a new {@code LRUMap} with the same mappings, iteration order, and
	 *         having the maximum size equal to the size specified map
	 */
	public static <K, V> LRUMap<K, V> create(
			final Map<? extends K, ? extends V> m) {
		checkNotNull(m);
		LRUMap<K, V> map = new LRUMap<K, V>(m.size(), m.size(), .75F);
		map.putAll(m);
		return map;
	}

	/**
	 * Associates the specified value with the specified key in this map,
	 * removing the <i>least-recently-accessed</i> entry as necessary to prevent
	 * this map from overflowing. If the map previously contained a mapping for
	 * the key, the old value is replaced by the specified value.
	 * 
	 * @return {@inheritDoc}
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws IllegalArgumentException
	 *             {@inheritDoc}
	 */
	@Override
	public V put(K key, V value) {
		return super.put(key, value);
	}

	/**
	 * Associates the specified value with the specified key in this map,
	 * removing the <i>least-recently-accessed</i> entry as necessary to prevent
	 * this map from overflowing. If the map previously contained a mapping for
	 * the key, the old value is replaced by the specified value.
	 * 
	 * @return {@code true}
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws IllegalArgumentException
	 *             {@inheritDoc}
	 */
	@Override
	public boolean offer(K key, V value) {
		put(key, value);
		return true;
	}

	/**
	 * Copies all of the mappings from the specified map to this map, removing
	 * stale entries (according to their access order) as necessary to prevent
	 * the map from overflowing. The effect of this call is equivalent to that
	 * of calling {@code put(K, V)} on this map once for each mapping in the
	 * specified map.
	 * 
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws IllegalArgumentException
	 *             if some property of a key or value in the specified map
	 *             prevents it from being stored in this map
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		super.putAll(m);
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return this.size() > maxSize;
	}

	@Override
	public int maxSize() {
		return maxSize;
	}

	@Override
	public int remainingCapacity() {
		return maxSize - size();
	}

}