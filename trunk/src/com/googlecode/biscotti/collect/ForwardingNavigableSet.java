package com.googlecode.biscotti.collect;

import java.util.Iterator;
import java.util.NavigableSet;

import com.google.common.collect.ForwardingObject;
import com.google.common.collect.ForwardingSortedSet;

/**
 * A {@link NavigableSet} which forwards all its method calls to another {@code
 * NavigableSet}. Subclasses should override one or more methods to modify the
 * behavior of the backing navigable set as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 * 
 * @see ForwardingObject
 * @author Zhenya Leonov
 */
public abstract class ForwardingNavigableSet<E> extends ForwardingSortedSet<E>
		implements NavigableSet<E> {

	@Override
	abstract protected NavigableSet<E> delegate();

	@Override
	public E ceiling(E e) {
		return delegate().ceiling(e);
	}

	@Override
	public Iterator<E> descendingIterator() {
		return delegate().descendingIterator();
	}

	@Override
	public NavigableSet<E> descendingSet() {
		return delegate().descendingSet();
	}

	@Override
	public E floor(E e) {
		return delegate().floor(e);
	}

	@Override
	public NavigableSet<E> headSet(E toElement, boolean inclusive) {
		return headSet(toElement, inclusive);
	}

	@Override
	public E higher(E e) {
		return higher(e);
	}

	@Override
	public E lower(E e) {
		return lower(e);
	}

	@Override
	public E pollFirst() {
		return pollFirst();
	}

	@Override
	public E pollLast() {
		return pollLast();
	}

	@Override
	public NavigableSet<E> subSet(E fromElement, boolean fromInclusive,
			E toElement, boolean toInclusive) {
		return subSet(fromElement, fromInclusive, toElement, toInclusive);
	}

	@Override
	public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
		return tailSet(fromElement, inclusive);
	}

}
