package com.googlecode.biscotti.collect;

import com.google.common.collect.ForwardingObject;
import com.google.common.collect.ForwardingQueue;

/**
 * A {@link BoundedQueue} which forwards all its method calls to another {@code
 * BoundedQueue}. Subclasses should override one or more methods to modify the
 * behavior of the backing queue as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 * 
 * @see ForwardingObject
 * @author Zhenya Leonov
 */
public abstract class ForwardingBoundedQueue<E> extends ForwardingQueue<E>
		implements BoundedQueue<E> {

	@Override
	protected abstract BoundedQueue<E> delegate();

	@Override
	public int maxSize() {
		return delegate().maxSize();
	}

	@Override
	public int remainingCapacity() {
		return delegate().remainingCapacity();
	}

}
