package com.googlecode.biscotti.collect;

import java.util.Map;

/**
 * A non-blocking capacity restricted {@link Map}. The size of this map can
 * vary, but never exceed the maximum number of entries (the bound) specified at
 * creation. Besides to the regular {@link Map#put(Object, Object) put(K, V)}
 * and {@link Map#putAll(Map) putAll(Map)} operations, this interface defines an
 * addition {@link #offer(Object, Object) offer(K, V)} operation, because
 * failure is a normal, rather than exceptional occurrence, when attempting to
 * store a new entry into a full map.
 * <p>
 * Typical implementations will define a policy for removing <i>stale</i>
 * mappings, or otherwise throw an {@code IllegalStateException} to prevent the
 * map from exceeding its capacity restrictions.
 * <p>
 * All implementations are strongly encouraged to define their behavior in their
 * API documentation.
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
	 * Associates the specified value with the specified key in this map if it
	 * is possible to do so immediately without violating capacity restrictions.
	 * If the map previously contained a mapping for the key, the old value is
	 * replaced by the specified value.
	 * 
	 * @return {@inheritDoc}
	 * @throws IllegalStateException
	 *             if the new entry is rejected due to capacity restrictions
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws IllegalArgumentException
	 *             {@inheritDoc}
	 */
	@Override
	public V put(K key, V value);

	/**
	 * Associates the specified value with the specified key in this map if it
	 * is possible to do so immediately without violating capacity restrictions.
	 * If the map previously contained a mapping for the key, the old value is
	 * replaced by the specified value. This method is generally preferable to
	 * {@link #put(Object, Object) put(K, V)}, which can fail only by throwing
	 * an exception.
	 * 
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param value
	 *            value to be associated with the specified key
	 * @return {@code true} if the entry was stored in this map, else
	 *         {@code false}
	 * @throws ClassCastException
	 *             if the class of the specified key or value prevents it from
	 *             being stored in this map
	 * @throws IllegalArgumentException
	 *             if some property of the specified key or value prevents it
	 *             from being stored in this map
	 */
	public boolean offer(K key, V value);

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalStateException
	 *             if an entry in the specified map is rejected due to capacity
	 *             restrictions
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws IllegalArgumentException
	 *             if some property of a key or value in the specified map
	 *             prevents it from being stored in this map
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