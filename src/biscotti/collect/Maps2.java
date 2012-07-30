package biscotti.collect;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.collect.Maps;

/**
 * Static utility methods which operate on or return {@link Map}s.
 * 
 * @author Zhenya Leonov
 * @see Maps
 */
public class Maps2 {

	private Maps2() {
	}

	/**
	 * Creates an empty {@code LinkedHashMap} which orders its keys according to
	 * their <i>access-order</i>.
	 * 
	 * @return an empty {@code LinkedHashMap} which orders its keys according to
	 *         their <i>access-order</i>
	 * @see LinkedHashMap
	 */
	public static <K, V> Map<K, V> newAccessOrderMap() {
		return new LinkedHashMap<K, V>(16, .75F, true);
	}

	/**
	 * Creates a {@code LinkedHashMap} which orders its keys according to their
	 * <i>access-order</i>, containing the same mappings as the specified map.
	 * 
	 * @param m
	 *            the map whose mappings this map should contain
	 * @return a {@code LinkedHashMap} which orders its keys according to their
	 *         <i>access-order</i>, containing the same mappings as the
	 *         specified map
	 * @see LinkedHashMap
	 */
	public static <K, V> Map<K, V> newAccessOrderMap(
			final Map<? extends K, ? extends V> m) {
		checkNotNull(m);
		final Map<K, V> map = new LinkedHashMap<K, V>(Math.max(
				(int) (m.size() / .75F) + 1, 16), .75F, true);
		map.putAll(m);
		return map;
	}

	/**
	 * Creates an empty {@code LinkedHashMap} which orders its keys according to
	 * their <i>access-order</i>, with enough capacity to hold the specified
	 * number of entries without rehashing.
	 * 
	 * @param expectedSize
	 *            the expected size
	 * @return an empty {@code LinkedHashMap} which orders its keys according to
	 *         their <i>access-order</i>, with enough capacity to hold the
	 *         specified number of entries without rehashing
	 * @see LinkedHashMap
	 */
	public static <K, V> Map<K, V> newAccessOrderMapWithExpectedSize(
			final int expectedSize) {
		checkArgument(expectedSize >= 0);
		return new LinkedHashMap<K, V>(Math.max(expectedSize * 2, 16), .75F,
				true);
	}

}
