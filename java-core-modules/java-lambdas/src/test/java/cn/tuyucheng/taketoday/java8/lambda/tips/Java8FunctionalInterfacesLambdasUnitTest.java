package cn.tuyucheng.taketoday.java8.lambda.tips;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class Java8FunctionalInterfacesLambdasUnitTest {

	private UseFoo useFoo;

	@BeforeEach
	void init() {
		useFoo = new UseFoo();
	}

	@Test
	void functionalInterfaceInstantiation_whenReturnDefiniteString_thenCorrect() {
		final Foo foo = parameter -> parameter + "from lambda";
		final String result = useFoo.add("Message ", foo);

		assertEquals("Message from lambda", result);
	}

	@Test
	void standardFIParameter_whenReturnDefiniteString_thenCorrect() {
		final Function<String, String> fn = parameter -> parameter + "from lambda";
		final String result = useFoo.addWithStandardFI("Message ", fn);

		assertEquals("Message from lambda", result);
	}

	@Test
	void defaultMethodFromExtendedInterface_whenReturnDefiniteString_thenCorrect() {
		final FooExtended fooExtended = string -> string;
		final String result = fooExtended.defaultCommon();

		assertEquals("Default Common from Bar", result);
	}

	@Test
	void lambdaAndInnerClassInstantiation_whenReturnSameString_thenCorrect() {
		final Foo foo = parameter -> parameter + "from Foo";

		final Foo fooByIC = new Foo() {
			@Override
			public String method(final String string) {
				return string + "from Foo";
			}
		};

		assertEquals(foo.method("Something "), fooByIC.method("Something "));
	}

	@Test
	void accessVariablesFromDifferentScopes_whenReturnPredefinedString_thenCorrect() {
		assertEquals("Results: resultIC = Inner class value, resultLambda = Enclosing scope value", useFoo.scopeExperiment());
	}

	@Test
	void shorteningLambdas_whenReturnEqualsResults_thenCorrect() {
		final Foo foo = parameter -> buildString(parameter);

		final Foo fooHuge = parameter -> {
			final String result = "Something " + parameter;
			// many lines of code
			return result;
		};

		assertEquals(foo.method("Something"), fooHuge.method("Something"));
	}

	private String buildString(final String parameter) {
		final String result = "Something " + parameter;
		// many lines of code
		return result;
	}

	@Test
	void mutatingOfEffectivelyFinalVariable_whenNotEquals_thenCorrect() {
		final int[] total = new int[1];
		final Runnable r = () -> total[0]++;
		r.run();

		assertNotEquals(0, total[0]);
	}
}