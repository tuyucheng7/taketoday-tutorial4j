package cn.tuyucheng.taketoday.staticmodifier;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

public class StaticBlockDemoUnitTest {

   @Test
   public void whenAddedListElementsThroughStaticBlock_thenEnsureCorrectOrder() {
      List<String> actualList = StaticBlockDemo.getRanks();
      assertThat(actualList, contains("Lieutenant", "Captain", "Major", "Colonel", "General"));
   }
}
