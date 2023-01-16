package cn.tuyucheng.taketoday.algorithms.stringpermutation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StringPermutationsGuavaUnitTest {

	@ParameterizedTest
	@CsvSource({"abc, 6",
		"hello, 120",
		"aaaaaa, 720"})
	void testPermutationsWithRepetitions(String string, int numberOfPermutations) {
		StringPermutationsGuava permutationGenerator = new StringPermutationsGuava();
		final List<String> permutations = permutationGenerator.permutationWithRepetitions(string);
		final int size = permutations.size();
		assertThat(permutations)
			.as("\"%s\" should have %d permutation, but had %d", string, numberOfPermutations, size)
			.hasSize(numberOfPermutations);
	}

	@ParameterizedTest
	@CsvSource({"abc, 6",
		"hello, 60",
		"aaaaaa, 1"})
	void testPermutationsWithoutRepetitions(String string, int numberOfPermutations) {
		StringPermutationsGuava permutationGenerator = new StringPermutationsGuava();
		final List<String> permutations = permutationGenerator.permutationWithoutRepetitions(string);
		final int size = permutations.size();
		assertThat(permutations)
			.as("\"%s\" should have %d permutation, but had %d", string, numberOfPermutations, size)
			.hasSize(numberOfPermutations);
	}

}