package cn.tuyucheng.taketoday.optional;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for{@link Optional}in Java 11.
 */

public class OptionalUnitTest {

   @Test
   public void givenAnEmptyOptional_isEmpty_thenBehavesAsExpected() {
      Optional<String> opt = Optional.of("Tuyucheng");
      assertFalse(opt.isEmpty());

      opt = Optional.ofNullable(null);
      assertTrue(opt.isEmpty());
   }
}