package cn.tuyucheng.taketoday.commons.lang3.test;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ImmutableTripleUnitTest {

	private static ImmutableTriple<String, String, String> immutableTriple;

	@BeforeClass
	public static void setUpImmutableTripleInstance() {
		immutableTriple = new ImmutableTriple<>("leftElement", "middleElement", "rightElement");
	}

	@Test
	public void givenImmutableTripleInstance_whenCalledgetLeft_thenCorrect() {
		assertThat(immutableTriple.getLeft()).isEqualTo("leftElement");
	}

	@Test
	public void givenImmutableTripleInstance_whenCalledgetMiddle_thenCorrect() {
		assertThat(immutableTriple.getMiddle()).isEqualTo("middleElement");
	}

	@Test
	public void givenImmutableInstance_whenCalledgetRight_thenCorrect() {
		assertThat(immutableTriple.getRight()).isEqualTo("rightElement");
	}
}
