package cn.tuyucheng.taketoday.java9.currentmethod;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurrentExecutingMethodUnitTest {

	@Test
	void givenJava9_whenWalkingTheStack_thenFindMethod() {
		StackWalker walker = StackWalker.getInstance();
		Optional<String> methodName = walker.walk(frames -> frames
				.findFirst()
				.map(StackWalker.StackFrame::getMethodName)
		);

		assertTrue(methodName.isPresent());
		assertEquals("givenJava9_whenWalkingTheStack_thenFindMethod", methodName.get());
	}
}