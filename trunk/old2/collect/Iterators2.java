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

package collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ForwardingListIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.UnmodifiableIterator;

/**
 * Static utility methods which operate on or return {@link Iterator}s.
 * 
 * @author Zhenya Leonov
 * @see Iterators
 */
public final class Iterators2 {

	private Iterators2() {
	}

	private static final ListIterator<Object> EMPTY_LIST_ITERATOR = new EmptyListIterator<Object>();
	private static final PeekingIterator<Object> EMPTY_PEEKING_ITERATOR = new EmptyPeekingIterator<Object>();

	/**
	 * Combines multiple list iterators into a single {@code ListIterator}. The
	 * returned list iterator supports the {@code add(E)}, {@code remove()}, and
	 * {@code set(E)} operations if the corresponding backing iterator supports
	 * them.
	 * 
	 * @param inputs
	 *            the source list iterators
	 * @return a {@code ListIterator} consisting of specified list iterators
	 */
	public static <E> ListIterator<E> concat(final ListIterator<E>... inputs) {
		checkNotNull(inputs);
		for (ListIterator<E> li : inputs)
			checkNotNull(li);
		return new ListIterator<E>() {
			private int count = 0;
			private int index = 0;

			@Override
			public void add(E e) {
				inputs[count].add(e);
			}

			@Override
			public boolean hasNext() {
				while (count < inputs.length) {
					if (inputs[count].hasNext())
						return true;
					count++;
				}
				count--;
				return false;
			}

			@Override
			public boolean hasPrevious() {
				while (count >= 0) {
					if (inputs[count].hasPrevious())
						return true;
					count--;
				}
				count = 0;
				return false;
			}

			@Override
			public E next() {
				if (hasNext()) {
					index++;
					return inputs[count].next();
				}
				throw new NoSuchElementException();
			}

			@Override
			public int nextIndex() {
				return index;
			}

			@Override
			public E previous() {
				if (hasPrevious()) {
					index--;
					return inputs[count].previous();
				}
				throw new NoSuchElementException();
			}

			@Override
			public int previousIndex() {
				return index - 1;
			}

			@Override
			public void remove() {
				inputs[count].remove();
			}

			@Override
			public void set(E e) {
				inputs[count].set(e);
			}
		};
	}

	/**
	 * Returns the number of elements in the given {@code Iterator} which
	 * satisfy the specified {@code Predicate}.
	 * 
	 * @param iterator
	 *            the given {@code Iterator}
	 * @param predicate
	 *            the specified {@code Predicate}
	 * @return the number of elements in {@code iterator} which satisfy
	 *         {@code predicate}
	 */
	public static <E> int countIf(final Iterator<E> iterator,
			final Predicate<? super E> predicate) {
		Preconditions.checkNotNull(iterator);
		Preconditions.checkNotNull(predicate);
		int count = 0;
		while (iterator.hasNext())
			if (predicate.apply(iterator.next()))
				count++;
		return count;
	}

	/**
	 * Returns the number of elements in the given {@code Iterator} which do not
	 * satisfy the specified {@code Predicate}.
	 * 
	 * @param <E>
	 *            the type of elements in the given {@code Iterator}
	 * @param iterator
	 *            the given {@code Iterator}
	 * @param predicate
	 *            the specified {@code Predicate}
	 * @return the number of elements in {@code iterator} which do not satisfy
	 *         {@code predicate}
	 */
	public static <E> int countIfNot(final Iterator<E> iterator,
			final Predicate<? super E> predicate) {
		Preconditions.checkNotNull(iterator);
		Preconditions.checkNotNull(predicate);
		int count = 0;
		while (iterator.hasNext())
			if (!predicate.apply(iterator.next()))
				count++;
		return count;
	}

	/**
	 * Decorates the given {@code ListIterator} to return only the elements that
	 * are instances of the specified {@code Class}.
	 * 
	 * @param unfiltered
	 *            a list iterator containing objects of any type
	 * @param type
	 *            the type of elements desired
	 * @return an {@code UnmodifiableListIterator} containing all elements of
	 *         the original iterator that were of the requested type
	 */
	public static <E> UnmodifiableListIterator<E> filter(
			final ListIterator<E> unfiltered, final Class<E> type) {
		return filter(unfiltered, Predicates.instanceOf(type));
	}

