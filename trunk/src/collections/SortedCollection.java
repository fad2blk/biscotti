package collections;

import java.util.Collection;
import java.util.Comparator;

/**
 * A {@link Collection} that further provides a <i>total ordering</i> on its
 * elements. This interface is the root of all <i>sorted</i> collection
 * interfaces and implementations.
 * <p>
 * Classes which implement this interface (directly or indirectly) are required
 * to implement the {@code comparator()} method which returns the comparator
 * used to order the elements in this collection. Essentially this allows
 * another sorted collection of a desired type to create a copy of this
 * collection. (Note: when using <i>natural ordering</i> the comparator method
 * may return a natural order comparator or {@code null} depending on the
 * specific implementation.)
 * <p>
 * In addition all implementing classes are expected to provide three static
 * creation methods: {@code create()}, returning a collection that orders its
 * elements according to their <i>natural ordering</i>, {@code
 * create(Comparator)} returning a collection which uses the specified
 * comparator to order its elements, and {@code create(Iterable)} returning a
 * collection containing the given initial elements. This is simply a refinement
 * of Java's constructor recommendations, reflecting the new developments of
 * Java 5.
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
