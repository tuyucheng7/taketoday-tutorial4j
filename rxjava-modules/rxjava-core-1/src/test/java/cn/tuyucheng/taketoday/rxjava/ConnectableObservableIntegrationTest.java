package cn.tuyucheng.taketoday.rxjava;

import org.junit.jupiter.api.Test;
import rx.Observable;
import rx.observables.ConnectableObservable;

import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ConnectableObservableIntegrationTest {

	@Test
	void givenConnectableObservable_whenConnect_thenGetMessage() throws InterruptedException {
		String[] result = {""};
		ConnectableObservable<Long> connectable
			= Observable.interval(500, TimeUnit.MILLISECONDS).publish();
		connectable.subscribe(i -> result[0] += i);
		assertNotEquals("01", result[0]);

		connectable.connect();
		await().until(() -> assertEquals("01", result[0]));
	}
}