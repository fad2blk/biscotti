package com.palamida.common.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

/**
 * Static utility methods which operate on or return {@link Set}s.
 * 
 * @author Zhenya Leonov
 * @see Sets
 */
public class MoreSets {

	private MoreSets() {
	}

	/**
	 * Creates a {@code TreeSet} containing the specified initial elements
	 * sorted according to their <i>natural ordering</i>.
	 * 
	 * @param first
	 *            the first element
	 * @param rest
	 *            an array of additional elements, possibly empty
	 * @return a {@code TreeSet} containing the specified initial elements
	 *         sorted according to their <i>natural ordering</i>
	 */
	public static <E extends Comparable<? super E>> TreeSet<E> newTreeSet(
			final E first, final E... rest) {
		checkNotNull(first);
		checkNotNull(rest);
		final TreeSet<E> treeSet = Sets.newTreeSet();
		treeSet.add(first);
		Collections.addAll(treeSet, rest);
		return treeSet;
	}

	/**
	 * Creates a {@code TreeSet} containing the elements returned by the
	 * specified iterator, sorted according to their <i>natural ordering</i>.
	 * 
	 * @param elements
	 *            the iterator whose elements are to be placed into this set
	 * @return a {@code TreeSet} containing the elements returned by the
	 *         specified iterator, sorted according to their <i>natural
	 *         ordering</i>
	 */
	public static <E extends Comparable<? super E>> TreeSet<E> newTreeSet(
			final Iterator<? extends E> elements) {
		checkNotNull(elements);
		final TreeSet<E> treeSet = Sets.newTreeSet();
		Iterators.addAll(treeSet, elements);
		return treeSet;
	}

}
