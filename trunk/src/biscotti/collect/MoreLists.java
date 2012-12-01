package biscotti.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

/**
 * Static utility methods which operate on or return {@link List}s.
 * 
 * @author Zhenya Leonov
 * @see Lists
 */
public class MoreLists {

	private MoreLists() {
	}

	/**
	 * Creates a {@code LinkedList} containing the specified initial elements.
	 * 
	 * @param first
	 *            the first element
	 * @param rest
	 *            an array of additional elements, possibly empty
	 * @return a {@code LinkedList} containing the specified initial elements
	 */
	public static <E> LinkedList<E> newLinkedList(final E first,
			final E... rest) {
		checkNotNull(first);
		checkNotNull(rest);
		final LinkedList<E> linkedList = Lists.newLinkedList();
		linkedList.add(first);
		Collections.addAll(linkedList, rest);
		return linkedList;
	}

	/**
	 * Returns the index of the first occurrence in the specified list of an
	 * element which satisfies the given predicate, or -1 if there is no such
	 * element.
	 * <p>
	 * Note: If the specified list allows {@code null} elements the given
	 * predicate must be able to handle {@code null} elements as well to avoid a
	 * {@code NullPointerException}.
	 * 
	 * @param list
	 *            the specified list
	 * @param predicate
	 *            the given predicate
	 * @return the index of the first occurrence in the specified list of an
	 *         element which satisfies the given predicate, or -1 if there is no
	 *         such element
	 */
	public static <E> int indexOf(List<E> list, Predicate<? super E> predicate) {
		checkNotNull(list);
		checkNotNull(predicate);
		final ListIterator<E> e = list.listIterator();
		while (e.hasNext())
			if (predicate.apply(e.next()))
				return e.previousIndex();
		return -1;
	}

	/**
	 * Returns the index of the last occurrence in the specified list of an
	 * element which satisfies the given predicate, or -1 if there is no such
	 * element.
	 * <p>
	 * Note: If the specified list allows {@code null} elements the given
	 * predicate must be able to handle {@code null} elements as well to avoid a
	 * {@code NullPointerException}.
	 * 
	 * @param list
	 *            the specified list
	 * @param predicate
	 *            the given predicate
	 * @return the index of the last occurrence in the specified list of an
	 *         element which satisfies the given predicate, or -1 if there is no
	 *         such element
	 */
	public static <E> int lastIndexOf(List<E> list,
			Predicate<? super E> predicate) {
		checkNotNull(list);
		checkNotNull(predicate);
		final ListIterator<E> e = list.listIterator(list.size());
		while (e.hasPrevious())
			if (predicate.apply(e.next()))
				return e.previousIndex();
		return -1;
	}

}
