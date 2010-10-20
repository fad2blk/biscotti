package com.googlecode.biscotti.collect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

public class TreeQueueTest {

	/**
	 * @tests java.util.PriorityQueue#iterator()
	 */
	@Test
	public void test_iterator() {
		TreeQueue<Integer> integerQueue = TreeQueue.create();
		Integer[] array = { 2, 45, 7, -12, 9 };
		for (int i = 0; i < array.length; i++) {
			integerQueue.offer(array[i]);
		}
		Iterator<Integer> iter = integerQueue.iterator();
		assertNotNull(iter);
		ArrayList<Integer> iterResult = new ArrayList<Integer>();
		while (iter.hasNext()) {
			iterResult.add(iter.next());
		}
		Object[] resultArray = iterResult.toArray();
		Arrays.sort(array);
		Arrays.sort(resultArray);
		assertTrue(Arrays.equals(array, resultArray));
	}

	/**
	 * @tests java.util.TreeQueue#iterator()
	 */
	@Test
	public void test_iterator_empty() {
		TreeQueue<Integer> integerQueue = TreeQueue.create();
		Iterator<Integer> iter = integerQueue.iterator();
		try {
			iter.next();
			fail("should throw NoSuchElementException");
		} catch (NoSuchElementException e) {
			// expected
		}

		iter = integerQueue.iterator();
		try {
			iter.remove();
			fail("should throw IllegalStateException");
		} catch (IllegalStateException e) {
			// expected
		}
	}

	/**
	 * @tests java.util.TreeQueue#iterator()
	 */
	@Test
	public void test_iterator_outofbound() {
		TreeQueue<Integer> integerQueue = TreeQueue.create();
		integerQueue.offer(0);
		Iterator<Integer> iter = integerQueue.iterator();
		iter.next();
		try {
			iter.next();
			fail("should throw NoSuchElementException");
		} catch (NoSuchElementException e) {
			// expected
		}

		iter = integerQueue.iterator();
		iter.next();
		iter.remove();
		try {
			iter.next();
			fail("should throw NoSuchElementException");
		} catch (NoSuchElementException e) {
			// expected
		}
	}

	/**
	 * @tests java.util.TreeQueue#iterator()
	 */
	@Test
	public void test_iterator_remove() {
		TreeQueue<Integer> integerQueue = TreeQueue.create();
		Integer[] array = { 2, 45, 7, -12, 9 };
		for (int i = 0; i < array.length; i++) {
			integerQueue.offer(array[i]);
		}
		Iterator<Integer> iter = integerQueue.iterator();
		assertNotNull(iter);
		for (int i = 0; i < array.length; i++) {
			iter.next();
			if (2 == i) {
				iter.remove();
			}
		}
		assertEquals(array.length - 1, integerQueue.size());

		iter = integerQueue.iterator();
		Integer[] newArray = new Integer[array.length - 1];
		for (int i = 0; i < newArray.length; i++) {
			newArray[i] = iter.next();
		}

		Arrays.sort(newArray);
		for (int i = 0; i < integerQueue.size(); i++) {
			assertEquals(newArray[i], integerQueue.poll());
		}
	}

	@Test
	public void test_iterator_removeEquals() {
		TreeQueue<String> integerQueue = TreeQueue
				.create(new MockComparatorStringByLength());
		String[] array = { "ONE", "TWO", "THREE", "FOUR", "FIVE" };
		for (int i = 0; i < array.length; i++) {
			integerQueue.offer(array[i]);
		}
		// Try removing an entry that the comparator says is equal
		assertFalse(integerQueue.remove("123"));
		assertFalse(integerQueue.remove("one"));
		assertTrue(integerQueue.remove("THREE"));
	}

	/**
	 * @tests java.util.TreeQueue#iterator()
	 */
	@Test
	public void test_iterator_remove_illegalState() {
		TreeQueue<Integer> integerQueue = TreeQueue.create();
		Integer[] array = { 2, 45, 7, -12, 9 };
		for (int i = 0; i < array.length; i++) {
			integerQueue.offer(array[i]);
		}
		Iterator<Integer> iter = integerQueue.iterator();
		assertNotNull(iter);
		try {
			iter.remove();
			fail("should throw IllegalStateException");
		} catch (IllegalStateException e) {
			// expected
		}
		iter.next();
		iter.remove();
		try {
			iter.remove();
			fail("should throw IllegalStateException");
		} catch (IllegalStateException e) {
			// expected
		}

	}

