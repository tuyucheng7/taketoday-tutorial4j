package cn.tuyucheng.taketoday.streamreduce.tests;

import cn.tuyucheng.taketoday.streamreduce.entities.Rating;
import cn.tuyucheng.taketoday.streamreduce.entities.Review;
import cn.tuyucheng.taketoday.streamreduce.entities.User;
import cn.tuyucheng.taketoday.streamreduce.utilities.NumberUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StreamReduceManualTest {

	@Test
	void givenIntegerList_whenReduceWithSumAccumulatorLambda_thenCorrect() {
		List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

		int result = numbers.stream().reduce(0, (a, b) -> a + b);

		assertThat(result).isEqualTo(21);
	}

	@Test
	void givenIntegerList_whenReduceWithSumAccumulatorMethodReference_thenCorrect() {
		List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

		int result = numbers.stream().reduce(0, Integer::sum);

		assertThat(result).isEqualTo(21);
	}

	@Test
	void givenStringList_whenReduceWithConcatenatorAccumulatorLambda_thenCorrect() {
		List<String> letters = Arrays.asList("a", "b", "c", "d", "e");

		String result = letters.stream().reduce("", (a, b) -> a + b);

		assertThat(result).isEqualTo("abcde");
	}

	@Test
	void givenStringList_whenReduceWithConcatenatorAccumulatorMethodReference_thenCorrect() {
		List<String> letters = Arrays.asList("a", "b", "c", "d", "e");

		String result = letters.stream().reduce("", String::concat);

		assertThat(result).isEqualTo("abcde");
	}

	@Test
	void givenStringList_whenReduceWithUppercaseConcatenatorAccumulator_thenCorrect() {
		List<String> letters = Arrays.asList("a", "b", "c", "d", "e");

		String result = letters.stream().reduce("", (a, b) -> a.toUpperCase() + b.toUpperCase());

		assertThat(result).isEqualTo("ABCDE");
	}

	@Test
	void givenUserList_whenReduceWithAgeAccumulatorAndSumCombiner_thenCorrect() {
		List<User> users = Arrays.asList(new User("John", 30), new User("Julie", 35));

		int result = users.stream().reduce(0, (partialAgeResult, user) -> partialAgeResult + user.getAge(), Integer::sum);

		assertThat(result).isEqualTo(65);
	}

	@Test
	void givenUserList_whenReduceWithGreaterAgeAccumulator_thenFindsOldest() {
		List<User> users = Arrays.asList(new User("John", 30), new User("Alex", 40), new User("Julie", 35));

		User oldest = users.stream().reduce(users.get(0), (user1, user2) -> user1.getAge() >= user2.getAge() ? user1 : user2);

		assertThat(oldest).isEqualTo(users.get(1));
	}

	@Test
	void givenUserListWithRatings_whenReduceWithGreaterAgeAccumulator_thenFindsOldest() {
		User john = new User("John", 30);
		john.getRating().add(new Review(5, ""));
		john.getRating().add(new Review(3, "not bad"));
		User julie = new User("Julie", 35);
		john.getRating().add(new Review(4, "great!"));
		john.getRating().add(new Review(2, "terrible experience"));
		john.getRating().add(new Review(4, ""));
		List<User> users = Arrays.asList(john, julie);

		Rating averageRating = users.stream().reduce(new Rating(), (rating, user) -> Rating.average(rating, user.getRating()), Rating::average);

		assertThat(averageRating.getPoints()).isEqualTo(3.6);
	}

	@Test
	void givenStringList_whenReduceWithParallelStream_thenCorrect() {
		List<String> letters = Arrays.asList("a", "b", "c", "d", "e");

		String result = letters.parallelStream().reduce("", String::concat);

		assertThat(result).isEqualTo("abcde");
	}

	@Test
	void givenNumberUtilsClass_whenCalledDivideListElements_thenCorrect() {
		List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

		assertThat(NumberUtils.divideListElements(numbers, 1)).isEqualTo(21);
	}

	@Test
	void givenNumberUtilsClass_whenCalledDivideListElementsWithExtractedTryCatchBlock_thenCorrect() {
		List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

		assertThat(NumberUtils.divideListElementsWithExtractedTryCatchBlock(numbers, 1)).isEqualTo(21);
	}

	@Test
	void givenNumberUtilsClass_whenCalledDivideListElementsWithExtractedTryCatchBlockAndListContainsZero_thenCorrect() {
		List<Integer> numbers = Arrays.asList(0, 1, 2, 3, 4, 5, 6);

		assertThat(NumberUtils.divideListElementsWithExtractedTryCatchBlock(numbers, 1)).isEqualTo(21);
	}

	@Test
	void givenNumberUtilsClass_whenCalledDivideListElementsWithExtractedTryCatchBlockAndDividerIsZero_thenCorrect() {
		List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

		assertThat(NumberUtils.divideListElementsWithExtractedTryCatchBlock(numbers, 0)).isEqualTo(0);
	}

	@Test
	void givenStream_whenCalledDivideListElementsWithApplyFunctionMethod_thenCorrect() {
		List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

		assertThat(NumberUtils.divideListElementsWithApplyFunctionMethod(numbers, 1)).isEqualTo(21);
	}

	@Test
	void givenTwoStreams_whenCalledReduceOnParallelizedStream_thenFasterExecutionTime() {
		List<User> userList = new ArrayList<>();
		for (int i = 0; i <= 1000000; i++) {
			userList.add(new User("John" + i, i));
		}
		long currentTime1 = System.currentTimeMillis();
		userList.stream().reduce(0, (partialAgeResult, user) -> partialAgeResult + user.getAge(), Integer::sum);
		long sequentialExecutionTime = System.currentTimeMillis() - currentTime1;
		long currentTime2 = System.currentTimeMillis();
		userList.parallelStream().reduce(0, (partialAgeResult, user) -> partialAgeResult + user.getAge(), Integer::sum);
		long parallelizedExecutionTime = System.currentTimeMillis() - currentTime2;

		assertThat(parallelizedExecutionTime).isLessThan(sequentialExecutionTime);
	}
}