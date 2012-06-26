package biscotti.common.collect;

import java.util.ListIterator;
import java.util.SortedSet;

/**
 * A <i>list-like</i> {@link SortedCollection}. This interface is the
 * <i>list</i> analog of {@link SortedSet}.
 * <p>
 * Users of this interface can access and remove elements by their integer
 * index. To take advantage of the specified ordering the
 * {@link Sortedlist#headList headList(E)}, {@link Sortedlist#subList subList(E,
 * E)}, {@link tailList#headList tailList(E)} operations are provided.
 * 
 * @author Zhenya Leonov
 * @param <E>
 *            the type of elements held in this list
 */
public interface Sortedlist<E> extends SortedCollection<E> {

	/**
	 * Returns the element at the specified position.
	 * 
	 * @param index
	 *            index of the element to return
	 * @return the element at the specified position
	 * @throws IndexOutOfBoundsException
	 *             if the specified index is out of range
	 */
	public E get(int index);

	/**
	 * Removes and returns the element at the specified position (optional
	 * operation).
	 * 
	 * @param index
	 *            the index of the element to be removed
	 * @return the element previously at the specified position
	 * @throws IndexOutOfBoundsException
	 *             if the specified index is out of range
	 */
	public E remove(int index);

	/**
	 * Returns the index of the first occurrence of the specified element, or -1
	 * if the element is not present.
	 * 
	 * @param o
	 *            element to search for
	 * @return the index of the first occurrence of the specified element, or -1
	 *         if the element is not present
	 */
	public int indexOf(Object o);

	/**
	 * Returns the index of the last occurrence of the specified element, or -1
	 * if the element is not present.
	 * 
	 * @param o
	 *            element to search for
	 * @return the index of the last occurrence of the specified element, or -1
	 *         if the element is not present
	 */
	public int lastIndexOf(Object o);

	/**
	 * Returns a list iterator over the elements in this sorted-list.
	 * <p>
	 * The returned iterator does not support the
	 * {@link ListIterator#add(Object) add(E)} and
	 * {@link ListIterator#set(Object) set(E)} operations.
	 * 
	 * @return a list iterator over the elements in this sorted-list
	 */
	public ListIterator<E> listIterator();

	/**
	 * Returns a list iterator over the elements in this sorted-list, starting
	 * at the specified position.
	 * <p>
	 * The returned iterator does not support the
	 * {@link ListIterator#add(Object) add(E)} and
	 * {@link ListIterator#set(Object) set(E)} operations.
	 * 
	 * @param index
	 *            index of the first element to be returned from the collection
	 *            iterator by a call to {@link ListIterator#next() next()}
	 * @return a list iterator over the elements in this sorted-list, starting
	 *         at the specified position
	 */
	public ListIterator<E> listIterator(int index);

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
	Sortedlist<E> sublist(E fromElement, boolean fromInclusive, E toElement,
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
	Sortedlist<E> headlist(E toElement, boolean inclusive);

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
	Sortedlist<E> taillist(E fromElement, boolean inclusive);

	/**
	 * Returns a view of the portion of this sorted-list whose elements range
	 * from {@code fromIndex} to {@code toIndex}. If {@code fromIndex} and
	 * {@code toIndex} are equal, the returned sorted-list is empty unless
	 * {@code fromExclusive} and {@code toExclusive} are both true. The returned
	 * sorted-list is backed by this sorted-list, so changes in the returned
	 * sorted-list are reflected in this sorted-list, and vice-versa.
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
	public Sortedlist<E> sublist(int fromIndex, boolean fromInclusive,
			int toIndex, boolean toInclusive);

}