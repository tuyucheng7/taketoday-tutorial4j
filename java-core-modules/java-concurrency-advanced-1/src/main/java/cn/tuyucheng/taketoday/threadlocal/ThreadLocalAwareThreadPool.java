package cn.tuyucheng.taketoday.threadlocal;

import java.util.concurrent.*;

public class ThreadLocalAwareThreadPool extends ThreadPoolExecutor {

	public ThreadLocalAwareThreadPool(int corePoolSize,
									  int maximumPoolSize,
									  long keepAliveTime,
									  TimeUnit unit,
									  BlockingQueue<Runnable> workQueue,
									  ThreadFactory threadFactory,
									  RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		// Call remove on each ThreadLocal
	}
}