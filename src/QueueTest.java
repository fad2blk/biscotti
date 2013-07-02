import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.MinMaxPriorityQueue;
import com.palamida.util.collect.TreeQueue;

public class QueueTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		MinMaxPriorityQueue<Integer> guavaQueue = MinMaxPriorityQueue.create();
		PriorityQueue<Integer> javaQueue = new PriorityQueue<Integer>();
		TreeQueue<Integer> treeQueue = TreeQueue.create();
		
		int num = 10000000;

		
		BenchmarkQueue(javaQueue, num);
		BenchmarkQueue(guavaQueue, num);
		BenchmarkQueue(treeQueue, num);

		
		

	}

	static void BenchmarkQueue(Queue<Integer> queue, int num) {
		System.gc();
		final Random random = new Random();
		final Stopwatch watch = new Stopwatch();
		
		System.out.println("Benchmarking " + queue.getClass().getSimpleName());

		watch.start();
		System.out.println("  queue.add * " + num);

		for (int i = 0; i < num; i++)
			queue.add(random.nextInt());
		
		System.out.println("  queue.size: " + queue.size());
		System.out.println("  queue.remove till empty");

		while (!queue.isEmpty())
			queue.remove();

		watch.stop();

		System.out.println(queue.getClass().getSimpleName() + ": " + watch.elapsed(TimeUnit.SECONDS) + " seconds.\n");
	}

}
