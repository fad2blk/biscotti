package com.palamida.common.collect;

/**
 * A {@link Sortedlist} augmented with navigation methods reporting closest
 * matches for given search targets.
 * 
 * @author Zhenya Leonov
 * @param <E>
 *            the type of elements held in this sorted-list
 */
interface Navigablelist<E> extends Sortedlist<E> {

	/**
	 * Returns a reverse order view of the elements contained in this
	 * sorted-list. The descending sorted-list is backed by this sorted-list, so
	 * changes to the sorted-list are reflected in the descending sorted-list,
	 * and vice-versa. If either sorted-list is modified while an iteration over
	 * either sorted-list is in progress (except through the iterator's own
	 * {@code remove} operation), the results of the iteration are undefined.
	 * 
	 * @return a reverse order view of this list
	 */
	Navigablelist<E> descendingList();

	/**
	 * Returns a view of the greatest elements in this sorted-list strictly less
	 * than the given element, or {@code null} if there is no such elements.
	 * 
	 * @param e
	 *            the value to match
	 * @return a view of greatest elements less than {@code e}, or {@code null}
	 *         if there is no such elements
	 */
	Navigablelist<E> lower(E e);

	/**
	 * Returns a view of the greatest elements in this sorted-list less than or
	 * equal to the given element, or {@code null} if there is no such elements.
	 * 
	 * @param e
	 *            the value to match
	 * @return a view of greatest elements less than or equal to {@code e}, or
	 *         {@code null} if there is no such elements
	 */
	Navigablelist<E> floor(E e);

	/**
	 * Returns a view of the least elements in this sorted-list greater than or
	 * equal to the given element, or {@code null} if there is no such elements.
	 * 
	 * @param e
	 *            the value to match
	 * @return a view of least elements greater than or equal to {@code e}, or
	 *         {@code null} if there is no such elements
	 */
	Navigablelist<E> ceiling(E e);

	/**
	 * Returns a view of the least elements in this sorted-list strictly greater
	 * than the given element, or {@code null} if there is no such elements.
	 * 
	 * @param e
	 *            the value to match
	 * @return a view of least elements greater than {@code e}, or {@code null}
	 *         if there is no such elements
	 */
	Navigablelist<E> higher(E e);

	/**
	 * Returns a view of the portion of this sorted-list whose elements range
	 * from {@code fromElement} to {@code toElement}. If {@code fromElement} and
	 * {@code toElement} are equal, the returned sorted-list is empty unless
	 * {@code fromExclusive} and {@code toExclusive} are both true. The returned
	 * sorted-list is backed by this sorted-list, so changes in the returned
	 * sorted-list are reflected in this sorted-list, and vice-versa.
	 * <p>
	 * The returned sorted-list will throw an {@code IllegalArgumentException}
	 * on an attempt to insert an element outside its range.
	 * 
	 * @param fromElement
	 *            low endpoint of the returned sorted-list
	 * @param fromInclusive
	 *            {@code true} if the low endpoint is to be included in the
	 *            returned view
	 * @param toElement
	 *            high endpoint of the returned sorted-list
	 * @param toInclusive
	 *            {@code true} if the high endpoint is to be included in the
	 *            returned view
	 * @return a view of the portion of this sorted-list whose elements range
	 *         from {@code fromElement}, inclusive, to {@code toElement},
	 *         exclusive
	 * @throws IllegalArgumentException
	 *             if {@code fromElement} is greater than {@code toElement}; or
	 *             if this sorted-list itself has a restricted range, and
	 *             {@code fromElement} or {@code toElement} lies outside the
	 *             bounds of the range
	 */
	Navigablelist<E> subList(E fromElement, boolean fromInclusive, E toElement,
			boolean toInclusive);

	/**
	 * Returns a view of the portion of this sorted-list whose elements are less
	 * than (or equal to, if {@code inclusive} is true) {@code toElement}. The
	 * returned sorted-list is backed by this sorted-list, so changes in the
	 * returned sorted-list are reflected in this sorted-list, and vice-versa.
	 * <p>
	 * The returned sorted-list will throw an {@code IllegalArgumentException}
	 * on an attempt to insert an element outside its range.
	 * 
	 * @param toElement
	 *            high endpoint of the returned sorted-list
	 * @param inclusive
	 *            {@code true} if the high endpoint is to be included in the
	 *            returned view
	 * @return a view of the portion of this sorted-list whose elements are less
	 *         than (or equal to, if {@code inclusive} is true)
	 *         {@code toElement}
	 * @throws IllegalArgumentException
	 *             if this sorted-list itself has a restricted range, and
	 *             {@code toElement} lies outside the bounds of the range
	 */
	Navigablelist<E> headList(E toElement, boolean inclusive);

	/**
	 * Returns a view of the portion of this sorted-list whose elements are
	 * greater than (or equal to, if {@code inclusive} is true)
	 * {@code fromElement}. The returned sorted-list is backed by this
	 * sorted-list, so changes in the returned sorted-list are reflected in this
	 * sorted-list, and vice-versa.
	 * <p>
	 * The returned sorted-list will throw an {@code IllegalArgumentException}
	 * on an attempt to insert an element outside its range.
	 * 
	 * @param fromElement
	 *            low endpoint of the returned sorted-list
	 * @param inclusive
	 *            {@code true} if the low endpoint is to be included in the
	 *            returned view
	 * @return a view of the portion of this sorted-list whose elements are
	 *         greater than or equal to {@code fromElement}
	 * @throws IllegalArgumentException
	 *             if this sorted-list itself has a restricted range, and
	 *             {@code fromElement} lies outside the bounds of the range
	 */
	Navigablelist<E> tailList(E fromElement, boolean inclusive);

}