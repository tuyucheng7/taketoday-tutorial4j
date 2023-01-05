package cn.tuyucheng.taketoday.rxjava;

import io.reactivex.Observable;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

class RxJavaRetryWithDelayUnitTest {

	@Test
	void givenObservable_whenSuccess_thenOnNext() {
		Observable.just(remoteCallSuccess())
			.subscribe(success -> {
				System.out.println("Success");
				System.out.println(success);
			}, err -> {
				System.out.println("Error");
				System.out.println(err);
			});
	}


	@Test
	void givenObservable_whenError_thenOnError() {
		Observable.just(remoteCallError())
			.subscribe(success -> {
				System.out.println("Success");
				System.out.println(success);
			}, err -> {
				System.out.println("Error");
				System.out.println(err);
			});
	}

	@Test
	void givenError_whenRetry_thenCanDelay() {
		Observable.just(remoteCallError())
			.retryWhen(attempts -> {
				return attempts.flatMap(err -> {
					if (customChecker(err)) {
						return Observable.timer(5000, TimeUnit.MILLISECONDS);
					} else {
						return Observable.error(err);
					}
				});
			});
	}


	private String remoteCallSuccess() {
		return "Success";
	}

	private String remoteCallError() {
		// consider a network call that failed over here.
		return "Error";
	}

	private boolean customChecker(Throwable t) {
		// this will include custom logic that decides whether resubscription should occur or not
		return true;
	}
}