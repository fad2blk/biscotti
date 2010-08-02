package collections;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A {@link LinkedHashMap} implementation of {@link BoundedMap} which removes
 * stale mappings in <i>insertion/first-in-first-out</i> (FIFO) order.
 * <p>
 * This implementation is not <i>thread-safe</i>. If multiple threads modify
 * this map concurrently it must be synchronized externally, consider "wrapping"
 * the map using the {@code Maps2.synchronizedBoundedMap(BoundedMap)} method.
 * 
 * @author Zhenya Leonov
 * @param <K>
 *            the type of keys maintained by this map
 * @param <V>
 *            the type of mapped values
 * @see LRUMap
 */
public final class FIFOMap<K, V> extends LinkedHashMap<K, V> implements
		BoundedMap<K, V> {

	private static final long serialVersionUID = -3822008757337993587L;
	private final int maxSize;

	private FIFOMap(final int maxSize, final int initialCapacity,
			final float loadFactor) {
		super(initialCapacity, loadFactor);
		this.maxSize = maxSize;
	}

	/**
	 * Creates a new {@code FIFOMap} having the specified maximum size.
	 * 
	 * @param maxSize
	 *            the maximum size of this map
	 * @return an empty {@code FIFOMap} having the specified maximum size
	 * @throws IllegalArgumentException
	 *             if {@code maxSize} is less than 1
	 */
	public static <K, V> FIFOMap<K, V> create(final int maxSize) {
		checkArgument(maxSize > 0);
		return new FIFOMap<K, V>(maxSize, 16, .75F);
	}

	/**
	 * Creates a new {@code FIFOMap} with the same mappings, iteration order,
	 * and having the maximum size equal to the size specified map.
	 * 
	 * @param m
	 *            the map whose mappings are to be placed in this map
	 * @return a new {@code FIFOMap} with the same mappings, iteration order,
	 *         and having the maximum size equal to the size specified map
	 */
	public static <K, V> FIFOMap<K, V> create(
			final Map<? extends K, ? extends V> m) {
		checkNotNull(m);
		FIFOMap<K, V> map = new FIFOMap<K, V>(m.size(), m.size(), .75F);
		map.putAll(m);
		return map;
	}

	@Override
	public V put(K key, V value) {
		return super.put(key, value);
	}

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