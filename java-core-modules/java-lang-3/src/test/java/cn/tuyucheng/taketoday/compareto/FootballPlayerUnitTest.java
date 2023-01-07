package cn.tuyucheng.taketoday.compareto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class FootballPlayerUnitTest {

	@Test
	public void givenInconsistentCompareToAndEqualsImpl_whenUsingSortedSet_thenSomeElementsAreNotAdded() {
		FootballPlayer messi = new FootballPlayer("Messi", 800);
		FootballPlayer ronaldo = new FootballPlayer("Ronaldo", 800);

		TreeSet<FootballPlayer> set = new TreeSet<>();
		set.add(messi);
		set.add(ronaldo);

		Assertions.assertThat(set).hasSize(1);
		Assertions.assertThat(set).doesNotContain(ronaldo);
	}

	@Test
	public void givenCompareToImpl_whenUsingCustomComparator_thenComparatorLogicIsApplied() {
		FootballPlayer ronaldo = new FootballPlayer("Ronaldo", 900);
		FootballPlayer messi = new FootballPlayer("Messi", 800);
		FootballPlayer modric = new FootballPlayer("Modric", 100);

		List<FootballPlayer> players = Arrays.asList(ronaldo, messi, modric);
		Comparator<FootballPlayer> nameComparator = Comparator.comparing(FootballPlayer::getName);
		Collections.sort(players, nameComparator);

		Assertions.assertThat(players).containsExactly(messi, modric, ronaldo);
	}

	@Test
	public void givenCompareToImpl_whenSavingElementsInTreeMap_thenKeysAreSortedUsingCompareTo() {
		FootballPlayer ronaldo = new FootballPlayer("Ronaldo", 900);
		FootballPlayer messi = new FootballPlayer("Messi", 800);
		FootballPlayer modric = new FootballPlayer("Modric", 100);

		Map<FootballPlayer, String> players = new TreeMap<>();
		players.put(ronaldo, "forward");
		players.put(messi, "forward");
		players.put(modric, "midfielder");

		Assertions.assertThat(players.keySet()).containsExactly(modric, messi, ronaldo);
	}

}
