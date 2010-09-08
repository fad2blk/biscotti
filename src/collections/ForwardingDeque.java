package collections;

import java.util.Deque;
import java.util.Iterator;

import com.google.common.collect.ForwardingObject;
import com.google.common.collect.ForwardingQueue;

/**
 * A {@link Deque} which forwards all its method calls to another {@code Deque}.
 * Subclasses should override one or more methods to modify the behavior of the
 * backing deque as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 * 
 * @see ForwardingObject
 * @author Zhenya Leonov
 */
public abstract class ForwardingDeque<E> extends ForwardingQueue<E> implements
		Deque<E> {

	@Override
	protected abstract Deque<E> delegate();

	@Override
	public void addFirst(E e) {
		delegate().addFirst(e);
	}

	@Override
	public void addLast(E e) {
		delegate().addLast(e);
	}

	@Override
	public Iterator<E> descendingIterator() {
		return delegate().descendingIterator();
	}

	@Override
	public E getFirst() {
		return delegate().getFirst();
	}

	@Override
	public E getLast() {
		return delegate().getLast();
	}

	@Override
	public boolean offerFirst(E e) {
		return delegate().offer(e);
	}

	@Override
	public boolean offerLast(E e) {
		return delegate().offerLast(e);
	}

	@Override
	public E peekFirst() {
		return delegate().peekFirst();
	}

	@Override
	public E peekLast() {
		return delegate().peekLast();
	}

	@Override
	public E pollFirst() {
		return delegate().pollFirst();
	}

	@Override
	public E pollLast() {
		return delegate().pollLast();
	}

	@Override
	public E pop() {
		return delegate().pop();
	}

	@Override
	public void push(E e) {
		delegate().push(e);
	}

	@Override
	public E removeFirst() {
		return delegate().removeFirst();
	}

	@Override
	public boolean removeFirstOccurrence(Object o) {
		return delegate().removeFirstOccurrence(o);
	}

	@Override
	public E removeLast() {
		return delegate().removeLast();
	}

	@Override
	public boolean removeLastOccurrence(Object o) {
		return removeLastOccurrence(o);
	}

}