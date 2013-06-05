/*
 * Copyright (C) 2012 Zhenya Leonov
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

package com.palamida.util.base;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An immutable {@code Pair} of non-{@code null} objects. Neither the first nor
 * second object can be changed after creation.
 * 
 * @author Zhenya Leonov
 * 
 * @param <T>
 *            The type of the first object
 * @param <U>
 *            The type of the second object
 */
final public class ImmutablePair<T, U> extends Pair<T, U> {

	private ImmutablePair(final T first, final U second) {
		super(first, second);
	}

	/**
	 * Returns an immutable {@code Pair} containing the specified objects.
	 * 
	 * @param first
	 *            the first object
	 * @param second
	 *            the second object
	 * @return an immutable {@code Pair} containing the specified objects
	 */
	public static <T, U> ImmutablePair<T, U> of(final T first, final U second) {
		return new ImmutablePair<T, U>(checkNotNull(first),
				checkNotNull(second));
	}

	/**
	 * Always throws {@code UnsupportedOperationException}.
	 * 
	 * @throws UnsupportedOperationException
	 *             always
	 */
	@Override
	public void setFirst(final T first) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Always throws {@code UnsupportedOperationException}.
	 * 
	 * @throws UnsupportedOperationException
	 *             always
	 */
	@Override
	public void setSecond(final U second) {
		throw new UnsupportedOperationException();
	}

}