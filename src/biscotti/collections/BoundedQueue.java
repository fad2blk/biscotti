package biscotti.collections;

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
	 * throwing an {@code IllegalStateException} if the element was rejected.
	 */
	@Override
	public boolean add(E e);

	/**
	 * Adds all of the elements in the specified collection to this queue,
	 * removing stale elements (according to this queue's policy) as necessary
	 * to prevent the queue from exceeding its capacity (optional operation).
	 * 
	 * @see Collection#add(Object) add(E)
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
	 * {@code false} if this element was rejected. This method is generally
	 * preferable to {@code add(E)}, which can fail to insert an element only by
	 * throwing an exception.
	 */
	@Override
	public boolean offer(E e);

	/**
	 * Returns the number of elements which can be added to this queue before it
	 * becomes full.
	 * 
	 * @return the remaining capacity of this queue
	 */
	public int remainingCapacity();

}
