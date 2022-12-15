package cn.tuyucheng.taketoday.nulls;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UsingLombokUnitTest {

	private UsingLombok classUnderTest;

	@BeforeEach
	void setup() {
		classUnderTest = new UsingLombok();
	}

	@Test
	void whenNullArg_thenThrowNullPointerException() {
		assertThrows(NullPointerException.class, () -> classUnderTest.accept(null));
	}
}