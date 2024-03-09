package cn.tuyucheng.taketoday.trim;

import com.google.common.base.CharMatcher;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LTrimRTrimUnitTest {

   private String src = "        White spaces left and right            ";
   private final static String ltrimResult = "White spaces left and right            ";
   private final static String rtrimResult = "        White spaces left and right";

   @Test
   public void givenString_whenCallingWhileCharacters_thenReturnsTrue() {
      String ltrim = LTrimRTrim.whileLtrim(src);
      String rtrim = LTrimRTrim.whileRtrim(src);

      // Compare the Strings obtained and the expected
      Assertions.assertTrue(ltrimResult.equalsIgnoreCase(ltrim));

      Assertions.assertTrue(rtrimResult.equalsIgnoreCase(rtrim));
   }

   @Test
   public void givenString_whenCallingContainsWithReplaceAll_shouldReturnTrue() {
      // Use replaceAll with Regular Expressions
      String ltrim = src.replaceAll("^\\s+", "");
      String rtrim = src.replaceAll("\\s+$", "");

      // Compare the Strings obtained and the expected
      Assertions.assertTrue(ltrimResult.equalsIgnoreCase(ltrim));

      Assertions.assertTrue(rtrimResult.equalsIgnoreCase(rtrim));
   }

   @Test
   public void givenString_whenCallingPaternCompileMatcherReplaceAll_thenReturnsTrue() {
      // Use Pattern Compile Matcher and Find to avoid case insensitive issues
      String ltrim = LTrimRTrim.patternLtrim(src);
      String rtrim = LTrimRTrim.patternRtrim(src);

      // Compare the Strings obtained and the expected
      Assertions.assertTrue(ltrimResult.equalsIgnoreCase(ltrim));

      Assertions.assertTrue(rtrimResult.equalsIgnoreCase(rtrim));
   }

   @Test
   public void givenString_whenCallingGuavaCharMatcher_thenReturnsTrue() {
      // Use StringUtils containsIgnoreCase to avoid case insensitive issues
      String ltrim = CharMatcher.whitespace().trimLeadingFrom(src);
      ;
      String rtrim = CharMatcher.whitespace().trimTrailingFrom(src);

      // Compare the Strings obtained and the expected
      Assertions.assertTrue(ltrimResult.equalsIgnoreCase(ltrim));

      Assertions.assertTrue(rtrimResult.equalsIgnoreCase(rtrim));
   }

   @Test
   public void givenString_whenCallingStringUtilsStripStartEnd_thenReturnsTrue() {
      // Use StringUtils containsIgnoreCase to avoid case insensitive issues
      String ltrim = StringUtils.stripStart(src, null);
      String rtrim = StringUtils.stripEnd(src, null);

      // Compare the Strings obtained and the expected
      Assertions.assertTrue(ltrimResult.equalsIgnoreCase(ltrim));
      Assertions.assertTrue(rtrimResult.equalsIgnoreCase(rtrim));
   }

}
