package cn.tuyucheng.taketoday.migration.junit4;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AssertionsExampleUnitTest {
   @Test
   @Ignore
   public void shouldFailBecauseTheNumbersAreNotEqualld() {
      assertEquals("Numbers are not equal!", 2, 3);
   }

   @Test
   public void shouldAssertAllTheGroup() {
      List<Integer> list = Arrays.asList(1, 2, 3);
      assertEquals("List is not incremental", 1, list.get(0).intValue());
      assertEquals("List is not incremental", 2, list.get(1).intValue());
      assertEquals("List is not incremental", 3, list.get(2).intValue());
   }
}