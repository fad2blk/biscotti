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