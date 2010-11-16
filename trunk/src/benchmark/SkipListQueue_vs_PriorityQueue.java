package benchmark;

import java.util.Collection;
import java.util.PriorityQueue;
import java.util.Random;

import com.googlecode.biscotti.collect.SkipListQueue;

public class SkipListQueue_vs_PriorityQueue {

	private static final long seed = System.currentTimeMillis();
	
	private static Random random;
	private static int n = 1000000;

	public static void main(String[] args) {

		PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
		SkipListQueue<Integer> slq = SkipListQueue.create();

		addBenchmark(pq, "PriorityQueue.add - " + n + " integers");
		addBenchmark(slq, "SkipListQueue.add - " + n + " integers");
	}

	public static void addBenchmark(Collection<Integer> c, String s) {
		random = new Random(seed);
		System.out.println(s);
		Long nanoTime = System.nanoTime();
		for (int i = 0; i <= n; i++)
			c.add(random.nextInt());
		System.out.println((System.nanoTime() - nanoTime) / 1000000000);
	}
}
