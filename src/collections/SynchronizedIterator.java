package collections;

import java.util.Iterator;

class SynchronizedIterator<E> implements Iterator<E>{
	
	private Iterator<E> iterator;
	private Object mutex;
	
	private SynchronizedIterator(Iterator<E> iterator, Object mutex){
		this.iterator = iterator;
		this.mutex = mutex;
	}
	
	public static <E> Iterator<E> create(Iterator<E> iterator, Object mutex){
		return new SynchronizedIterator<E>(iterator, mutex);
	}

	@Override
	public boolean hasNext() {
		synchronized (mutex) {
			return iterator.hasNext();
		}
	}

	@Override
	public E next() {
		synchronized (mutex) {
			return iterator.next();
		}
	}

	@Override
	public void remove() {
		synchronized (mutex) {
			iterator.remove();
		}
	}

}
