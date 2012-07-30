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

package biscotti.collect;

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
