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

import com.google.common.base.Objects;

/**
 * A container of two possibly {@code null} objects.
 * <p>
 * A convenience class, similar to the Pair class in the C++ STL library, most
 * commonly used for storing and passing pairs of objects.
 * 
 * @author Zhenya Leonov
 * @param <T>
 *            The type of the first object
 * @param <U>
 *            The type of the second object
 */
public abstract class Pair<T, U> {
	private T first;
	private U second;

	/**
	 * Creates a new {@code Pair} of objects.
	 * 
	 * @param first
	 *            the first object
	 * @param second
	 *            the second object
	 */
	public Pair(final T first, final U second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Returns the first object.
	 * 
	 * @return the second object
	 */
	public T getFirst() {
		return first;
	}

	/**
	 * Sets the first object.
	 * 
	 * @param first
	 *            the first object
	 */
	public abstract void setFirst(final T first);

	/**
	 * Sets the second object.
	 * 
	 * @param second
	 *            the second object
	 */
	public abstract void setSecond(final U second);

	/**
	 * Returns the second object.
	 * 
	 * @return the second object
	 */
	public U getSecond() {
		return second;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(first, second);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Pair<?, ?> other = (Pair<?, ?>) obj;
		return Objects.equal(first, other.first) && Objects.equal(second, other.second);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("first", first).add("second", second).toString();
	}

}
