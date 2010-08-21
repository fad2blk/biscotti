package collections;

import java.util.Collection;
import java.util.Comparator;

public interface SortedCollection<E> extends Collection<E> {

	/**
	 * Returns the comparator used to order the elements in this collection.
	 */
	public Comparator<? super E> comparator();

}