	/**
	 * Decorates the given {@code ListIterator} to return only the elements that
	 * satisfy the specified {@code Predicate}.
	 * 
	 * @param unfiltered
	 *            the given list iterator
	 * @param predicate
	 *            the specified predicate
	 * @return an {@code UnmodifiableListIterator} which returns only the
	 *         elements of {@code unfiltered} that satisfy {@code predicate}
	 */
	public static <E> UnmodifiableListIterator<E> filter(
			final ListIterator<E> unfiltered,
			final Predicate<? super E> predicate) {
		checkNotNull(unfiltered);
		checkNotNull(predicate);
		return new UnmodifiableListIterator<E>() {
			public E nextElement;
			public E prevElement;
			public int index = 0;
			public boolean hasNext;
			public boolean hasPrev;
			public boolean next = false;
			public boolean prev = false;

			@Override
			public boolean hasNext() {
				if (next && hasNext)
					return true;
				prev = false;
				while (delegate().hasNext()) {
					nextElement = delegate().next();
					if (predicate.apply(nextElement))
						return next = hasNext = true;
				}
				return false;
			}

			@Override
			public boolean hasPrevious() {
				if (prev && hasPrev)
					return true;
				next = false;
				while (delegate().hasPrevious()) {
					prevElement = delegate().previous();
					if (predicate.apply(prevElement))
						return prev = hasPrev = true;
				}
				return false;
			}

			@Override
			public E next() {
				if (!next)
					hasNext();
				if (hasNext) {
					index++;
					next = false;
					return nextElement;
				} else
					throw new NoSuchElementException();
			}

			@Override
			public int nextIndex() {
				return index;
			}

			@Override
			public E previous() {
				if (!prev)
					hasPrevious();
				if (hasPrev) {
					index--;
					prev = false;
					return prevElement;
				} else
					throw new NoSuchElementException();
			}

			@Override
			public int previousIndex() {
				return index - 1;
			}

			@Override
			protected ListIterator<E> delegate() {
				return unfiltered;
			}
		};
	}

	/**
	 * Returns an immutable empty {@code ListIterator}.
	 * 
	 * @return an immutable empty {@code ListIterator}
	 * @deprecated {@link Collections#emptyListIterator()} is provided in Java
	 *             SE 7 API.
	 */
	@SuppressWarnings("unchecked")
	public static <E> ListIterator<E> emptyListIterator() {
		return (ListIterator<E>) EMPTY_LIST_ITERATOR;
	}

	/**
	 * Returns an immutable empty {@code PeekingIterator}.
	 * 
	 * @return an immutable empty {@code PeekingIterator}
	 */
	@SuppressWarnings("unchecked")
	public static <E> PeekingIterator<E> emptyPeekingIterator() {
		return (PeekingIterator<E>) EMPTY_PEEKING_ITERATOR;
	}

	/**
	 * Decorates the specified {@code Iterator} to behave like a
	 * {@code ListIterator} by maintaining a {@code List} of all elements
	 * returned from calls to {@code next()}.
	 * <p>
	 * The {@code remove()}, {@code set(E)}, and {@code add(E)} operations are
	 * not supported. Modifying the underlying {@code Iterator} yields
	 * unpredictable results.
	 * 
	 * @param iterator
	 *            the underlying {@code Iterator}
	 * @return a {@code UnmodifiableListIterator} backed by {@code iterator}
	 */
	public static <E> UnmodifiableListIterator<E> listIterator(
			final Iterator<E> iterator) {
		checkNotNull(iterator);
		return new UnmodifiableListIterator<E>() {
			private final List<E> list = Lists.newArrayList();
			private int index = -1;

			@Override
			public boolean hasNext() {
				if (index == list.size() - 1)
					return iterator.hasNext();
				else
					return true;
			}

			@Override
			public E next() {
				E nextElement;
				if (index == list.size() - 1) {
					nextElement = iterator.next();
					list.add(nextElement);
					index++;
				} else
					nextElement = list.get(++index);
				return nextElement;
			}

			@Override
			public boolean hasPrevious() {
				return index >= 0;
			}

			@Override
			public E previous() {
				if (index < 0)
					throw new NoSuchElementException();
				return list.get(index--);
			}

			@Override
			public int nextIndex() {
				return index + 1;
			}

			@Override
			public int previousIndex() {
				return index;
			}

			// This method is never used.
			@Override
			protected ListIterator<E> delegate() {
				return this;
			}
		};
	}

	/**
	 * Returns an {@code Iterator} over the specified {@code Collection} in
	 * reverse sequential order. If the specified collection is an instance of
	 * {@code Deque} or {@code NavigableSet} the iterator is obtained by
	 * invoking the {@code descendingIterator()} method, else an unmodifiable
	 * iterator is returned by calling the {@code reverseOrder(Iterator)}
	 * method.
	 * 
	 * @param c
	 *            the specified collection
	 * @return an iterator over the specified collection in reverse sequential
	 *         order
	 */
	public static <E> Iterator<E> reverseOrder(final Collection<? extends E> c) {
		checkNotNull(c);
		if (c instanceof Deque<?>)
			return ((Deque) c).descendingIterator();
		if (c instanceof NavigableSet<?>)
			return ((NavigableSet) c).descendingIterator();
		else
			return reverseOrder(c.iterator());

	}

