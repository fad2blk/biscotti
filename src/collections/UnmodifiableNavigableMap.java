package collections;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;

final class UnmodifiableNavigableMap<K, V> extends ForwardingNavigableMap<K, V>
		implements Serializable {

	private static final long serialVersionUID = 2012701510166912384L;

	private final NavigableMap<? extends K, ? extends V> navigableMap;

	UnmodifiableNavigableMap(
			final NavigableMap<? extends K, ? extends V> navigableMap) {
		this.navigableMap = checkNotNull(navigableMap);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected NavigableMap<K, V> delegate() {
		return (NavigableMap<K, V>) navigableMap;
	}

	@Override
	public java.util.Map.Entry<K, V> ceilingEntry(K key) {
		return new AbstractMap.SimpleImmutableEntry<K, V>(delegate()
				.ceilingEntry(key));
	}

	@Override
	public NavigableSet<K> descendingKeySet() {
		return Collections3.unmodifiableNavigableSet(delegate()
				.descendingKeySet());
	}

	@Override
	public NavigableMap<K, V> descendingMap() {
		return new UnmodifiableNavigableMap<K, V>(delegate().descendingMap());
	}

	@Override
	public java.util.Map.Entry<K, V> firstEntry() {
		return new AbstractMap.SimpleImmutableEntry<K, V>(delegate()
				.firstEntry());
	}

	@Override
	public java.util.Map.Entry<K, V> floorEntry(K key) {
		return new AbstractMap.SimpleImmutableEntry<K, V>(delegate()
				.floorEntry(key));
	}

	@Override
	public SortedMap<K, V> headMap(K toKey) {
		return Collections.unmodifiableSortedMap(delegate().headMap(toKey));
	}

	@Override
	public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
		return new UnmodifiableNavigableMap<K, V>(delegate().headMap(toKey,
				inclusive));
	}

	@Override
	public java.util.Map.Entry<K, V> higherEntry(K key) {
		return new AbstractMap.SimpleImmutableEntry<K, V>(delegate()
				.higherEntry(key));
	}

	@Override
	public java.util.Map.Entry<K, V> lastEntry() {
		return new AbstractMap.SimpleImmutableEntry<K, V>(delegate()
				.lastEntry());
	}

	@Override
	public java.util.Map.Entry<K, V> lowerEntry(K key) {
		return new AbstractMap.SimpleImmutableEntry<K, V>(delegate()
				.lowerEntry(key));
	}

	@Override
	public NavigableSet<K> navigableKeySet() {
		return Collections3.unmodifiableNavigableSet(delegate()
				.navigableKeySet());
	}

	@Override
	public java.util.Map.Entry<K, V> pollFirstEntry() {
		return new AbstractMap.SimpleImmutableEntry<K, V>(delegate()
				.pollFirstEntry());
	}

	@Override
	public java.util.Map.Entry<K, V> pollLastEntry() {
		return new AbstractMap.SimpleImmutableEntry<K, V>(delegate()
				.pollLastEntry());
	}

	@Override
	public SortedMap<K, V> subMap(K fromKey, K toKey) {
		return Collections.unmodifiableSortedMap(delegate().subMap(fromKey,
				toKey));
	}

	@Override
	public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey,
			boolean toInclusive) {
		return new UnmodifiableNavigableMap<K, V>(delegate().subMap(fromKey,
				fromInclusive, toKey, toInclusive));
	}

	@Override
	public SortedMap<K, V> tailMap(K fromKey) {
		return Collections.unmodifiableSortedMap(delegate().tailMap(fromKey));
	}

	@Override
	public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
		return new UnmodifiableNavigableMap<K, V>(delegate().tailMap(fromKey,
				inclusive));
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return Collections.unmodifiableSet(delegate().entrySet());
	}

	@Override
	public Set<K> keySet() {
		return Collections.unmodifiableSet(delegate().keySet());
	}

	@Override
	public Collection<V> values() {
		return Collections.unmodifiableCollection(delegate().values());
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public V put(K key, V value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public V remove(Object key) {
		throw new UnsupportedOperationException();
	}

}