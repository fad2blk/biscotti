import java.util.List;
import java.util.NavigableSet;
import java.util.SortedSet;

import biscotti.common.collect.Skiplist;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Skiplist<Integer> skiplist = Skiplist.create();
		
		skiplist.add(1);
		skiplist.add(6);
		skiplist.add(2);
		skiplist.add(5);
//		skiplist.add(3);
		skiplist.add(3);
		System.out.println(skiplist);

		Skiplist<Integer> skipSubList = skiplist.subList(2, 5);
		
		System.out.println(skipSubList.size());
		
		skipSubList.remove(new Integer(6));
		skipSubList.remove(new Integer(5));
		skipSubList.remove(new Integer(3));
		
		System.out.println(skipSubList.size());
		
		System.out.println(skipSubList);
		
		
		
		System.out.println(skipSubList.indexOf(5));
		System.out.println(skipSubList.lastIndexOf(5));
		
		
		
		
		System.out.println("\n\n");
		
		List<Integer> list = Lists.newArrayList();
		
		list.add(1);
		list.add(2);
//		list.add(3);
		list.add(3);
		list.add(5);
		list.add(6);
		System.out.println(list);
		
		List<Integer> subList = list.subList(2, 5);
		
		System.out.println(subList.size());
		
		System.out.println(subList);
		
		System.out.println(subList.indexOf(5));
		System.out.println(subList.lastIndexOf(5));
		
		System.out.println("\n\n");
		
		NavigableSet<Integer> sortedSet = Sets.newTreeSet();
		
//		sortedSet.add(1);
//		sortedSet.add(6);
//		sortedSet.add(2);
//		sortedSet.add(5);
////		skiplist.add(3);
//		sortedSet.add(3);
//		System.out.println(sortedSet);
//
//		SortedSet<Integer> sortedSubset = sortedSet.subSet(3, 7);
//		
//		sortedSubset.add(7
//				);
//		
//		System.out.println(sortedSubset.size());
//		
//		System.out.println(sortedSubset);
		
		//System.out.println(skipSubList.indexOf(5));
		//System.out.println(skipSubList.lastIndexOf(5));

		
	}

}

