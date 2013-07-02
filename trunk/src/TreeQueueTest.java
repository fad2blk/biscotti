import com.palamida.util.collect.MoreQueues;
import com.palamida.util.collect.TreeQueue;


public class TreeQueueTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//BoundedPriorityQueue<Integer> bpq = BoundedPriorityQueue.maxElements(4).create();
		
		TreeQueue<Integer> tq = TreeQueue.maxElements(4).create();
		
		MoreQueues.offerAll(tq, 6, 5, 4, 3, 2, 1, 7);
		
		System.out.println(tq);
	}

}
