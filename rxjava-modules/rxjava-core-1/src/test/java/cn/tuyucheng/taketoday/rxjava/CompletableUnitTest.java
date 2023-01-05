package cn.tuyucheng.taketoday.rxjava;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.observers.DisposableCompletableObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CompletableUnitTest {

	Completable first;
	Completable second;
	Completable error;
	Throwable throwable = new RuntimeException();

	@BeforeEach
	void setUpCompletables() {
		first = Completable.fromSingle(Single.just(1));
		second = Completable.fromRunnable(() -> {
		});
		error = Single.error(throwable)
			.ignoreElement();
	}

	@Test
	void whenCompletableConstructed_thenCompletedSuccessfully() {
		Completable completed = Completable.complete();
		completed.subscribe(new DisposableCompletableObserver() {
			@Override
			public void onComplete() {
				System.out.println("Completed!");
			}

			@Override
			public void onError(Throwable e) {
				e.printStackTrace();
			}
		});
		Flowable<String> flowable = Flowable.just("request received", "user logged in");
		Completable flowableCompletable = Completable.fromPublisher(flowable);
		Completable singleCompletable = Single.just(1)
			.ignoreElement();

		completed.andThen(flowableCompletable)
			.andThen(singleCompletable)
			.test()
			.assertComplete();
	}

	@Test
	void whenCombiningCompletables_thenCompletedSuccessfully() {
		first.andThen(second)
			.test()
			.assertComplete();
	}

	@Test
	void whenCombinedWithError_thenCompletedWithError() {
		first.andThen(second)
			.andThen(error)
			.test()
			.assertError(throwable);
	}

	@Test
	void whenCombinedWithNever_thenDoesNotComplete() {
		first.andThen(second)
			.andThen(Completable.never())
			.test()
			.assertNotComplete();
	}

	@Test
	void whenMergedCompletables_thenCompletedSuccessfully() {
		Completable.mergeArray(first, second)
			.test()
			.assertComplete();
	}

	@Test
	void whenMergedWithError_thenCompletedWithError() {
		Completable.mergeArray(first, second, error)
			.test()
			.assertError(throwable);
	}

	@Test
	void whenFlatMaped_thenCompletedSuccessfully() {
		Completable allElementsCompletable = Flowable.just("request received", "user logged in")
			.flatMapCompletable(message -> Completable
				.fromRunnable(() -> System.out.println(message))
			);
		allElementsCompletable
			.test()
			.assertComplete();
	}

	@Test
	void whenAmbWithNever_thenCompletedSuccessfully() {
		Completable.ambArray(first, Completable.never(), second)
			.test()
			.assertComplete();
	}

	@Test
	void whenAmbWithError_thenCompletedWithError() {
		Completable.ambArray(error, first, second)
			.test()
			.assertError(throwable);
	}
}