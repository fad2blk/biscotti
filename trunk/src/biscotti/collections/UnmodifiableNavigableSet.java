package biscotti.collections;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;

final class UnmodifiableNavigableSet<E> extends ForwardingNavigableSet<E>
		implements Serializable {

	private static final long serialVersionUID = 260809171552801698L;
	private final NavigableSet<? extends E> navigableSet;

	UnmodifiableNavigableSet(NavigableSet<? extends E> navigableSet) {
		this.navigableSet = Preconditions.checkNotNull(navigableSet);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected NavigableSet<E> delegate() {
		return (NavigableSet<E>) navigableSet;
	}

	@Override
	public Iterator<E> descendingIterator() {
		return Iterators.unmodifiableIterator(delegate().iterator());
	}

	@Override
	public NavigableSet<E> descendingSet() {
		return new UnmodifiableNavigableSet<E>(delegate().descendingSet());
	}

	@Override
	public SortedSet<E> headSet(E toElement) {
		return Collections.unmodifiableSortedSet(delegate().headSet(toElement));
	}

	@Override
	public NavigableSet<E> headSet(E toElement, boolean inclusive) {
		return new UnmodifiableNavigableSet<E>(delegate().headSet(toElement,
				inclusive));
	}

	@Override
	public Iterator<E> iterator() {
		return Iterators.unmodifiableIterator(delegate().iterator());
	}

	@Override
	public SortedSet<E> subSet(E fromElement, E toElement) {
		return Collections.unmodifiableSortedSet(delegate().subSet(fromElement,
				toElement));
	}

	@Override
	public NavigableSet<E> subSet(E fromElement, boolean fromInclusive,
			E toElement, boolean toInclusive) {
		return new UnmodifiableNavigableSet<E>(delegate().subSet(fromElement,
				fromInclusive, toElement, toInclusive));
	}

	@Override
	public SortedSet<E> tailSet(E fromElement) {
		return Collections.unmodifiableSortedSet((delegate()
				.headSet(fromElement)));
	}

	@Override
	public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
		return new UnmodifiableNavigableSet<E>(delegate().tailSet(fromElement,
				inclusive));
	}

	@Override
	public boolean add(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

}