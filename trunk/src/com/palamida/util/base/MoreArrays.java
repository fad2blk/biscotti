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

import java.util.Arrays;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ObjectArrays;

/**
 * Static utility methods that operate on or return arrays.
 * 
 * @author Zhenya Leonov
 * @see Arrays
 * @see ObjectArrays
 */
final public class MoreArrays {

	private MoreArrays() {
	};

	/**
	 * Returns a new array containing the result of {@code function.apply(F)} on
	 * each element of the specified array.
	 * 
	 * @param function
	 *            the function to apply to each element of the specified array
	 * @param from
	 *            the array to transform
	 * @return new array containing the result of {@code function.apply(F)} on
	 *         each element of the specified array
	 */
	@SuppressWarnings("unchecked")
	public static <F, T> T[] transform(final F[] from, final Function<? super F, ? extends T> function) {
		checkNotNull(from);
		checkNotNull(function);
		final Object[] to = new Object[from.length];
		for (int i = 0; i < to.length; i++)
			to[i] = function.apply(from[i]);
		return (T[]) to;
	}

	/**
	 * Returns {@code true} if the specified array contains the given element,
	 * {@code false} otherwise.
	 * 
	 * @param array
	 *            the specified array
	 * @param element
	 *            the object to look find
	 * @return {@code true} if the specified array contains the given element,
	 *         {@code false} otherwise
	 */
	public static <T> boolean contains(final T[] array, final Object element) {
		checkNotNull(array);
		checkNotNull(element);
		for (final Object o : array)
			if (Objects.equal(o, element))
				return true;
		return false;
	}

}