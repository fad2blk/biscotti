import java.util.Comparator;

import com.google.common.base.Charsets;

import biscotti.collect.Skiplist;
import biscotti.collect.Sortedlist;
import biscotti.collect.Treelist;

public class Test {

	static class ComparatorImpl implements Comparator<Double> {

		static final ComparatorImpl INSTANCE = new ComparatorImpl();

		private ComparatorImpl() {
		}

		@Override
		public int compare(Double o1, Double o2) {
			double a = Math.floor(o1);
			double b = Math.floor(o2);

			if (a < b)
				return -1;
			else if (a > b)
				return 1;
			else
				return 0;

		}
		
		
		
		
		
		

		

	}

	/**
	 * @param args
	 */
	
	public static void print(Sortedlist<?> col){
		System.out.println(col + ", size:"+ col.size() + " firstElement:" + col.get(0) + " lastElement:" + col.get(col.size() - 1));
	}
	public static void main(String[] args) {
		
//		Treelist<Integer> tl = Treelist.create();
//		
//		tl.add(5);
//		tl.add(-3);
//		tl.add(17);
//		tl.add(-9);
//		tl.add(4);
//		tl.add(6);
//		tl.add(4);
//		
//		print(tl);
//		
//		
//		Treelist<Integer> sl = tl.sublist(1, 6);
//		
//		print(sl);
//		
//		sl.add(6);
//		
//		print(sl);
//		
//		print(tl);
//		
//		System.out.println("\n\n");
//		
//		Skiplist<Integer> sl1 = Skiplist.create();
//		
//		sl1.add(5);
//		sl1.add(-3);
//		sl1.add(17);
//		sl1.add(-9);
//		sl1.add(4);
//		sl1.add(6);
//		sl1.add(4);
//		
//		print(sl1);
//		
//		
//		Skiplist<Integer> sl2 = sl1.sublist(1, 6);
//		
//		print(sl2);
//		
//		sl2.add(6);
//		
//		print(sl2);
//		
//		print(sl1);		
//		
//		
		
		
		
//		Skiplist<Double> sl = Skiplist.create(ComparatorImpl.INSTANCE);
//		
//		sl.add(-3.5);
//		sl.add(2.5);
//		sl.add(3.5);
//		sl.add(-7.2);
//		sl.add(1.1);
//		sl.add(4.5);
//		sl.add(1.2);
//		sl.add(1.45);
//		
//		
//		
//		System.out.println(sl + " size: " + sl.size());
//		
////		sl.remove(1.1);
//		
////		//System.out.println(sl);
////		
//////		
//////		
////		Skiplist<Double> sl2 = sl.subList(1, 7);
////		
////		System.out.println(sl2 + " sub-size: " + sl2.size() + " size: " + sl.size());
////		sl2.remove(3.7);
////		//sl2.remove(1.7);
////		System.out.println(sl2 + " sub-size: " + sl2.size() +" size: " + sl.size());
////		
////		System.out.println("from: " + ((Skiplist.Sublist)sl2).from.element);
////		System.out.println("to: " + ((Skiplist.Sublist)sl2).to.element);
//////		
//////		
//////		
//////		
//////		
//////		sl2.remove(1.1);
//////		sl2.remove(1.1);
//////		sl2.remove(1.1);
//////		
//////		System.out.println(sl2);
//////		System.out.println("from: " + ((Skiplist.Sublist)sl2).from.element);
//////		System.out.println("to: " + ((Skiplist.Sublist)sl2).to.element);
//////		
//		Skiplist<Double> sl = Skiplist.create(ComparatorImpl.INSTANCE);
//		sl.add(-3.5);
//		sl.add(2.5);
//		sl.add(3.5);
//		sl.add(-7.2);
//		sl.add(1.1);
//		sl.add(4.5);
//		sl.add(1.2);
//		sl.add(1.45);
//		
//		
//		
//		System.out.println(sl+ ":"+sl.size()); 
//				
//		
//		sl.remove(1.5);
//		
//		System.out.println(sl+ ":"+sl.size());
//		
////		
////		
//		Skiplist<Double> sl2 = sl.subList(1, 5);
//		System.out.println(sl2+ ":"+sl.size());
//		sl2.remove(1.7);
//		System.out.println(sl2+ ":"+sl.size());
//		
//	//	System.out.println("from: " + ((Skiplist.Sublist)sl2).from.element);
////		System.out.println("to: " + ((Skiplist.Sublist)sl2).to.element);
////		
////		
////		
////		
////		
////		sl2.remove(2.1);
////		sl2.remove(1.1);
////		sl2.remove(1.1);
////		
////		System.out.println(sl2);
////		System.out.println("from: " + ((Skiplist.Sublist)sl2).from.element);
////		System.out.println("to: " + ((Skiplist.Sublist)sl2).to.element);
////		
		
		long[] longs = new long[]{1, 2, 3};
		
		for(long l : longs){
			System.out.println(l);
		}
		
		System.out.println(Charsets.US_ASCII);

	}
}