	/**
	 * @tests java.util.TreeQueue.size()
	 */
	@Test
	public void test_size() {
		TreeQueue<Integer> integerQueue = TreeQueue.create();
		assertEquals(0, integerQueue.size());
		int[] array = { 2, 45, 7, -12, 9 };
		for (int i = 0; i < array.length; i++) {
			integerQueue.offer(array[i]);
		}
		assertEquals(array.length, integerQueue.size());
	}

	/**
	 * @tests java.util.TreeQueue#TreeQueue()
	 */
	@Test
	public void test_Constructor() {
		// TreeQueue<Object> queue = TreeQueue.create();
		// assertNotNull(queue);
		// assertEquals(0, queue.size());
		// assertNull(queue.comparator());
	}

	/**
	 * @tests java.util.TreeQueue#TreeQueue(int)
	 */
	@Test
	public void test_ConstructorI() {
		// TreeQueue queue = TreeQueue.create();
		// assertNotNull(queue);
		// assertEquals(0, queue.size());
	}

	/**
	 * @tests java.util.TreeQueue#TreeQueue(int, Comparator<? super E>)
	 */
	@Test
	public void test_ConstructorILjava_util_Comparator() {
		// TreeQueue<Object> queue = TreeQueue.create<Object>(100,
		// (Comparator<Object>) null);
		// assertNotNull(queue);
		// assertEquals(0, queue.size());
		// assertNull(queue.comparator());
		//
		// MockComparator<Object> comparator = new MockComparator<Object>();
		// queue = new TreeQueue<Object>(100, comparator);
		// assertNotNull(queue);
		// assertEquals(0, queue.size());
		// assertEquals(comparator, queue.comparator());
	}

	/**
	 * @tests java.util.TreeQueue#TreeQueue(int, Comparator<? super E>)
	 */
	@Test
	public void test_ConstructorILjava_util_Comparator_illegalCapacity() {
		// try {
		// new TreeQueue<Object>(0, new MockComparator<Object>());
		// fail("should throw IllegalArgumentException");
		// } catch (IllegalArgumentException e) {
		// // expected
		// }
		//
		// try {
		// new TreeQueue<Object>(-1, new MockComparator<Object>());
		// ` fail("should throw IllegalArgumentException");
		// } catch (IllegalArgumentException e) {
		// // expected
		// }
	}

	/**
	 * @tests java.util.TreeQueue#TreeQueue(int, Comparator<? super E>)
	 */
	@Test
	public void test_ConstructorILjava_util_Comparator_cast() {
		MockComparatorCast<Object> objectComparator = new MockComparatorCast<Object>();
		TreeQueue<Integer> integerQueue = TreeQueue.create(objectComparator);
		assertNotNull(integerQueue);
		assertEquals(0, integerQueue.size());
		assertEquals(objectComparator, integerQueue.comparator());
		Integer[] array = { 2, 45, 7, -12, 9 };
		List<Integer> list = Arrays.asList(array);
		integerQueue.addAll(list);
		assertEquals(list.size(), integerQueue.size());
		// just test here no cast exception raises.
	}

	/**
	 * @tests java.util.TreeQueue#TreeQueue(Collection)
	 */
	@Test
	public void test_ConstructorLjava_util_Colleciton() {
		Integer[] array = { 2, 45, 7, -12, 9 };
		List<Integer> list = Arrays.asList(array);
		TreeQueue<Integer> integerQueue = TreeQueue.create(list);
		assertEquals(array.length, integerQueue.size());
		// assertNull(integerQueue.comparator());
		Arrays.sort(array);
		for (int i = 0; i < array.length; i++) {
			assertEquals(array[i], integerQueue.poll());
		}
	}

	/**
	 * @tests java.util.TreeQueue#TreeQueue(Collection)
	 */
	@Test
	public void test_ConstructorLjava_util_Colleciton_null() {
		ArrayList<Object> list = new ArrayList<Object>();
		list.add(new Float(11));
		list.add(null);
		list.add(new Integer(10));
		try {
			TreeQueue.create(list);
			fail("should throw NullPointerException");
		} catch (NullPointerException e) {
			// expected
		}
	}

	/**
	 * @tests java.util.TreeQueue#TreeQueue(Collection)
	 */
	@Test
	public void test_ConstructorLjava_util_Colleciton_non_comparable() {
		ArrayList<Object> list = new ArrayList<Object>();
		list.add(new Float(11));
		list.add(new Integer(10));
		try {
			TreeQueue.create(list);
			fail("should throw ClassCastException");
		} catch (ClassCastException e) {
			// expected
		}
	}

