package com.googlecode.biscotti.collect;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

/**
 * A non-blocking capacity restricted {@link Queue}. The size of this queue can
 * vary, but never exceed the maximum number of elements (the bound) specified
 * at creation.
 * <p>
 * Classes which implement this interface are not required to be to be
 * <i>thread-safe</i>, and should not be used as a substitute for the
 * {@link BlockingQueue} implementations offered in the {@code
 * java.util.concurrent} package.
 * <p>
 * Typical implementations will define a policy for removing <i>stale</i>
 * elements, or otherwise throw an {@code IllegalStateException} to prevent the
 * queue from exceeding its capacity restrictions.
 * 
 * @author Zhenya Leonov
 * @param <E>
 *            the type of elements held in this queue
 * @see BlockingQueue
 */
public interface BoundedQueue<E> extends Queue<E> {

	/**
	 * Returns the maximum size (the bound) of this queue.
	 * 
	 * @return the maximum size of this queue
	 */
	public int maxSize();

	/**
	 * Returns the number of elements which can be added to this queue before it
	 * becomes full.
	 * 
	 * @return the remaining capacity of this queue
	 */
	public int remainingCapacity();

}