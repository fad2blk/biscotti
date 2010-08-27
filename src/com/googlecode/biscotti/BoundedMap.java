package com.googlecode.biscotti;

import java.util.Map;

/**
 * A capacity restricted {@link Map}. The size of this map can vary, but never
 * exceed the maximum number of entries (the bound) specified at creation.
 * <p>
 * Classes which implement this interface must prevent the map from overflowing
 * by defining a policy for removing stale mappings, and must clearly document
 * that policy in their API.
 * 
 * @author Zhenya Leonov
 * @param <K>
 *            the type of keys maintained by this map
 * @param <V>
 *            the type of mapped values
 */
public interface BoundedMap<K, V> extends Map<K, V> {

	/**
	 * Returns the maximum size (the bound) of this map.
	 * 
	 * @return the maximum size of this map
	 */
	public int maxSize();

	/**
	 * Associates the specified value with the specified key in this map,
	 * removing the <i>eldest</i> entry (according to this map's policy) as
	 * necessary to prevent this map from overflowing. If the map previously
	 * contained a mapping for the key, the old value is replaced by the
	 * specified value.
	 */
	@Override
	public V put(K key, V value);

	/**
	 * Copies all of the mappings from the specified map to this map, removing
	 * stale entries (according to this map's policy) as necessary to prevent
	 * the map from overflowing (optional operation). The effect of this call is
	 * equivalent to that of calling {@code put(K, V)} on this map once for each
	 * mapping in the specified map.
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> m);

	/**
	 * Returns the number of new key-value pairs that can be stored in this map
	 * before it becomes full.
	 * 
	 * @return the remaining capacity of this map
	 */
	public int remainingCapacity();

}