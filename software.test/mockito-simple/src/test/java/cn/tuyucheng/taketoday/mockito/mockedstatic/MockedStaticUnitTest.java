package cn.tuyucheng.taketoday.mockito.mockedstatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

class MockedStaticUnitTest {

	@Test
	void givenStaticMethodWithNoArgs_whenMocked_thenReturnsMockSuccessfully() {
		assertThat(StaticUtils.name()).isEqualTo("Baeldung");

		try (MockedStatic<StaticUtils> utilities = mockStatic(StaticUtils.class)) {
			utilities.when(StaticUtils::name).thenReturn("Eugen");
			assertThat(StaticUtils.name()).isEqualTo("Eugen");
		}

		assertThat(StaticUtils.name()).isEqualTo("Baeldung");
	}

	@Test
	void givenStaticMethodWithArgs_whenMocked_thenReturnsMockSuccessfully() {
		assertThat(StaticUtils.range(2, 6)).containsExactly(2, 3, 4, 5);

		try (MockedStatic<StaticUtils> utilities = mockStatic(StaticUtils.class)) {
			utilities.when(() -> StaticUtils.range(2, 6))
				.thenReturn(Arrays.asList(10, 11, 12));

			assertThat(StaticUtils.range(2, 6)).containsExactly(10, 11, 12);
		}

		assertThat(StaticUtils.range(2, 6)).containsExactly(2, 3, 4, 5);
	}

}