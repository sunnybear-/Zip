package threadpool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class ThreadPoolTest {
	private static List<Future<String>> result = new ArrayList<>();

	public static void main(String[] args) throws Exception {
		ExecutorProcessPool pool = ExecutorProcessPool.newInstance(ExecutorPoolType.SCHEDULED, 2);
		for (int i = 0; i < 10; i++) {
			int finalI = i;
			Future<String> future = pool.submit(new Callable<String>() {

				@Override
				public String call() throws Exception {
					System.out.println("-------------这里执行业务逻辑，Callable TaskName = " + finalI + "-------------" + "线程名:"
							+ Thread.currentThread().getName());
					Thread.sleep(3 * 1000);
					System.out.println("-------------业务逻辑处理完成，Callable TaskName = " + finalI + "-------------");
					return ">>>>>>>>>>>>>线程返回值，Callable TaskName = " + finalI + "<<<<<<<<<<<<<<";
				}
			});
			result.add(future);
		}
		pool.shutdown();
		for (Future<String> future : result) {
			System.out.println("future返回值:" + future.get() + ",线程名:" + Thread.currentThread().getName());
		}
		// Stack<String> myList = new Stack<String>();
		// myList.add("1");
		// myList.add("2");
		// myList.add("3");
		// myList.add("4");
		// myList.add("5");
		// System.out.println("List Last Value:" + myList.lastElement());
		// System.out.println("Source List Value:" + myList.toString());
		//
		// for (Iterator<String> iterator = myList.iterator();
		// iterator.hasNext();) {
		// String value = iterator.next();
		// if (!"3".equals(value)) {
		// iterator.remove();
		// // myList.remove(value);
		// }
		// }
		// System.out.println("List Value:" + myList.toString());
	}
}
