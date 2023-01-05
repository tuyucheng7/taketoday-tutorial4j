package cn.tuyucheng.taketoday.rxjava;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RxJavaHooksUnitTest {

	private boolean initHookCalled = false;
	private boolean hookCalled = false;

	@Test
	void givenObservable_whenError_shouldExecuteTheHook() {
		RxJavaPlugins.setErrorHandler(throwable -> {
			hookCalled = true;
		});

		Observable.error(new IllegalStateException())
			.subscribe();
		assertTrue(hookCalled);
	}

	@Test
	void givenCompletable_whenAssembled_shouldExecuteTheHook() {
		RxJavaPlugins.setOnCompletableAssembly(completable -> {
			hookCalled = true;
			return completable;
		});
		Completable.fromSingle(Single.just(1));
		assertTrue(hookCalled);
	}

	@Test
	void givenCompletable_whenSubscribed_shouldExecuteTheHook() {
		RxJavaPlugins.setOnCompletableSubscribe((completable, observer) -> {
			hookCalled = true;
			return observer;
		});

		Completable.fromSingle(Single.just(1))
			.test();
		assertTrue(hookCalled);
	}

	@Test
	void givenObservable_whenAssembled_shouldExecuteTheHook() {
		RxJavaPlugins.setOnObservableAssembly(observable -> {
			hookCalled = true;
			return observable;
		});

		Observable.range(1, 10);
		assertTrue(hookCalled);
	}

	@Test
	void givenObservable_whenSubscribed_shouldExecuteTheHook() {
		RxJavaPlugins.setOnObservableSubscribe((observable, observer) -> {
			hookCalled = true;
			return observer;
		});

		Observable.range(1, 10)
			.test();
		assertTrue(hookCalled);
	}

	@Test
	void givenConnectableObservable_whenAssembled_shouldExecuteTheHook() {
		RxJavaPlugins.setOnConnectableObservableAssembly(connectableObservable -> {
			hookCalled = true;
			return connectableObservable;
		});

		ConnectableObservable.range(1, 10)
			.publish()
			.connect();
		assertTrue(hookCalled);
	}

	@Test
	void givenFlowable_whenAssembled_shouldExecuteTheHook() {
		RxJavaPlugins.setOnFlowableAssembly(flowable -> {
			hookCalled = true;
			return flowable;
		});

		Flowable.range(1, 10);
		assertTrue(hookCalled);
	}

	@Test
	void givenFlowable_whenSubscribed_shouldExecuteTheHook() {
		RxJavaPlugins.setOnFlowableSubscribe((flowable, observer) -> {
			hookCalled = true;
			return observer;
		});

		Flowable.range(1, 10)
			.test();
		assertTrue(hookCalled);
	}

	@Test
	void givenConnectableFlowable_whenAssembled_shouldExecuteTheHook() {
		RxJavaPlugins.setOnConnectableFlowableAssembly(connectableFlowable -> {
			hookCalled = true;
			return connectableFlowable;
		});

		ConnectableFlowable.range(1, 10)
			.publish()
			.connect();
		assertTrue(hookCalled);
	}

	@Test
	void givenParallel_whenAssembled_shouldExecuteTheHook() {
		RxJavaPlugins.setOnParallelAssembly(parallelFlowable -> {
			hookCalled = true;
			return parallelFlowable;
		});

		Flowable.range(1, 10)
			.parallel();
		assertTrue(hookCalled);
	}

	@Test
	void givenMaybe_whenAssembled_shouldExecuteTheHook() {
		RxJavaPlugins.setOnMaybeAssembly(maybe -> {
			hookCalled = true;
			return maybe;
		});

		Maybe.just(1);
		assertTrue(hookCalled);
	}

	@Test
	void givenMaybe_whenSubscribed_shouldExecuteTheHook() {
		RxJavaPlugins.setOnMaybeSubscribe((maybe, observer) -> {
			hookCalled = true;
			return observer;
		});

		Maybe.just(1)
			.test();
		assertTrue(hookCalled);
	}

	@Test
	void givenSingle_whenAssembled_shouldExecuteTheHook() {
		RxJavaPlugins.setOnSingleAssembly(single -> {
			hookCalled = true;
			return single;
		});

		Single.just(1);
		assertTrue(hookCalled);
	}

	@Test
	void givenSingle_whenSubscribed_shouldExecuteTheHook() {
		RxJavaPlugins.setOnSingleSubscribe((single, observer) -> {
			hookCalled = true;
			return observer;
		});

		Single.just(1)
			.test();
		assertTrue(hookCalled);
	}

	@Test
	void givenAnyScheduler_whenCalled_shouldExecuteTheHook() {
		RxJavaPlugins.setScheduleHandler((runnable) -> {
			hookCalled = true;
			return runnable;
		});

		Observable.range(1, 10)
			.map(v -> v * 2)
			.subscribeOn(Schedulers.single())
			.test();
		hookCalled = false;
		Observable.range(1, 10)
			.map(v -> v * 2)
			.subscribeOn(Schedulers.computation())
			.test();
		assertTrue(hookCalled);
	}

	// @Test
	// void givenComputationScheduler_whenCalled_shouldExecuteTheHooks() {
	// 	RxJavaPlugins.setInitComputationSchedulerHandler((scheduler) -> {
	// 		initHookCalled = true;
	// 		return scheduler.call();
	// 	});
	// 	RxJavaPlugins.setComputationSchedulerHandler((scheduler) -> {
	// 		hookCalled = true;
	// 		return scheduler;
	// 	});
	//
	// 	Observable.range(1, 10)
	// 		.map(v -> v * 2)
	// 		.subscribeOn(Schedulers.computation())
	// 		.test();
	// 	assertTrue(hookCalled && initHookCalled);
	// }

	@AfterEach
	void reset() {
		initHookCalled = false;
		hookCalled = false;
		RxJavaPlugins.reset();
	}
}