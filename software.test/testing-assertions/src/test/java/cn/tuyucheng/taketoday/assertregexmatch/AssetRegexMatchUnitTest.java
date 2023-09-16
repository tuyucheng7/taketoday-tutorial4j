package cn.tuyucheng.taketoday.assertregexmatch;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;

public class AssetRegexMatchUnitTest {

   @Test
   void whenUsingJunit5assertTrue_thenGetExpectedResult() {
      assertTrue("Java at Taketoday".matches(".* at Taketoday"));
      assertFalse("something else".matches(".* at Taketoday"));
   }

   @Test
   void whenUsingJunit5assertLinesMatch_thenGetExpectedResult() {
      assertLinesMatch(List.of(".* at Taketoday$"), List.of("Kotlin at Taketoday"));
   }

   @Test
   void whenUsingJunit5assertLinesMatch_thenEqualsIsCheckedFirst() {
      assertFalse(".* at Taketoday$".matches(".* at Taketoday$"));
      assertLinesMatch(List.of(".* at Taketoday$"), List.of(".* at Taketoday$"));
   }

   @Test
   void whenUsingAssertJMatches_thenGetExpectedResult() {
      org.assertj.core.api.Assertions.assertThat("Linux at Taketoday").matches(".* at Taketoday$");
      org.assertj.core.api.Assertions.assertThat("something unrelated").doesNotMatch(".* at Taketoday$");
   }

   @Test
   void whenUsingHamcrestMatches_thenGetExpectedResult() {
      org.hamcrest.MatcherAssert.assertThat("Computer science at Taketoday", matchesPattern(".* at Taketoday$"));
      org.hamcrest.MatcherAssert.assertThat("something unrelated", not(matchesPattern(".* at Taketoday$")));
   }
}