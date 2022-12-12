package cn.tuyucheng.taketoday.java8.lambda.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class LambdaExceptionWrappersUnitTest {

	private static final Logger LOG = LoggerFactory.getLogger(LambdaExceptionWrappersUnitTest.class);

	private List<Integer> integers;

	@BeforeEach
	void init() {
		integers = Arrays.asList(3, 9, 7, 0, 10, 20);
	}

	@Test
	void whenNoExceptionFromLambdaWrapper_thenSuccess() {
		integers.forEach(LambdaExceptionWrappers.lambdaWrapper(i -> LOG.debug("{}", 50 / i)));
	}

	@Test
	void whenNoExceptionFromConsumerWrapper_thenSuccess() {
		integers.forEach(LambdaExceptionWrappers.consumerWrapper(i -> LOG.debug("{}", 50 / i), ArithmeticException.class));
	}

	@Test
	void whenExceptionFromThrowingConsumerWrapper_thenSuccess() {
		assertThrows(RuntimeException.class, () -> integers.forEach(
			LambdaExceptionWrappers.throwingConsumerWrapper(this::writeToFile)
		));
	}

	@Test
	void whenNoExceptionFromHandlingConsumerWrapper_thenSuccess() {
		integers.forEach(LambdaExceptionWrappers.handlingConsumerWrapper(this::writeToFile, IOException.class));
	}

	private void writeToFile(Integer i) throws IOException {
		if (i == 0) {
			throw new IOException(); // mock IOException
		}
	}
}