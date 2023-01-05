package cn.tuyucheng.taketoday.rxjava;

import org.junit.jupiter.api.Test;
import rx.BackpressureOverflow;
import rx.Observable;
import rx.exceptions.MissingBackpressureException;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RxJavaBackpressureLongRunningUnitTest {

	@Test
	void givenColdObservable_shouldNotThrowException() {
		// given
		TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();

		// when
		Observable.range(1, 1_000_000).observeOn(Schedulers.computation()).subscribe(testSubscriber);

		// then
		testSubscriber.awaitTerminalEvent();
		assertEquals(0, testSubscriber.getOnErrorEvents().size());
	}

	@Test
	void givenHotObservable_whenBackpressureNotDefined_shouldTrowException() {
		// given
		TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();
		PublishSubject<Integer> source = PublishSubject.create();

		source.observeOn(Schedulers.computation()).subscribe(testSubscriber);

		// when
		IntStream.range(0, 1_000_000).forEach(source::onNext);

		// then
		testSubscriber.awaitTerminalEvent();
		testSubscriber.assertError(MissingBackpressureException.class);
	}

	@Test
	void givenHotObservable_whenWindowIsDefined_shouldNotThrowException() {
		// given
		TestSubscriber<Observable<Integer>> testSubscriber = new TestSubscriber<>();
		PublishSubject<Integer> source = PublishSubject.create();

		// when
		source.window(500).observeOn(Schedulers.computation()).subscribe(testSubscriber);

		IntStream.range(0, 1_000).forEach(source::onNext);

		// then
		testSubscriber.awaitTerminalEvent(2, TimeUnit.SECONDS);
		assertEquals(0, testSubscriber.getOnErrorEvents().size());
	}

	@Test
	void givenHotObservable_whenBufferIsDefined_shouldNotThrowException() {
		// given
		TestSubscriber<List<Integer>> testSubscriber = new TestSubscriber<>();
		PublishSubject<Integer> source = PublishSubject.create();

		// when
		source.buffer(1024).observeOn(Schedulers.computation()).subscribe(testSubscriber);

		IntStream.range(0, 1_000).forEach(source::onNext);

		// then
		testSubscriber.awaitTerminalEvent(2, TimeUnit.SECONDS);
		assertEquals(0, testSubscriber.getOnErrorEvents().size());
	}

	@Test
	void givenHotObservable_whenSkippingOperationIsDefined_shouldNotThrowException() {
		// given
		TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();
		PublishSubject<Integer> source = PublishSubject.create();

		// when
		source.sample(100, TimeUnit.MILLISECONDS)
			// .throttleFirst(100, TimeUnit.MILLISECONDS)
			.observeOn(Schedulers.computation()).subscribe(testSubscriber);

		IntStream.range(0, 1_000).forEach(source::onNext);

		// then
		testSubscriber.awaitTerminalEvent(2, TimeUnit.SECONDS);
		assertEquals(0, testSubscriber.getOnErrorEvents().size());
	}

	@Test
	void givenHotObservable_whenOnBackpressureBufferDefined_shouldNotThrowException() {
		// given
		TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();

		// when
		Observable.range(1, 1_000_000).onBackpressureBuffer(16, () -> {
		}, BackpressureOverflow.ON_OVERFLOW_DROP_OLDEST).observeOn(Schedulers.computation()).subscribe(testSubscriber);

		// then
		testSubscriber.awaitTerminalEvent(2, TimeUnit.SECONDS);
		assertEquals(0, testSubscriber.getOnErrorEvents().size());
	}

	@Test
	void givenHotObservable_whenOnBackpressureDropDefined_shouldNotThrowException() {
		// given
		TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();

		// when
		Observable.range(1, 1_000_000).onBackpressureDrop().observeOn(Schedulers.computation())
			.subscribe(testSubscriber);

		// then
		testSubscriber.awaitTerminalEvent(2, TimeUnit.SECONDS);
		assertEquals(0, testSubscriber.getOnErrorEvents().size());
	}
}