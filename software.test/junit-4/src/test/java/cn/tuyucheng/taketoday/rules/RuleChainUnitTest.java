package cn.tuyucheng.taketoday.rules;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import static org.junit.Assert.assertTrue;

public class RuleChainUnitTest {

   @Rule
   public RuleChain chain = RuleChain.outerRule(new MessageLogger("First rule"))
         .around(new MessageLogger("Second rule"))
         .around(new MessageLogger("Third rule"));

   @Test
   public void givenRuleChain_whenTestRuns_thenChainOrderApplied() {
      assertTrue(true);
   }
}