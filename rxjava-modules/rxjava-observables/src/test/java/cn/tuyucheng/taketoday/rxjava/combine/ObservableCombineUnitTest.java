package cn.tuyucheng.taketoday.rxjava.combine;

import org.junit.jupiter.api.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

class ObservableCombineUnitTest {

	@Test
	void givenTwoObservables_whenMerged_shouldEmitCombinedResults() {
		TestSubscriber<String> testSubscriber = new TestSubscriber<>();

		Observable.merge(
			Observable.from(asList("Hello", "World")),
			Observable.from(asList("I love", "RxJava"))
		).subscribe(testSubscriber);

		testSubscriber.assertValues("Hello", "World", "I love", "RxJava");
	}

	@Test
	void givenTwoObservables_whenZipped_thenReturnCombinedResults() {
		TestSubscriber<String> testSubscriber = new TestSubscriber<>();

		Observable.zip(
				Observable.from(asList("Simple", "Moderate", "Complex")),
				Observable.from(asList("Solutions", "Success", "Hierarchy")),
				(str1, str2) -> String.format("%s %s", str1, str2))
			.subscribe(testSubscriber);

		testSubscriber.assertValues("Simple Solutions", "Moderate Success", "Complex Hierarchy");
	}

	@Test
	void givenMutipleObservablesOneThrows_whenMerged_thenCombineBeforePropagatingError() {
		TestSubscriber<String> testSubscriber = new TestSubscriber<>();

		Observable.mergeDelayError(
			Observable.from(asList("hello", "world")),
			Observable.error(new RuntimeException("Some exception")),
			Observable.from(asList("rxjava"))
		).subscribe(testSubscriber);

		testSubscriber.assertValues("hello", "world", "rxjava");
		testSubscriber.assertError(RuntimeException.class);
	}

	@Test
	void givenAStream_whenZippedWithInterval_shouldDelayStreamEmmission() {
		TestSubscriber<String> testSubscriber = new TestSubscriber<>();

		Observable<String> data = Observable.just("one", "two", "three", "four", "five");
		Observable<Long> interval = Observable.interval(1L, TimeUnit.SECONDS);

		Observable
			.zip(data, interval, (strData, tick) -> String.format("[%d]=%s", tick, strData))
			.toBlocking().subscribe(testSubscriber);

		testSubscriber.assertCompleted();
		testSubscriber.assertValueCount(5);
		testSubscriber.assertValues("[0]=one", "[1]=two", "[2]=three", "[3]=four", "[4]=five");
	}
}