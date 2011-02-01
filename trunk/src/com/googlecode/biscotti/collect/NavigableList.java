package com.googlecode.biscotti.collect;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NavigableSet;

/**
 * A {@link List} extended with navigation methods reporting closest matches for
 * given search targets. Methods {@code lower(E)}, {@code floor(E)},
 * {@code ceiling(E)}, and {@code higher(E)} return views containing elements
 * respectively less than, less than or equal, greater than or equal, and
 * greater than a given element, returning {@code null} if no matches can be
 * found. This interface is the {@code List} analog of {@link NavigableSet}.
 * <p>
 * A {@code NavigableList} may be accessed and traversed in either ascending or
 * descending order. The {@code descendingList()} method returns a view of the
 * list with the senses of all relational and directional methods inverted.
 * <p>
 * The performance of ascending operations and views is likely to be faster than
 * that of descending ones.
 * 
 * @author Zhenya Leonov
 * @param <E>
 *            the type of elements contained in this list
 */
public interface NavigableList<E> extends List<E> {

	/**
	 * Returns a view of the greatest elements in this list strictly less than
	 * the given element, or {@code null} if there is no such elements.
	 * 
	 * @param e
	 *            the value to match
	 * @return a view of greatest elements less than {@code e}, or {@code null}
	 *         if there is no such elements
	 */
	NavigableList<E> lower(E e);

	/**
	 * Returns a view of the greatest elements in this list less than or equal
	 * to the given element, or {@code null} if there is no such elements.
	 * 
	 * @param e
	 *            the value to match
	 * @return a view of greatest elements less than or equal to {@code e}, or
	 *         {@code null} if there is no such elements
	 */
	NavigableList<E> floor(E e);

	/**
	 * Returns a view of the least elements in this list greater than or equal
	 * to the given element, or {@code null} if there is no such elements.
	 * 
	 * @param e
	 *            the value to match
	 * @return a view of least elements greater than or equal to {@code e}, or
	 *         {@code null} if there is no such elements
	 */
	NavigableList<E> ceiling(E e);

	/**
	 * Returns a view of the least elements in this list strictly greater than
	 * the given element, or {@code null} if there is no such elements.
	 * 
	 * @param e
	 *            the value to match
	 * @return a view of least elements greater than {@code e}, or {@code null}
	 *         if there is no such elements
	 */
	NavigableList<E> higher(E e);

	/**
	 * Returns a list iterator over the elements in this list, in descending
	 * order.
	 * 
	 * @return a list iterator of the elements in this list, in descending
	 *         order.
	 */
	ListIterator<E> descendingListIterator();

	/**
	 * Returns a list iterator over the elements in this list, in descending
	 * order, starting at the specified position in this list. The specified
	 * index indicates the first element that would be returned by an initial
	 * call to {@link ListIterator#next next()}.
	 * 
	 * @param index
	 *            index of first element to be returned from the list iterator
	 *            (by a call to the {@code next()} method)
	 * @return a list iterator of the elements in this list, in descending
	 *         order, starting at the specified position in this list
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range
	 *             ({@code  index < 0 || index > size()})
	 */
	ListIterator<E> descendingListIterator(int index);

	/**
	 * Returns a reverse order view of the elements contained in this list. The
	 * descending list is backed by this list, so changes to the list are
	 * reflected in the descending list, and vice-versa. If either list is
	 * modified while an iteration over either list is in progress (except
	 * through the iterator's list {@code remove} operation), the results of the
	 * iteration are undefined.
	 * 
	 * @return a reverse order view of this list
	 */
	NavigableList<E> descendingList();

	/**
	 * Returns an iterator over the elements in this list, in descending order.
	 * Equivalent in effect to {@code descendingList().iterator()}.
	 * 
	 * @return an iterator over the elements in this list, in descending order
	 */
	Iterator<E> descendingIterator();

	/**
	 * Returns a view of the portion of this list whose elements range from
	 * {@code fromElement} to {@code toElement}. If {@code fromElement} and
	 * {@code toElement} are equal, the returned list is empty unless
	 * {@code fromExclusive} and {@code toExclusive} are both true. The returned
	 * list is backed by this list, so changes in the returned list are
	 * reflected in this list, and vice-versa. The returned list supports all
	 * optional list operations that this list supports.
	 * <p>
	 * The returned list will throw an {@code IllegalArgumentException} on an
	 * attempt to insert an element outside its range.
	 * 
	 * @param fromElement
	 *            low endpoint of the returned list
	 * @param fromInclusive
	 *            {@code true} if the low endpoint is to be included in the
	 *            returned view
	 * @param toElement
	 *            high endpoint of the returned list
	 * @param toInclusive
	 *            {@code true} if the high endpoint is to be included in the
	 *            returned view
	 * @return a view of the portion of this list whose elements range from
	 *         {@code fromElement}, inclusive, to {@code toElement}, exclusive
	 * @throws IllegalArgumentException
	 *             if {@code fromElement} is greater than {@code toElement}; or
	 *             if this list itself has a restricted range, and
	 *             {@code fromElement} or {@code toElement} lies outside the
	 *             bounds of the range
	 */
	NavigableList<E> subList(E fromElement, boolean fromInclusive, E toElement,
			boolean toInclusive);

	/**
	 * Returns a view of the portion of this list whose elements are less than
	 * (or equal to, if {@code inclusive} is true) {@code toElement}. The
	 * returned list is backed by this list, so changes in the returned list are
	 * reflected in this list, and vice-versa. The returned list supports all
	 * optional list operations that this list supports.
	 * <p>
	 * The returned list will throw an {@code IllegalArgumentException} on an
	 * attempt to insert an element outside its range.
	 * 
	 * @param toElement
	 *            high endpoint of the returned list
	 * @param inclusive
	 *            {@code true} if the high endpoint is to be included in the
	 *            returned view
	 * @return a view of the portion of this list whose elements are less than
	 *         (or equal to, if {@code inclusive} is true) {@code toElement}
	 * @throws IllegalArgumentException
	 *             if this list itself has a restricted range, and
	 *             {@code toElement} lies outside the bounds of the range
	 */
	NavigableList<E> headList(E toElement, boolean inclusive);

	/**
	 * Returns a view of the portion of this list whose elements are greater
	 * than (or equal to, if {@code inclusive} is true) {@code fromElement}. The
	 * returned list is backed by this list, so changes in the returned list are
	 * reflected in this list, and vice-versa. The returned list supports all
	 * optional list operations that this list supports.
	 * <p>
	 * The returned list will throw an {@code IllegalArgumentException} on an
	 * attempt to insert an element outside its range.
	 * 
	 * @param fromElement
	 *            low endpoint of the returned list
	 * @param inclusive
	 *            {@code true} if the low endpoint is to be included in the
	 *            returned view
	 * @return a view of the portion of this list whose elements are greater
	 *         than or equal to {@code fromElement}
	 * @throws IllegalArgumentException
	 *             if this list itself has a restricted range, and
	 *             {@code fromElement} lies outside the bounds of the range
	 *             SortedList
	 */
	NavigableList<E> tailList(E fromElement, boolean inclusive);

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	@Override
	NavigableList<E> subList(int fromIndex, int toIndex);

}