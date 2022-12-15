package cn.tuyucheng.taketoday.nulls;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UsingStringUtilsUnitTest {

	private UsingStringUtils classUnderTest;

	@BeforeEach
	void setup() {
		classUnderTest = new UsingStringUtils();
	}

	@Test
	void givenArgIsNull_whenCallingAccept_throwIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> classUnderTest.accept(null));
	}

	@Test
	void givenArgIsEmpty_whenCallingAccept_throwIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> classUnderTest.accept(""));
	}

	@Test
	void givenArgIsNull_whenCallingAcceptOnlyNonBlank_throwIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> classUnderTest.acceptOnlyNonBlank(null));
	}

	@Test
	void givenArgIsEmpty_whenCallingAcceptOnlyNonBlank_throwIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> classUnderTest.acceptOnlyNonBlank(""));
	}

	@Test
	void givenArgIsBlank_whenCallingAcceptOnlyNonBlank_throwIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> classUnderTest.acceptOnlyNonBlank(" "));
	}
}