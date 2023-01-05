package cn.tuyucheng.taketoday.rxjava;

import org.junit.jupiter.api.Test;
import rx.Observable;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourceManagementUnitTest {

	@Test
	void givenResource_whenUsingOberservable_thenCreatePrintDisposeResource() throws InterruptedException {

		String[] result = {""};
		Observable<Character> values = Observable.using(
			() -> "MyResource",
			r -> Observable.create(o -> {
				for (Character c : r.toCharArray())
					o.onNext(c);
				o.onCompleted();
			}),
			r -> System.out.println("Disposed: " + r)
		);

		values.subscribe(
			v -> result[0] += v,
			e -> result[0] += e
		);
		assertEquals("MyResource", result[0]);
	}
}