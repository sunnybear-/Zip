package threadpool;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 线程处理类 Created by chenkai.gu on 2017/2/24.
 */
public final class ExecutorProcessPool {
	private ExecutorServiceFactory mExecutorServiceFactory;
	private ExecutorService mExecutorService;

	private ExecutorProcessPool(ExecutorPoolType type, int maxThread) {
		maxThread = maxThread == -1 ? Runtime.getRuntime().availableProcessors() * 2 + 1 : maxThread;
		mExecutorServiceFactory = ExecutorServiceFactory.getInstance();
		switch (type) {
		case SCHEDULED:
			mExecutorService = mExecutorServiceFactory.createScheduledThreadPool(maxThread);
			break;
		case SINGLE:
			mExecutorService = mExecutorServiceFactory.createSingleThreadPool();
			break;
		case CACHE:
			mExecutorService = mExecutorServiceFactory.createCacheThreadPool();
			break;
		case FIXED:
			mExecutorService = mExecutorServiceFactory.createFixedThreadPool(maxThread);
			break;
		}
	}

	public synchronized static ExecutorProcessPool newInstance(ExecutorPoolType type, int maxThread) {
		return new ExecutorProcessPool(type, maxThread);
	}

	public synchronized static ExecutorProcessPool newInstance(ExecutorPoolType type) {
		return new ExecutorProcessPool(type, -1);
	}

	/**
	 * 关闭线程池. 这里要说明的是:调用关闭线程池方法后,线程池会执行完队列中的所有任务才退出
	 */
	public void shutdown() {
		mExecutorService.shutdown();
	}

	/**
	 * 线程池是否已关闭
	 */
	public boolean isShutdown() {
		return mExecutorService.isShutdown();
	}

	/**
	 * 线程池中的线程是否已经执行终结
	 */
	public boolean isTerminated() {
		return mExecutorService.isTerminated();
	}

	/**
	 * 提交任务到线程池,可以接收线程返回值
	 */
	public <T> Future<T> submit(Runnable task) {
		return (Future<T>) mExecutorService.submit(task);
	}

	/**
	 * 提交任务到线程池,可以接收线程返回值(可延时周期运行)
	 */
	public <T> Future<T> submit(Runnable task, long initialDelay, long period, TimeUnit unit) {
		if (mExecutorService instanceof ScheduledExecutorService)
			return (Future<T>) ((ScheduledExecutorService) mExecutorService).scheduleAtFixedRate(task, initialDelay,
					period, unit);
		return submit(task);
	}

	/**
	 * 提交任务到线程池,可以接收线程返回值
	 */
	public <T> Future<T> submit(Callable<T> task) {
		return mExecutorService.submit(task);
	}

	/**
	 * 直接提交任务到线程池,无返回值
	 */
	public void execute(Runnable task) {
		mExecutorService.execute(task);
	}
}
