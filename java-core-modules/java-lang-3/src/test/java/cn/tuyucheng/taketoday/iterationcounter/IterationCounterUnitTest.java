package cn.tuyucheng.taketoday.iterationcounter;

import org.junit.Test;

import static cn.tuyucheng.taketoday.iterationcounter.IterationCounter.IMDB_TOP_MOVIES;
import static cn.tuyucheng.taketoday.iterationcounter.IterationCounter.getRankingsWithForEachLoop;
import static cn.tuyucheng.taketoday.iterationcounter.IterationCounter.getRankingsWithForLoop;
import static cn.tuyucheng.taketoday.iterationcounter.IterationCounter.getRankingsWithFunctionalForEachLoop;
import static cn.tuyucheng.taketoday.iterationcounter.IterationCounter.getRankingsWithStream;
import static org.assertj.core.api.Assertions.assertThat;

public class IterationCounterUnitTest {
	@Test
	public void givenRankings_whenCalculateWithForLoop_thenRankingsCorrect() {
		assertThat(getRankingsWithForLoop(IMDB_TOP_MOVIES))
			.containsExactly("1: The Shawshank Redemption",
				"2: The Godfather", "3: The Godfather II", "4: The Dark Knight");
	}

	@Test
	public void givenRankings_whenCalculateWithForEachLoop_thenRankingsCorrect() {
		assertThat(getRankingsWithForEachLoop(IMDB_TOP_MOVIES))
			.containsExactly("1: The Shawshank Redemption",
				"2: The Godfather", "3: The Godfather II", "4: The Dark Knight");
	}

	@Test
	public void givenRankings_whenCalculateWithFunctionalForEach_thenRankingsCorrect() {
		assertThat(getRankingsWithFunctionalForEachLoop(IMDB_TOP_MOVIES))
			.containsExactly("1: The Shawshank Redemption",
				"2: The Godfather", "3: The Godfather II", "4: The Dark Knight");
	}

	@Test
	public void givenRankings_whenCalculateWithStream_thenRankingsCorrect() {
		assertThat(getRankingsWithStream(IMDB_TOP_MOVIES.stream()))
			.containsExactly("1: The Shawshank Redemption",
				"2: The Godfather", "3: The Godfather II", "4: The Dark Knight");
	}
}
