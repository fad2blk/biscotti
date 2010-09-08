package collections;

import java.util.Comparator;

import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.ForwardingObject;

/**
 * A {@link SortedCollection} which forwards all its method calls to another
 * {@code SortedCollection}. Subclasses should override one or more methods to
 * modify the behavior of the backing collection as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 * 
 * @see ForwardingObject
 * @author Zhenya Leonov
 */
public abstract class ForwardingSortedCollection<E> extends
		ForwardingCollection<E> implements SortedCollection<E> {

	@Override
	protected abstract SortedCollection<E> delegate();

	@Override
	public Comparator<? super E> comparator() {
		return delegate().comparator();
	}

}