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

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Static utility methods which operate on or return {@link Iterable}s.
 * 
 * @author Zhenya Leonov
 * @see Iterables
 * 
 */
public final class Iterables2 {

	private Iterables2() {
	}

	/**
	 * Returns the number of elements in the given {@code Iterable} which
	 * satisfy the specified {@code Predicate}.
	 * 
	 * @param <E>
	 *            the type of elements in the provided {@code Iterable}
	 * @param iterable
	 *            the provided {@code Iterable}
	 * @param predicate
	 *            the specified {@code Predicate}
	 * @return the number of elements in {@code iterable} which satisfy
	 *         {@code predicate}
	 */
	public static <E> int countIf(final Iterable<E> iterable,
			final Predicate<? super E> predicate) {
		Preconditions.checkNotNull(iterable);
		Preconditions.checkNotNull(predicate);
		int count = 0;
		for (E element : iterable)
			if (predicate.apply(element))
				count++;
		return count;
	}

	/**
	 * Returns the number of elements in the given {@code Iterable} which do not
	 * satisfy the specified {@code Predicate}.
	 * 
	 * @param <E>
	 *            the type of elements in the provided {@code Iterable}
	 * @param iterable
	 *            the provided {@code Iterable}
	 * @param predicate
	 *            the the specified {@code Predicate}
	 * @return the number of elements in {@code iterable} which do not satisfy
	 *         {@code predicate}
	 */
	public static <E> int countyIfNot(final Iterable<E> iterable,
			final Predicate<? super E> predicate) {
		Preconditions.checkNotNull(iterable);
		Preconditions.checkNotNull(predicate);
		int count = 0;
		for (E element : iterable)
			if (!predicate.apply(element))
				count++;
		return count;
	}

}