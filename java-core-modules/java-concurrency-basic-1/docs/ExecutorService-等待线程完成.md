## 1. 概述

[ExecutorService](https://www.baeldung.com/java-executor-service-tutorial)框架使得在多个线程中处理任务变得容易，我们将举例说明一些等待线程完成执行的场景。

此外，我们还将展示如何优雅地关闭ExecutorService，并等待已经运行的线程完成它们的执行。

## 2. 在Executor关闭之后

使用Executor时，我们可以通过调用shutdown()或shutdownNow()方法将其关闭。**不过，它不会等到所有线程停止执行**。

**等待现有线程完成它们的执行可以通过使用awaitTermination()方法来实现**。

这会阻塞线程，直到所有任务完成执行或达到指定的超时：

```java
public void awaitTerminationAfterShutdown(ExecutorService threadPool) {
    threadPool.shutdown();
    try {
        if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
            threadPool.shutdownNow();
        }
    } catch (InterruptedException e) {
        threadPool.shutdownNow();
        Thread.currentThread().interrupt();
    }
}
```

## 3. 使用CountDownLatch

接下来，让我们看看解决这个问题的另一种方法-使用CountDownLatch来发出任务完成的信号。

我们可以使用一个值来初始化它，该值表示在所有调用await()方法的线程收到通知之前它可以递减的次数。

例如，如果我们需要当前线程等待另外N个线程完成它们的执行，我们可以使用N初始化CountDownLatch：

```java
@Test
void givenMultipleThreads_whenUsingCountDownLatch_thenMainShoudWaitForAllToFinish() {
	ExecutorService WORKER_THREAD_POOL = Executors.newFixedThreadPool(10);
	try {
		long startTime = System.currentTimeMillis();
		// create a CountDownLatch that waits for the 2 threads to finish
		CountDownLatch latch = new CountDownLatch(2);
		for (int i = 0; i < 2; i++) {
			WORKER_THREAD_POOL.submit(() -> {
				try {
					Thread.sleep(1000);
					latch.countDown();
				} catch (InterruptedException e) {
					e.printStackTrace();
					Thread.currentThread().interrupt();
				}
			});
		}
        
		// wait for the latch to be decremented by the two threads
		latch.await();
		long processingTime = System.currentTimeMillis() - startTime;
        
		assertTrue(processingTime >= 1000);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	awaitTerminationAfterShutdown(WORKER_THREAD_POOL);
}
```

## 4. 使用invokeAll()

我们可以用来运行线程的第一种方法是invokeAll()方法，**该方法在所有任务完成或超时到期后返回Future对象的集合**。

此外，我们必须注意，返回的Future对象的顺序与提供的Callable对象的顺序相同：

```java
@Test
void givenMultipleThread_whenInvokeAll_thenMainThreadShouldWaitForAllToFinish() {
    ExecutorService WORK_THREAD_POOL = Executors.newFixedThreadPool(10);
    List<Callable<String>> callables = Arrays.asList(
            new DelayedCallable("fast thread", 100),
            new DelayedCallable("slow thread", 3000));
	
    try {
        long startProcessingTime = System.currentTimeMillis();
        List<Future<String>> futures = WORK_THREAD_POOL.invokeAll(callables);
        awaitTerminationAfterShutdown(WORK_THREAD_POOL);
		
        try {
            WORK_THREAD_POOL.submit((Callable<String>) () -> {
                TimeUnit.MILLISECONDS.sleep(1000);
                return null;
            });
        } catch (RejectedExecutionException e) {
            // ignore
        }
        long totalProcessingTime = System.currentTimeMillis() - startProcessingTime;
        assertTrue(totalProcessingTime >= 3000);
		
        String firstThreadResponse = futures.get(0).get();
        assertEquals("fast thread", firstThreadResponse, "First Response should be from the fast thread");
		
        String secondThreadResponse = futures.get(1).get();
        assertEquals("slow thread", secondThreadResponse, "Last Response should be from the slow thread");
    } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
    }
}
```

## 5. 使用ExecutorCompletionService

运行多个线程的另一种方法是使用ExecutorCompletionService，它使用提供的ExecutorService来执行任务。

与invokeAll()的一个区别是，代表已执行任务的Future返回的顺序。**ExecutorCompletionService使用队列按完成的顺序存储结果**，而invokeAll()返回的集合的顺序与给定任务集合生成的顺序相同：

```java
@Test
void givenMultipleThreads_whenUsingCompletionService_thenMainThreadShouldWaitForAllToFinish() {
	CompletionService<String> service = new ExecutorCompletionService<>(WORKER_THREAD_POOL);
	List<Callable<String>> callables = Arrays.asList(
		new DelayedCallable("fast thread", 100),
		new DelayedCallable("slow thread", 3000));
	
	for (Callable<String> callable : callables) {
		service.submit(callable);
	}
}
```

可以使用take()方法访问结果：

```java
long startProcessingTime = System.currentTimeMillis();

Future<String> future = service.take();
String firstThreadResponse = future.get();
long totalProcessingTime = System.currentTimeMillis() - startProcessingTime;

assertTrue("First response should be from the fast thread", "fast thread".equals(firstThreadResponse));
assertTrue(totalProcessingTime >= 100 && totalProcessingTime < 1000);

LOG.debug("Thread finished after: " + totalProcessingTime + " milliseconds");

future = service.take();
String secondThreadResponse = future.get();
totalProcessingTime = System.currentTimeMillis() - startProcessingTime;

assertTrue("Last response should be from the slow thread", "slow thread".equals(secondThreadResponse));
assertTrue(totalProcessingTime >= 3000 && totalProcessingTime < 4000);
LOG.debug("Thread finished after: " + totalProcessingTime + " milliseconds");

awaitTerminationAfterShutdown(WORKER_THREAD_POOL);
```

## 6. 总结

根据用例的不同，我们有不同的方法来等待线程完成它们的执行。

**当我们需要一种机制来通知一个或多个线程在其他线程执行的一组操作已经完成时，CountDownLatch非常有用**。

**当我们需要尽快访问任务结果时，ExecutorCompletionService很有用；当我们想等待所有正在运行的任务完成时，ExecutorCompletionService也很有用**。