package biscotti.collections;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;

import com.google.common.base.Preconditions;

/**
 * Static methods which operate on or return {@link Set}s.
 * 
 * @author Zhenya Leonov
 */
public final class Sets2 {

	private Sets2() {
	};

	/**
	 * Returns an unmodifiable view of the specified {@code NavigableSet}.
	 * Attempts to modify the returned set directly, via its iterator, or via
	 * its {@code subSet}, {@code headSet}, {@code tailSet}, or {@code
	 * descendingSet} views, result in {@code UnsupportedOperationException}s.
	 * <p>
	 * The returned set will be serializable if the specified navigable set is
	 * serializable.
	 * 
	 * @param navigableSet
	 *            the specified navigable set
	 * @return an unmodifiable view of the given navigable set
	 */
	public static <E> NavigableSet<E> unmodifiableNavigableSet(
			final NavigableSet<? extends E> navigableSet) {
		return new UnmodifiableNavigableSet<E>(navigableSet);
	}

	/**
	 * Returns a synchronized (thread-safe) {@code NavigableSet} backed by the
	 * specified navigable set. In order to guarantee serial access, it is
	 * critical that <b>all</b> access to the backing set is accomplished
	 * through the returned navigable set (or its views).
	 * <p>
	 * Clients must manually synchronize on the returned set when iterating over
	 * it or any of its {@code subSet}, {@code headSet}, {@code tailSet}, or
	 * {@code descendingSet} views.
	 * <p>
	 * The returned set will be serializable if the specified set is
	 * serializable.
	 * 
	 * @param navigableSet
	 *            the specified navigable set to be synchronized
	 * @return a synchronized view of the specified navigable set
	 */
	public static <E> NavigableSet<E> synchronizedNavigableSet(
			final NavigableSet<E> navigableSet) {
		return new SynchronizedNavigableSet<E>(navigableSet);
	}

	// copied directly from java.util.Collections
	static class SynchronizedSet<E> extends
			Collections3.SynchronizedCollection<E> implements Set<E> {
		private static final long serialVersionUID = 487447009682186044L;

		SynchronizedSet(Set<E> s) {
			super(s);
		}

		SynchronizedSet(Set<E> s, Object mutex) {
			super(s, mutex);
		}

		public boolean equals(Object o) {
			synchronized (mutex) {
				return c.equals(o);
			}
		}

		public int hashCode() {
			synchronized (mutex) {
				return c.hashCode();
			}
		}
	}

	// copied directly from java.util.Collections
	static class SynchronizedSortedSet<E> extends SynchronizedSet<E> implements
			SortedSet<E> {
		private static final long serialVersionUID = 8695801310862127406L;
		final private SortedSet<E> ss;

		SynchronizedSortedSet(SortedSet<E> s) {
			super(s);
			ss = s;
		}

		SynchronizedSortedSet(SortedSet<E> s, Object mutex) {
			super(s, mutex);
			ss = s;
		}

		public Comparator<? super E> comparator() {
			synchronized (mutex) {
				return ss.comparator();
			}
		}

		public SortedSet<E> subSet(E fromElement, E toElement) {
			synchronized (mutex) {
				return new SynchronizedSortedSet<E>(ss.subSet(fromElement,
						toElement), mutex);
			}
		}

		public SortedSet<E> headSet(E toElement) {
			synchronized (mutex) {
				return new SynchronizedSortedSet<E>(ss.headSet(toElement),
						mutex);
			}
		}

		public SortedSet<E> tailSet(E fromElement) {
			synchronized (mutex) {
				return new SynchronizedSortedSet<E>(ss.tailSet(fromElement),
						mutex);
			}
		}

		public E first() {
			synchronized (mutex) {
				return ss.first();
			}
		}

		public E last() {
			synchronized (mutex) {
				return ss.last();
			}
		}
	}

	final static class SynchronizedNavigableSet<E> extends
			SynchronizedSortedSet<E> implements NavigableSet<E> {

		private static final long serialVersionUID = 4564689088714970757L;
		private final NavigableSet<E> ns;

		SynchronizedNavigableSet(final NavigableSet<E> ns) {
			super(Preconditions.checkNotNull(ns));
			this.ns = ns;
		}

		SynchronizedNavigableSet(final NavigableSet<E> ns, final Object mutex) {
			super(Preconditions.checkNotNull(ns), Preconditions
					.checkNotNull(mutex));
			this.ns = ns;
		}

		@Override
		public E ceiling(E e) {
			return ns.ceiling(e);
		}

		@Override
		public Iterator<E> descendingIterator() {
			return ns.descendingIterator();
		}

		@Override
		public NavigableSet<E> descendingSet() {
			return new SynchronizedNavigableSet<E>(ns.descendingSet(), mutex);
		}

		@Override
		public E floor(E e) {
			return ns.floor(e);
		}

		@Override
		public NavigableSet<E> headSet(E toElement, boolean inclusive) {
			return new SynchronizedNavigableSet<E>(ns.headSet(toElement,
					inclusive), mutex);
		}

		@Override
		public E higher(E e) {
			return ns.higher(e);
		}

		@Override
		public E lower(E e) {
			return ns.lower(e);
		}

		@Override
		public E pollFirst() {
			return ns.pollFirst();
		}

		@Override
		public E pollLast() {
			return ns.pollLast();
		}

		@Override
		public NavigableSet<E> subSet(E fromElement, boolean fromInclusive,
				E toElement, boolean toInclusive) {
			return new SynchronizedNavigableSet<E>(ns.subSet(fromElement,
					fromInclusive, toElement, toInclusive), mutex);
		}

		@Override
		public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
			return new SynchronizedNavigableSet<E>(ns.tailSet(fromElement,
					inclusive), mutex);
		}
	}

}
