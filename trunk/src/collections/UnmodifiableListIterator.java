package collections;

import java.util.ListIterator;

import com.google.common.collect.ForwardingListIterator;

/**
 * A {@link ListIterator} that does not support {@code remove()}, {@code add()},
 * and {@code set(E)}.
 * 
 * @author Zhenya Leonov
 */
public abstract class UnmodifiableListIterator<E> extends
		ForwardingListIterator<E> {

	/**
	 * Guaranteed to throw an {@code UnsupportedOperationException} exception
	 * and leave the underlying data unmodified.
	 * 
	 * @throws UnsupportedOperationException
	 *             always
	 */
	@Override
	public final void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Guaranteed to throw an {@code UnsupportedOperationException} exception
	 * and leave the underlying data unmodified.
	 * 
	 * @throws UnsupportedOperationException
	 *             always
	 */
	@Override
	public final void add(E e) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Guaranteed to throw an {@code UnsupportedOperationException} exception
	 * and leave the underlying data unmodified.
	 * 
	 * @throws UnsupportedOperationException
	 *             always
	 */
	@Override
	public final void set(E e) {
		throw new UnsupportedOperationException();
	}

}