	/**
	 * @tests java.util.TreeQueue#TreeQueue(Collection)
	 */
	@Test
	public void test_ConstructorLjava_util_Colleciton_from_priorityqueue() {
		String[] array = { "AAAAA", "AA", "AAAA", "AAAAAAAA" };
		TreeQueue<String> queue = TreeQueue
				.create(new MockComparatorStringByLength());
		for (int i = 0; i < array.length; i++) {
			queue.offer(array[i]);
		}
		Collection<String> c = queue;
		TreeQueue<String> constructedQueue = TreeQueue.create(c);
		assertEquals(queue.comparator(), constructedQueue.comparator());
		while (queue.size() > 0) {
			assertEquals(queue.poll(), constructedQueue.poll());
		}
		assertEquals(0, constructedQueue.size());
	}

	/**
	 * @tests java.util.TreeQueue#TreeQueue(Collection)
	 */
	@Test
	public void test_ConstructorLjava_util_Colleciton_from_sortedset() {
		int[] array = { 3, 5, 79, -17, 5 };
		TreeSet<Integer> treeSet = new TreeSet<Integer>(
				new MockComparator<Integer>());
		for (int i = 0; i < array.length; i++) {
			treeSet.add(array[i]);
		}
		Collection<? extends Integer> c = treeSet;
		TreeQueue<Integer> queue = TreeQueue.create(c);
		assertEquals(treeSet.comparator(), queue.comparator());
		Iterator<Integer> iter = treeSet.iterator();
		while (iter.hasNext()) {
			assertEquals(iter.next(), queue.poll());
		}
		assertEquals(0, queue.size());
	}

	/**
	 * @tests java.util.TreeQueue#TreeQueue(TreeQueue<? * extends E>)
	 */
	@Test
	public void test_ConstructorLjava_util_TreeQueue() {
		TreeQueue<Integer> integerQueue = TreeQueue.create();
		int[] array = { 2, 45, 7, -12, 9 };
		for (int i = 0; i < array.length; i++) {
			integerQueue.offer(array[i]);
		}
		TreeQueue objectQueue = TreeQueue.create(integerQueue);
		assertEquals(integerQueue.size(), objectQueue.size());
		assertEquals(integerQueue.comparator(), objectQueue.comparator());
		Arrays.sort(array);
		for (int i = 0; i < array.length; i++) {
			assertEquals(array[i], objectQueue.poll());
		}
	}

	/**
	 * @tests java.util.TreeQueue#TreeQueue(TreeQueue<? * extends E>)
	 */
	@Test
	public void test_ConstructorLjava_util_TreeQueue_null() {
		try {
			TreeQueue.create((TreeQueue<Integer>) null);
			fail("should throw NullPointerException");
		} catch (NullPointerException e) {
			// expected
		}
	}

	/**
	 * @tests java.util.TreeQueue#TreeQueue(SortedSet<? extends E>)
	 */
	@Test
	public void test_ConstructorLjava_util_SortedSet() {
		int[] array = { 3, 5, 79, -17, 5 };
		TreeSet<Integer> treeSet = new TreeSet<Integer>();
		for (int i = 0; i < array.length; i++) {
			treeSet.add(array[i]);
		}
		TreeQueue<Integer> queue = TreeQueue.create(treeSet);
		Iterator<Integer> iter = treeSet.iterator();
		while (iter.hasNext()) {
			assertEquals(iter.next(), queue.poll());
		}
	}

	/**
	 * @tests java.util.TreeQueue#TreeQueue(SortedSet<? extends E>)
	 */
	@Test
	public void test_ConstructorLjava_util_SortedSet_null() {
		try {
			TreeQueue.create((SortedSet<? extends Integer>) null);
			fail("should throw NullPointerException");
		} catch (NullPointerException e) {
			// expected
		}
	}

	/**
	 * @tests java.util.TreeQueue#offer(Object)
	 */
	@Test
	public void test_offerLjava_lang_Object() {
		TreeQueue<String> queue = TreeQueue
				.create(new MockComparatorStringByLength());
		String[] array = { "AAAAA", "AA", "AAAA", "AAAAAAAA" };
		for (int i = 0; i < array.length; i++) {
			queue.offer(array[i]);
		}
		String[] sortedArray = { "AA", "AAAA", "AAAAA", "AAAAAAAA" };
		for (int i = 0; i < sortedArray.length; i++) {
			assertEquals(sortedArray[i], queue.poll());
		}
		assertEquals(0, queue.size());
		assertNull(queue.poll());
	}

