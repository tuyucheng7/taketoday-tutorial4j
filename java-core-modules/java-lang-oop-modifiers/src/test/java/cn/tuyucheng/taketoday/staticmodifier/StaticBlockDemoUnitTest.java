package cn.tuyucheng.taketoday.staticmodifier;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;

public class StaticBlockDemoUnitTest {

	@Test
	public void whenAddedListElementsThroughStaticBlock_thenEnsureCorrectOrder() {
		List<String> actualList = StaticBlockDemo.getRanks();
		assertThat(actualList, contains("Lieutenant", "Captain", "Major", "Colonel", "General"));
	}
}
