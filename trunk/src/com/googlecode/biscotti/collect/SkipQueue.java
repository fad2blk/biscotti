package com.googlecode.biscotti.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Random;

import com.google.common.collect.Ordering;

public class SkipQueue<E> extends AbstractQueue<E> implements
		SortedCollection<E> {

	private Entry<E> head;
	private int randomSeed = new Random().nextInt() | 0x100;
	private int size = 0;
	private int modCount = 0;
	Comparator<? super E> comparator;

	SkipQueue(final Comparator<? super E> comparator) {
		if (comparator != null)
			this.comparator = comparator;
		else
			this.comparator = (Comparator<? super E>) Ordering.natural();
		initialize();
	}

	SkipQueue(final Iterable<? extends E> elements) {
		Comparator<? super E> comparator = null;
		if (elements instanceof NavigableSet<?>)
			comparator = ((NavigableSet) elements).comparator();
		else if (elements instanceof java.util.PriorityQueue<?>)
			comparator = ((java.util.PriorityQueue) elements).comparator();
		else if (elements instanceof SortedCollection<?>)
			comparator = ((SortedCollection) elements).comparator();
		if (comparator == null)
			this.comparator = (Comparator<? super E>) Ordering.natural();
		else
			this.comparator = comparator;
		initialize();
		for (E element : elements)
			add(element);
	}

	private void initialize() {
		head = new Entry<E>();
		head.prev = head;
		head.next = head;
	}

	public static <E extends Comparable<? super E>> SkipQueue<E> create() {
		return new SkipQueue<E>((Comparator<? super E>) null);
	}

	public static <E> SkipQueue<E> create(final Comparator<? super E> comparator) {
		checkNotNull(comparator);
		return new SkipQueue<E>(comparator);
	}

	public static <E> SkipQueue<E> create(final Iterable<? extends E> elements) {
		checkNotNull(elements);
		return new SkipQueue<E>(elements);
	}

	@Override
	public Comparator<? super E> comparator() {
		return comparator;
	}

	@Override
	public boolean offer(E element) {
		checkNotNull(element);
        int headLevel = head.level();
        int level = Math.min(generateRandomLevel(), headLevel + 1);
        if(level > headLevel) {
            Pointer<E>[] pts = new Pointer[level];
            for(int i = 0; i < headLevel; i++) {
                pts[i] = head.pts[i];
            }
            for(int i = headLevel; i < level; i++) {
                pts[i] = new Pointer<E>(head, head, 0);
            }
            head.pts = pts;
            headLevel = level;
        }

        Entry<E> prev = head.prev;
        Entry<E> next = head;
        Entry<E> e = new Entry<E>(level, prev, next, element);
        next.prev = e;
        prev.next = e;

        int prevDistance = 1;
        int nextDistance = 1;
        for(int i = 0; i < level; i++) {
            while(prev.pts == null) {
                prevDistance++;
                prev = prev.prev;
            }
            int lv = prev.level();
            while(lv <= i) {
                Pointer<E> prevPt = prev.pts[lv - 1];
                prevDistance += prevPt.prev.pts[lv - 1].distance;
                prev = prevPt.prev;
                lv = prev.pts.length;
            }
            while(next.pts == null) {
                nextDistance++;
                next = next.next;
            }
            lv = next.level();
            while(lv <= i) {
                Pointer<E> nextPt = next.pts[lv - 1];
                nextDistance += nextPt.distance;
                next = nextPt.next;
                lv = next.pts.length;
            }

            e.pts[i] = new Pointer<E>(prev, next, nextDistance);

            prev.pts[i].next = e;
            prev.pts[i].distance = prevDistance;
            next.pts[i].prev = e;
        }
        for(int i = level; i < headLevel; i++) {
            while(prev.pts == null) {
                prev = prev.prev;
            }
            while(prev.pts.length <= i) {
                prev = prev.pts[prev.pts.length - 1].prev;
            }
            prev.pts[i].distance++;
        }

        size++;
		return true;
	}

	@Override
	public E peek() {
		if (isEmpty())
			return null;
		// do
		return null;
	}

	@Override
	public E poll() {
		if (isEmpty())
			return null;
		// do
		return null;
	}

	@Override
	public boolean contains(Object o) {
		// return o != null && search((E) o) != null;
		return false;
	}

	/**
	 * Returns an iterator over the elements of this queue in priority order
	 * from first (head) to last (tail).
	 * 
	 * @return an iterator over the elements of this queue in priority order
	 */
	@Override
	public Iterator<E> iterator() {
		return new EntryIterator(head);
	}

	@Override
	public boolean remove(Object o) {
		checkNotNull(o);
		// Node node = search((E) o);
		// if (node == null)
		// return false;
		// delete(node);
		// return true;
		return false;
	}

	@Override
	public int size() {
		return size;
	}
	
	// Skip List
	
    private int generateRandomLevel() {
        int x = randomSeed;
        x ^= x << 13;
        x ^= x >>> 17;
        randomSeed = x ^= x << 5;
        if((x & 0x8001) != 0) {
            return 0;
        }
        int level = 0;
        while(((x >>>= 1) & 1) != 0) {
            level++;
        }
        return level;
    }
    
    protected static class Pointer<T> implements Serializable {
        private static final long serialVersionUID = -5260753036548236032L;

        public Entry<T> prev;
        public Entry<T> next;
        public int distance;

        public Pointer(Entry<T> prev, Entry<T> next, int distance) {
            this.prev = prev;
            this.next = next;
            this.distance = distance;
        }
    }

    protected static class Entry<T> implements Serializable {
        private static final long serialVersionUID = 6623755413831454813L;

        public Pointer<T>[] pts;
        public Entry<T> prev;
        public Entry<T> next;
        public T value;

        public Entry() {
        }

        //@SuppressWarnings("unchecked")
        public Entry(int level, Entry<T> prev, Entry<T> next, T value) {
            if(level > 0) {
                this.pts = new Pointer[level];
            }
            this.prev = prev;
            this.next = next;
            this.value = value;
        }

        public int level() {
            return pts != null ? pts.length : 0;
        }
    }
    
    protected class EntryIterator implements Iterator<E> {
        private Entry<E> current;
        private int expectedSize;

        public EntryIterator(Entry<E> current) {
            this.current = current;
            expectedSize = size;
        }

        public boolean hasNext() {
            return current.next != head;
        }

        public E next() {
            if(!hasNext()) {
                throw new NoSuchElementException();
            }
            current = current.next;
            return current.value;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }


}
