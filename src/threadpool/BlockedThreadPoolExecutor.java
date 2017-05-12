package threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ┏┓　　　┏┓
 * ┏┛┻━━━┛┻┓
 * ┃　　　　　　　┃
 * ┃　　　━　　　┃
 * ┃　┳┛　┗┳　┃
 * ┃　　　　　　　┃
 * ┃　　　┻　　　┃
 * ┃　　　　　　　┃
 * ┗━┓　　　┏━┛
 * ┃　　　┃   神兽保佑
 * ┃　　　┃   代码无BUG！
 * ┃　　　┗━━━┓
 * ┃　　　　　　　┣┓
 * ┃　　　　　　　┏┛
 * ┗┓┓┏━┳┓┏┛
 * ┃┫┫　┃┫┫
 * ┗┻┛　┗┻┛
 * 阻塞式线程池
 * Created by chenkai.gu on 2017/3/15.
 */
public class BlockedThreadPoolExecutor extends ThreadPoolExecutor {
    private ReentrantLock pauseLock;
    private Condition unpaused;

    public BlockedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        initialize();
    }

    public BlockedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        initialize();
    }

    public BlockedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        initialize();
    }

    public BlockedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        initialize();
    }

    private void initialize() {
        pauseLock = new ReentrantLock();
        unpaused = pauseLock.newCondition();
    }

    @Override
    public void execute(Runnable command) {
        pauseLock.lock();
        try {
            while (getPoolSize() == getMaximumPoolSize() && getQueue().remainingCapacity() == 0) {
                unpaused.await();
                //放到lock外面的话,在压力测试下会有漏网的!
                super.execute(command);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            pauseLock.unlock();
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        try {
            pauseLock.lock();
            unpaused.signal();
        } finally {
            pauseLock.unlock();
        }
    }
}
