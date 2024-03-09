package cn.tuyucheng.taketoday.contains;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

public class CaseInsensitiveWorkaroundsUnitTest {

   private String src = "Lorem ipsum dolor sit amet";
   private String dest = "lorem";

   @Test
   public void givenString_whenCallingContainsWithToLowerOrUpperCase_shouldReturnTrue() {
      // Use toLowerCase to avoid case insensitive issues
      Assertions.assertTrue(src.toLowerCase().contains(dest.toLowerCase()));

      // Use toUpperCase to avoid case insensitive issues
      Assertions.assertTrue(src.toUpperCase().contains(dest.toUpperCase()));
   }

   @Test
   public void givenString_whenCallingStringMatches_thenReturnsTrue() {
      // Use String Matches to avoid case insensitive issues
      Assertions.assertTrue(src.matches("(?i).*" + dest + ".*"));
   }

   @Test
   public void givenString_whenCallingStringRegionMatches_thenReturnsTrue() {
      // Use String Region Matches to avoid case insensitive issues
      CaseInsensitiveWorkarounds comparator = new CaseInsensitiveWorkarounds();
      Assertions.assertTrue(comparator.processRegionMatches(src, dest));
   }


   @Test
   public void givenString_whenCallingPaternCompileMatcherFind_thenReturnsTrue() {
      // Use Pattern Compile Matcher and Find to avoid case insensitive issues
      Assertions.assertTrue(Pattern.compile(Pattern.quote(dest),
            Pattern.CASE_INSENSITIVE).matcher(src).find());
   }

   @Test
   public void givenString_whenCallingStringUtilsContainsIgnoreCase_thenReturnsTrue() {
      // Use StringUtils containsIgnoreCase to avoid case insensitive issues
      Assertions.assertTrue(StringUtils.containsIgnoreCase(src, dest));
   }

}
