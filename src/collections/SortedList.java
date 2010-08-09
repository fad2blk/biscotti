package collections;

import java.util.Comparator;
import java.util.List;

/**
 * A {@link List} that further provides a <i>total ordering</i> on its elements.
 * The elements are ordered using their {@code natural ordering}, or by an
 * explicit {@link Comparator} provided at creation time.
 * <p>
 * To take advantage of the specified ordering additional {@code headList(E)},
 * {@code subList(E, E)}, {@code tailList(E)} operations which return a view of
 * this list are provided.
 * <p>
 * This class may be thought of as the {@code List} analog of {@code SortedSet}
 * with several key differences. In order to maintain element order typical view
 * implementations do not support most or all of the optional operations in the
 * {@code List} interface, further a view returned by this list is not
 * guaranteed to be an instance of {@code SortedList}.
 * 
 * 
 * 
 * 
 * @author Zhenya Leonov
 * @param <E>
 */
public interface SortedList<E> extends List<E> {

	/**
	 * Returns the comparator used to order the elements in this list.
	 */
	public Comparator<? super E> comparator();

	/**
	 * Returns a view of the portion of this list whose elements are strictly
	 * less than {@code toElement}. This implies that if the list contains
	 * duplicate elements, <i>none</i> of the elements which are equal to
	 * {@code toElement}, will be included in the returned list. The returned
	 * list is backed by this list, so changes in the returned list are
	 * reflected in this list.
	 * 
	 * @param toElement
	 *            high endpoint (exclusive) of the returned list
	 * @return a view of the portion of this list whose elements are strictly
	 *         less than {@code toElement}
	 * @throws ClassCastException
	 *             if {@code toElement} is not comparable to the elements in
	 *             this list
	 * @throws NullPointerException
	 *             if {@code toElement} is {@code null} and this list does not
	 *             permit {@code null} elements
	 */
	public List<E> headList(E toElement);

	/**
	 * Returns a view of the portion of this list whose elements range from
	 * {@code fromElement}, inclusive, to {@code toElement}, exclusive. This
	 * implies that if the list contains duplicate elements, <i>all</i> of the
	 * elements which are equal to {@code fromElement} and <i>none</i> of the
	 * elements which are equal to {@code toElement}, will be included in the
	 * returned list. (If {@code fromElement} and {@code toElement} are equal,
	 * the returned list is empty.) The returned list is backed by this list, so
	 * changes in the returned list are reflected in this list.
	 * 
	 * @param fromElement
	 *            low endpoint (inclusive) of the returned list
	 * @param toElement
	 *            high endpoint (exclusive) of the returned list
	 * @return a view of the portion of this list whose elements range from
	 *         {@code fromElement}, inclusive, to {@code toElement}, exclusive
	 * @throws ClassCastException
	 *             if {@code fromElement} or {@code toElement} is not comparable
	 *             to the elements in this list
	 * @throws NullPointerException
	 *             if {@code fromElement} or {@code toElement} is {@code null}
	 *             and this list does not permit {@code null} elements
	 * @throws IllegalArgumentException
	 *             if {@code fromElement} is greater than {@code toElement}
	 */
	public List<E> subList(E fromElement, E toElement);

	/**
	 * Returns a view of the portion of this list whose elements are greater
	 * than or equal to {@code fromElement}. This implies that if the list
	 * contains duplicate elements, <i>all</i> of the elements which are equal
	 * to {@code fromElement}, will be included in the returned list. The
	 * returned list is backed by this list, so changes in the returned list are
	 * reflected in this list.
	 * 
	 * @param fromElement
	 *            low endpoint (inclusive) of the returned list
	 * @return a view of the portion of this list whose elements are greater
	 *         than or equal to {@code fromElement}
	 * @throws ClassCastException
	 *             if {@code fromElement} is not comparable to the elements in
	 *             this list
	 * @throws NullPointerException
	 *             if {@code fromElement} is {@code null} and this list does not
	 *             permit {@code null} elements
	 */
	public List<E> tailList(E fromElement);

}