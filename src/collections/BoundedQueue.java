package collections;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

/**
 * A capacity restricted {@link Queue}. The size of this queue can vary, but
 * never exceed the maximum number of elements (the bound) specified at
 * creation.
 * <p>
 * This interface does not require its implementations to be <i>thread-safe</i>,
 * nor does it define a specific strategy to prevent the queue from violating
 * its capacity restrictions.
 * <p>
 * Classes which implement this interface must prevent the queue from
 * overflowing by defining a policy for removing stale elements, and must
 * clearly document that policy in their API.
 * 
 * @author Zhenya Leonov
 * @param <E>
 *            the type of elements held in this queue
 * @see BlockingQueue
 */
public interface BoundedQueue<E> extends Queue<E> {

	/**
	 * Inserts the specified element into this queue, removing the <i>eldest</i>
	 * element (according to this queue's policy) as necessary to prevent the
	 * queue from exceeding its capacity, returning {@code true} upon success or
	 * throwing an {@code IllegalStateException} if the element was rejected due
	 * to capacity restrictions.
	 */
	@Override
	public boolean add(E e);

	/**
	 * Adds all of the elements in the specified collection to this queue,
	 * removing stale elements (according to this queue's policy) as necessary
	 * to prevent the queue from exceeding its capacity, returning {@code true}
	 * upon success or throwing an {@code IllegalStateException} if not all the
	 * elements can be added due to capacity restrictions.
	 * 
	 * @see #add(Object) add(E)
	 */
	@Override
	public boolean addAll(Collection<? extends E> c);

	/**
	 * Returns the maximum size (the bound) of this queue.
	 * 
	 * @return the maximum size of this queue
	 */
	public int maxSize();

	/**
	 * Inserts the specified element into this queue, removing the <i>eldest</i>
	 * element (according to this queue's policy) as necessary to prevent the
	 * queue from exceeding its capacity, returning {@code true} upon success or
	 * {@code false} if this element was rejected due to capacity restrictions.
	 * This method is generally preferable to {@code add(E)}, which can fail to
	 * insert an element only by throwing an exception.
	 */
	@Override
	public boolean offer(E e);

	/**
	 * Adds all of the elements in the specified collection to this queue,
	 * removing stale elements (according to this queue's policy) as necessary
	 * to prevent the queue from exceeding its capacity; ignoring rejected
	 * elements quietly <i>without</i> throwing exceptions. This method is
	 * generally preferable to {@code addAll(Collection)}, which can fail only
	 * by throwing an exception when one or more elements cannot be added due to
	 * capacity restrictions.
	 * 
	 * @param c
	 *            collection containing elements to be added to this collection
	 * @return {@code true} if at least one element was added to this queue,
	 *         else returns {@code false}
	 * @throws UnsupportedOperationException
	 *             if the {@code offerAll(Collection)} operation is not
	 *             supported
	 * @throws ClassCastException
	 *             if the class of an element of the specified collection
	 *             prevents it from being added to this queue
	 * @throws NullPointerException
	 *             if the specified collection contains a {@code null} element
	 *             and this queue does not permit {@code null} elements, or if
	 *             the specified collection is {@code null}
	 * @see Queue#offer(Object) offer(E)
	 * @see Queue#add(Object) add(E)
	 */
	public boolean offerAll(Collection<? extends E> c);

	/**
	 * Returns the number of elements which can be added to this queue before it
	 * becomes full.
	 * 
	 * @return the remaining capacity of this queue
	 */
	public int remainingCapacity();

}