	/**
	 * @tests java.util.TreeQueue#offer(Object)
	 */
	@Test
	public void test_offerLjava_lang_Object_null() {
		TreeQueue queue = TreeQueue.create();
		try {
			queue.offer(null);
			fail("should throw NullPointerException");
		} catch (NullPointerException e) {
			// expected
		}
	}

	/**
	 * @tests java.util.TreeQueue#offer(Object)
	 */
	@Test
	public void test_offer_Ljava_lang_Object_non_Comparable() {
		TreeQueue queue = TreeQueue.create();
		queue.offer(new Integer(10));
		try {
			queue.offer(new Float(1.3));
			fail("should throw ClassCastException");
		} catch (ClassCastException e) {
			// expected
		}

		queue = TreeQueue.create();
		queue.offer(new Integer(10));
		try {
			queue.offer(new Object());
			fail("should throw ClassCastException");
		} catch (ClassCastException e) {
			// expected
		}
	}

	/**
	 * @tests java.util.TreeQueue#poll()
	 */
	@Test
	public void test_poll() {
		TreeQueue<String> stringQueue = TreeQueue.create();
		String[] array = { "MYTESTSTRING", "AAAAA", "BCDEF", "ksTRD", "AAAAA" };
		for (int i = 0; i < array.length; i++) {
			stringQueue.offer(array[i]);
		}
		Arrays.sort(array);
		for (int i = 0; i < array.length; i++) {
			assertEquals(array[i], stringQueue.poll());
		}
		assertEquals(0, stringQueue.size());
		assertNull(stringQueue.poll());
	}

	/**
	 * @tests java.util.TreeQueue#poll()
	 */
	@Test
	public void test_poll_empty() {
		TreeQueue queue = TreeQueue.create();
		assertEquals(0, queue.size());
		assertNull(queue.poll());
	}

	/**
	 * @tests java.util.TreeQueue#peek()
	 */
	@Test
	public void test_peek() {
		TreeQueue<Integer> integerQueue = TreeQueue.create();
		int[] array = { 2, 45, 7, -12, 9 };
		for (int i = 0; i < array.length; i++) {
			integerQueue.add(array[i]);
		}
		Arrays.sort(array);
		assertEquals(new Integer(array[0]), integerQueue.peek());
		assertEquals(new Integer(array[0]), integerQueue.peek());
	}

	/**
	 * @tests java.util.TreeQueue#peek()
	 */
	@Test
	public void test_peek_empty() {
		TreeQueue queue = TreeQueue.create();
		assertEquals(0, queue.size());
		assertNull(queue.peek());
		assertNull(queue.peek());
	}

	/**
	 * @tests java.util.TreeQueue#Clear()
	 */
	@Test
	public void test_clear() {
		TreeQueue<Integer> integerQueue = TreeQueue.create();
		int[] array = { 2, 45, 7, -12, 9 };
		for (int i = 0; i < array.length; i++) {
			integerQueue.offer(array[i]);
		}
		integerQueue.clear();
		assertTrue(integerQueue.isEmpty());
	}

	/**
	 * @tests java.util.TreeQueue#add(Object)
	 */
	@Test
	public void test_add_Ljava_lang_Object() {
		TreeQueue<Integer> integerQueue = TreeQueue.create();
		Integer[] array = { 2, 45, 7, -12, 9 };
		for (int i = 0; i < array.length; i++) {
			integerQueue.add(array[i]);
		}
		Arrays.sort(array);
		assertEquals(array.length, integerQueue.size());
		for (int i = 0; i < array.length; i++) {
			assertEquals(array[i], integerQueue.poll());
		}
		assertEquals(0, integerQueue.size());
	}

	/**
	 * @tests java.util.TreeQueue#add(Object)
	 */
	@Test
	public void test_add_Ljava_lang_Object_null() {
		TreeQueue queue = TreeQueue.create();
		try {
			queue.add(null);
			fail("should throw NullPointerException");
		} catch (NullPointerException e) {
			// expected
		}
	}

