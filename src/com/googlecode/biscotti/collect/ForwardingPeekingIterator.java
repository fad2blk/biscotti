package com.googlecode.biscotti.collect;

import com.google.common.collect.ForwardingIterator;
import com.google.common.collect.PeekingIterator;

/**
 * A {@link PeekingIterator} which forwards all its method calls to another
 * {@code PeekingIterator}. Subclasses should override one or more methods to
 * modify the behavior of the backing iterator as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 * 
 * @author Zhenya Leonov
 */
public abstract class ForwardingPeekingIterator<E> extends ForwardingIterator<E>
		implements PeekingIterator<E> {

	@Override
	protected abstract PeekingIterator<E> delegate();

	@Override
	public E peek() {
		return delegate().peek();
	}

}