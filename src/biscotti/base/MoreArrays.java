package biscotti.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ObjectArrays;

/**
 * Static utility methods that operate on or return arrays.
 * 
 * @author Zhenya Leonov
 * @see Arrays
 * @see ObjectArrays
 */
final public class MoreArrays {

	private MoreArrays() {
	};

	/**
	 * Returns a new array containing the result of {@code function.apply(F)} on
	 * each element of the specified array.
	 * 
	 * @param function
	 *            the function to apply on each element of the specified array
	 * @param from
	 *            the array to transform
	 * @return new array containing the result of {@code function.apply(F)} on
	 *         each element of the specified array
	 */
	@SuppressWarnings("unchecked")
	public static <F, T> T[] transform(final F[] from,
			final Function<? super F, ? extends T> function) {
		checkNotNull(from);
		checkNotNull(function);
		final Object[] to = new Object[from.length];
		for (int i = 0; i < to.length; i++)
			to[i] = function.apply(from[i]);
		return (T[]) to;
	}

	/**
	 * Returns {@code true} if the specified array contains the given element,
	 * {@code false} otherwise.
	 * 
	 * @param array
	 *            the specified array
	 * @param element
	 *            the object to look find
	 * @return {@code true} if the specified array contains the given element,
	 *         {@code false} otherwise
	 */
	public static <T> boolean contains(final T[] array, final Object element) {
		checkNotNull(array);
		checkNotNull(element);
		for (Object o : array)
			return Objects.equal(o, element);
		return false;
	}

}