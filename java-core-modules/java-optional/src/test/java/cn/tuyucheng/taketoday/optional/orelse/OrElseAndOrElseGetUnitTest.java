package cn.tuyucheng.taketoday.optional.orelse;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrElseAndOrElseGetUnitTest {

   private OrElseAndOrElseGet orElsevsOrElseGet = new OrElseAndOrElseGet();

   private static final Logger LOG = LoggerFactory.getLogger(OrElseAndOrElseGetUnitTest.class);

   @Test
   public void givenNonEmptyOptional_whenOrElseUsed_thenGivenStringReturned() {
      LOG.info("In givenNonEmptyOptional_whenOrElseUsed_thenGivenStringReturned()");
      String name = orElsevsOrElseGet.getNameUsingOrElse("tuyucheng");
      assertEquals(name, "tuyucheng");
   }

   @Test
   public void givenEmptyOptional_whenOrElseUsed_thenRandomStringReturned() {
      LOG.info("In givenEmptyOptional_whenOrElseUsed_thenRandomStringReturned()");
      String name = orElsevsOrElseGet.getNameUsingOrElse(null);
      assertTrue(orElsevsOrElseGet.names.contains(name));
   }

   @Test
   public void givenNonEmptyOptional_whenOrElseGetUsed_thenGivenStringReturned() {
      LOG.info("In givenNonEmptyOptional_whenOrElseGetUsed_thenGivenStringReturned()");
      String name = orElsevsOrElseGet.getNameUsingOrElseGet("tuyucheng");
      assertEquals(name, "tuyucheng");
   }

   @Test
   public void givenEmptyOptional_whenOrElseGetUsed_thenRandomStringReturned() {
      LOG.info("In givenEmptyOptional_whenOrElseGetUsed_thenRandomStringReturned()");
      String name = orElsevsOrElseGet.getNameUsingOrElseGet(null);
      assertTrue(orElsevsOrElseGet.names.contains(name));
   }
}
