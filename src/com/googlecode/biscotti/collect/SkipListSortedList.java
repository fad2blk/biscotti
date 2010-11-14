
package com.googlecode.biscotti.collect;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

public class SkipListSortedList<E> extends AbstractList<E> implements Cloneable, Serializable {
    private static final long serialVersionUID = 8750206937467686912L;

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
            if(expectedSize != size) {
                throw new ConcurrentModificationException();
            }
            removeEntry(current);
            expectedSize = size;
        }
    }

    protected class BackwardEntryIterator implements Iterator<E> {
        private Entry<E> current;
        private int expectedSize;

        public BackwardEntryIterator(Entry<E> current) {
            this.current = current;
            expectedSize = size;
        }

        public boolean hasNext() {
            return current.prev != head;
        }

        public E next() {
            if(!hasNext()) {
                throw new NoSuchElementException();
            }
            current = current.prev;
            return current.value;
        }

        public void remove() {
            if(expectedSize != size) {
                throw new ConcurrentModificationException();
            }
            removeEntry(current);
            expectedSize = size;
        }
    }

    protected Entry<E> head;
    protected int size;

    private int randomSeed;

    public SkipListSortedList() {
        Random rand = new Random();
        randomSeed = rand.nextInt() | 0x100;
        buildHead();
    }

    private void buildHead() {
        head = new Entry<E>();
        head.prev = head;
        head.next = head;
    }

    // [ref] java.util.concurrent.ConcurrentSkipListMap
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

    @SuppressWarnings("unchecked")
    protected Entry<E> addBefore(E element, Entry<E> entry) {
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

        Entry<E> prev = entry.prev;
        Entry<E> next = entry;
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
        return e;
    }

    protected Entry<E> getEntry(int index) {
        if(index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("size: " + size + ", index: " + index);
        }
        Entry<E> e = head;
        int level = e.level();
        int curIndex = -1;
        while(curIndex != index) {
            if(level == 0) {
                e = e.next;
                curIndex++;
            }
            else {
                Pointer<E> p = e.pts[level - 1];
                int n = curIndex + p.distance;
                if(n <= index) {
                    e = p.next;
                    curIndex = n;
                }
                else {
                    level--;
                }
            }
        }
        return e;
    }

    protected void removeEntry(Entry<E> entry) {
        Entry<E> prev = entry.prev;
        Entry<E> next = entry.next;
        prev.next = next;
        next.prev = prev;
        int level = entry.level();
        if(level > 0) {
            for(int i = 0; i < level; i++) {
                Pointer<E> p = entry.pts[i];
                prev = p.prev;
                next = p.next;
                prev.pts[i].next = next;
                next.pts[i].prev = prev;
                prev.pts[i].distance += p.distance - 1;
            }
            prev = entry.pts[level - 1].prev;
        }
        int headLevel = head.level();
        for(int i = level; i < headLevel; i++) {
            while(prev.pts == null) {
                prev = prev.prev;
            }
            while(i >= prev.pts.length) {
                prev = prev.pts[prev.pts.length - 1].prev;
            }
            prev.pts[i].distance--;
        }
        size--;
    }

    protected int getIndex(Entry<E> entry) {
        Entry<E> e = entry;
        int distance = 0;
        while(e != head) {
            if(e.pts == null) {
                distance++;
                e = e.next;
            }
            else {
                Pointer<E> p = e.pts[e.pts.length - 1];
                distance += p.distance;
                e = p.next;
            }
        }
        return size - distance;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        buildHead();
        size = 0;
    }

    @Override
    public boolean add(E element) {
        addBefore(element, head);
        return true;
    }

    @Override
    public void add(int index, E element) {
        if(index == size) {
            addBefore(element, head);
        }
        else {
            Entry<E> entry = getEntry(index);
            addBefore(element, entry);
        }
    }

    @Override
    public E remove(int index) {
        Entry<E> entry = getEntry(index);
        removeEntry(entry);
        return entry.value;
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        if(index == -1) {
            return false;
        }
        remove(index);
        return true;
    }

    @Override
    public E get(int index) {
        return getEntry(index).value;
    }

    @Override
    public E set(int index, E value) {
        Entry<E> entry = getEntry(index);
        E oldValue = entry.value;
        entry.value = value;
        return oldValue;
    }

    @Override
    public int indexOf(Object o) {
        int index = 0;
        Entry<E> e = head.next;
        if(o == null) {
            for(; e != head; e = e.next, index++) {
                if(e.value == null) {
                    return index;
                }
            }
        }
        else {
            for(; e != head; e = e.next, index++) {
                if(e.value.equals(o)) {
                    return index;
                }
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        int index = size - 1;
        Entry<E> e = head.prev;
        if(o == null) {
            for(; e != head; e = e.prev, index--) {
                if(e.value == null) {
                    return index;
                }
            }
        }
        else {
            for(; e != head; e = e.prev, index--) {
                if(e.value.equals(o)) {
                    return index;
                }
            }
        }
        return -1;
    }

    @Override
    public Iterator<E> iterator() {
        return new EntryIterator(head);
    }

    public Iterator<E> backwardIterator() {
        return new BackwardEntryIterator(head);
    }
}
