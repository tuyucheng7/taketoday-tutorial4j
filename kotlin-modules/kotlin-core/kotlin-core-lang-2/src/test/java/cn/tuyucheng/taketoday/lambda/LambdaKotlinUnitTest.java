package cn.tuyucheng.taketoday.lambda;

import kotlin.jvm.functions.Function1;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LambdaKotlinUnitTest {

	@Test
	void givenJava6_whenUsingAnonymousClass_thenReturnLambdaResult() {
		assertTrue(LambdaKt.invokeLambda(new Function1<Double, Boolean>() {
			@Override
			public Boolean invoke(Double c) {
				return c >= 0;
			}
		}));
	}

	@Test
	void givenJava8_whenUsingLambda_thenReturnLambdaResult() {
		assertTrue(LambdaKt.invokeLambda(c -> c >= 0));
	}

	@Test
	void givenJava8_whenCallingMethodWithStringExtension_thenImplementExtension() {
		String actual = LambdaKt.extendString("Word", 90);
		String expected = "Word90";

		assertEquals(expected, actual);
	}
}