package cn.tuyucheng.taketoday.nulls;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UsingObjectsUnitTest {

	private UsingObjects classUnderTest;

	@BeforeEach
	void setup() {
		classUnderTest = new UsingObjects();
	}

	@Test
	void whenArgIsNull_thenThrowException() {
		assertThrows(NullPointerException.class, () -> classUnderTest.accept(null));
	}

	@Test
	void whenArgIsNonNull_thenDoesNotThrowException() {
		assertDoesNotThrow(() -> classUnderTest.accept("test    "));
	}
}