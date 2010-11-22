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
