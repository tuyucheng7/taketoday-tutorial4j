package cn.tuyucheng.taketoday.concurrent.interrupt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InterruptExampleUnitTest {

	@Test
	void whenPropagateException_thenThrowsInterruptedException() {
		assertThrows(InterruptedException.class, InterruptExample::propagateException);
	}

	@Test
	void whenRestoreTheState_thenReturnsTrue() {
		assertTrue(InterruptExample.restoreTheState());
	}

	@Test
	void whenThrowCustomException_thenContainsExpectedMessage() {
		Exception exception = assertThrows(CustomInterruptedException.class, InterruptExample::throwCustomException);
		String expectedMessage = "This thread was interrupted";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void whenHandleWithCustomException_thenReturnsTrue() throws CustomInterruptedException {
		assertTrue(InterruptExample.handleWithCustomException());
	}
}