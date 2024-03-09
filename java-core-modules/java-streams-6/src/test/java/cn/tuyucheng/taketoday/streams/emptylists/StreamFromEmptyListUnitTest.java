package cn.tuyucheng.taketoday.streams.emptylists;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StreamFromEmptyListUnitTest {

   @Test
   public void givenEmptyList_whenConvertedToStream_thenResultantStreamIsEmpty() {
      List<String> emptyList = new ArrayList<>();
      Stream<String> emptyStream = emptyList.stream();
      assertTrue(emptyStream.findAny().isEmpty());
   }

   @Test
   public void givenEmptyList_whenCollectStreamOfEmptyListToAnotherList_thenResultantListIsEmpty() {
      List<String> emptyList = new ArrayList<>();
      List<String> collectedList = emptyList.stream().collect(Collectors.toList());
      assertTrue(collectedList.isEmpty());
   }
}