	/**
	 * @tests java.util.TreeQueue#add(Object)
	 */
	@Test
	public void test_add_Ljava_lang_Object_non_Comparable() {
		TreeQueue queue = TreeQueue.create();
		queue.add(new Integer(10));
		try {
			queue.add(new Float(1.3));
			fail("should throw ClassCastException");
		} catch (ClassCastException e) {
			// expected
		}

		queue = TreeQueue.create();
		queue.add(new Integer(10));
		try {
			queue.add(new Object());
			fail("should throw ClassCastException");
		} catch (ClassCastException e) {
			// expected
		}
	}

	/**
	 * @tests java.util.TreeQueue#remove(Object)
	 * 
	 */
	@Test
	public void test_remove_Ljava_lang_Object() {
		Integer[] array = { 2, 45, 7, -12, 9, 23, 17, 1118, 10, 16, 39 };
		List<Integer> list = Arrays.asList(array);
		TreeQueue<Integer> integerQueue = TreeQueue.create(list);
		assertTrue(integerQueue.remove(16));
		Integer[] newArray = { 2, 45, 7, -12, 9, 23, 17, 1118, 10, 39 };
		Arrays.sort(newArray);
		for (int i = 0; i < newArray.length; i++) {
			assertEquals(newArray[i], integerQueue.poll());
		}
		assertEquals(0, integerQueue.size());
	}

	/**
	 * @tests java.util.TreeQueue#remove(Object)
	 * 
	 */
	@Test
	public void test_remove_Ljava_lang_Object_using_comparator() {
		TreeQueue<String> queue = TreeQueue
				.create(new MockComparatorStringByLength());
		String[] array = { "AAAAA", "AA", "AAAA", "AAAAAAAA" };
		for (int i = 0; i < array.length; i++) {
			queue.offer(array[i]);
		}
		assertFalse(queue.contains("BB"));
		assertTrue(queue.remove("AA"));
	}

	/**
	 * @tests java.util.TreeQueue#remove(Object)
	 * 
	 */
	@Test
	public void test_remove_Ljava_lang_Object_not_exists() {
		Integer[] array = { 2, 45, 7, -12, 9, 23, 17, 1118, 10, 16, 39 };
		List<Integer> list = Arrays.asList(array);
		TreeQueue<Integer> integerQueue = TreeQueue.create(list);
		assertFalse(integerQueue.remove(111));
		// assertFalse(integerQueue.remove(null));
		// assertFalse(integerQueue.remove(""));
	}

	/**
	 * @tests java.util.TreeQueue#remove(Object)
	 * 
	 */
	@Test
	public void test_remove_Ljava_lang_Object_null() {
		Integer[] array = { 2, 45, 7, -12, 9, 23, 17, 1118, 10, 16, 39 };
		List<Integer> list = Arrays.asList(array);
		TreeQueue<Integer> integerQueue = TreeQueue.create(list);
		// assertFalse(integerQueue.remove(null));
	}

	/**
	 * @tests java.util.TreeQueue#remove(Object)
	 * 
	 */
	@Test
	public void test_remove_Ljava_lang_Object_not_Compatible() {
		Integer[] array = { 2, 45, 7, -12, 9, 23, 17, 1118, 10, 16, 39 };
		List<Integer> list = Arrays.asList(array);
		TreeQueue<Integer> integerQueue = TreeQueue.create(list);
		// assertFalse(integerQueue.remove(new Float(1.3F)));

		// although argument element type is not compatible with those in queue,
		// but comparator supports it.
		MockComparator<Object> comparator = new MockComparator<Object>();
		TreeQueue<Integer> integerQueue1 = TreeQueue.create(comparator);
		integerQueue1.offer(1);
		assertFalse(integerQueue1.remove(new Float(1.3F)));

		TreeQueue queue = TreeQueue.create();
		Object o = new Object();
		// queue.offer(o);
	}

	/**
	 * @tests java.util.TreeQueue#comparator()
	 */
	@Test
	public void test_comparator() {

		MockComparator<Object> comparator = new MockComparator<Object>();
		SortedCollection queue = TreeQueue.create(comparator);
		assertEquals(comparator, queue.comparator());
	}

