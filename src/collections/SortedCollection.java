package collections;

import java.util.Collection;
import java.util.Comparator;

/**
 * A {@link Collection} that further provides a <i>total ordering</i> on its
 * elements.
 * 
 * @author Zhenya Leonov
 * @param <E>
 *            the type of elements held in this collection
 */
public interface SortedCollection<E> extends Collection<E> {

	/**
	 * Returns the comparator used to order the elements in this collection.
	 */
	public Comparator<? super E> comparator();

}
