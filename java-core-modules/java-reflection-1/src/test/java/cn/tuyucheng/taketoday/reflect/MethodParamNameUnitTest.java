package cn.tuyucheng.taketoday.reflect;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class MethodParamNameUnitTest {

	@Test
	void whenGetConstructorParams_thenOk() throws NoSuchMethodException, SecurityException {
		List<Parameter> parameters
			= Arrays.asList(Person.class.getConstructor(String.class).getParameters());
		Optional<Parameter> parameter
			= parameters.stream().filter(Parameter::isNamePresent).findFirst();
		assertThat(parameter.get().getName()).isEqualTo("fullName");
	}

	@Test
	void whenGetMethodParams_thenOk() throws NoSuchMethodException, SecurityException {
		List<Parameter> parameters
			= Arrays.asList(
			Person.class.getMethod("setFullName", String.class).getParameters());
		Optional<Parameter> parameter
			= parameters.stream().filter(Parameter::isNamePresent).findFirst();
		assertThat(parameter.get().getName()).isEqualTo("fullName");
	}
}