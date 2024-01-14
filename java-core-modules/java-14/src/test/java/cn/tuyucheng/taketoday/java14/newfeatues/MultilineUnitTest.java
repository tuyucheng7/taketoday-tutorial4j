package cn.tuyucheng.taketoday.java14.newfeatues;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MultilineUnitTest {

   @SuppressWarnings("preview")
   String multiline = """
            A quick brown fox jumps over a lazy dog; \
         the lazy dog howls loudly.""";

   @SuppressWarnings("preview")
   String anotherMultiline = """
            A quick brown fox jumps over a lazy dog;
         the lazy dog howls loudly.""";

   @Test
   public void givenMultilineString_whenSlashUsed_thenNoNewLine() {
      assertFalse(multiline.contains("\n"));
      assertTrue(anotherMultiline.contains("\n"));
   }
}