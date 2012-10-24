import java.util.Comparator;

import biscotti.collect.Skiplist;
import biscotti.collect.Sortedlist;

public class CopyOfTest {

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

		
		
		
		Skiplist<Double> sl = Skiplist.orderedBy(ComparatorImpl.INSTANCE).create();
		
		sl.add(-1.1);
		sl.add(-2.2);
		sl.add(1.1);
		sl.add(2.2);
		sl.add(3.5);
		sl.add(3.6);
		sl.add(4.4);
		sl.add(5.5);

//		
//		
//		
		print(sl);
//		
		System.out.println("remove 3.7");
		sl.remove(3.7);
//		
		print(sl);
////		
//////		
//////	
		System.out.println("sublist");
		Skiplist<Double> sl2 = sl.sublist(2, 7);
		print(sl2);
////		
////		System.out.println(sl2 + " sub-size: " + sl2.size() + " size: " + sl.size());
		System.out.println("remove 5.5");
		sl2.remove(5.5);
		print(sl2);
////		//sl2.remove(1.7);
///		System.out.println(sl2 + " sub-size: " + sl2.size() +" size: " + sl.size());
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

	}
}
