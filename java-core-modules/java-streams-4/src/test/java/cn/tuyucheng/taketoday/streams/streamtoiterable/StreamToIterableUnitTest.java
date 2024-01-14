package cn.tuyucheng.taketoday.streams.streamtoiterable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class StreamToIterableUnitTest {

   @Test
   public void givenList_whenLambdaIsUsed_ThenStreamAsIterable() {
      StreamToIterable streamToIterable = new StreamToIterable();
      String actualString = streamToIterable.streamToIterableLambda(getListOfStrings());
      String expectedString = "Thisisasentencewithnospaces";
      Assertions.assertEquals(expectedString, actualString);
   }

   @Test
   public void givenList_whenMethodReferenceIsUsed_ThenStreamAsIterable() {
      StreamToIterable streamToIterable = new StreamToIterable();
      String actualString = streamToIterable.streamToIterableMethodReference(getListOfStrings());
      String expectedString = "Thisisasentencewithnospaces";
      Assertions.assertEquals(expectedString, actualString);
   }

   @Test
   public void givenList_whenCollectedToList_ThenStreamAsIterable() {
      StreamToIterable streamToIterable = new StreamToIterable();
      String actualString = streamToIterable.streamToList(getListOfStrings());
      String expectedString = "Thisisasentencewithnospaces";
      Assertions.assertEquals(expectedString, actualString);
   }

   private List<String> getListOfStrings() {
      List<String> listOfStrings = new ArrayList<>();
      listOfStrings.add("This");
      listOfStrings.add("is");
      listOfStrings.add("a");
      listOfStrings.add(null);
      listOfStrings.add("sentence");
      listOfStrings.add("with");
      listOfStrings.add("no");
      listOfStrings.add(null);
      listOfStrings.add("spaces");
      return listOfStrings;
   }
}
