package com.googlecode.biscotti;

import com.google.common.collect.PeekingIterator;

/**
 * A {@link PeekingIterator} that does not support {@code remove()}.
 * 
 * @author Zhenya Leonov
 */
public abstract class UnmodifiablePeekingIterator<E> extends
		ForwardingPeekingIterator<E> {

	/**
	 * Guaranteed to throw an {@code UnsupportedOperationException} exception
	 * and leave the underlying data unmodified.
	 * 
	 * @throws UnsupportedOperationException
	 *             always
	 */
	@Override
	public final void remove() {
		throw new UnsupportedOperationException();
	}

}
