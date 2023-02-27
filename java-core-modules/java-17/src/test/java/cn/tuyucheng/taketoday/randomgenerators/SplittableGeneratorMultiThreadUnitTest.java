package cn.tuyucheng.taketoday.randomgenerators;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SplittableGeneratorMultiThreadUnitTest {

	@Test
	@Disabled("Fail on GitHub Actions")
	void givenSplittableGenerator_whenUsingMultipleThreads_thenListOfIntsIsGenerated() {
		List<Integer> numbers = SplittableGeneratorMultiThread.generateNumbersInMultipleThreads();
		assertThat(numbers).hasSize(20).allMatch(number -> number >= 0 && number <= 10);
	}
}