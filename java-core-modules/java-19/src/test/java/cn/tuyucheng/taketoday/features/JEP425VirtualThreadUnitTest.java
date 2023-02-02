package cn.tuyucheng.taketoday.features;

import jdk.internal.vm.Continuation;
import jdk.internal.vm.ContinuationScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class JEP425VirtualThreadUnitTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(JEP425VirtualThreadUnitTest.class);
	private static final Pattern WORKER_PATTERN = Pattern.compile("worker-[\\d?]");
	private static final Pattern POOL_PATTERN = Pattern.compile("ForkJoinPool-[\\d?]");
	private final static Object OBJECT_LOCK = new Object();
	private final static Lock LOCK = new ReentrantLock();
	private static int counter;

	@BeforeEach
	void setup() {
		counter = 0;
	}

	@Test
	void whenCreatePlatformThreadAndVirtualThread_thenShouldAreDifferentType() throws InterruptedException {
		Thread pthread = Thread.ofPlatform().unstarted(() -> {
			LOGGER.info("{}", Thread.currentThread());
			assertThat(Thread.currentThread().isVirtual()).isFalse();
		});
		pthread.start();
		pthread.join();

		Thread vthread = Thread.ofVirtual().unstarted(() -> {
			LOGGER.info("{}", Thread.currentThread());
			assertThat(Thread.currentThread().isVirtual()).isTrue();
		});
		vthread.start();
		vthread.join();
	}

	@Test
	void givenVThread_whenSleepAndWakeup_thenShouldBindToDifferentPThread() throws InterruptedException {
		var threads = IntStream.range(0, 10)
			.mapToObj(index -> Thread.ofVirtual()
				.unstarted(() -> {
					if (index == 0) {
						LOGGER.info("{}", Thread.currentThread());
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					if (index == 0) {
						LOGGER.info("{}", Thread.currentThread());
					}
				})).toList();

		threads.forEach(Thread::start);
		for (Thread thread : threads) thread.join();
	}

	@Test
	void whenCreatingLargeNumberVirtualThread_thenCorrect() throws InterruptedException {
		Set<String> poolNames = ConcurrentHashMap.newKeySet();
		Set<String> pThreadNames = ConcurrentHashMap.newKeySet();

		final int threadNum = 10_000;
		var threads = IntStream.range(0, threadNum)
			.mapToObj(index -> Thread.ofVirtual()
				.unstarted(() -> {
					String poolName = readPoolName();
					poolNames.add(poolName);
					String workerName = readWorkerName();
					pThreadNames.add(workerName);
				})).toList();

		Instant begin = Instant.now();
		threads.forEach(Thread::start);
		for (Thread thread : threads) {
			thread.join();
		}
		Instant end = Instant.now();
		LOGGER.info("Time = {} ms", Duration.between(begin, end).toMillis());
		LOGGER.info("# Cores = {}", Runtime.getRuntime().availableProcessors());
		LOGGER.info("# Pools = {}", poolNames.size());
		LOGGER.info("# Platform threads = {}", pThreadNames.size());

		assertThat(pThreadNames).hasSizeLessThan(threadNum);
	}

	@Test
	void givenVirtualThread_whenWorkWithSynced_thenShouldBindToTheSamePlatformThread() throws InterruptedException {
		Set<String> pThreadNames = ConcurrentHashMap.newKeySet();

		ChronoUnit delay = ChronoUnit.MICROS;

		var threads = IntStream.range(0, 100)
			.mapToObj(index -> Thread.ofVirtual()
				.unstarted(() -> {
					try {
						if (index == 0) {
							LOGGER.info("{}", Thread.currentThread());
						}
						pThreadNames.add(readWorkerName());
						synchronized (OBJECT_LOCK) {
							Thread.sleep(Duration.of(1, delay));
							counter++;
						}
						if (index == 0) {
							LOGGER.info("{}", Thread.currentThread());
						}
						pThreadNames.add(readWorkerName());
						synchronized (OBJECT_LOCK) {
							Thread.sleep(Duration.of(1, delay));
							counter++;
						}
						if (index == 0) {
							LOGGER.info("{}", Thread.currentThread());
						}
						pThreadNames.add(readWorkerName());
						synchronized (OBJECT_LOCK) {
							Thread.sleep(Duration.of(1, delay));
							counter++;
						}
						if (index == 0) {
							LOGGER.info("{}", Thread.currentThread());
						}
						pThreadNames.add(readWorkerName());
					} catch (InterruptedException ignored) {
					}
				})).toList();
		threads.forEach(Thread::start);
		for (var thread : threads) thread.join();
		synchronized (OBJECT_LOCK) {
			LOGGER.info("# Counter = {}", counter);
			assertThat(counter).isEqualTo(300);
		}
		LOGGER.info("# Platform threads = {}", pThreadNames.size());
	}

	@Test
	void givenVirtualThread_whenWorkWithLock_thenShouldWakeupDifferentPlatformThread() throws InterruptedException {
		Set<String> pThreadNames = ConcurrentHashMap.newKeySet();

		ChronoUnit delay = ChronoUnit.MICROS;

		var threads = IntStream.range(0, 100)
			.mapToObj(index -> Thread.ofVirtual()
				.unstarted(() -> {
					try {
						if (index == 0) {
							LOGGER.info("{}", Thread.currentThread());
						}
						pThreadNames.add(readWorkerName());
						LOCK.lock();
						try {
							Thread.sleep(Duration.of(1, delay));
							counter++;
						} finally {
							LOCK.unlock();
						}
						if (index == 0) {
							LOGGER.info("{}", Thread.currentThread());
						}
						pThreadNames.add(readWorkerName());
						LOCK.lock();
						try {
							Thread.sleep(Duration.of(1, delay));
							counter++;
						} finally {
							LOCK.unlock();
						}
						if (index == 0) {
							LOGGER.info("{}", Thread.currentThread());
						}
						pThreadNames.add(readWorkerName());
						LOCK.lock();
						try {
							Thread.sleep(Duration.of(1, delay));
							counter++;
						} finally {
							LOCK.unlock();
						}
						if (index == 0) {
							LOGGER.info("{}", Thread.currentThread());
						}
						pThreadNames.add(readWorkerName());
					} catch (InterruptedException ignored) {
					}
				})).toList();
		threads.forEach(Thread::start);
		for (var thread : threads) thread.join();
		LOCK.lock();
		try {
			LOGGER.info("# Counter = {}", counter);
			assertThat(counter).isEqualTo(300);
		} finally {
			LOCK.unlock();
		}
		LOGGER.info("# Platform threads = {}", pThreadNames.size());
	}

	private static String readWorkerName() {
		String name = Thread.currentThread().toString();
		Matcher workerMatcher = WORKER_PATTERN.matcher(name);
		if (workerMatcher.find()) {
			return workerMatcher.group();
		}
		return "";
	}

	private static String readPoolName() {
		String name = Thread.currentThread().toString();
		Matcher poolMatcher = POOL_PATTERN.matcher(name);
		if (poolMatcher.find()) {
			return poolMatcher.group();
		}
		return "";
	}

	@Test
	void givenContinuationScope_whenStartupNormal_thenCorrect() {
		ContinuationScope scope = new ContinuationScope("scope");
		Continuation continuation = new Continuation(scope, () -> {
			LOGGER.info("Running");
			Continuation.yield(scope);
			LOGGER.info("Still Running");
		});
		LOGGER.info("Start");
		continuation.run();
		LOGGER.info("Back");
		continuation.run();
		LOGGER.info("Done");
	}
}