package cn.tuyucheng.taketoday.gradle;

import org.junit.jupiter.api.Test;

import static cn.tuyucheng.taketoday.gradle.RxHelloWorld.hello;

/**
 * Unit test for {@link RxHelloWorld}.
 */
final class RxHelloWorldUnitTest {
	@Test
	void it_emits_hello_world_values() {
		hello().test().assertValues("hello", "world").assertComplete();
	}
}