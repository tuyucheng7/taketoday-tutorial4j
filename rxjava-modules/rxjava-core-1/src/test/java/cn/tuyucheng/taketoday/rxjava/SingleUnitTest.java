package cn.tuyucheng.taketoday.rxjava;

import org.junit.jupiter.api.Test;
import rx.Observable;
import rx.Single;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SingleUnitTest {

	@Test
	void givenSingleObservable_whenSuccess_thenGetMessage() throws InterruptedException {
		String[] result = {""};
		Single<String> single = Observable.just("Hello")
			.toSingle()
			.doOnSuccess(i -> result[0] += i)
			.doOnError(error -> {
				throw new RuntimeException(error.getMessage());
			});
		single.subscribe();
		assertEquals("Hello", result[0]);
	}
}