package cn.tuyucheng.taketoday.java9.language;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TryWithResourcesUnitTest {

	static int closeCount = 0;

	static class MyAutoCloseable implements AutoCloseable {
		final FinalWrapper finalWrapper = new FinalWrapper();

		public void close() {
			closeCount++;
		}

		static class FinalWrapper {
			final AutoCloseable finalCloseable = new AutoCloseable() {
				@Override
				public void close() throws Exception {
					closeCount++;
				}
			};
		}
	}

	@Test
	void tryWithResourcesTest() {
		MyAutoCloseable mac = new MyAutoCloseable();

		try (mac) {
			assertEquals(0, closeCount, "Expected and Actual does not match");
		}

		try (mac.finalWrapper.finalCloseable) {
			assertEquals(1, closeCount, "Expected and Actual does not match");
		} catch (Exception ex) {
		}

		try (new MyAutoCloseable() {
		}.finalWrapper.finalCloseable) {
			assertEquals(2, closeCount, "Expected and Actual does not match");
		} catch (Exception ex) {
		}

		try ((closeCount > 0 ? mac : new MyAutoCloseable()).finalWrapper.finalCloseable) {
			assertEquals(3, closeCount, "Expected and Actual does not match");
		} catch (Exception ex) {
		}

		try {
			throw new CloseableException();
		} catch (CloseableException ex) {
			try (ex) {
				assertEquals(4, closeCount, "Expected and Actual does not match");
			}
		}
		assertEquals(5, closeCount, "Expected and Actual does not match");
	}

	static class CloseableException extends Exception implements AutoCloseable {
		@Override
		public void close() {
			closeCount++;
		}
	}
}