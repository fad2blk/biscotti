package com.googlecode.biscotti.collect;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ForwardingObject;

/**
 * A {@link BoundedMap} which forwards all its method calls to another {@code
 * BoundedMap}. Subclasses should override one or more methods to modify the
 * behavior of the backing map as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 * 
 * @see ForwardingObject
 * @author Zhenya Leonov
 */
public abstract class ForwardingBoundedMap<K, V> extends ForwardingMap<K, V>
		implements BoundedMap<K, V> {

	@Override
	protected abstract BoundedMap<K, V> delegate();

	@Override
	public int maxSize() {
		return delegate().maxSize();
	}

	@Override
	public boolean offer(K key, V value) {
		return delegate().offer(key, value);
	}

	@Override
	public int remainingCapacity() {
		return delegate().remainingCapacity();
	}

}