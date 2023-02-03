package cn.tuyucheng.taketoday.reflection.exception.invocationtarget;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InvocationTargetUnitTest {

	@Test
	void whenCallingMethodThrowsException_thenAssertCauseOfInvocationTargetException() throws Exception {
		InvocationTargetExample targetExample = new InvocationTargetExample();
		Method method = InvocationTargetExample.class.getMethod("divideByZeroExample");

		Exception exception = assertThrows(InvocationTargetException.class, () -> method.invoke(targetExample));

		assertEquals(ArithmeticException.class, exception.getCause().getClass());
	}
}