	/**
	 * @tests serialization/deserialization.
	 */
	@Test
	public void test_Serialization() throws Exception {

		TreeQueue<Integer> out = Collections3.newTreeQueue(1, 2, 3);
		byte[] bytes;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(out);
		oos.close();

		bytes = baos.toByteArray();
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
				bytes));

		TreeQueue<Integer> in = (TreeQueue<Integer>) ois.readObject();

		ois.close();
		
		

		// Integer[] array = { 2, 45, 7, -12, 9, 23, 17, 1118, 10, 16, 39 };
		// List<Integer> list = Arrays.asList(array);
		// TreeQueue<Integer> srcIntegerQueue = TreeQueue.create(list);
		// TreeQueue<Integer> destIntegerQueue = (TreeQueue<Integer>)
		// SerializationTester
		// .getDeserilizedObject(srcIntegerQueue);
		// Arrays.sort(array);
		// for (int i = 0; i < array.length; i++) {
		// assertEquals(array[i], destIntegerQueue.poll());
		// }
		// assertEquals(0, destIntegerQueue.size());
	}

	/**
	 * @tests serialization/deserialization.
	 */
	@Test
	public void test_Serialization_casting() throws Exception {
		// Integer[] array = { 2, 45, 7, -12, 9, 23, 17, 1118, 10, 16, 39 };
		// List<Integer> list = Arrays.asList(array);
		// TreeQueue<Integer> srcIntegerQueue = TreeQueue.create(list);
		// TreeQueue<String> destStringQueue = (TreeQueue<String>)
		// SerializationTester
		// .getDeserilizedObject(srcIntegerQueue);
		// // will not incur class cast exception.
		// Object o = destStringQueue.peek();
		// Arrays.sort(array);
		// Integer I = (Integer) o;
		// assertEquals(array[0], I);
	}

	/**
	 * @tests serialization/deserialization compatibility with RI.
	 */
	@Test
	public void test_SerializationCompatibility_cast() throws Exception {
		// Integer[] array = { 2, 45, 7, -12, 9, 23, 17, 1118, 10, 16, 39 };
		// List<Integer> list = Arrays.asList(array);
		// TreeQueue<Integer> srcIntegerQueue = TreeQueue.create(list);
		// TreeQueue<String> destStringQueue = (TreeQueue<String>)
		// SerializationTester
		// .readObject(srcIntegerQueue, SERIALIZATION_FILE_NAME);
		//
		// // will not incur class cast exception.
		// Object o = destStringQueue.peek();
		// Arrays.sort(array);
		// Integer I = (Integer) o;
		// assertEquals(array[0], I);
	}

	// @Test
	// public void testSerialization() throws Exception {
	// TreeQueue<Integer> out = Collections3.newTreeQueue(5, 4, 3, 0, 1, 1, -2,
	// 1);
	//
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// ObjectOutputStream oos = new ObjectOutputStream(baos);
	// oos.writeObject(out);
	// oos.close();
	// baos.close();
	//
	// byte[] bytes = baos.toByteArray();
	//
	// ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
	// ObjectInputStream ois = new ObjectInputStream(bais);
	// TreeQueue<Integer> in = (TreeQueue<Integer>) ois.readObject();
	// ois.close();
	// bais.close();
	//
	// assertEquals(out.size(), in.size());
	// assertEquals(out.comparator, in.comparator);
	//
	//
	//
	//
	// //ByteArrayInputStream bais = new ByteArrayInputStream(oos);
	//
	//
	// // 140 FileOutputStream fos = null;
	// // 150 ObjectOutputStream out = null;
	// // 160 try
	// // 170 {
	// // 180 fos = new FileOutputStream(filename);
	// // 190 out = new ObjectOutputStream(fos);
	// // 200 out.writeObject(time);
	// // 210 out.close();
	// // 220 }
	// // 230 catch(IOException ex)
	// // 240 {
	// // 250 ex.printStackTrace();
	// // 260 }
	// // 270 }
	// // 280 }
	//
	//
	// }

	private static class MockComparator<E> implements Comparator<E> {

		public int compare(E object1, E object2) {
			int hashcode1 = object1.hashCode();
			int hashcode2 = object2.hashCode();
			if (hashcode1 > hashcode2) {
				return 1;
			} else if (hashcode1 == hashcode2) {
				return 0;
			} else {
				return -1;
			}
		}
	}

	private static class MockComparatorStringByLength implements
			Comparator<String> {

		public int compare(String object1, String object2) {
			int length1 = object1.length();
			int length2 = object2.length();
			if (length1 > length2) {
				return 1;
			} else if (length1 == length2) {
				return 0;
			} else {
				return -1;
			}
		}

	}

	private static class MockComparatorCast<E> implements Comparator<E> {

		public int compare(E object1, E object2) {
			return 0;
		}
	}

}
