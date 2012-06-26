package biscotti.common.collect;

import java.util.ListIterator;
import java.util.SortedSet;

/**
 * A <i>list-like</i> {@link SortedCollection}. This interface is the
 * <i>list</i> analog of {@link SortedSet}.
 * <p>
 * Users of this interface can access and remove elements by their integer
 * index. To take advantage of the specified ordering the
 * {@link SortedList#headList headList(E)}, {@link SortedList#subList subList(E,
 * E)}, {@link tailList#headList tailList(E)} operations are provided.
 * 
 * @author Zhenya Leonov
 * @param <E>
 *            the type of elements held in this list
 */
public interface SortedList<E> extends SortedCollection<E> {

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
	 * Returns a list iterator over the elements in this collection (in sorted
	 * order).
	 * <p>
	 * The returned iterator does not support the
	 * {@link ListIterator#add(Object) add(E)} and
	 * {@link ListIterator#set(Object) set(E)} operations.
	 */
	public ListIterator<E> listIterator();

	/**
	 * Returns a list iterator over the elements in this collection (in sorted
	 * order), starting at the specified index.
	 * <p>
	 * The returned iterator does not support the
	 * {@link ListIterator#add(Object) add(E)} and
	 * {@link ListIterator#set(Object) set(E)} operations.
	 * 
	 * @param index
	 *            index of the first element to be returned from the list
	 *            iterator by a call to {@link ListIterator#next() next()}
	 * @return a list iterator over the elements in this list (in proper
	 *         sequence), starting at the specified position in the list
	 */
	public ListIterator<E> listIterator(int index);

	/**
	 * 
	 * @param fromIndex
	 * @param fromInclusive
	 * @param toIndex
	 * @param toInclusive
	 * @return
	 */
	public SortedList<E> subList(int fromIndex, boolean fromInclusive,
			int toIndex, boolean toInclusive);

	/**
	 * 
	 * @param fromElement
	 * @param fromInclusive
	 * @param toElement
	 * @param toInclusive
	 * @return
	 */
	public SortedList<E> subList(E fromElement, boolean fromInclusive,
			E toElement, boolean toInclusive);

	/**
	 * 
	 * @param toElement
	 * @param inclusive
	 * @return
	 */
	public SortedList<E> headList(E toElement, boolean inclusive);

	/**
	 * Returns a view of the portion of this collection whose elements are
	 * greater than or equal to {@code fromElement}. The returned list is backed
	 * by this list, so changes in the returned list are reflected in this list.
	 * <p>
	 * Attempts to insert an element outside the specified range will result in
	 * an {@code IllegalArgumentException}.
	 * 
	 * @param fromElement
	 *            low endpoint (inclusive) of the returned collection
	 * @param inclusive
	 *            {@code true} if the low endpoint is to be included in the
	 *            returned view
	 * @return a view of the portion of this collection whose elements are
	 *         greater than or equal to {@code fromElement}
	 * @throws ClassCastException
	 *             if {@code fromElement} is not comparable to the elements in
	 *             this collection
	 * @throws NullPointerException
	 *             if {@code fromElement} is {@code null} and this collection
	 *             does not permit {@code null} elements
	 */
	public SortedList<E> tailList(E fromElement, boolean inclusive);

}