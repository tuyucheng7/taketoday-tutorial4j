package cn.tuyucheng.taketoday.rxjava;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import org.junit.jupiter.api.Test;

class MaybeUnitTest {
	@Test
	void whenEmitsSingleValue_thenItIsObserved() {
		Maybe<Integer> maybe = Flowable.just(1, 2, 3, 4, 5)
			.firstElement();

		maybe.map(x -> x + 7)
			.filter(x -> x > 0)
			.test()
			.assertResult(8)
			.assertComplete();
	}

	@Test
	void whenEmitsNoValue_thenSignalsCompletionAndNoValueObserved() {
		Maybe<Integer> maybe = Flowable.just(1, 2, 3, 4, 5)
			.skip(5)
			.firstElement();

		maybe.test()
			.assertComplete()
			.assertNoValues();
	}

	@Test
	void whenThrowsError_thenErrorIsRaised() {
		Maybe<Integer> maybe = Flowable.<Integer>error(new Exception("msg"))
			.firstElement();

		maybe.test()
			.assertErrorMessage("msg");
	}
}