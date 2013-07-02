/*
 * Copyright (C) 2010 Zhenya Leonov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package collect;

import java.util.NavigableMap;
import java.util.NavigableSet;

import com.google.common.collect.ForwardingObject;
import com.google.common.collect.ForwardingSortedMap;

/**
 * A {@link NavigableMap} which forwards all its method calls to another {@code
 * NavigableMap}. Subclasses should override one or more methods to modify the
 * behavior of the backing navigable map as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 * 
 * @see ForwardingObject
 * @author Zhenya Leonov
 */
public abstract class ForwardingNavigableMap<K, V> extends ForwardingSortedMap<K, V>
		implements NavigableMap<K, V> {

	@Override
	abstract protected NavigableMap<K, V> delegate();

	@Override
	public Entry<K, V> ceilingEntry(K key) {
		return delegate().ceilingEntry(key);
	}

	@Override
	public K ceilingKey(K key) {
		return delegate().ceilingKey(key);
	}

	@Override
	public NavigableSet<K> descendingKeySet() {
		return delegate().descendingKeySet();
	}

	@Override
	public NavigableMap<K, V> descendingMap() {
		return delegate().descendingMap();
	}

	@Override
	public Entry<K, V> firstEntry() {
		return delegate().firstEntry();
	}

	@Override
	public Entry<K, V> floorEntry(K key) {
		return delegate().floorEntry(key);
	}

	@Override
	public K floorKey(K key) {
		return delegate().floorKey(key);
	}

	@Override
	public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
		return delegate().headMap(toKey, inclusive);
	}

	@Override
	public Entry<K, V> higherEntry(K key) {
		return delegate().higherEntry(key);
	}

	@Override
	public K higherKey(K key) {
		return delegate().higherKey(key);
	}

	@Override
	public Entry<K, V> lastEntry() {
		return delegate().lastEntry();
	}

	@Override
	public Entry<K, V> lowerEntry(K key) {
		return delegate().lowerEntry(key);
	}

	@Override
	public K lowerKey(K key) {
		return lowerKey(key);
	}

	@Override
	public NavigableSet<K> navigableKeySet() {
		return delegate().navigableKeySet();
	}

	@Override
	public Entry<K, V> pollFirstEntry() {
		return delegate().pollFirstEntry();
	}

	@Override
	public Entry<K, V> pollLastEntry() {
		return delegate().pollLastEntry();
	}

	@Override
	public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey,
			boolean toInclusive) {
		return delegate().subMap(fromKey, fromInclusive, toKey, toInclusive);
	}

	@Override
	public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
		return delegate().tailMap(fromKey, inclusive);
	}

}
