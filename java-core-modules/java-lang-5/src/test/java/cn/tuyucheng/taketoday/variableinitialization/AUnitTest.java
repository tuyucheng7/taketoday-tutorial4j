package cn.tuyucheng.taketoday.variableinitialization;

import cn.tuyucheng.taketoday.variableInitialization.A;
import cn.tuyucheng.taketoday.variableInitialization.B;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

public class AUnitTest {

	@Test
	public void whenCreatingTest_useDependencyInjection() {
		// given
		B b = mock(B.class);
		A a = new A(b);
	}
}
