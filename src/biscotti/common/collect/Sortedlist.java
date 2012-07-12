package biscotti.common.collect;

import java.util.ListIterator;

/**
 * A <i>list-like</i> {@link SortedCollection}. Like a sorted set a sorted-list
 * maintains its elements in sorted order according to their <i>natural
 * ordering</i> or by a given {@code Comparator}.
 * <p>
 * Unlike a sorted set a sorted-list accepts duplicate elements and allows users
 * to access and remove elements by their integer index.
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
	 * between {@code fromIndex}, inclusive, and {@code toIndex}, exclusive. If
	 * {@code fromIndex} and {@code toIndex} are equal, the returned sorted-list
	 * is empty. The returned sorted-list is backed by this sorted-list, so
	 * changes in the returned sorted-list are reflected in this sorted-list,
	 * and vice-versa.
	 * <p>
	 * The returned sorted-list will throw an {@code IllegalArgumentException}
	 * on an attempt to insert an element outside its range.
	 * 
	 * @param fromIndex
	 *            low endpoint (inclusive) of the returned sorted-list
	 * @param toIndex
	 *            high endpoint (exclusive) of the returned sorted-list
	 * @return a view of the portion of this sorted-list within the specified
	 *         range
	 * @throws IndexOutOfBoundsException
	 *             if
	 *             {@code fromIndex < 0 || toIndex > size || fromIndex > toIndex}
	 */
	public Sortedlist<E> subList(int fromIndex, int toIndex);

}