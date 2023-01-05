package cn.tuyucheng.taketoday.rxjava.onerror;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OnErrorRetryIntegrationTest {

	private Error UNKNOWN_ERROR = new Error("unknown error");

	@Test
	void givenSubscriberAndError_whenRetryOnError_thenRetryConfirmed() {
		TestObserver<String> testObserver = new TestObserver<>();
		AtomicInteger atomicCounter = new AtomicInteger(0);

		Observable
			.<String>error(() -> {
				atomicCounter.incrementAndGet();
				return UNKNOWN_ERROR;
			})
			.retry(1)
			.subscribe(testObserver);

		testObserver.assertError(UNKNOWN_ERROR);
		testObserver.assertNotComplete();
		testObserver.assertNoValues();
		assertEquals(2, atomicCounter.get(), "should call twice");
	}

	@Test
	void givenSubscriberAndError_whenRetryConditionallyOnError_thenRetryConfirmed() {
		TestObserver<String> testObserver = new TestObserver<>();

		AtomicInteger atomicCounter = new AtomicInteger(0);

		Observable
			.<String>error(() -> {
				atomicCounter.incrementAndGet();
				return UNKNOWN_ERROR;
			})
			.retry((integer, throwable) -> integer < 4)
			.subscribe(testObserver);

		testObserver.assertError(UNKNOWN_ERROR);
		testObserver.assertNotComplete();
		testObserver.assertNoValues();
		assertEquals(4, atomicCounter.get(), "should call 4 times");
	}

	@Test
	void givenSubscriberAndError_whenRetryUntilOnError_thenRetryConfirmed() {
		TestObserver<String> testObserver = new TestObserver<>();
		AtomicInteger atomicCounter = new AtomicInteger(0);

		Observable
			.<String>error(UNKNOWN_ERROR)
			.retryUntil(() -> atomicCounter.incrementAndGet() > 3)
			.subscribe(testObserver);

		testObserver.assertError(UNKNOWN_ERROR);
		testObserver.assertNotComplete();
		testObserver.assertNoValues();
		assertEquals(4, atomicCounter.get(), "should call 4 times");
	}

	@Test
	void givenSubscriberAndError_whenRetryWhenOnError_thenRetryConfirmed() {
		TestObserver<String> testObserver = new TestObserver<>();
		Exception noretryException = new Exception("don't retry");

		Observable
			.<String>error(UNKNOWN_ERROR)
			.retryWhen(throwableObservable -> Observable.<String>error(noretryException))
			.subscribe(testObserver);

		testObserver.assertError(noretryException);
		testObserver.assertNotComplete();
		testObserver.assertNoValues();
	}

	@Test
	void givenSubscriberAndError_whenRetryWhenOnError_thenCompleted() {
		TestObserver<String> testObserver = new TestObserver<>();
		AtomicInteger atomicCounter = new AtomicInteger(0);

		Observable
			.<String>error(() -> {
				atomicCounter.incrementAndGet();
				return UNKNOWN_ERROR;
			})
			.retryWhen(throwableObservable -> Observable.empty())
			.subscribe(testObserver);

		testObserver.assertNoErrors();
		testObserver.assertComplete();
		testObserver.assertNoValues();
		assertEquals(0, atomicCounter.get(), "should not retry");
	}

	@Test
	void givenSubscriberAndError_whenRetryWhenOnError_thenResubscribed() {
		TestObserver<String> testObserver = new TestObserver<>();
		AtomicInteger atomicCounter = new AtomicInteger(0);

		Observable
			.<String>error(() -> {
				atomicCounter.incrementAndGet();
				return UNKNOWN_ERROR;
			})
			.retryWhen(throwableObservable -> Observable.just("anything"))
			.subscribe(testObserver);

		testObserver.assertNoErrors();
		testObserver.assertComplete();
		testObserver.assertNoValues();
		assertEquals(1, atomicCounter.get(), "should retry once");
	}

	@Test
	void givenSubscriberAndError_whenRetryWhenForMultipleTimesOnError_thenResumed() {
		TestObserver<String> testObserver = new TestObserver<>();
		long before = System.currentTimeMillis();

		Observable
			.<String>error(UNKNOWN_ERROR)
			.retryWhen(throwableObservable -> throwableObservable
				.zipWith(Observable.range(1, 3), (throwable, integer) -> integer)
				.flatMap(integer -> {
					System.out.println("retried " + integer + " times");
					return Observable.timer(integer, TimeUnit.SECONDS);
				}))
			.blockingSubscribe(testObserver);

		testObserver.assertNoErrors();
		testObserver.assertComplete();
		testObserver.assertNoValues();
		long secondsElapsed = (System.currentTimeMillis() - before) / 1000;
		assertEquals(6, secondsElapsed, "6 seconds should elapse");
	}
}