	/**
	 * Returns an {@code UnmodifiableIterator} which reverses the iteration
	 * order of the specified {@code Iterator}. The underlying iterator must be
	 * non-blocking and finite, because it is necessary to iterate through all
	 * its elements on startup.
	 * 
	 * @param iterator
	 *            the underlying {@code Iterator}
	 * @return a reverse view of {@code iterator}
	 */
	public static <E> UnmodifiableIterator<E> reverseOrder(
			final Iterator<? extends E> iterator) {
		checkNotNull(iterator);
		final Deque<E> stack = Lists.newLinkedList();
		while (iterator.hasNext()) {
			stack.push(iterator.next());
		}
		return new UnmodifiableIterator<E>() {
			@Override
			public boolean hasNext() {
				return !stack.isEmpty();
			}

			@Override
			public E next() {
				return stack.pop();
			}
		};
	}

	/**
	 * Returns a {@code ListIterator} that applies the specified
	 * {@code Function} to each element of the given list iterator.
	 * <p>
	 * The returned list iterator does not support {@code add(E)} and
	 * {@code set(E)} but does support {@code remove()} if the given list
	 * iterator does.
	 * 
	 * @param <E>
	 *            the type of elements traversed by the given list iterator
	 * @param <T>
	 *            the type of output of the specified function
	 * @param fromIterator
	 *            the given list iterator
	 * @param function
	 *            the specified function
	 * @return a list iterator that applies {@code function} to each element of
	 *         {@code fromIterator}
	 */
	public static <E, T> ListIterator<T> transform(
			final ListIterator<E> fromIterator,
			final Function<? super E, ? extends T> function) {
		checkNotNull(fromIterator);
		checkNotNull(function);
		return new ForwardingListIterator<T>() {
			@Override
			public T next() {
				return function.apply(fromIterator.next());
			}

			@Override
			public void add(T e) {
				throw new UnsupportedOperationException();
			}

			@Override
			public T previous() {
				return function.apply(fromIterator.previous());
			}

			@Override
			public void set(T e) {
				throw new UnsupportedOperationException();
			}

			@Override
			// ok since type T is only used in next, previous, add, and set
			protected ListIterator delegate() {
				return fromIterator;
			}
		};
	}

	/**
	 * Decorates the specified {@code ListIterator} to prevent {@code remove()},
	 * {@code add(E)}, and {@code set(E)}.
	 * 
	 * @param listIterator
	 *            the underlying {@code ListIterator}
	 * @return an unmodifiable view of {@code listIterator}
	 */
	public static <E> UnmodifiableListIterator<E> unmodifiable(
			final ListIterator<? extends E> listIterator) {
		checkNotNull(listIterator);
		return new UnmodifiableListIterator<E>() {
			@SuppressWarnings("unchecked")
			@Override
			protected ListIterator<E> delegate() {
				return (ListIterator<E>) listIterator;
			}
		};
	}

	/**
	 * Decorates the specified {@code PeekingIterator} to prevent
	 * {@code remove()}.
	 * 
	 * @param peekingIterator
	 *            the underlying {@code PeekingIterator}
	 * @return an unmodifiable view of {@code peekingIterator}
	 */
	public static <E> UnmodifiablePeekingIterator<E> unmodifiable(
			final PeekingIterator<? extends E> peekingIterator) {
		checkNotNull(peekingIterator);
		return new UnmodifiablePeekingIterator<E>() {
			@SuppressWarnings("unchecked")
			@Override
			protected PeekingIterator<E> delegate() {
				return (PeekingIterator<E>) peekingIterator;
			}
		};
	}

	private static class EmptyListIterator<E> implements ListIterator<E> {

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public boolean hasPrevious() {
			return false;
		}

		@Override
		public E next() {
			throw new NoSuchElementException();
		}

		@Override
		public int nextIndex() {
			return 0;
		}

		@Override
		public E previous() {
			throw new NoSuchElementException();
		}

		@Override
		public int previousIndex() {
			return -1;
		}

		@Override
		public final void remove() {
			throw new IllegalStateException();
		}

		@Override
		public final void add(E e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public final void set(E e) {
			throw new IllegalStateException();
		}
	}

	private static class EmptyPeekingIterator<E> implements PeekingIterator<E> {

		@Override
		public E next() {
			throw new NoSuchElementException();
		}

		@Override
		public E peek() {
			throw new NoSuchElementException();
		}

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public final void remove() {
			throw new IllegalStateException();
		}
	}

}