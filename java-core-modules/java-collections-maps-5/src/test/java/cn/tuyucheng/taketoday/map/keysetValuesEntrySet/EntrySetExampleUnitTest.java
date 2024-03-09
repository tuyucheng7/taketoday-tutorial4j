package cn.tuyucheng.taketoday.map.keysetValuesEntrySet;

import org.junit.jupiter.api.Test;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EntrySetExampleUnitTest {

   @Test
   public void givenHashMap_whenEntrySetApplied_thenShouldReturnSetOfEntries() {

      Map<String, Integer> map = new HashMap<>();
      map.put("one", 1);
      map.put("two", 2);

      Set<Map.Entry<String, Integer>> actualValues = map.entrySet();

      assertEquals(2, actualValues.size());
      assertTrue(actualValues.contains(new SimpleEntry<>("one", 1)));
      assertTrue(actualValues.contains(new SimpleEntry<>("two", 2)));

   }

}
