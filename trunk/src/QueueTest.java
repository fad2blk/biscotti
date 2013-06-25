import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.google.common.collect.MinMaxPriorityQueue;
import com.palamida.util.collect.TreeQueue;
import com.palamida.util.collect.Treelist;

public class QueueTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		MinMaxPriorityQueue<Integer> guavaQueue = MinMaxPriorityQueue.create();
		PriorityQueue<Integer> javaQueue = new PriorityQueue<Integer>();
		TreeQueue<Integer> treeQueue = TreeQueue.create();
		Treelist<Integer> treeList = Treelist.create();

//		Random random = new Random();
//
//		Stopwatch watch = new Stopwatch().start();
//
//		for (int i = 0; i < 5000000; i++)
//			javaQueue.add(random.nextInt());
//
//		System.out.println(javaQueue.size());
//
//		while (!javaQueue.isEmpty())
//			javaQueue.remove();
//
//		watch.stop();
//		System.out.println("java.util.PriorityQueue finished in: " + watch.elapsed(TimeUnit.SECONDS) + " seconds.");
//		
//		watch.reset().start();
//		
//		for (int i = 0; i < 5000000; i++)
//			treeList.add(random.nextInt());
//
//		System.out.println(treeList.size());
//
//		while (!treeQueue.isEmpty())
//			treeList.remove(0);
//
//		watch.stop();
//		System.out.println("java.util.TreeQueue finished in: " + watch.elapsed(TimeUnit.SECONDS) + " seconds.");
		
		
		TreeQueue q = TreeQueue.create();
		
		q.add(1);
		q.add(3);
		q.add(6);
		q.add(4);
		//q.remove();
		//q.remove();
		
		while (!q.isEmpty())
		q.remove();

	}

}
