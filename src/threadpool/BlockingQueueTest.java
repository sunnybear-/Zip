package threadpool;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockingQueueTest {

	public static void main(String[] args) throws Exception {
		BlockingQueue<Integer> blockingQueue = new LinkedBlockingQueue<>(4);
		Producer producer = new Producer(blockingQueue);
		Consumer consumer = new Consumer(blockingQueue);
		// 创建5个生产者，5个消费者
		for (int i = 0; i < 10; i++) {
			if (i < 5)
				new Thread(producer, "producer:" + i).start();
			else
				new Thread(consumer, "consumer:" + i).start();
		}
		Thread.sleep(1000);
		producer.shutDown();
		consumer.shutDown();
	}

	/**
	 * 生产者
	 * 
	 * @author 1261267
	 *
	 */
	static class Producer implements Runnable {
		private final BlockingQueue<Integer> blockingQueue;
		private volatile boolean flag;
		private Random random;

		public Producer(BlockingQueue<Integer> blockingQueue) {
			this.blockingQueue = blockingQueue;
			flag = false;
			random = new Random();
		}

		@Override
		public void run() {
			while (!flag) {
				try {
					int info = random.nextInt(100);
					blockingQueue.put(info);
					System.out.println("线程名:" + Thread.currentThread().getName() + ",producer " + info);
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void shutDown() {
			flag = true;
		}
	}

	/**
	 * 消费者
	 * 
	 * @author 1261267
	 *
	 */
	static class Consumer implements Runnable {
		private final BlockingQueue<Integer> blockinQueue;
		private volatile boolean flag;

		public Consumer(BlockingQueue<Integer> blockinQueue) {
			this.blockinQueue = blockinQueue;
		}

		@Override
		public void run() {
			while (!flag) {
				try {
					int info = blockinQueue.take();
					System.out.println("线程名:" + Thread.currentThread().getName() + ",consumer " + info);
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void shutDown() {
			flag = true;
		}
	}
}
