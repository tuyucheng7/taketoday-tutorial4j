package cn.tuyucheng.taketoday.stringtostream;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StringToCharStreamUnitTest {

   private String testString = "Tests";

   @Test
   public void givenTestString_whenChars_thenReturnIntStream() {
      assertThat(testString.chars(), instanceOf(IntStream.class));
   }

   @Test
   public void givenTestString_whenCodePoints_thenReturnIntStream() {
      assertThat(testString.codePoints(), instanceOf(IntStream.class));
   }

   @Test
   public void givenTestString_whenCodePoints_thenShowOccurences() throws Exception {
      Map<Character, Integer> map = testString.codePoints()
            .mapToObj(c -> (char) c)
            .filter(Character::isLetter)
            .collect(Collectors.toMap(c -> c, c -> 1, Integer::sum));

      System.out.println(map);
   }

   @Test
   public void givenIntStream_whenMapToObj_thenReturnCharacterStream() {
      Stream<Character> characterStream = testString.chars()
            .mapToObj(c -> (char) c);
      Stream<Character> characterStream1 = testString.codePoints()
            .mapToObj(c -> (char) c);
      assertNotNull(characterStream, "IntStream returned by chars() did not map to Stream<Character>");
      assertNotNull(characterStream1, "IntStream returned by codePoints() did not map to Stream<Character>");
   }

   @Test
   public void givenIntStream_whenMapToObj_thenReturnStringStream() {
      List<String> strings = testString.codePoints()
            .mapToObj(c -> String.valueOf((char) c))
            .collect(Collectors.toList());

      assertEquals(strings.size(), 5);
   }

}
