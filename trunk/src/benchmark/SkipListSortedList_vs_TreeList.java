package benchmark;

import java.util.Collection;
import java.util.Random;

import com.googlecode.biscotti.collect.SkipListSortedList;
import com.googlecode.biscotti.collect.TreeList;

public class SkipListSortedList_vs_TreeList {
	private static final long seed = System.currentTimeMillis();

	private static Random random;
	private static int n = 5000000;

	public static void main(String[] args) {

		TreeList<Integer> tl = TreeList.create();
		SkipListSortedList<Integer> sl = new SkipListSortedList<Integer>();

		addBenchmark(tl, "TreeList.add - " + n + " integers");
		tl = null;
		//System.gc();
		addBenchmark(sl, "SkipListSortedList.add - " + n + " integers");
		// System.out.println(pq.size());
	}

	public static void addBenchmark(Collection<Integer> c, String s) {
		random = new Random(seed);
		System.out.println(s);
		long start = System.nanoTime();
		for (int i = 0; i < n; i++)
			c.add(random.nextInt());
		System.out.println((System.nanoTime() - start) / 1000000000F);
	}
}
