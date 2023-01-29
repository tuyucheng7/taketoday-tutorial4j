package cn.tuyucheng.taketoday.concurrent.threadsafety.application;

import cn.tuyucheng.taketoday.concurrent.threadsafety.callables.AtomicCounterCallable;
import cn.tuyucheng.taketoday.concurrent.threadsafety.callables.CounterCallable;
import cn.tuyucheng.taketoday.concurrent.threadsafety.callables.ExtrinsicLockCounterCallable;
import cn.tuyucheng.taketoday.concurrent.threadsafety.callables.MessageServiceCallable;
import cn.tuyucheng.taketoday.concurrent.threadsafety.callables.ReentranReadWriteLockCounterCallable;
import cn.tuyucheng.taketoday.concurrent.threadsafety.callables.ReentrantLockCounterCallable;
import cn.tuyucheng.taketoday.concurrent.threadsafety.mathutils.MathUtils;
import cn.tuyucheng.taketoday.concurrent.threadsafety.services.AtomicCounter;
import cn.tuyucheng.taketoday.concurrent.threadsafety.services.Counter;
import cn.tuyucheng.taketoday.concurrent.threadsafety.services.MessageService;
import cn.tuyucheng.taketoday.concurrent.threadsafety.services.ObjectLockCounter;
import cn.tuyucheng.taketoday.concurrent.threadsafety.services.ReentrantLockCounter;
import cn.tuyucheng.taketoday.concurrent.threadsafety.services.ReentrantReadWriteLockCounter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Application {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		new Thread(() -> System.out.println(MathUtils.factorial(10))).start();
		new Thread(() -> System.out.println(MathUtils.factorial(5))).start();

		ExecutorService executorService = Executors.newFixedThreadPool(10);
		MessageService messageService = new MessageService("Welcome to Tuyucheng!");
		Future<String> future1 = executorService.submit(new MessageServiceCallable(messageService));
		Future<String> future2 = executorService.submit(new MessageServiceCallable(messageService));
		System.out.println(future1.get());
		System.out.println(future2.get());

		Counter counter = new Counter();
		Future<Integer> future3 = executorService.submit(new CounterCallable(counter));
		Future<Integer> future4 = executorService.submit(new CounterCallable(counter));
		System.out.println(future3.get());
		System.out.println(future4.get());

		ObjectLockCounter objectLockCounter = new ObjectLockCounter();
		Future<Integer> future5 = executorService.submit(new ExtrinsicLockCounterCallable(objectLockCounter));
		Future<Integer> future6 = executorService.submit(new ExtrinsicLockCounterCallable(objectLockCounter));
		System.out.println(future5.get());
		System.out.println(future6.get());

		ReentrantLockCounter reentrantLockCounter = new ReentrantLockCounter();
		Future<Integer> future7 = executorService.submit(new ReentrantLockCounterCallable(reentrantLockCounter));
		Future<Integer> future8 = executorService.submit(new ReentrantLockCounterCallable(reentrantLockCounter));
		System.out.println(future7.get());
		System.out.println(future8.get());

		ReentrantReadWriteLockCounter reentrantReadWriteLockCounter = new ReentrantReadWriteLockCounter();
		Future<Integer> future9 = executorService.submit(new ReentranReadWriteLockCounterCallable(reentrantReadWriteLockCounter));
		Future<Integer> future10 = executorService.submit(new ReentranReadWriteLockCounterCallable(reentrantReadWriteLockCounter));
		System.out.println(future9.get());
		System.out.println(future10.get());

		AtomicCounter atomicCounter = new AtomicCounter();
		Future<Integer> future11 = executorService.submit(new AtomicCounterCallable(atomicCounter));
		Future<Integer> future12 = executorService.submit(new AtomicCounterCallable(atomicCounter));
		System.out.println(future11.get());
		System.out.println(future12.get());

		Collection<Integer> syncCollection = Collections.synchronizedCollection(new ArrayList<>());
		Thread thread11 = new Thread(() -> syncCollection.addAll(Arrays.asList(1, 2, 3, 4, 5, 6)));
		Thread thread12 = new Thread(() -> syncCollection.addAll(Arrays.asList(1, 2, 3, 4, 5, 6)));
		thread11.start();
		thread12.start();

		Map<String, String> concurrentMap = new ConcurrentHashMap<>();
		concurrentMap.put("1", "one");
		concurrentMap.put("2", "two");
		concurrentMap.put("3", "three");
	}
}