package biscotti.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;

/**
 * Static utility methods which operate on or return {@link Sortedlist}s.
 * 
 * @author Zhenya Leonov
 */
public class Sortedlists {

	private Sortedlists() {
	}

	/**
	 * Creates a {@code Treelist} containing the specified initial elements
	 * sorted according to their <i>natural ordering</i>.
	 * 
	 * @param first
	 *            the first element
	 * @param rest
	 *            an array of additional elements, possibly empty
	 * @return a {@code Treelist} containing the specified initial elements
	 *         sorted according to their <i>natural ordering</i>
	 */
	public static <E extends Comparable<? super E>> Treelist<E> newTreelist(
			final E first, final E... rest) {
		checkNotNull(first);
		checkNotNull(rest);
		final Treelist<E> tl = Treelist.create();
		tl.add(first);
		Collections.addAll(tl, rest);
		return tl;
	}

	/**
	 * Creates a {@code Skiplist} containing the specified initial elements
	 * sorted according to their <i>natural ordering</i>.
	 * 
	 * @param first
	 *            the first element
	 * @param rest
	 *            an array of additional elements, possibly empty
	 * @return a {@code Skiplist} containing the specified initial elements
	 *         sorted according to their <i>natural ordering</i>
	 */
	public static <E extends Comparable<? super E>> Skiplist<E> newSkiplist(
			final E first, final E... rest) {
		checkNotNull(first);
		checkNotNull(rest);
		final Skiplist<E> sl = Skiplist.create();
		sl.add(first);
		Collections.addAll(sl, rest);
		return sl;
	}

}
